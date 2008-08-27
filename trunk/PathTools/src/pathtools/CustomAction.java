package pathtools;

import java.io.File;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IResource;
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
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;

public class CustomAction implements IWorkbenchWindowPulldownDelegate {
	private Menu customActionsMenu;

	private File fileObject;

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub

	}

	public void run(IAction action) {

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

	public Menu getMenu(Control parent) {
		if (customActionsMenu != null) {
			customActionsMenu.dispose();
		}
		customActionsMenu = new Menu(parent);

		if (fileObject != null) {
			String commands = Activator
					.getDefault()
					.getPreferenceStore()
					.getString(
							fileObject.isDirectory() ? Activator.FOLDER_COMMANDS_KEY
									: Activator.FILE_COMMANDS_KEY);
			if (commands != null && commands.length() > 0) {
				String[] commandsArray = commands.split(Pattern.quote("|"));
				for (String command : commandsArray) {
					MenuItem commandMenuItem = new MenuItem(customActionsMenu, SWT.PUSH);					
					commandMenuItem.setText(Utilities.formatCommand(command,
							fileObject));
					commandMenuItem.setData(command);
					final String finalCommand = command;
					commandMenuItem.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							Object data = e.widget.getData();
							if (data instanceof String) {
								Utilities.launch(finalCommand,
												fileObject);
							}
						}
					});
				}
			}
		}
		return customActionsMenu;
	}
}
