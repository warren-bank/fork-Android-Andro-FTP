### [Andro FTP](https://github.com/warren-bank/fork-Android-Andro-FTP)

Andro FTP is an ftp client application for Android platform.

#### Notes:

* minimum supported version of Android:
  - Android 3.1 (Honeycomb, API 12)

#### Credit and Copyright

* I did not write this app!
* the original source code was published on [_Google Code_](https://code.google.com/archive/p/andro-ftp/)
  - _Google Code_ is now a read-only archive and its ["Export to GitHub"](https://code.google.com/export-to-github/export?project=andro-ftp) tool has been disabled
  - [this bash script](https://github.com/warren-bank/fork-Android-Andro-FTP/raw/migrate-google-code-archive/migrate-google-code-archive.sh) shows the migration from `svn` to [`git`](https://github.com/warren-bank/fork-Android-Andro-FTP/tree/master)
* the copyright belongs completely to the original author: `buchir`
  - released with public license: [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt)

#### Fork

* my initial goal is to build and release an APK
  - none is available online
  - would like to see this app's UI and test its functionality
* the first release doesn't make any significant changes
  - the code looks _very_ well written
  - only the build tools and app permissions require update

#### Comments

* [AndFTP](http://www.lysesoft.com/products/andftp/) is the ftp client that I've used on Android for years
  - pros:
    * works great!
      - not a single complaint
      - connects perfectly to [`swissfileknife`](https://github.com/warren-bank/mirror-swissfileknife)
        * [`bash` script](https://github.com/warren-bank/mirror-swissfileknife/blob/recipes/bin/ftpd.sh) to start an ftp server on the command-line
        * [Windows `cmd` script](https://github.com/warren-bank/mirror-swissfileknife/blob/recipes/bin/ftpd.cmd) to start an ftp server on the command-line, or by drag-drop of a target directory onto a desktop shortcut to the script
  - cons:
    * closed source
    * more recent versions contain ads
* I'm always on the lookout for an open-source alternative with comparable features
  - TBD&hellip;

#### Known Issues

* UI
  - forced landscape orientation
  - layout for MainActivity doesn't work well on small screens
    * there are 2 ListViews:
      - local filesystem on the left
      - remote filesystem on the right
    * on small screens, only icons are shown..<br>filenames aren't visible
  - ConnectingDialog has no way to cancel, or dismiss the dialog

* Functionality
  - a successful connection to an ftp server does not display the remote directory listing
  - a runtime permission check needs to be added for Android 6+
    * currently, permission to access external storage needs to be granted manually:
      ```text
        Android Settings > Apps > Andro FTP > Permissions
      ```

* Android Studio Project
  - enabling Proguard causes the app to immediately crash when launched
    * the error message pertains to the resource ID (0x0) for a drawable that cannot be found

#### Conclusions

* this app is interesting&hellip;
  - it _could_ serve as an excellent foundation to build upon
  - as is, the client is completely unusable
