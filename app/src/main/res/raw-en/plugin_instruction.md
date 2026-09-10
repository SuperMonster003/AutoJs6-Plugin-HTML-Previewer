Use HTML Previewer from the file manager:

1. Install and enable the `HTML Previewer` plugin.
2. Open the overflow menu for one supported HTML or MHTML file.
3. Select `HTML Previewer`.

The plugin receives temporary read access to the selected file and its parent directory through content URIs. It does not receive a raw filesystem path.

Supported extensions: `html`, `htm`, `shtm`, `shtml`, `xht`, `xhtml`, `mht`, `mhtml`.

For MHTML, the viewer selects the archive's HTML root and maps permitted embedded styles, images, fonts, audio, and video to an isolated virtual origin. Archive locations and content IDs are only internal labels; ambiguous labels are ignored and nothing is extracted to storage.

Scripts are disabled by default. For a trusted, complete ordinary HTML file, enable `Interactive mode` in `Settings` to run JavaScript, button events, WebGL, and local storage. MHTML, truncated HTML, source view, and PDF export always keep scripts disabled. Cookies, direct file/content access, native JavaScript bridges, and new windows remain disabled. Safe mode disables page storage; interactive mode allows local storage for page preferences and game scores. Source view and PDF use a fresh sanitized rendering.

The viewer also supports in-page search, adjustable text size, and theme selection. BOM, MIME charset, and early HTML encoding declarations are recognized. Ordinary HTML over 8 MB requires confirmation before only the first 8 MB is shown with a clear cutoff; MHTML must remain complete and is rejected above 8 MB.

Use `View source` in the viewer menu to inspect the original text in a read-only monospace layout, then `View rendered page` to return. Tags remain literal text in source mode, and all outbound requests are blocked.

Local resources are read from the authorized directory, or from the selected MHTML archive. Safe mode allows public HTTPS images; interactive mode also allows public HTTPS scripts, styles, and requests when `Load network resources` is enabled. Turning the network switch off blocks outbound WebView requests. Interactive scripts can send page data to remote services, so enable this mode only for trusted pages.

Choose `Export PDF` to open Android's system print panel with a freshly reread and sanitized light-theme copy. This remains true when exporting from source view, and the current viewer state is unchanged. Select `Save as PDF` and the destination in the system UI; the plugin requests no storage permission.

Explorer Action v2 supports both the primary previewer button and the overflow menu for a single file, using temporary read grants for the document and its parent directory. AutoJs6 build 5269 or later is required.

Menus and dialogs follow the AutoJs6 language and dark mode. GitHub (Auto), or the automatic HTML theme, follows AutoJs6 as well. The toolbar and system bars use one representative color sampled from the rendered page edges, with contrasting black or white text and icons. For gradients, images, and animation, that color stays fixed until reload to avoid flicker.
