# Magic Box Initial Specification

## Goal and Scope

Build an offline Android host application that lets users import and manage independent web-based functions. The host starts without preinstalled functions.

## Host Application

- Name the application Magic Box and support Android 10 (API 29) and later
- Use English interface text and require no account or network connection
- Provide ALL, Search, and Settings tabs in that order in the bottom navigation, with Search selected at startup
- Place Import, Manage, and About actions on the Settings tab
- Show the project GitHub address, https://github.com/hiSean0909/Android-Magic-Box, in About; provide a Copy button that copies the address and confirms the action without requiring network access
- Show all enabled tools and games on ALL, search function names without regard to English letter case, and open an enabled function from the results
- Center each tab's main content within the available area, including the search field and settings actions; keep longer content scrollable
- Let users browse accessible phone storage directories through the Android file picker, select a function ZIP package, and import it into application storage
- Validate package metadata, entry point, and host interface compatibility before installation; report invalid or incompatible packages without changing installed functions
- On the Manage screen, show each installed function's enabled state and an Enable or Disable button, and provide an Uninstall button with a confirmation prompt; identify updates by the function's unique identifier
- Enable newly installed and reinstalled functions by default; preserve the enabled state when updating an installed function
- Make a successfully imported or enabled function available without reinstalling or restarting the host
- Close a running function before applying its update, disabling it, or removing it
- Allow users to return from a function to the host

## Function Package and Runtime

- Deliver each function as a single ZIP package containing metadata, an HTML entry point, JavaScript, and any other resources required for offline operation
- Metadata identifies the function, its name, category, version, entry point, and required host interface version
- Run function pages locally and expose only the host capabilities required by the function
- Provide screen measurement and persistent settings capabilities for Ruler through a defined host interface
- Provide a defined host interface for a function to enter and exit fullscreen display, hiding system bars while fullscreen and restoring them afterward
- Require host interface version 2 for functions using fullscreen; continue to accept version 1 functions
- Isolate function settings by function identifier and preserve them across application restarts
- Preserve installed functions, their enabled states, and their settings when the host is updated in place
- Handle initialization and cleanup when opening and closing a function
- Accept packages from trusted sources; do not grant functions unrestricted access to host capabilities

## Delivery and Acceptance

- Provide the latest development APK in the project root, named `MagicBox-v<major>.<minor>.<sequence>-dev.apk`; use a four-digit sequence starting at `0001` and increment it for each development version
- Publish `MagicBox-v<major>.<minor>-release.apk` only after user confirmation, placing the latest release in `release` and earlier releases in `release/archive`
- Set the initial APK version name to `0.1.0001` and version code to 4; increase the version code for subsequent deliveries
- Keep the application ID and signing certificate consistent across APK deliveries so a newer version can update the installed application in place
- Verify that the host installs and opens without network access and that no functions are installed initially
- Verify the three navigation tabs, centered content, ALL function list, and the Import, Manage, and About actions on Settings
- Verify that About displays the project GitHub address and Copy places the address on the clipboard with confirmation while offline
- On the target phone, verify that the system file picker can browse accessible internal storage directories and import a selected function ZIP
- Verify offline import, search, opening, disabling, enabling, updating, and uninstalling representative functions without reinstalling the host
- Verify that new installations and reinstallations are enabled by default, updates preserve the enabled state, and each installed function shows its state and management buttons
- Verify that invalid or incompatible packages are rejected and an installed function remains usable after a failed update
- Verify the screen measurement and persistent settings interfaces used by functions
- Verify that a function can enter and exit fullscreen and that system bars are restored when the function closes
- On the target phone, install an update over the existing compatible APK and verify that installed functions, enabled states, and settings remain available
- Verify the phone's Android version during development and record the user's device test feedback
