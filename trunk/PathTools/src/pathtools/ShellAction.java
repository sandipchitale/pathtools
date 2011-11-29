package pathtools;

import java.io.File;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

public class ShellAction implements IObjectActionDelegate, IMenuCreator {
	private File fileObject;
	private File projectFileObject;

	protected IWorkbenchWindow window;

	public void dispose() {
		if (exploreMenuInFileMenu != null) {
			exploreMenuInFileMenu.dispose();
		}
		if (exploreMenu != null) {
			exploreMenu.dispose();
		}
	}
	
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.window = targetPart.getSite().getWorkbenchWindow();
		action.setMenuCreator(this);
	}

	public void run(IAction action) {
		if (fileObject == null) {
			shell(ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile());
		} else {
			// Is this a physical file on the disk ?
			shell(fileObject);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		fileObject = null;
		IPath location = null;
		IPath projectLocation = null;
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			// Is only one item selected?
			if (structuredSelection.size() == 1) {
				Object firstElement = structuredSelection.getFirstElement();
				if (firstElement instanceof IResource) {
					// Is this an IResource ?
					IResource resource = (IResource) firstElement;
					location = resource.getLocation();
					if (!(resource instanceof IProject)) {
						IProject project = resource.getProject();
						projectLocation = project.getLocation();
					}
				} else if (firstElement instanceof IAdaptable) {
					IAdaptable adaptable = (IAdaptable) firstElement;
					// Is this an IResource adaptable ?
					IResource resource = (IResource) adaptable
							.getAdapter(IResource.class);
					if (resource != null) {
						location = resource.getLocation();
						if (!(resource instanceof IProject)) {
							IProject project = resource.getProject();
							projectLocation = project.getLocation();
						}
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
					} else if (activeEditor instanceof MultiPageEditorPart) {
						MultiPageEditorPart multiPageEditorPart = (MultiPageEditorPart) activeEditor;
						Object multiPageEditorActivePage = multiPageEditorPart.getSelectedPage();
						if (multiPageEditorActivePage instanceof ITextEditor) {
							ITextEditor abstractTextEditor = (ITextEditor) multiPageEditorActivePage;
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
		}
		if (location != null) {
			fileObject = location.toFile();
		}
		
		if (projectLocation != null) {
			projectFileObject = projectLocation.toFile();
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
			if (fileObject.isFile()) {
				fileObject = fileObject.getParentFile();
			}
			int times = 2;
			File shellAtFile = fileObject;
			for (int i = 0; shellAtFile != null && i < times; i++) {
				final File finalGotoFile = shellAtFile;
				MenuItem shellAtParentAction = new MenuItem(menu, SWT.PUSH);
				shellAtParentAction.setText("Command Line Shell at " + shellAtFile.getAbsolutePath());
				shellAtParentAction.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						shell(finalGotoFile);
					}
				});
				shellAtFile = shellAtFile.getParentFile();
			}
			new MenuItem(menu, SWT.SEPARATOR);
		}

		if (projectFileObject != null) {
			final File finalGotoFile = projectFileObject;
			MenuItem shellAtParentAction = new MenuItem(menu, SWT.PUSH);
			shellAtParentAction.setText("Command Line Shell at Project Folder: " + finalGotoFile.getAbsolutePath());
			shellAtParentAction.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					shell(finalGotoFile);
				}
			});
			new MenuItem(menu, SWT.SEPARATOR);
		}
		
		MenuItem shellAtAction = new MenuItem(menu, SWT.PUSH);
		shellAtAction.setText("Command Line Shell at...");
		shellAtAction.addSelectionListener(new SelectionAdapter() {
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
				DirectoryDialog directoryDialog = new DirectoryDialog(window.getShell());
				File defaultDirectory = new File(defaultValue);
				if (defaultDirectory.exists() && defaultDirectory.isDirectory()) {
					directoryDialog.setFilterPath(defaultDirectory.getAbsolutePath());
				}
				String directory = directoryDialog.open();
				if (directory != null) {
					File file = new File(directory);
					// Is this a physical file on the disk ?
					if (file.exists()) {
						// Get the configured explorer commands for folder
						// and file
						shell(file);
					}
				}
			}
		});

		new MenuItem(menu, SWT.SEPARATOR);

		final IPath workspaceLocation = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		MenuItem shellAtWorkspace = new MenuItem(menu, SWT.PUSH);
		shellAtWorkspace.setText("Command Line Shell at Workspace Folder: " + workspaceLocation.toFile().getAbsolutePath());
		shellAtWorkspace.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell(workspaceLocation.toFile());
			}
		});

		Location configurationLocation = Platform.getConfigurationLocation();
		if (configurationLocation != null) {
			final URL url = configurationLocation.getURL();
			if (url != null && new File(url.getFile()).exists()) {
				MenuItem shellAtConfigurationFolder = new MenuItem(menu, SWT.PUSH);
				shellAtConfigurationFolder.setText("Command Line Shell at Configuration Folder: " + url.getFile());
				shellAtConfigurationFolder.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						shell(new File(url.getFile()));
					}
				});
			}
		}

		Location userDataLocation = Platform.getUserLocation();
		if (userDataLocation != null) {
			final URL url = userDataLocation.getURL();
			if (url != null && (new File(url.getFile()).exists())) {
				MenuItem shellAtUserFolder = new MenuItem(menu, SWT.PUSH);
				shellAtUserFolder.setText("Command Line Shell at User Data Folder: " + url.getFile());
				shellAtUserFolder.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						shell(new File(url.getFile()));
					}
				});
			}
		}
		Location installLocation = Platform.getInstallLocation();
		if (installLocation != null) {
			final URL url = installLocation.getURL();
			if (url != null) {
				MenuItem shellAtInstallFolder = new MenuItem(menu, SWT.PUSH);
				shellAtInstallFolder.setText("Command Line Shell at Install Folder: " + url.getFile());
				shellAtInstallFolder.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						shell(new File(url.getFile()));
					}
				});
			}
		}
		new MenuItem(menu, SWT.SEPARATOR);
		MenuItem userHomeFolder = new MenuItem(menu, SWT.PUSH);
		userHomeFolder.setText("Command Line Shell at user.home: " + System.getProperty("user.home"));
		userHomeFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				shell(new File(System.getProperty("user.home")));
			}
		});
		MenuItem userDirFolder = new MenuItem(menu, SWT.PUSH);
		userDirFolder.setText("Command Line Shell at user.dir: " + System.getProperty("user.dir"));
		userDirFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell(new File(System.getProperty("user.dir")));
			}
		});
		MenuItem javaIoTmpFolder = new MenuItem(menu, SWT.PUSH);
		javaIoTmpFolder.setText("Command Line Shell at java.io.tmpdir: " + System.getProperty("java.io.tmpdir"));
		javaIoTmpFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				shell(new File(System.getProperty("java.io.tmpdir")));
			}
		});
	}
	
	public static void shell(File file) {
		// Get the configured explorer commands for folder and file
		if (file != null && file.exists()) {
			String folderShellComand = Activator.getDefault().getPreferenceStore().getString(PathToolsPreferences.FOLDER_SHELL_COMMAND_KEY);
			String fileShellComand = Activator.getDefault().getPreferenceStore().getString(PathToolsPreferences.FILE_SHELL_COMMAND_KEY);
			String shellCommand;
			if (file.isDirectory()) {
				shellCommand = folderShellComand;
			} else {
				shellCommand = fileShellComand;				
			}
			if (shellCommand != null) {
				try {
					PathToolsVariableResolver.setFile(file);
					Utilities.launch(shellCommand);
				} finally {
					PathToolsVariableResolver.setFile(null);
				}
			}
		}
	}
}
