package pathtools;

import java.io.File;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Sandip V. Chitale
 * 
 */
public class Activator extends AbstractUIPlugin {
	static final String FOLDER_EXPLORE_COMMAND_KEY = "folderExploreCommand";
	static final String FILE_EXPLORE_COMMAND_KEY = "fileExploreCommand";

	static String defaultFolderExploreCommand = "";
	static String defaultFileExploreCommand = "";

	static final String FOLDER_EDIT_COMMAND_KEY = "folderEditCommand";
	static final String FILE_EDIT_COMMAND_KEY = "fileEditCommand";

	static String defaultFolderEditCommand = "";
	static String defaultFileEditCommand = "";

	static final String FOLDER_COMMANDS_KEY = "folderCommands";
	static final String FILE_COMMANDS_KEY = "fileCommands";
	
	static String defaultFolderCommands = "";
	static String defaultFileCommands = "";

	static {
		if (Platform.OS_MACOSX.equals(Platform.getOS())) {
			defaultFolderExploreCommand = "/usr/bin/open -a /System/Library/CoreServices/Finder.app \""
					+ Activator.FILE_PATH + "\"";
			defaultFileExploreCommand = "/usr/bin/open -a /System/Library/CoreServices/Finder.app \""
					+ Activator.FILE_PARENT_PATH + "\"";
			defaultFolderEditCommand = "/usr/bin/open -a /System/Library/CoreServices/Finder.app \""
					+ Activator.FILE_PATH + "\"";
			defaultFileEditCommand = "/usr/bin/open -a /Applications/TextEdit.app \""
					+ Activator.FILE_PATH + "\"";
		} else if (Platform.OS_WIN32.equals(Platform.getOS())) {
			defaultFolderExploreCommand = "cmd /C start explorer /select,/e,\""
					+ Activator.FILE_PATH + "\"";
			defaultFileExploreCommand = "cmd /C start explorer /select,/e,\""
					+ Activator.FILE_PATH + "\"";
			defaultFolderEditCommand = "cmd /C start explorer /select,/e,\""
					+ Activator.FILE_PATH + "\"";
			defaultFileEditCommand = "cmd /C start notepad \""
					+ Activator.FILE_PATH + "\"";
		} else if (Platform.OS_LINUX.equals(Platform.getOS())) {
			if (new File("/usr/bin/konqueror").exists()) {
				defaultFolderExploreCommand = "/usr/bin/konqueror \""
						+ Activator.FILE_PATH + "\"";
				defaultFileExploreCommand = "/usr/bin/konqueror \""
						+ Activator.FILE_PARENT_PATH + "\"";
				defaultFolderEditCommand = "/usr/bin/konqueror \""
						+ Activator.FILE_PATH + "\"";
			} else if (new File("/usr/bin/nautilus").exists()) {
				defaultFolderExploreCommand = "/usr/bin/nautilus \""
						+ Activator.FILE_PATH + "\"";
				defaultFileExploreCommand = "/usr/bin/nautilus \""
						+ Activator.FILE_PARENT_PATH + "\"";
				defaultFolderEditCommand = "/usr/bin/nautilus \""
						+ Activator.FILE_PATH + "\"";
			}
			if (new File("/usr/bin/kedit").exists()) {
				defaultFileEditCommand = "/usr/bin/kedit \""
						+ Activator.FILE_PATH + "\"";
			} else if (new File("/usr/bin/gedit").exists()) {
				defaultFileEditCommand = "/usr/bin/gedit \""
						+ Activator.FILE_PATH + "\"";
			}
		} else if (Platform.OS_SOLARIS.equals(Platform.getOS())) {
			if (new File("/usr/bin/konqueror").exists()) {
				defaultFolderExploreCommand = "/usr/bin/konqueror \""
						+ Activator.FILE_PATH + "\"";
				defaultFileExploreCommand = "/usr/bin/konqueror \""
						+ Activator.FILE_PARENT_PATH + "\"";
				defaultFolderEditCommand = "/usr/bin/konqueror \""
						+ Activator.FILE_PATH + "\"";
			} else if (new File("/usr/bin/nautilus").exists()) {
				defaultFolderExploreCommand = "/usr/bin/nautilus \""
						+ Activator.FILE_PATH + "\"";
				defaultFileExploreCommand = "/usr/bin/nautilus \""
						+ Activator.FILE_PARENT_PATH + "\"";
				defaultFolderEditCommand = "/usr/bin/nautilus \""
						+ Activator.FILE_PATH + "\"";
			} else {
				defaultFolderExploreCommand = "filemgr -c -d \""
						+ Activator.FILE_PATH + "\"";
				defaultFolderExploreCommand = "filemgr -c -d \""
						+ Activator.FILE_PATH + "\"";
				defaultFileEditCommand = "filemgr -c -d \""
						+ Activator.FILE_PARENT_PATH + "\"";
			}
		}
	}

	// The plug-in ID
	public static final String PLUGIN_ID = "PathTools";

	// The shared instance
	private static Activator plugin;
	static final String FILE_PATH = "{path}";
	static final String FILE_PARENT_PATH = "{parent-path}";
	static final String FILE_NAME= "{name}";
	static final String FILE_PARENT_NAME = "{parent-name}";
	static final String FILE_PATH_SLASHES = "{path-slashes}";
	static final String FILE_PARENT_PATH_SLASHES = "{parent-path-slashes}";
	static final String FILE_PATH_BACKSLASHES = "{path-backslashes}";
	static final String FILE_PARENT_PATH_BACKSLASHES = "{parent-path-backslashes}";

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

	@SuppressWarnings("deprecation")
	@Override
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		store.setDefault(FOLDER_EXPLORE_COMMAND_KEY,
				defaultFolderExploreCommand);
		store.setDefault(FILE_EXPLORE_COMMAND_KEY, defaultFileExploreCommand);
		store.setDefault(FOLDER_EDIT_COMMAND_KEY, defaultFolderEditCommand);
		store.setDefault(FILE_EDIT_COMMAND_KEY, defaultFileEditCommand);
		store.setDefault(FOLDER_COMMANDS_KEY, defaultFolderCommands);
		store.setDefault(FILE_COMMANDS_KEY, defaultFileCommands);
		super.initializeDefaultPreferences(store);
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
		return parseString(getPreferenceStore().getString(Activator.FOLDER_COMMANDS_KEY));
	}
	
	String[] getFileCustomActions() {
		return parseString(getPreferenceStore().getString(Activator.FILE_COMMANDS_KEY));
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
