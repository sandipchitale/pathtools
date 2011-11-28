package pathtoolssubclipse;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.team.core.RepositoryProvider;
import org.tigris.subversion.subclipse.core.SVNTeamProvider;

public class SVNPropertyTester extends PropertyTester {
	private static final String IS_SVN_RESOURCE = "isSVNResource";

	public SVNPropertyTester() {
	}

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (IS_SVN_RESOURCE.equals(property)) {
			IResource resource = null;
			if (receiver instanceof IResource) {
				resource = (IResource) receiver;
			} else if (receiver instanceof IAdaptable) {
				IAdaptable adaptable = (IAdaptable) receiver;
				resource = (IResource) adaptable.getAdapter(IResource.class);
			}

			if (resource != null) {
				IProject project = resource.getProject();
				if (RepositoryProvider.isShared(project)) {
					RepositoryProvider provider = RepositoryProvider
							.getProvider(project);
					if (provider instanceof SVNTeamProvider) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
