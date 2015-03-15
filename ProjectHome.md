# What is new? #
  * Added support for Copy Path, Explore and Command Line Shell actions to local resources in RSE (Needs RSE)
  * Added Copy SVN URL for resources in SVN based projects (Needs Subclipse)
  * All the Path Tools variables are also available as Eclipse string substitution variables.
  * Now you can use other Eclipse string substitution variables such as ${eclipse\_home} in Path Tools commands. Interesting things are possible using the ${string\_prompt} or ${folder\_prompt} etc.
  * The Path Tool commands now work for Text Editor tabs of Multipage editors such as Plug-in Manifest editor.

# Update Site #

http://pathtools.googlecode.com/svn/trunk/PathToolsUpdateSite/site.xml

# Path Tools Eclipse Plug-in #

This plug-in adds the following actions to Eclipse.

  * Copy Paths - this action copies the fully qualified paths of selected folders and files into the Clipboard. Copy Paths action is now a drop down and allows you to copy paths in all different formats. The last used format is remembered and used. The workspace resource paths, fully qualified name of a Java files, and some well known locations e.g. workspace location, can also be copied.
  * Explore - this action opens the selected folder or the folder containing the selected file in the OS file explorer.  The user can configure the exact command that is used to launch the file explorer.  The Explore drop down has actions to go to well known places or user specified location.
  * Open Command Line Shell - this action opens a command line shell window at the selected folder or the folder containing the selected file.
  * Open in external editor... - this action opens the selected folder or file using the selected external editor.  The user can configure the exact command that is used to launch the external editor.
  * Custom actions pull down menu. These custom actions can be defined in Path Tools preferences page. The custom action can be added, duplicated, removed and reordered using table editor. Clicking on the Custom actions buttons shows the Path Tools preferences page. Here are some examples of custom commands:
    * Open terminal
      * Ubuntu - /usr/bin/gnome-terminal --working-directory="{path}"
      * Windows - cmd /K "cd {path}"

The actions are also enabled when an editor with file editor input is active.

When there is no selected file in the workbench, the actions above work on the workspace folder.

The actions have been grouped into a Path Tools sub menu in pop-up menus as the pop-up menus were getting too long.

**Toobar**

![http://pathtools.googlecode.com/files/PathToolsToolbar3.png](http://pathtools.googlecode.com/files/PathToolsToolbar3.png)

**Preferences Page**

![http://pathtools.googlecode.com/files/PathToolsPreferencesPage.png](http://pathtools.googlecode.com/files/PathToolsPreferencesPage.png)