package pathtools;

import java.io.File;
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
 * This launches the OS file explorer showing the selected folder or the folder
 * containing the selected file.
 * 
 * @author Sandip V. Chitale
 * 
 */
public class ExploreAction implements IWorkbenchWindowActionDelegate {
	private File fileObject;

	private static String fileExploreComand = null;
	private static String folderExploreComand = null;

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {

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
							'\\'),
					fileObject.getName(),
					fileObject.getParentFile().getName()});
			// Launch the explore command
			CommandLauncher.launch(command);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		fileObject = null;
		action.setEnabled(false);
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
				}
			}
			if (location != null) {
				fileObject = location.toFile();
			}
		}
		action.setEnabled(fileObject != null);
	}

}
