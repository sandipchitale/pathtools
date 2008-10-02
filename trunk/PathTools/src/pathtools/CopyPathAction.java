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
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.PlatformUI;

/**
 * This copies the absolute paths of selected folders and files (one per line)
 * into the Clipboard.
 * 
 * @author Sandip V. Chitale
 * 
 */
public class CopyPathAction implements IWorkbenchWindowPulldownDelegate {
	private List<File> files = new LinkedList<File>();

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {

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
							files.add((File) object);
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
						files.add(file);
					}
				}
			}
		}
		action.setEnabled(files.size() > 0);
	}

	private Menu copyPathsMenu;
	
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
	
	public Menu getMenu(Control parent) {
		if (copyPathsMenu == null) {
			copyPathsMenu = new Menu(parent);			
			for (String pathFormat: pathFormats) {
				MenuItem commandMenuItem = new MenuItem(copyPathsMenu, SWT.PUSH);					
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
		}
		boolean enable = files.size() > 0;
		for (MenuItem menuItem : copyPathsMenu.getItems()) {
			menuItem.setEnabled(enable);
		}
		return copyPathsMenu;
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
			// Get Clipboard
			Clipboard clipboard = new Clipboard(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell().getDisplay());
			// Put the paths string into the Clipboard
			clipboard.setContents(new Object[] { stringBuilder.toString() },
					new Transfer[] { TextTransfer.getInstance() });
		}
	}
}
