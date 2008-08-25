package pathtools;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * A simple external process launcher.
 * 
 * @author Sandip V. Chitale
 * 
 */
public class CommandLauncher {

	public static void launch(final String command) {
		// Launch command on a separate thread.
		new Thread(new Runnable() {
			public void run() {
				Activator activator = Activator.getDefault();
				String[] commandArray = Utilities.parseParameters(command);
				try {
					Process process = Runtime.getRuntime().exec(commandArray);
					// TODO Should handle STDOUT and STDERR here.
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

}
