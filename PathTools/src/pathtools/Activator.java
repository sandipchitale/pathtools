package pathtools;

import java.util.regex.Pattern;

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
	
	String[] getFolderCustomActions() {
		return parseString(getPreferenceStore().getString(PathToolsPreferences.FOLDER_COMMANDS_KEY));
	}
	
	String[] getFileCustomActions() {
		return parseString(getPreferenceStore().getString(PathToolsPreferences.FILE_COMMANDS_KEY));
	}
	
	private static String SEPARATOR = "@@@@";
	
	static String createList(String[] items) {
		StringBuilder stringBuilder = new StringBuilder();
		for (String item : items) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(SEPARATOR);
			}
			stringBuilder.append(item);
		}
		return stringBuilder.toString();
	}
	
	static String[] parseString(String stringList) {
		if (stringList != null && stringList.length() > 0) {
			return stringList.split(Pattern.quote(SEPARATOR));
		}
		return new String[0];
	}

}
