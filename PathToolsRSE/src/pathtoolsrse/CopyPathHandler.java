package pathtoolsrse;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFile;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import pathtools.CopyPathAction;

public class CopyPathHandler extends AbstractHandler {

	@SuppressWarnings("rawtypes")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			Iterator selectionIterator = structuredSelection.iterator();
			if (selectionIterator.hasNext()) {
				StringBuilder sb = new StringBuilder();
				while (selectionIterator.hasNext()) {
					Object firstElement = selectionIterator.next();
					if (firstElement instanceof IRemoteFile) {
						IRemoteFile remoteFile = (IRemoteFile) firstElement;
						if (sb.length() > 0) {
							sb.append("\n");
						}
						sb.append(remoteFile.getAbsolutePath());
					}
				}
				if (sb.length() > 0) {
					CopyPathAction.copyToClipboard(sb.toString());
				} else {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getShell().getDisplay().beep();
				}
			}
		}
		return null;
	}

}