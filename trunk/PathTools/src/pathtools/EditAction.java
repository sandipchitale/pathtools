package pathtools;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
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
 * This launches the external text editor for selected folder or file.
 *
 * @author Sandip V. Chitale
 *
 */
public class EditAction implements IWorkbenchWindowPulldownDelegate2 {
	private File fileObject;

	private IWorkbenchWindow window;

	public void dispose() {
		if (editMenuInFileMenu != null) {
			editMenuInFileMenu.dispose();
		}
		if (editMenu != null) {
			editMenu.dispose();
		}
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void run(IAction action) {
		if (fileObject == null) {
			edit(ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile());
		} else {
			// Is this a physical file on the disk ?
			edit(fileObject);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		fileObject = null;
		IPath location = null;
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			// Is only one item selected?
			if (structuredSelection.size() == 1) {
				Object firstElement = structuredSelection.getFirstElement();
				if (firstElement instanceof IResource) {
					// Is this an IResource
					IResource resource = (IResource) firstElement;
					location = resource.getLocation();
				} else if (firstElement instanceof IAdaptable) {
					IAdaptable adaptable = (IAdaptable) firstElement;
					// Is this an IResource adaptable
					IResource resource = (IResource) adaptable
							.getAdapter(IResource.class);
					if (resource != null) {
						location = resource.getLocation();
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
	}

	private Menu editMenuInFileMenu;
	public Menu getMenu(Menu parent) {
		editMenuInFileMenu = new Menu(parent);
		editMenuInFileMenu.addMenuListener(new MenuListener() {
			public void menuHidden(MenuEvent e) {}
			public void menuShown(MenuEvent e) {
				MenuItem[] items = editMenuInFileMenu.getItems();
				for (MenuItem menuItem : items) {
					menuItem.dispose();
				}
				fillMenu(editMenuInFileMenu);
			}
		});
		return editMenuInFileMenu;
	}

	private Menu editMenu;
	public Menu getMenu(Control parent) {
		if (editMenu != null) {
			editMenu.dispose();
		}
		editMenu = new Menu(parent);
		fillMenu(editMenu);
		return editMenu;
	}

	private void fillMenu(Menu menu) {
		if (fileObject != null) {
			MenuItem runAction = new MenuItem(menu, SWT.PUSH);
			runAction.setText("Open in external editor " + fileObject.getAbsolutePath());
			runAction.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					edit(fileObject);
				}
			});
			new MenuItem(menu, SWT.SEPARATOR);
		}
		final File logFile = Platform.getLogFileLocation().toFile();
		if (logFile.exists() && logFile.isFile()) {
			MenuItem editWorkspaceLog = new MenuItem(menu, SWT.PUSH);
			editWorkspaceLog.setText("Open in external editor " + logFile.getAbsolutePath());
			editWorkspaceLog.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					edit(logFile);
				}
			});
		}
		
		// Support opening of .ini file.
		String eclipseDotCommands = System.getProperty("eclipse.commands");
		if (eclipseDotCommands != null) {
			File iniFile = null;
			String[] eclipseDotCommandsArray = eclipseDotCommands.split("\n");
			for (int i = 0; i < eclipseDotCommandsArray.length; i++) {
				if ("--launcher.ini".equals(eclipseDotCommandsArray[i])) {
					if (i < (eclipseDotCommandsArray.length - 1)) {
						String launcherIni = eclipseDotCommandsArray[++i];
						iniFile = new File(launcherIni);
						if (iniFile.exists()) {
							break;
						} else {
							iniFile = null;
						}
					}
				} else if ("-launcher".equals(eclipseDotCommandsArray[i])) {
					if (i < (eclipseDotCommandsArray.length - 1)) {
						String launcher = eclipseDotCommandsArray[++i];
						File launcherFile = new File(launcher);
						if (launcherFile.exists() && launcherFile.isFile()) {
							String launcherFileName = launcherFile.getName();
							int lastIndexOfDot = launcherFileName.lastIndexOf('.');
							if (lastIndexOfDot == -1) {
								iniFile = new File(launcherFile.getAbsolutePath()+".ini");
							} else {
								launcherFileName = launcherFileName.substring(0, lastIndexOfDot);
								iniFile = new File(launcherFile.getParentFile(), launcherFileName+".ini");
							}
							break;
						}
					}
				}
			}
			if (iniFile != null && iniFile.exists() && iniFile.isFile()) {
				MenuItem editIniFile = new MenuItem(menu, SWT.PUSH);
				editIniFile.setText("Open in external editor " + iniFile.getAbsolutePath());
				final File finalIniFile = iniFile;
				editIniFile.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						edit(finalIniFile);
					}
				});
			}
		}
	}

	private static void edit(File file) {
		// Get the configured explorer commands for folder and file
		if (file != null && file.exists()) {
			String folderEditComand = Activator.getDefault().getPreferenceStore().getString(PathToolsPreferences.FOLDER_EDIT_COMMAND_KEY);
			String fileEditComand = Activator.getDefault().getPreferenceStore().getString(PathToolsPreferences.FILE_EDIT_COMMAND_KEY);
			String editCommand;
			if (file.isDirectory()) {
				editCommand = folderEditComand;
			} else {
				editCommand = fileEditComand;
			}
			if (editCommand != null) {
				try {
					Activator.getDefault().setFile(file);
					Utilities.launch(editCommand);
				} finally {
					Activator.getDefault().setFile(null);
				}
			}
		}
	}
}
