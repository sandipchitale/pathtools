package pathtools;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IDynamicVariableResolver;

/**
 * This is variable resolver for the following variables:
 *
 * <ul>
 * <li><code>${path}</code></li>
 * <li><code>${path-slashes}</code></li>
 * <li><code>${path-backslashes}</code></li>
 * <li><code>${parent-path}</code></li>
 * <li><code>${parent-path-slashes}</code></li>
 * <li><code>${parent-path-backslashes}</code></li>
 * <li><code>${name}</code></li>
 * <li><code>${name-sans-extension}</code></li>
 * <li><code>${extension}</code></li>
 * <li><code>${parent-Apath}</code></li>
 * <li><code>${parent-Aname}</code></li>
 * <li><code>${parent-Aname-sans-extension}</code></li>
 * <li><code>${parent-Aextension}</code></li>
 * </ul>
 *
 * @author Sandip V. Chitale
 *
 */
public class PathToolsVariableResolver implements IDynamicVariableResolver {
	
	private static ThreadLocal<File> threadLocalFile = new ThreadLocal<File>(); 

	/* (non-Javadoc)
	 * @see org.eclipse.core.variables.IDynamicVariableResolver#resolveValue(org.eclipse.core.variables.IDynamicVariable, java.lang.String)
	 */
	public String resolveValue(IDynamicVariable variable, String argument)
			throws CoreException {
		File file = threadLocalFile.get();
		if (file == null) {
			File[] files = Activator.getDefault().getFiles();
			if (files != null && files.length > 0) {
				file = files[0];
			}
		}

		if (file != null) {
			String variableName = variable.getName();
			File parentFile = file.getParentFile();

			if ("path".equals(variableName)) {
				return file.getAbsolutePath();
			} else if ("name".equals(variableName)) {
				return file.getName();
			} else if ("name-sans-extension".equals(variableName)) {
				return splitNameAndExtension(file.getName())[0];
			} else if ("extension".equals(variableName)) {
				return splitNameAndExtension(file.getName())[1];
			} else if (parentFile != null) {
				if ("parent-path".equals(variableName)) {
					return parentFile.getAbsolutePath();
				} else if ("parent-path-slashes".equals(variableName)) {
					return parentFile.getAbsolutePath().replace('\\', '/');
				} else if ("parent-path-backslashes".equals(variableName)) {
					return parentFile.getAbsolutePath().replace('/', '\\');
				} else if ("parent-name".equals(variableName)) {
					return parentFile.getName();
				} else if ("parent-name-sans-extension".equals(variableName)) {
					return splitNameAndExtension(parentFile.getName())[0];
				} else if ("parent-extension".equals(variableName)) {
					return splitNameAndExtension(parentFile.getName())[1];
				}
			}
		}
		return "";
	}

	/**
	 * Return the name without extension and extension for a name.
	 * @param nameAndExtension
	 * @return the name without extension and extension for a name.
	 */
	private static String[] splitNameAndExtension(String nameAndExtension) {
		String[] nameAndExtensionArray = new String[] { nameAndExtension, "" };
		if (nameAndExtensionArray != null) {
			int lastIndexOfDot = nameAndExtension.lastIndexOf('.');
			if (lastIndexOfDot != -1) {
				nameAndExtensionArray[0] = nameAndExtension.substring(0,
						lastIndexOfDot);
				nameAndExtensionArray[1] = nameAndExtension
						.substring(lastIndexOfDot + 1);
			}
		}
		return nameAndExtensionArray;
	}

	public static void setFile(File file) {
		PathToolsVariableResolver.threadLocalFile.set(file);
	}

	public static File getFile() {
		return threadLocalFile.get();
	}

}
