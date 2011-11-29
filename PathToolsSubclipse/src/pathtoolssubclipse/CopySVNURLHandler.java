package pathtoolssubclipse;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.UIJob;
import org.tigris.subversion.subclipse.core.ISVNLocalResource;
import org.tigris.subversion.subclipse.core.SVNTeamProvider;
import org.tigris.subversion.subclipse.core.resources.SVNWorkspaceRoot;
import org.tigris.subversion.svnclientadapter.SVNUrl;

import pathtools.CopyPathAction;

public class CopySVNURLHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			if (structuredSelection.size() == 1) {
				IResource nonFinalResource = null;
				Object firstElement = structuredSelection.getFirstElement();
				if (firstElement instanceof IResource) {
					nonFinalResource = (IResource) firstElement;
				} else if (firstElement instanceof IAdaptable) {
					IAdaptable adaptable = (IAdaptable) firstElement;
					nonFinalResource = (IResource) adaptable
							.getAdapter(IResource.class);
				}

				if (nonFinalResource != null) {
					final IResource resource = nonFinalResource;
					UIJob job = new UIJob("Copy SVN URL") {
						@Override
						public IStatus runInUIThread(IProgressMonitor monitor) {
							IResource nonFinalResource = resource;
							IProject project = nonFinalResource.getProject();
							if (RepositoryProvider.isShared(project)) {
								RepositoryProvider provider = RepositoryProvider
										.getProvider(project);
								if (provider instanceof SVNTeamProvider) {
									ISVNLocalResource SVNLocalResource = SVNWorkspaceRoot
											.getSVNResourceFor(nonFinalResource);
									if (SVNLocalResource != null
											&& SVNLocalResource.exists()) {
										SVNUrl url = SVNLocalResource.getUrl();
										CopyPathAction.copyToClipboard(url
												.toString());
									}
								}
							}
							return Status.OK_STATUS;
						}
					};
					job.setSystem(true);
					job.setPriority(Job.INTERACTIVE);
					job.schedule();
				}
			}
		}
		return null;
	}

}