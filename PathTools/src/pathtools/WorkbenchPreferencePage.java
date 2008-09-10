package pathtools;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

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
				+ Activator.FILE_PATH
				+ "  - path of the selected object with default file separator.\n"
				+ Activator.FILE_PARENT_PATH
				+ "  - path of the parent of selected object with default file separator.\n"
				+ Activator.FILE_NAME
				+ "  - name of the selected object.\n"
				+ Activator.FILE_PARENT_NAME
				+ "  - name of the parent of selected object.\n"
				+ Activator.FILE_PATH_SLASHES
				+ "  - path of the selected object with / file separator.\n"
				+ Activator.FILE_PARENT_PATH_SLASHES
				+ "  - path of the parent of selected object with / file separator.\n"
				+ Activator.FILE_PATH_BACKSLASHES
				+ "  - path of the selected object with \\ File separator.\n"
				+ Activator.FILE_PARENT_PATH_BACKSLASHES
				+ " - path of the parent of selected object with \\ file separator.\n";
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

		ListEditor folderCommandsListEditor = new CommandListEditor(
				Activator.FOLDER_COMMANDS_KEY, "Folder", getFieldEditorParent());
		addField(folderCommandsListEditor);
		ListEditor fileCommandsListEditor = new CommandListEditor(
				Activator.FILE_COMMANDS_KEY, "File", getFieldEditorParent());
		addField(fileCommandsListEditor);
	}

	private static class CommandListEditor extends EntryModifiableListEditor {
		private final String item;

		CommandListEditor(String key, String item, Composite parent) {
			super(key, "Custom " + item + " commands:", parent);
			this.item = item;
		}
		
		@Override
		protected String createList(String[] items) {
			return Activator.createList(items); 
		}

		@Override
		protected String[] parseString(String stringList) {
			return Activator.parseString(stringList);
		}

		@Override
		protected String getNewInputObject() {
			InputDialog commandDialog = new InputDialog(PlatformUI
					.getWorkbench().getActiveWorkbenchWindow().getShell(),"Custom " + item
					+ " Command", "Enter custom " + item + " command:", "", null);
			if (commandDialog.open() == InputDialog.OK) {
				return commandDialog.getValue();
			}
			return null;
		}
		
		@Override
		protected String getModifiedEntry(String original) {
			InputDialog commandDialog = new InputDialog(PlatformUI
					.getWorkbench().getActiveWorkbenchWindow().getShell(),"Edit Custom " + item
					+ " Command", "Edit custom " + item + " command:", original, null);
			if (commandDialog.open() == InputDialog.OK) {
				return commandDialog.getValue();
			}
			return null;
		}

		@Override
		protected void doFillIntoGrid(Composite parent, int numColumns) {
			super.doFillIntoGrid(parent, numColumns);
			List listControl = getListControl(parent);
			Composite buttonBoxControl = getButtonBoxControl(parent);
			Composite composite = new Composite(parent, SWT.NONE);
			GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
			gridData.horizontalSpan = 2;
			gridData.widthHint = 550;
			composite.setLayoutData(gridData);
			composite.setLayout(new GridLayout(2, false));
			listControl.setParent(composite);
			buttonBoxControl.setParent(composite);
		}
	}

}
