package pathtools;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.texteditor.ITextEditor;

public class CustomAction implements IWorkbenchWindowPulldownDelegate {
	private Menu customActionsMenu;

	private File fileObject;

	private IWorkbenchWindow window;

	public void dispose() {}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void run(IAction action) {
		String[] displayedIds = new String[] {"PathTools.page"};
		PreferenceDialog pathToolsPreferenceDialog = PreferencesUtil.createPreferenceDialogOn(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				displayedIds[0],
				displayedIds,
				null);
		pathToolsPreferenceDialog.open();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		fileObject = null;
		action.setEnabled(false);
		try {
			IPath location = null;
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				// Is only one item selected?
				if (structuredSelection.size() == 1) {
					Object firstElement = structuredSelection.getFirstElement();
					if (firstElement instanceof IResource) {
						// Is this an IResource ?
						IResource resource = (IResource) firstElement;
						location = resource.getLocation();
					} else if (firstElement instanceof IAdaptable) {
						IAdaptable adaptable = (IAdaptable) firstElement;
						// Is this an IResource adaptable ?
						IResource resource = (IResource) adaptable
								.getAdapter(IResource.class);
						if (resource != null) {
							location = resource.getLocation();
						}
					} else if (firstElement.getClass().getName().equals("com.aptana.ide.core.ui.io.file.LocalFile")) {
						try {
							Method getFile = firstElement.getClass().getDeclaredMethod("getFile");
							Object object = getFile.invoke(firstElement);
							if (object instanceof File){
								fileObject = (File) object;
								return;
							}
						} catch (SecurityException e) {
						} catch (NoSuchMethodException e) {
						} catch (IllegalArgumentException e) {
						} catch (IllegalAccessException e) {
						} catch (InvocationTargetException e) {
						}
					}
				}
			}
			if (fileObject == null) {
				IWorkbenchPart activeEditor = window.getActivePage().getActivePart();
	            if (activeEditor instanceof ITextEditor) {
	            	ITextEditor abstractTextEditor = (ITextEditor) activeEditor;
					IEditorInput editorInput = abstractTextEditor.getEditorInput();
					if (editorInput instanceof IFileEditorInput) {
						IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
						IFile iFile = fileEditorInput.getFile();
						if (iFile != null) {
							location = iFile.getLocation();
						}
					}
	            }
			}
			if (location != null) {
				fileObject = location.toFile();
			}
		} finally {
			action.setEnabled(fileObject != null);
		}
	}

	public Menu getMenu(Control parent) {
		if (customActionsMenu != null) {
			customActionsMenu.dispose();
		}
		customActionsMenu = new Menu(parent);

		if (fileObject != null) {
			String[] commandsArray = null;
			if (fileObject.isDirectory()) {
				commandsArray = Activator.getDefault().getFolderCustomActions();
			} else {
				commandsArray = Activator.getDefault().getFileCustomActions();
			}
			for (String command : commandsArray) {
				MenuItem commandMenuItem = new MenuItem(customActionsMenu, SWT.PUSH);					
				commandMenuItem.setText(Utilities.formatCommand(command,
						fileObject));
				final String finalCommand = command;
				commandMenuItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						Utilities.launch(finalCommand,
										fileObject);
					}
				});
			}
		}
		return customActionsMenu;
	}
}
