package pathtools;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 *
 * @author Sandip V. Chitale
 *
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "PathTools";

	// The shared instance
	private static Activator plugin;

	private File[] files;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	String[][] getFolderCustomActions() {
		return PathToolsPreferences.parseString(getPreferenceStore().getString(PathToolsPreferences.FOLDER_COMMANDS_KEY));
	}

	String[][] getFileCustomActions() {
		return PathToolsPreferences.parseString(getPreferenceStore().getString(PathToolsPreferences.FILE_COMMANDS_KEY));
	}

	void setFile(File file) {
		setFiles(file == null ? null : new File[] {file});
	}

	void setFiles(File[] files) {
		this.files = files;
	}

	public File[] getFiles() {
		if (files == null) {
			ISelectionService selectionService =
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
			ISelection selection = selectionService.getSelection();
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				if (structuredSelection.size() == 1) {
					Object firstElement = structuredSelection.getFirstElement();
					if (firstElement instanceof IResource) {
						IResource resource = (IResource) firstElement;
						IPath location = resource.getLocation();
						if (location != null) {
							files = new File[] {new File(location.toOSString())};
						}
					} else if (firstElement instanceof IAdaptable) {
						IAdaptable adaptable = (IAdaptable) firstElement;
						IResource resource = (IResource) adaptable.getAdapter(IResource.class);
						if (resource != null) {
							IPath location = resource.getLocation();
							if (location != null) {
								files =  new File[] {new File(location.toOSString())};
							}
						}
					}
				}
			}
		}
		return files;
	}

}
