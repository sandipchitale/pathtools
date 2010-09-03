package pathtools;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.texteditor.ITextEditor;

public class CustomAction implements IObjectActionDelegate, IMenuCreator {
	private Menu customActionsMenu;
	private Menu customActionsSubMenu;

	private File fileObject;

	protected IWorkbenchWindow window;

	public void dispose() {
		if (customActionsMenu != null) {
			customActionsMenu.dispose();
		}
		if (customActionsSubMenu != null) {
			customActionsSubMenu.dispose();
		}
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.window = targetPart.getSite().getWorkbenchWindow();
		action.setMenuCreator(this);
	}
	
	public void run(IAction action) {
		showPathToolsPreferences();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		fileObject = null;
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
				}
			}
		}
		if (fileObject == null) {
			if (window != null) {
				IWorkbenchPage activePage = window.getActivePage();
				if (activePage != null) {
					IWorkbenchPart activeEditor = activePage.getActivePart();
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
			}
		}
		if (location != null) {
			fileObject = location.toFile();
		}
	}

	public Menu getMenu(Menu parent) {
		if (customActionsSubMenu != null) {
			customActionsSubMenu.dispose();
		}
		customActionsSubMenu = new Menu(parent);
		customActionsSubMenu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				// re-populates the menu dynamically
				Menu menu = customActionsSubMenu;
				MenuItem[] items = menu.getItems();
				for (MenuItem item : items) {
					item.dispose();
				}
				fillMenu(customActionsSubMenu);
			}
		});
		return customActionsSubMenu;
	}

	public Menu getMenu(Control parent) {
		if (customActionsMenu != null) {
			customActionsMenu.dispose();
		}
		customActionsMenu = new Menu(parent);
		fillMenu(customActionsMenu);
		return customActionsMenu;
	}
	
	private void fillMenu(Menu menu) {
		if (fileObject != null) {
			String[] commandsArray = null;
			if (fileObject.isDirectory()) {
				commandsArray = Activator.getDefault().getFolderCustomActions();
			} else {
				commandsArray = Activator.getDefault().getFileCustomActions();
			}
			if (commandsArray.length > 0) {
				for (String command : commandsArray) {
					MenuItem commandMenuItem = new MenuItem(menu, SWT.PUSH);					
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
				new MenuItem(menu, SWT.SEPARATOR);
			}
			MenuItem preferecesMenuItem = new MenuItem(menu, SWT.PUSH);
			preferecesMenuItem.setText("Edit Custom commands...");
			preferecesMenuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					showPathToolsPreferences();
				}
			});
			
		}
	}

	private void showPathToolsPreferences() {
		String[] displayedIds = new String[] {"PathTools.page"};
		PreferenceDialog pathToolsPreferenceDialog = PreferencesUtil.createPreferenceDialogOn(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				displayedIds[0],
				displayedIds,
				null);
		pathToolsPreferenceDialog.open();
	}
}
