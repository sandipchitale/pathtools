package pathtools;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;

/**
 * This launches the Command Line Shell with the selected folder or the folder
 * containing the selected file as the pwd.
 * 
 * @author Sandip V. Chitale
 * 
 */
public class ShellWindowPulldownAction extends ShellAction implements IWorkbenchWindowPulldownDelegate2 {

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

}