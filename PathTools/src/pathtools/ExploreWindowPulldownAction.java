package pathtools;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;

/**
 * This launches the OS file explorer showing the selected folder or the folder
 * containing the selected file.
 *
 * @author Sandip V. Chitale
 *
 */
public class ExploreWindowPulldownAction extends ExploreAction implements IWorkbenchWindowPulldownDelegate2 {

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

}