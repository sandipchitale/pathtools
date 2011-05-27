package pathtools;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.ITextEditor;
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

	String[][] getFolderCustomActions() {
		return PathToolsPreferences.parseString(getPreferenceStore().getString(PathToolsPreferences.FOLDER_COMMANDS_KEY));
	}

	String[][] getFileCustomActions() {
		return PathToolsPreferences.parseString(getPreferenceStore().getString(PathToolsPreferences.FILE_COMMANDS_KEY));
	}

	public File[] getFiles() {
		final List<File> filesList = new LinkedList<File>();
		final IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench != null) {
			Display display = workbench.getDisplay();
			if (display != null) {
				display.syncExec(new Runnable() {
					public void run() {
						IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
						if (activeWorkbenchWindow != null) {
							IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
							if (activePage != null) {
								IWorkbenchPart activeEditor = activePage.getActivePart();
								if (activeEditor instanceof ITextEditor) {
									ITextEditor abstractTextEditor = (ITextEditor) activeEditor;
									IEditorInput editorInput = abstractTextEditor.getEditorInput();
									if (editorInput instanceof IFileEditorInput) {
										IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
										IFile iFile = fileEditorInput.getFile();
										if (iFile != null) {
											filesList.add(new File(iFile.getLocation().toOSString()));
											return;
										}
									}
								} else if (activeEditor instanceof MultiPageEditorPart) {
									MultiPageEditorPart multiPageEditorPart = (MultiPageEditorPart) activeEditor;
									Object multiPageEditorActivePage = multiPageEditorPart.getSelectedPage();
									if (multiPageEditorActivePage instanceof ITextEditor) {
										ITextEditor abstractTextEditor = (ITextEditor) multiPageEditorActivePage;
										IEditorInput editorInput = abstractTextEditor.getEditorInput();
										if (editorInput instanceof IFileEditorInput) {
											IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
											IFile iFile = fileEditorInput.getFile();
											if (iFile != null) {
												filesList.add(new File(iFile.getLocation().toOSString()));
												return;
											}
										}
									}
								}
							}
							ISelectionService selectionService =
								activeWorkbenchWindow.getSelectionService();
							if (selectionService != null) {
								ISelection selection = selectionService.getSelection();
								if (selection instanceof IStructuredSelection) {
									IStructuredSelection structuredSelection = (IStructuredSelection) selection;
									if (structuredSelection.size() > 0) {
										@SuppressWarnings("rawtypes")
										Iterator iterator = structuredSelection.iterator();
										while (iterator.hasNext()) {
											Object object = iterator.next();
											if (object instanceof IResource) {
												IResource resource = (IResource) object;
												IPath location = resource.getLocation();
												if (location != null) {
													filesList.add(new File(location.toOSString()));
												}
											} else if (object instanceof IAdaptable) {
												IAdaptable adaptable = (IAdaptable) object;
												IResource resource = (IResource) adaptable.getAdapter(IResource.class);
												if (resource != null) {
													IPath location = resource.getLocation();
													if (location != null) {
														filesList.add(new File(location.toOSString()));
													}
												}
											}
										}
									}
								}
							}
						}
					}
				});
			}
		}
		return filesList.toArray(new File[filesList.size()]);
	}

}
