# Magic Box

Magic Box is an offline Android application for installing and managing optional plugins.

See [spec.md](spec.md) for requirements. The latest development APK is in this directory; releases are in `release`.

Install `MagicBox-v0.1.0003-dev.apk` on the phone. Use ALL to browse plugins and Search to find them. Open Settings and use Import to select a plugin ZIP; use Manage to enable, disable, or remove plugins. Plugin source and packages are maintained in [Android-Magic-Box-Plugins](https://github.com/hiSean0909/Android-Magic-Box-Plugins).

Development builds use the existing signing keystore at `.secrets/debug.keystore`. Preserve this file to allow in-place updates; the build fails if it is missing.

This is a debug build. Device verification, including the phone's Android version and ruler calibration, is pending.
