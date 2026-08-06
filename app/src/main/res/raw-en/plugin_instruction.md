Use HTML Preview from the main AutoJs6 Explorer:

1. Install and enable the `HTML Preview` plugin.
2. Open the overflow menu for one supported HTML file.
3. Select `Preview HTML`.

The plugin receives temporary read access to the selected file and its parent directory through content URIs. It does not receive a raw filesystem path.

Supported extensions: `html`, `htm`, `shtm`, `shtml`, `xht`, `xhtml`.

The viewer sanitizes content before display. It removes scripts, event handler attributes, frames, embedded objects, and unsafe resource addresses. JavaScript, WebView storage, cookies, and direct file access remain disabled.

Version 1 supports only single-file read-only actions in the main AutoJs6 Explorer.
