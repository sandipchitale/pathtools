package pathtools;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * This launches the external text editor for selected folder or file.
 * 
 * @author Sandip V. Chitale
 * 
 */
public class EditAction implements IWorkbenchWindowActionDelegate {
	private File fileObject;

	private static String fileEditComand = null;
	private static String folderEditComand = null;

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {

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

			// Substitute parameter values ad format the explore command
			String command = MessageFormat.format(Utilities
					.convertParameters(commandFormat), new Object[] {
					fileObject.getAbsolutePath().replace('/',
							File.separatorChar).replace('\\',
							File.separatorChar),
					fileObject.getParentFile().getAbsolutePath().replace('/',
							File.separatorChar).replace('\\',
							File.separatorChar),
					fileObject.getAbsolutePath().replace('\\', '/'),
					fileObject.getParentFile().getAbsolutePath().replace('\\',
							'/'),
					fileObject.getAbsolutePath().replace('/', '\\'),
					fileObject.getParentFile().getAbsolutePath().replace('/',
							'\\'), });
			// Launch the explore command
			CommandLauncher.launch(command);
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
				if (location != null) {
					fileObject = location.toFile();
				}
			}
		} finally {
			action.setEnabled(fileObject != null);
		}
	}

}
