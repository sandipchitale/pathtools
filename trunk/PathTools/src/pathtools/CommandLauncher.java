package pathtools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.progress.UIJob;

/**
 * A simple external process launcher.
 * 
 * @author Sandip V. Chitale
 * 
 */
public class CommandLauncher {
	
	private static MessageConsole messageConsole;
	
	public static void launch(final String command) {
		// Launch command on a separate thread.
		new Thread(new Runnable() {
			public void run() {
				Activator activator = Activator.getDefault();
				String[] commandArray = Utilities.parseParameters(command);
				try {
					Process process = Runtime.getRuntime().exec(commandArray);
					final MessageConsole messageConsole = getMessageConsole();
					messageConsole.clearConsole();
					messageConsole.newMessageStream().println(command);
					UIJob uiJob = new UIJob("") {
						@Override
						public IStatus runInUIThread(IProgressMonitor monitor) {
							IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
							if (activeWorkbenchWindow != null) {
								IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
								if (activePage != null) {
									IConsoleView view;
									try {
										view = (IConsoleView) activePage.showView(IConsoleConstants.ID_CONSOLE_VIEW);
										view.display(messageConsole);
									} catch (PartInitException e) {
										return Status.CANCEL_STATUS;
									}
								}
							}
							return Status.OK_STATUS;
						}
					};
					uiJob.schedule();

					new Thread(new MessageConsoleWriter(messageConsole, process.getInputStream())).start();
					new Thread(new MessageConsoleWriter(messageConsole, process.getErrorStream(), true)).start();
					
					int status = process.waitFor();
					if (status == 0) {
						// Good
					} else {
						activator.getLog().log(
								new Status(IStatus.ERROR, activator.getBundle()
										.getSymbolicName(), "Process '"
										+ Arrays.asList(commandArray)
												.toString()
										+ "' exited with status: " + status));
					}
				} catch (InterruptedException ex) {
					activator.getLog().log(
							new Status(IStatus.ERROR, activator.getBundle()
									.getSymbolicName(),
									"Exception while executing '"
											+ Arrays.asList(commandArray)
													.toString() + "'", ex));
				} catch (IOException ioe) {
					activator.getLog().log(
							new Status(IStatus.ERROR, activator.getBundle()
									.getSymbolicName(),
									"Exception while executing '"
											+ Arrays.asList(commandArray)
													.toString() + "'", ioe));
				}

			}
		}, "Launching - " + command).start();
	}
	
	private static MessageConsole getMessageConsole() {
		if (messageConsole == null) {
			messageConsole = new MessageConsole("Path Tools Console", null);
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{messageConsole});			
		}
		return messageConsole;
	}
	
	private static class MessageConsoleWriter implements Runnable {		
		private final MessageConsole messageConsole;
		private final InputStream from;
		private final boolean stderr;
		
		private MessageConsoleWriter(MessageConsole messageConsole, InputStream from) {
			this(messageConsole, from, false);
		}
		
		private MessageConsoleWriter(MessageConsole messageConsole, InputStream from, boolean stderr) {
			this.messageConsole = messageConsole;
			this.from = from;
			this.stderr = stderr;
		}
		
		public void run() {
			MessageConsoleStream messageConsoleStream = messageConsole.newMessageStream();
			if (stderr) {
				messageConsoleStream.setColor(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(from));
			String output = null;
			try {
				while ((output = reader.readLine()) != null) {
					messageConsoleStream.println(output);
				}
			} catch (IOException e) {
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
				}
				try {
					messageConsoleStream.close();
				} catch (IOException e) {
				}
			}
		}		
	}

}
