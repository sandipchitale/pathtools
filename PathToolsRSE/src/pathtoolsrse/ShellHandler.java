package pathtoolsrse;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFile;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import pathtools.ShellAction;

public class ShellHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			if (structuredSelection.size() == 1) {
					Object firstElement = structuredSelection.getFirstElement();
					if (firstElement instanceof IRemoteFile) {
						IRemoteFile remoteFile = (IRemoteFile) firstElement;
						Object fileObject = remoteFile.getFile();
						if (fileObject instanceof File) {
							ShellAction.shell((File) fileObject);
						}
					}
			} else {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell().getDisplay().beep();
			}
		}
		return null;
	}

}