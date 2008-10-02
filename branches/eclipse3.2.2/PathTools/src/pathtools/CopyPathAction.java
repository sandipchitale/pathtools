package pathtools;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

/**
 * This copies the absolute paths of selected folders and files (one per line)
 * into the Clipboard.
 * 
 * @author Sandip V. Chitale
 * 
 */
public class CopyPathAction implements IWorkbenchWindowActionDelegate {
	private List<String> paths = new LinkedList<String>();

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {

	}

	public void run(IAction action) {
		// Are there any paths selected ?
		if (paths.size() > 0) {
			// Build a string with each path on separate line
			StringBuilder stringBuilder = new StringBuilder();
			for (String path : paths) {
				stringBuilder.append(path + "\n");
			}
			// Get Clipboard
			Clipboard clipboard = new Clipboard(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell().getDisplay());
			// Put the paths string into the Clipboard
			clipboard.setContents(new Object[] { stringBuilder.toString() },
					new Transfer[] { TextTransfer.getInstance() });
		}
	}

	@SuppressWarnings("unchecked")
	public void selectionChanged(IAction action, ISelection selection) {
		// Start with a clear list
		paths.clear();
		if (selection instanceof IStructuredSelection) {
			// Get structured selection
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;

			// Iterate through selected items
			Iterator iterator = structuredSelection.iterator();
			while (iterator.hasNext()) {
				Object firstElement = iterator.next();
				IPath location = null;
				if (firstElement instanceof IResource) {
					// Is it a IResource ?
					IResource resource = (IResource) firstElement;
					// Get the location
					location = resource.getLocation();
				} else if (firstElement instanceof IAdaptable) {
					// Is it a IResource adaptable ?
					IAdaptable adaptable = (IAdaptable) firstElement;
					IResource resource = (IResource) adaptable
							.getAdapter(IResource.class);
					if (resource != null) {
						// Get the location
						location = resource.getLocation();
					}
				} else if (firstElement.getClass().getName().equals("com.aptana.ide.core.ui.io.file.LocalFile")) {
					try {
						Method getFile = firstElement.getClass().getDeclaredMethod("getFile");
						Object object = getFile.invoke(firstElement);
						if (object instanceof File){
							paths.add(((File) object).getAbsolutePath());
						}
					} catch (SecurityException e) {
					} catch (NoSuchMethodException e) {
					} catch (IllegalArgumentException e) {
					} catch (IllegalAccessException e) {
					} catch (InvocationTargetException e) {
					}
				}
				if (location != null) {
					// Get the file for the location
					File file = location.toFile();
					if (file != null) {
						// Add the absolute path to the list
						paths.add(file.getAbsolutePath());
					}
				}
			}
		}
		action.setEnabled(paths.size() > 0);
	}
}
