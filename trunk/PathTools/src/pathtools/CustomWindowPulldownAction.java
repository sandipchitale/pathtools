package pathtools;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;

public class CustomWindowPulldownAction extends CustomAction implements IWorkbenchWindowPulldownDelegate2 {
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}
