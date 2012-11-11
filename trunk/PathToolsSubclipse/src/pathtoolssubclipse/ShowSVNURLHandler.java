package pathtoolssubclipse;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.UIJob;
import org.tigris.subversion.subclipse.core.ISVNLocalResource;
import org.tigris.subversion.subclipse.core.SVNException;
import org.tigris.subversion.subclipse.core.SVNTeamProvider;
import org.tigris.subversion.subclipse.core.resources.LocalResourceStatus;
import org.tigris.subversion.subclipse.core.resources.SVNWorkspaceRoot;

public class ShowSVNURLHandler extends AbstractHandler {

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
					nonFinalResource = (IResource) adaptable.getAdapter(IResource.class);
				}

				if (nonFinalResource != null) {
					final IResource resource = (nonFinalResource instanceof IFile ? nonFinalResource.getParent() : nonFinalResource);
					UIJob job = new UIJob("Show SVN URL") {
						@Override
						public IStatus runInUIThread(IProgressMonitor monitor) {
							IResource nonFinalResource = resource;
							IProject project = nonFinalResource.getProject();
							if (RepositoryProvider.isShared(project)) {
								RepositoryProvider provider = RepositoryProvider.getProvider(project);
								if (provider instanceof SVNTeamProvider) {
									ISVNLocalResource svnResource = SVNWorkspaceRoot.getSVNResourceFor(resource);
									if (svnResource == null) {
										return Status.OK_STATUS;
									}
									LocalResourceStatus localResourceStatus;
									try {
										localResourceStatus = svnResource.getStatus();
										if (localResourceStatus == null) {
											return Status.OK_STATUS;
										}
										try {
											PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser()
													.openURL(new URL(localResourceStatus.getUrlString()));
										} catch (PartInitException e) {
										} catch (MalformedURLException e) {
										}
									} catch (SVNException e) {
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