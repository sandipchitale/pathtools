package pathtools;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
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
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * This launches the external text editor for selected folder or file.
 * 
 * @author Sandip V. Chitale
 * 
 */
public class EditAction implements IWorkbenchWindowPulldownDelegate {
	private File fileObject;

	private static String fileEditComand = null;
	private static String folderEditComand = null;

	private IWorkbenchWindow window;

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void run(IAction action) {
		// Get the configured explorer commands for folder and file
		folderEditComand = Activator.getDefault().getPreferenceStore()
				.getString(Activator.FOLDER_EDIT_COMMAND_KEY);
		fileEditComand = Activator.getDefault().getPreferenceStore().getString(
				Activator.FILE_EDIT_COMMAND_KEY);
		if (fileEditComand == null || folderEditComand == null) {
			return;
		}
		// Is this a physical file on the disk ?
		if (fileObject != null) {
			String commandFormat = fileObject.isDirectory() ? folderEditComand
					: fileEditComand;

			// Substitute parameter values and format the edit command
			String command = Utilities.formatCommand(commandFormat, fileObject);
			
			// Launch the edit command
			CommandLauncher.launch(command);
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
					} else if (firstElement.getClass().getName().equals("com.aptana.ide.core.ui.io.file.LocalFile")) {
						try {
							Method getFile = firstElement.getClass().getDeclaredMethod("getFile");
							Object object = getFile.invoke(firstElement);
							if (object instanceof File){
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
	
	private Menu editMenu;
	
	public Menu getMenu(Control parent) {
		if (editMenu != null) {
			editMenu.dispose();
		}
		
		editMenu = new Menu(parent);
		
		final IPath workspaceLocation = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		File workspaceFile = workspaceLocation.toFile();
		if (workspaceFile.exists()) {
			final File logFile = new File(workspaceFile, ".metadata/.log");
			if (logFile.exists() && logFile.isFile()) {
				MenuItem editWorkspaceLog = new MenuItem(editMenu, SWT.PUSH);
				editWorkspaceLog.setText("Open in external editor " + logFile.getAbsolutePath());
				editWorkspaceLog.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						String commandFormat =Activator.getDefault().getPreferenceStore().getString(
								Activator.FILE_EDIT_COMMAND_KEY);

						// Substitute parameter values and format the edit command
						String command = Utilities.formatCommand(commandFormat, logFile);
						
						// Launch the edit command
						CommandLauncher.launch(command);
					}
				});
			}
		}
		return editMenu;
	}

}
