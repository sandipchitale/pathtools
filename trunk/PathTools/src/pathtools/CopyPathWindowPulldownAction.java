package pathtools;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;

/**
 * This copies the absolute paths of selected folders and files (one per line)
 * into the Clipboard.
 * 
 * @author Sandip V. Chitale
 * 
 */
public class CopyPathWindowPulldownAction extends CopyPathAction implements IWorkbenchWindowPulldownDelegate2 {

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}
