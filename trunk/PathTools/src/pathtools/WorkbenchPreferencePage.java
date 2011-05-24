package pathtools;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 *
 * This implements the preferences page using the FieldEditor.
 *
 * @author Sandip V. Chitale
 *
 */
public class WorkbenchPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public WorkbenchPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
	}

	public void init(IWorkbench workbench) {
		// Initialize the preference store we wish to use
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	@Override
	public String getDescription() {
		return "You can use \"\" (quotes) around command arguments with spaces in their value.\n"
				+ "You can use the following parameters in the commands:\n\n"
				+ PathToolsPreferences.FILE_PATH
				+ "  - path of the selected object with default file separator.\n"
				+ PathToolsPreferences.FILE_PARENT_PATH
				+ "  - path of the parent of selected object with default file separator.\n"
				+ PathToolsPreferences.FILE_NAME
				+ "  - name of the selected object.\n"
				+ PathToolsPreferences.FILE_PARENT_NAME
				+ "  - name of the parent of selected object.\n"
				+ PathToolsPreferences.FILE_PATH_SLASHES
				+ "  - path of the selected object with / file separator.\n"
				+ PathToolsPreferences.FILE_PARENT_PATH_SLASHES
				+ "  - path of the parent of selected object with / file separator.\n"
				+ PathToolsPreferences.FILE_PATH_BACKSLASHES
				+ "  - path of the selected object with \\ file separator.\n"
				+ PathToolsPreferences.FILE_PARENT_PATH_BACKSLASHES
				+ " - path of the parent of selected object with \\ file separator.\n\n"
				+ "You can also use any of the available substitution variables such as ${eclipse_home}.\n\n";
	}

	@Override
	protected void createFieldEditors() {
		// Folder explore command field
		StringFieldEditor folderExploreCommad = new StringFieldEditor(
				PathToolsPreferences.FOLDER_EXPLORE_COMMAND_KEY, "Explore Folder:",
				getFieldEditorParent());
		addField(folderExploreCommad);

		// File explore command field
		StringFieldEditor fileExploreCommad = new StringFieldEditor(
				PathToolsPreferences.FILE_EXPLORE_COMMAND_KEY, "Explore File:",
				getFieldEditorParent());
		addField(fileExploreCommad);

		// Folder shell command field
		StringFieldEditor folderShellCommad = new StringFieldEditor(
				PathToolsPreferences.FOLDER_SHELL_COMMAND_KEY, "Shell at Folder:",
				getFieldEditorParent());
		addField(folderShellCommad);

		// File shell command field
		StringFieldEditor fileShellCommad = new StringFieldEditor(
				PathToolsPreferences.FILE_SHELL_COMMAND_KEY, "Shell at File:",
				getFieldEditorParent());
		addField(fileShellCommad);

		// Folder editor command field
		StringFieldEditor folderEditCommad = new StringFieldEditor(
				PathToolsPreferences.FOLDER_EDIT_COMMAND_KEY, "Edit Folder:",
				getFieldEditorParent());
		addField(folderEditCommad);

		// File editor command field
		StringFieldEditor fileEditCommad = new StringFieldEditor(
				PathToolsPreferences.FILE_EDIT_COMMAND_KEY, "Edit File:",
				getFieldEditorParent());
		addField(fileEditCommad);

		TableFieldEditor folderCommandsTableEditor = new CommandTableEditor(
				PathToolsPreferences.FOLDER_COMMANDS_KEY, "Folder", getFieldEditorParent());
		addField(folderCommandsTableEditor);
		TableFieldEditor fileCommandsTableEditor = new CommandTableEditor(
				PathToolsPreferences.FILE_COMMANDS_KEY, "File", getFieldEditorParent());
		addField(fileCommandsTableEditor);
	}

	private static class CommandTableEditor extends TableFieldEditor {
		CommandTableEditor(String key, String item, Composite parent) {
			super(key, "Custom " + item + " commands:",
					new String[] {"Name", "Pattern", "Command"},
					new int[] {150, 100, 300},
					parent);
		}

		@Override
		protected String createList(String[][] items) {
			return PathToolsPreferences.createList(items);
		}

		@Override
		protected String[][] parseString(String stringList) {
			return PathToolsPreferences.parseString(stringList);
		}

		@Override
		protected String[] getNewInputObject() {
			return new String[] {"", "*", ""};
		}

	}

}
