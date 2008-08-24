package pathtools;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
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
		return "Specify the commands for exploring folders and fies. You can\n"
				+ "use \"\" (quotes) around command arguments with spaces in their value.\n"
				+ "You can use the following parameters in the commands:\n\n"
				+ Utilities.FILE_PATH
				+ "  - path of the selected object with default file separator.\n"
				+ Utilities.FILE_PARENT_PATH
				+ "  - path of the parent of selected object with default file separator.\n"
				+ Utilities.FILE_NAME
				+ "  - name of the selected object.\n"
				+ Utilities.FILE_PARENT_NAME
				+ "  - name of the parent of selected object.\n"
				+ Utilities.FILE_PATH_SLASHES
				+ "  - path of the selected object with / file separator.\n"
				+ Utilities.FILE_PARENT_PATH_SLASHES
				+ "  - path of the parent of selected object with / file separator.\n"
				+ Utilities.FILE_PATH_BACKSLASHES
				+ "  - path of the selected object with \\ File separator.\n"
				+ Utilities.FILE_PARENT_PATH_BACKSLASHES
				+ "{parent-path-backslashes}  - path of the parent of selected object with \\ file separator.\n"
				+ "\n";
	}

	@Override
	protected void createFieldEditors() {
		// Folder explore command field
		StringFieldEditor folderExploreCommad = new StringFieldEditor(
				Activator.FOLDER_EXPLORE_COMMAND_KEY, "Explore Folder:",
				getFieldEditorParent());
		addField(folderExploreCommad);

		// File explore command field
		StringFieldEditor fileExploreCommad = new StringFieldEditor(
				Activator.FILE_EXPLORE_COMMAND_KEY, "Explore File:",
				getFieldEditorParent());
		addField(fileExploreCommad);

		// Folder editor command field
		StringFieldEditor folderEditCommad = new StringFieldEditor(
				Activator.FOLDER_EDIT_COMMAND_KEY, "Edit Folder:",
				getFieldEditorParent());
		addField(folderEditCommad);

		// File editor command field
		StringFieldEditor fileEditCommad = new StringFieldEditor(
				Activator.FILE_EDIT_COMMAND_KEY, "Edit File:",
				getFieldEditorParent());
		addField(fileEditCommad);
	}

}
