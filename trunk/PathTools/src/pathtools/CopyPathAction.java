package pathtools;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * This copies the absolute paths of selected folders and files (one per line)
 * into the Clipboard.
 * 
 * @author Sandip V. Chitale
 * 
 */
public class CopyPathAction implements IWorkbenchWindowPulldownDelegate2 {
	private List<File> files = new LinkedList<File>();
	private List<IPath> resourcePaths = new LinkedList<IPath>();
	private List<String> javaQualifiedNames = new LinkedList<String>();
	private IWorkbenchWindow window;

	public void dispose() {
		if (copyPathsMenuInEditMenu != null) {
			copyPathsMenuInEditMenu.dispose(); 
		}
		if (copyPathsMenu != null) {
			copyPathsMenu.dispose(); 
		}
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void run(IAction action) {
		copyToClipboard(
				Activator.getDefault().getPreferenceStore().getString(Activator.LAST_COPY_PATH_FORMAT),
				files);
	}

	@SuppressWarnings("unchecked")
	public void selectionChanged(IAction action, ISelection selection) {
		action.setText("Copy " + 
				Activator.getDefault().getPreferenceStore().getString(Activator.LAST_COPY_PATH_FORMAT));
		// Start with a clear list
		files.clear();
		resourcePaths.clear();
		javaQualifiedNames.clear();
		if (selection instanceof IStructuredSelection) {
			// Get structured selection
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;

			// Iterate through selected items
			Iterator iterator = structuredSelection.iterator();
			while (iterator.hasNext()) {
				Object firstElement = iterator.next();
				IPath fullPath = null;
				IPath location = null;
				if (firstElement instanceof IResource) {
					// Is it a IResource ?
					IResource resource = (IResource) firstElement;
					// Get the location
					location = resource.getLocation();
					fullPath = resource.getFullPath();
				} else if (firstElement instanceof IAdaptable) {
					// Is it a IResource adaptable ?
					IAdaptable adaptable = (IAdaptable) firstElement;
					IResource resource = (IResource) adaptable
							.getAdapter(IResource.class);
					if (resource != null) {
						// Get the location
						location = resource.getLocation();
						fullPath = resource.getFullPath();
					}
				}
				if (location != null) {
					// Get the file for the location
					File file = location.toFile();
					if (file != null) {
						// Add the absolute path to the list
						files.add(file);
					}
				}
				if (fullPath != null) {
					resourcePaths.add(fullPath);
				}
				System.out.println(firstElement);
				Class<? extends Object> aClass  = firstElement.getClass();
				Class[] interfaces = aClass.getInterfaces();
				for (Class anInterface : interfaces) {
					if ("org.eclipse.jdt.core.ICompilationUnit".equals(anInterface.getName())) {
						try {
							Method method = aClass.getMethod("findPrimaryType");
							Object primaryType = method.invoke(firstElement);
							if (primaryType != null) {
								method = primaryType.getClass().getMethod("getFullyQualifiedName");
								Object primaryTypeFQN = method.invoke(primaryType);
								if (primaryTypeFQN != null) {
									javaQualifiedNames.add(primaryTypeFQN.toString());
								}
							}
						} catch (SecurityException e) {
						} catch (NoSuchMethodException e) {
						} catch (IllegalArgumentException e) {
						} catch (IllegalAccessException e) {
						} catch (InvocationTargetException e) {
						}
					}
				}
			}
		}
		if (files.size() == 0) {
			if (window != null) {
				IWorkbenchPage activePage = window.getActivePage();
				if (activePage != null) {
					IWorkbenchPart activeEditor = activePage.getActivePart();
					if (activeEditor instanceof ITextEditor) {
						ITextEditor abstractTextEditor = (ITextEditor) activeEditor;
						IEditorInput editorInput = abstractTextEditor.getEditorInput();
						if (editorInput instanceof IFileEditorInput) {
							IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
							IFile iFile = fileEditorInput.getFile();
							if (iFile != null) {
								File file = iFile.getLocation().toFile();
								if (file != null) {
									files.add(file);
									resourcePaths.add(iFile.getFullPath());
								}
							}
						}
					}
				}
			}
		}
		action.setEnabled(files.size() > 0);
	}
    	
	private static String[] pathFormats = new String[] {
		Activator.FILE_PATH,
		Activator.FILE_PARENT_PATH,
		Activator.FILE_NAME,
		Activator.FILE_PARENT_NAME,
		Activator.FILE_PATH_SLASHES,
		Activator.FILE_PARENT_PATH_SLASHES,
		Activator.FILE_PATH_BACKSLASHES,
		Activator.FILE_PARENT_PATH_BACKSLASHES,
	};

	private Menu copyPathsMenuInEditMenu;
	public Menu getMenu(Menu parent) {
		if (copyPathsMenuInEditMenu != null) {
			copyPathsMenuInEditMenu.dispose();
		}
		copyPathsMenuInEditMenu = new Menu(parent);
		fillMenu(copyPathsMenuInEditMenu);
		return copyPathsMenuInEditMenu;
	}
	
	private Menu copyPathsMenu;
	public Menu getMenu(Control parent) {
		if (copyPathsMenu != null) {
			copyPathsMenu.dispose();
		}
		copyPathsMenu = new Menu(parent);
		fillMenu(copyPathsMenu);
		return copyPathsMenu;
	}
	
	private void fillMenu(Menu menu)
	{
		for (String pathFormat: pathFormats) {
			MenuItem commandMenuItem = new MenuItem(menu, SWT.PUSH);					
			commandMenuItem.setText("Copy " + pathFormat);
			final String finalPathFormat = pathFormat;
			commandMenuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					copyToClipboard(finalPathFormat, files);
					Activator.getDefault().getPreferenceStore().setValue(
							Activator.LAST_COPY_PATH_FORMAT, finalPathFormat);
				}
			});
		}
		boolean enable = files.size() > 0;
		for (MenuItem menuItem : menu.getItems()) {
			menuItem.setEnabled(enable);
		}
		
		new MenuItem(menu, SWT.SEPARATOR);
		
		if (resourcePaths.size() > 0) {
			MenuItem resourcePathsMenuItem = new MenuItem(menu, SWT.PUSH);
			resourcePathsMenuItem.setText("Copy resource paths");
			resourcePathsMenuItem.setEnabled(resourcePaths.size() > 0);
			resourcePathsMenuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					StringBuilder stringBuilder = new StringBuilder();
					for (IPath path : resourcePaths) {
						stringBuilder.append(path.toString());
						if (resourcePaths.size() > 0) {
							stringBuilder.append(System.getProperty("line.separator", "\n"));
						}
					}
					copyToClipboard(stringBuilder.toString());
				}
			});
		}
		
		if (javaQualifiedNames.size() > 0) {
			MenuItem fqnMenuItem = new MenuItem(menu, SWT.PUSH);
			fqnMenuItem.setText("Copy FQN (Java)");
			fqnMenuItem.setEnabled(resourcePaths.size() > 0);
			fqnMenuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					StringBuilder stringBuilder = new StringBuilder();
					for (String javaQualifiedName : javaQualifiedNames) {
						stringBuilder.append(javaQualifiedName);
						if (javaQualifiedNames.size() > 0) {
							stringBuilder.append(System.getProperty("line.separator", "\n"));
						}
					}
					copyToClipboard(stringBuilder.toString());
				}
			});
		}
		
		new MenuItem(menu, SWT.SEPARATOR);
		
		final IPath workspaceLocation = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		if (workspaceLocation != null) {
			MenuItem copyWorkspacePath = new MenuItem(menu, SWT.PUSH);
			copyWorkspacePath.setText("Copy Workspace Folder location : " + workspaceLocation.toFile().getAbsolutePath());
			copyWorkspacePath.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					copyToClipboard(workspaceLocation.toFile().getAbsolutePath());
				}
			});
		}
		
		Location configurationLocation = Platform.getConfigurationLocation();
		if (configurationLocation != null) {
			final URL url = configurationLocation.getURL();
			if (url != null) {
				MenuItem copyConfigurationFolderLocation = new MenuItem(menu, SWT.PUSH);
				copyConfigurationFolderLocation.setText("Copy Configuration Folder location: " + url.getFile());
				copyConfigurationFolderLocation.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						copyToClipboard(url.getFile());
					}
				});
			}
		}

		Location userDataLocation = Platform.getUserLocation();
		if (userDataLocation != null) {
			final URL url = userDataLocation.getURL();
			if (url != null) {
				MenuItem copyUserFolderLocation = new MenuItem(menu, SWT.PUSH);
				copyUserFolderLocation.setText("Copy User Data Folder location: " + url.getFile());
				copyUserFolderLocation.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						copyToClipboard(url.getFile());
					}
				});
			}
		}

		Location installLocation = Platform.getInstallLocation();
		if (installLocation != null) {
			final URL url = installLocation.getURL();
			if (url != null) {
				MenuItem copyInstallFolderLocation = new MenuItem(menu, SWT.PUSH);
				copyInstallFolderLocation.setText("Copy Install Folder location: " + url.getFile());
				copyInstallFolderLocation.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						copyToClipboard(url.getFile());
					}
				});
			}
		}
		
		new MenuItem(menu, SWT.SEPARATOR);
		
		MenuItem userHomeFolder = new MenuItem(menu, SWT.PUSH);
		userHomeFolder.setText("Copy user.home: " + System.getProperty("user.home"));
		userHomeFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				copyToClipboard(System.getProperty("user.home"));
			}
		});
		MenuItem userDirFolder = new MenuItem(menu, SWT.PUSH);
		userDirFolder.setText("Copy user.dir: " + System.getProperty("user.dir"));
		userDirFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				copyToClipboard(System.getProperty("user.dir"));
			}
		});
		MenuItem javaIoTmpFolder = new MenuItem(menu, SWT.PUSH);
		javaIoTmpFolder.setText("Copy java.io.tmpdir: " + System.getProperty("java.io.tmpdir"));
		javaIoTmpFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				copyToClipboard(System.getProperty("java.io.tmpdir"));
			}
		});		
	}
	
	private static void copyToClipboard(String pathFormat, List<File> files) {
		// Are there any paths selected ?
		if (files.size() > 0) {
			// Build a string with each path on separate line
			StringBuilder stringBuilder = new StringBuilder();
			for (File file : files) {
				stringBuilder.append(Utilities.formatCommand(pathFormat, file)
						+ (files.size() > 1 ? "\n" : ""));
			}
			copyToClipboard(stringBuilder.toString());
		}
	}
	
	private static void copyToClipboard(String string) {
		// Get Clipboard
		Clipboard clipboard = new Clipboard(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell().getDisplay());
		// Put the paths string into the Clipboard
		clipboard.setContents(new Object[] { string },
				new Transfer[] { TextTransfer.getInstance() });
	}
}
