package pathtools;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * The preferences initializer for Path Tools
 * 
 * @author Sandip V. Chitale
 * 
 */
public class PathToolsPreferences extends AbstractPreferenceInitializer {
	
	static final String FOLDER_EXPLORE_COMMAND_KEY = "folderExploreCommand";
	static final String FILE_EXPLORE_COMMAND_KEY = "fileExploreCommand";

	static String defaultFolderExploreCommand = "";
	static String defaultFileExploreCommand = "";
	
	static final String FOLDER_SHELL_COMMAND_KEY = "folderShellCommand";
	static final String FILE_SHELL_COMMAND_KEY = "fileShellCommand";
	
	static String defaultFolderShellCommand = "";
	static String defaultFileShellCommand = "";

	static final String FOLDER_EDIT_COMMAND_KEY = "folderEditCommand";
	static final String FILE_EDIT_COMMAND_KEY = "fileEditCommand";

	static String defaultFolderEditCommand = "";
	static String defaultFileEditCommand = "";
	
	static final String FOLDER_COMMANDS_KEY = "folderCommands";
	static final String FILE_COMMANDS_KEY = "fileCommands";
	
	static String defaultFolderCommands = "";
	static String defaultFileCommands = "";

	static final String FILE_PATH = "{path}";
	static final String FILE_PARENT_PATH = "{parent-path}";
	static final String FILE_NAME= "{name}";
	static final String FILE_PARENT_NAME = "{parent-name}";
	static final String FILE_PATH_SLASHES = "{path-slashes}";
	static final String FILE_PARENT_PATH_SLASHES = "{parent-path-slashes}";
	static final String FILE_PATH_BACKSLASHES = "{path-backslashes}";
	static final String FILE_PARENT_PATH_BACKSLASHES = "{parent-path-backslashes}";
	
	static final String LAST_COPY_PATH_FORMAT = "lastCopyPathFormat";
	static final String defaultLLastCopyPathFormat = FILE_PATH;
	
	static {
		if (Platform.OS_MACOSX.equals(Platform.getOS())) {
			defaultFolderExploreCommand = "/usr/bin/open -a /System/Library/CoreServices/Finder.app \""
					+ PathToolsPreferences.FILE_PATH + "\"";
			defaultFileExploreCommand = "/usr/bin/open -a /System/Library/CoreServices/Finder.app \""
					+ PathToolsPreferences.FILE_PARENT_PATH + "\"";
			defaultFolderEditCommand = "/usr/bin/open -a /System/Library/CoreServices/Finder.app \""
					+ PathToolsPreferences.FILE_PATH + "\"";
			defaultFileEditCommand = "/usr/bin/open -a /Applications/TextEdit.app \""
					+ PathToolsPreferences.FILE_PATH + "\"";
			defaultFolderShellCommand = "open -a /Applications/Terminal.app";
			defaultFileShellCommand = "open -a /Applications/Terminal.app";
		} else if (Platform.OS_WIN32.equals(Platform.getOS())) {
			defaultFolderExploreCommand = "cmd /C start explorer /select,/e, \""
					+ PathToolsPreferences.FILE_PATH + "\"";
			defaultFileExploreCommand = "cmd /C start explorer /select,/e, \""
					+ PathToolsPreferences.FILE_PATH + "\"";
			defaultFolderShellCommand = "cmd /K start cd /D \""
				+ PathToolsPreferences.FILE_PATH + "\"";
			defaultFileShellCommand = "cmd /K start cd /D \""
				+ PathToolsPreferences.FILE_PARENT_PATH + "\"";
			defaultFolderEditCommand = "cmd /C start explorer /select,/e, \""
					+ PathToolsPreferences.FILE_PATH + "\"";
			defaultFileEditCommand = "cmd /C start notepad \""
					+ PathToolsPreferences.FILE_PATH + "\"";
		} else if (Platform.OS_LINUX.equals(Platform.getOS())) {
			if (new File("/usr/bin/konqueror").exists()) {
				defaultFolderExploreCommand = "/usr/bin/konqueror \""
						+ PathToolsPreferences.FILE_PATH + "\"";
				defaultFileExploreCommand = "/usr/bin/konqueror \""
						+ PathToolsPreferences.FILE_PARENT_PATH + "\"";
				defaultFolderEditCommand = "/usr/bin/konqueror \""
						+ PathToolsPreferences.FILE_PATH + "\"";
			} else if (new File("/usr/bin/nautilus").exists()) {
				defaultFolderExploreCommand = "/usr/bin/nautilus \""
						+ PathToolsPreferences.FILE_PATH + "\"";
				defaultFileExploreCommand = "/usr/bin/nautilus \""
						+ PathToolsPreferences.FILE_PARENT_PATH + "\"";
				defaultFolderEditCommand = "/usr/bin/nautilus \""
						+ PathToolsPreferences.FILE_PATH + "\"";
			}
			if (new File("/usr/bin/gnome-terminal").exists()) {
				defaultFolderShellCommand = "gnome-terminal --working-directory=\""
					+ PathToolsPreferences.FILE_PATH + "\"";
				defaultFileShellCommand = "gnome-terminal --working-directory=\""
					+ PathToolsPreferences.FILE_PARENT_PATH + "\"";
			} else {
				defaultFolderShellCommand = "xterm -e \"cd \\\"" + PathToolsPreferences.FILE_PATH + "\\\" && /bin/bash\"";
				defaultFileShellCommand = "xterm -e \"cd \\\"" + PathToolsPreferences.FILE_PARENT_PATH + "\\\" && /bin/bash\"";
			}
			if (new File("/usr/bin/kedit").exists()) {
				defaultFileEditCommand = "/usr/bin/kedit \""
						+ PathToolsPreferences.FILE_PATH + "\"";
			} else if (new File("/usr/bin/gedit").exists()) {
				defaultFileEditCommand = "/usr/bin/gedit \""
						+ PathToolsPreferences.FILE_PATH + "\"";
			}
		} else if (Platform.OS_SOLARIS.equals(Platform.getOS())) {
			if (new File("/usr/bin/konqueror").exists()) {
				defaultFolderExploreCommand = "/usr/bin/konqueror \""
						+ PathToolsPreferences.FILE_PATH + "\"";
				defaultFileExploreCommand = "/usr/bin/konqueror \""
						+ PathToolsPreferences.FILE_PARENT_PATH + "\"";
				defaultFolderEditCommand = "/usr/bin/konqueror \""
						+ PathToolsPreferences.FILE_PATH + "\"";
			} else if (new File("/usr/bin/nautilus").exists()) {
				defaultFolderExploreCommand = "/usr/bin/nautilus \""
						+ PathToolsPreferences.FILE_PATH + "\"";
				defaultFileExploreCommand = "/usr/bin/nautilus \""
						+ PathToolsPreferences.FILE_PARENT_PATH + "\"";
				defaultFolderEditCommand = "/usr/bin/nautilus \""
						+ PathToolsPreferences.FILE_PATH + "\"";
			} else {
				defaultFolderExploreCommand = "filemgr -c -d \""
						+ PathToolsPreferences.FILE_PATH + "\"";
				defaultFolderExploreCommand = "filemgr -c -d \""
						+ PathToolsPreferences.FILE_PATH + "\"";
				defaultFileEditCommand = "filemgr -c -d \""
						+ PathToolsPreferences.FILE_PARENT_PATH + "\"";
			}
		}
	}

	private Object terminalDotScpt;
	
	/**
	 * The constructor
	 */
	public PathToolsPreferences() {
	}

	@Override
	public void initializeDefaultPreferences() {
		if (Platform.getOS().equals(Platform.OS_MACOSX)) {
			try {
				URL entry = Activator.getDefault().getBundle().getEntry("/scripts/cdterminal.scpt");
				if (entry != null) {
					terminalDotScpt = FileLocator.toFileURL(entry).getFile();
					if (terminalDotScpt != null) {
						defaultFolderShellCommand = "/usr/bin/osascript \"" + terminalDotScpt + "\" \"" + PathToolsPreferences.FILE_PATH + "\"";
						defaultFileShellCommand = "/usr/bin/osascript \"" + terminalDotScpt + "\" \"" + PathToolsPreferences.FILE_PARENT_PATH + "\"";
					}
				} 
			}catch (IOException el) {
			}
		}
		IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		prefs.setDefault(FOLDER_EXPLORE_COMMAND_KEY, defaultFolderExploreCommand);
		prefs.setDefault(FILE_EXPLORE_COMMAND_KEY, defaultFileExploreCommand);
		prefs.setDefault(FOLDER_SHELL_COMMAND_KEY, defaultFolderShellCommand);
		prefs.setDefault(FILE_SHELL_COMMAND_KEY, defaultFileShellCommand);
		prefs.setDefault(FOLDER_EDIT_COMMAND_KEY, defaultFolderEditCommand);
		prefs.setDefault(FILE_EDIT_COMMAND_KEY, defaultFileEditCommand);
		prefs.setDefault(FOLDER_COMMANDS_KEY, defaultFolderCommands);
		prefs.setDefault(FILE_COMMANDS_KEY, defaultFileCommands);
		prefs.setDefault(LAST_COPY_PATH_FORMAT, defaultLLastCopyPathFormat);
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
