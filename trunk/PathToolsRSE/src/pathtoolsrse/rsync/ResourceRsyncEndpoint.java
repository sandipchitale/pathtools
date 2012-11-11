package pathtoolsrse.rsync;

import org.eclipse.core.resources.IResource;

public class ResourceRsyncEndpoint extends RsyncEndpoint {

	private IResource resource;

	protected ResourceRsyncEndpoint(RSYNC_ENDPOINT rsyncEndpoint, IResource resource) {
		super(rsyncEndpoint);
		this.resource = resource;
	}

	public IResource getResource() {
		return resource;
	}
	
	public void setResource(IResource resource) {
		this.resource = resource;
	}
}
