package pathtools;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;

/**
 * This launches the OS file explorer showing the selected folder or the folder
 * containing the selected file.
 * 
 * @author Sandip V. Chitale
 * 
 */
public class ExploreAction implements IWorkbenchWindowPulldownDelegate {
	private File fileObject;

	private static String fileExploreComand = null;
	private static String folderExploreComand = null;

	private IWorkbenchWindow window;

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void run(IAction action) {
		// Get the configured explorer commands for folder and file
		folderExploreComand = Activator.getDefault().getPreferenceStore()
				.getString(Activator.FOLDER_EXPLORE_COMMAND_KEY);
		fileExploreComand = Activator.getDefault().getPreferenceStore()
				.getString(Activator.FILE_EXPLORE_COMMAND_KEY);
		if (fileExploreComand == null || folderExploreComand == null) {
			return;
		}
		// Is this a physical file on the disk ?
		if (fileObject != null) {
			String commandFormat = fileObject.isDirectory() ? folderExploreComand
					: fileExploreComand;

			Utilities.launch(commandFormat, fileObject);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		fileObject = null;
		action.setEnabled(false);
		try {
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				IPath location = null;
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
				if (location != null) {
					fileObject = location.toFile();
				}
			}
		} finally {
			action.setEnabled(fileObject != null);
		}
	}

	private Menu exploreMenu;

	public Menu getMenu(Control parent) {
		if (exploreMenu == null) {
			exploreMenu = new Menu(parent);
			MenuItem gotoAction = new MenuItem(exploreMenu, SWT.PUSH);
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
							String commandFormat = file.isDirectory() ?
									Activator
									.getDefault()
									.getPreferenceStore()
									.getString(
											Activator.FOLDER_EXPLORE_COMMAND_KEY)
									: Activator
											.getDefault()
											.getPreferenceStore()
											.getString(
													Activator.FILE_EXPLORE_COMMAND_KEY);

							Utilities.launch(commandFormat, file);
						}
					}
				}
			});
			MenuItem gotoWorkspace = new MenuItem(exploreMenu, SWT.PUSH);
			gotoWorkspace.setText("Go to Workspace Folder");
			gotoWorkspace.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					IPath workspaceLocation = ResourcesPlugin.getWorkspace().getRoot().getLocation();
					openFolder(workspaceLocation.toFile());
				}
			});
			MenuItem gotoConfigurationFolder = new MenuItem(exploreMenu, SWT.PUSH);
			gotoConfigurationFolder.setText("Go to Configuration Folder");
			gotoConfigurationFolder.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					Location configurationLocation = Platform.getConfigurationLocation();
					if (configurationLocation == null) {
						return;
					}
					URL url = configurationLocation.getURL();
					if (url == null) {
						return;
					}
					
					openFolder(new File(url.getFile()));
				}
			});
			MenuItem gotoUserFolder = new MenuItem(exploreMenu, SWT.PUSH);
			gotoUserFolder.setText("Go to User Data Folder");
			gotoUserFolder.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					Location userDataLocation = Platform.getUserLocation();
					if (userDataLocation == null) {
						return;
					}
					URL url = userDataLocation.getURL();
					if (url == null) {
						return;
					}
					
					openFolder(new File(url.getFile()));
				}
			});
			MenuItem gotoInstallFolder = new MenuItem(exploreMenu, SWT.PUSH);
			gotoInstallFolder.setText("Go to Install Folder");
			gotoInstallFolder.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					Location installLocation = Platform.getInstallLocation();
					if (installLocation == null) {
						return;
					}
					URL url = installLocation.getURL();
					if (url == null) {
						return;
					}
					
					openFolder(new File(url.getFile()));
				}
			});
		}
		return exploreMenu;
	}
	
	private static void openFolder(File file) {
		if (file != null && file.exists() && file.isDirectory()) {
			Utilities.launch(Activator
					.getDefault()
					.getPreferenceStore()
					.getString(
							Activator.FOLDER_EXPLORE_COMMAND_KEY), file);
		}
	}

}
