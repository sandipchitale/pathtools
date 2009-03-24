package pathtools;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
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
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * This launches the OS file explorer showing the selected folder or the folder
 * containing the selected file.
 * 
 * @author Sandip V. Chitale
 * 
 */
public class ExploreAction implements IWorkbenchWindowPulldownDelegate2 {
	private File fileObject;

	private IWorkbenchWindow window;

	public void dispose() {
		if (exploreMenuInFileMenu != null) {
			exploreMenuInFileMenu.dispose();
		}
		if (exploreMenu != null) {
			exploreMenu.dispose();
		}
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void run(IAction action) {
		// Is this a physical file on the disk ?
		if (fileObject != null) {
			explore(fileObject);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		fileObject = null;
		action.setEnabled(false);
		try {
			IPath location = null;
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				// Is only one item selected?
				if (structuredSelection.size() == 1) {
					Object firstElement = structuredSelection.getFirstElement();
					if (firstElement instanceof IResource) {
						// Is this an IResource ?
						IResource resource = (IResource) firstElement;
						location = resource.getLocation();
					} else if (firstElement instanceof IAdaptable) {
						IAdaptable adaptable = (IAdaptable) firstElement;
						// Is this an IResource adaptable ?
						IResource resource = (IResource) adaptable
								.getAdapter(IResource.class);
						if (resource != null) {
							location = resource.getLocation();
						}
					} else if (firstElement.getClass().getName().equals(
							"com.aptana.ide.core.ui.io.file.LocalFile")) {
						try {
							Method getFile = firstElement.getClass()
									.getDeclaredMethod("getFile");
							Object object = getFile.invoke(firstElement);
							if (object instanceof File) {
								fileObject = (File) object;
								return;
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
			if (fileObject == null) {
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
									location = iFile.getLocation();
								}
							}
						}
					}
				}
			}
			if (location != null) {
				fileObject = location.toFile();
			}
		} finally {
			action.setEnabled(fileObject != null);
		}
	}


	private Menu exploreMenuInFileMenu;
	public Menu getMenu(Menu parent) {
		exploreMenuInFileMenu = new Menu(parent);
		exploreMenuInFileMenu.addMenuListener(new MenuListener() {
			public void menuHidden(MenuEvent e) {}
			public void menuShown(MenuEvent e) {
				MenuItem[] items = exploreMenuInFileMenu.getItems();
				for (MenuItem menuItem : items) {
					menuItem.dispose();
				}
				fillMenu(exploreMenuInFileMenu);
			}			
		});
		return exploreMenuInFileMenu;
	}
	
	private Menu exploreMenu;
	public Menu getMenu(Control parent) {
		if (exploreMenu != null) {
			exploreMenu.dispose();
		}
		exploreMenu = new Menu(parent);
		fillMenu(exploreMenu);
		return exploreMenu;
		
	}

	private void fillMenu(Menu menu) {
		if (fileObject != null) {
			File gotoFile = fileObject;
			while (gotoFile != null) {
				final File finalGotoFile = gotoFile;
				MenuItem gotoParentAction = new MenuItem(menu, SWT.PUSH);
				gotoParentAction.setText("Go to " + gotoFile.getAbsolutePath());
				gotoParentAction.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						explore(finalGotoFile);
					}
				});
				gotoFile = gotoFile.getParentFile();
			}
			new MenuItem(menu, SWT.SEPARATOR);
		}

		MenuItem gotoAction = new MenuItem(menu, SWT.PUSH);
		gotoAction.setText("Go to...");
		gotoAction.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String defaultValue = "";
				Clipboard clipboard = new Clipboard(window.getShell().getDisplay());
				Object contents = clipboard.getContents(TextTransfer.getInstance());
				if (contents instanceof String) {
					defaultValue = (String) contents;
					if (!new File(defaultValue).exists()) {
						defaultValue = System.getProperty("user.home", ".");
					}
				}
				InputDialog inputDialog = new InputDialog(
						window.getShell(), "Go to Path", "Path:", defaultValue, null);
				if (inputDialog.open() == Window.OK) {
					String path = inputDialog.getValue();
					File file = new File(path);
					// Is this a physical file on the disk ?
					if (file.exists()) {
						// Get the configured explorer commands for folder
						// and file
						explore(file);
					}
				}
			}
		});

		new MenuItem(menu, SWT.SEPARATOR);

		final IPath workspaceLocation = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		MenuItem gotoWorkspace = new MenuItem(menu, SWT.PUSH);
		gotoWorkspace.setText("Go to Workspace Folder: " + workspaceLocation.toFile().getAbsolutePath());
		gotoWorkspace.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				explore(workspaceLocation.toFile());
			}
		});

		Location configurationLocation = Platform.getConfigurationLocation();
		if (configurationLocation != null) {
			final URL url = configurationLocation.getURL();
			if (url != null && new File(url.getFile()).exists()) {
				MenuItem gotoConfigurationFolder = new MenuItem(menu, SWT.PUSH);
				gotoConfigurationFolder.setText("Go to Configuration Folder: " + url.getFile());
				gotoConfigurationFolder.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						explore(new File(url.getFile()));
					}
				});
			}
		}

		Location userDataLocation = Platform.getUserLocation();
		if (userDataLocation != null) {
			final URL url = userDataLocation.getURL();
			if (url != null && (new File(url.getFile()).exists())) {
				MenuItem gotoUserFolder = new MenuItem(menu, SWT.PUSH);
				gotoUserFolder.setText("Go to User Data Folder: " + url.getFile());
				gotoUserFolder.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						explore(new File(url.getFile()));
					}
				});
			}
		}
		Location installLocation = Platform.getInstallLocation();
		if (installLocation != null) {
			final URL url = installLocation.getURL();
			if (url != null) {
				MenuItem gotoInstallFolder = new MenuItem(menu, SWT.PUSH);
				gotoInstallFolder.setText("Go to Install Folder: " + url.getFile());
				gotoInstallFolder.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						explore(new File(url.getFile()));
					}
				});
			}
		}
		new MenuItem(menu, SWT.SEPARATOR);
		MenuItem userHomeFolder = new MenuItem(menu, SWT.PUSH);
		userHomeFolder.setText("Go to user.home: " + System.getProperty("user.home"));
		userHomeFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				explore(new File(System.getProperty("user.home")));
			}
		});
		MenuItem userDirFolder = new MenuItem(menu, SWT.PUSH);
		userDirFolder.setText("Go to user.dir: " + System.getProperty("user.dir"));
		userDirFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				explore(new File(System.getProperty("user.dir")));
			}
		});
		MenuItem javaIoTmpFolder = new MenuItem(menu, SWT.PUSH);
		javaIoTmpFolder.setText("Go to java.io.tmpdir: " + System.getProperty("java.io.tmpdir"));
		javaIoTmpFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				explore(new File(System.getProperty("java.io.tmpdir")));
			}
		});
	}
	
	private static void explore(File file) {
		// Get the configured explorer commands for folder and file
		if (file != null && file.exists()) {
			String folderExploreComand = Activator.getDefault().getPreferenceStore().getString(Activator.FOLDER_EXPLORE_COMMAND_KEY);
			String fileExploreComand = Activator.getDefault().getPreferenceStore().getString(Activator.FILE_EXPLORE_COMMAND_KEY);
			String exploreCommand;
			if (file.isDirectory()) {
				exploreCommand = folderExploreComand;
			} else {
				exploreCommand = fileExploreComand;				
			}
			if (exploreCommand != null) {
				Utilities.launch(exploreCommand, file);
			}
		}
	}

}
