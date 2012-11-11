package pathtoolsrse.rsync;

public abstract class RsyncEndpoint {
	public enum RSYNC_ENDPOINT {WORKSPACE_RESOURCE, LOCAL, REMOTE};
	
	protected final RSYNC_ENDPOINT rsyncEndpoint;
	
	protected RsyncEndpoint(RSYNC_ENDPOINT rsyncEndpoint) {
		this.rsyncEndpoint = rsyncEndpoint;
	}
	
	public RSYNC_ENDPOINT getRsyncEndpoint() {
		return rsyncEndpoint;
	}
	
}
