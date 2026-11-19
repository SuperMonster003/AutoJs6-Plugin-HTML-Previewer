HTML Previewer previews HTML and MHTML files inside AutoJs6 without opening a browser. Pages open in a safe mode that runs no scripts; you can also view the source, find text in the page, or export it as a PDF. Previewing only reads the file and never changes the original.

### Getting started

1. Make sure this plugin is enabled in AutoJs6 "Plugin Center".
2. Open the AutoJs6 file manager and locate the HTML or MHTML file.
3. Choose "Preview HTML" from the file menu (in AutoJs6 6.8.1 and later it is under the top-level menu "HTML Previewer").
4. Use the menu at the top right of the preview for find, view source, PDF export, "Settings" and more.

### Main features

- **Find in page**: highlights the matches of the text you type and lets you jump between them.
- **View source**: shows the original text in a read-only monospace layout; choose "View rendered page" to return to the page.
- **Export PDF**: opens the system print screen, where you choose to save as PDF and pick a location; the plugin needs no storage permission.
- **Interactive mode**: turn it on in "Settings" to run scripts so that buttons, animations and local storage work; it turns off again for every new preview, so turn it on only for trusted pages.
- **Network resources**: turn on "Load network images" to show HTTPS images; with interactive mode on, this option becomes "Load network resources" and also allows HTTPS scripts and styles.
- **Text size and theme**: adjust "Text size" in "Settings" and choose the theme "Follow AutoJs6", "Light" or "Dark".
- **Fullscreen mode**: hides the toolbar so you can focus on reading; turn on "Start in fullscreen mode" to enter fullscreen for every preview.
- **MHTML archives**: shows the page in a web archive with its embedded styles, images and fonts, without extracting anything to storage.

### Requirements

- Android 7.0 or later.
- AutoJs6 6.8.0 or later.
- No storage permission is needed; the plugin only reads the selected file and its folder, and uses the network only to load HTTPS resources.

### FAQ

**Why buttons and animations do not respond**

Pages run no scripts by default. For a complete HTML file you trust, turn on "Interactive mode" in "Settings". MHTML files, large files of which only the beginning is shown, and the source view never run scripts.

**Why some images or styles are missing**

Local images and styles must be in the folder of the HTML file or one of its subfolders. Network images need "Load network images" turned on, and only HTTPS addresses are supported.

**Why only the beginning of a large file is shown**

For an HTML file larger than 8 MB, the plugin asks first, then shows only the first 8 MB and marks where it is cut off. MHTML archives must be shown in full, so one larger than 8 MB cannot be previewed.

**Why "Preview HTML" is missing from the file menu**

The plugin only handles files with the extensions html, htm, shtm, shtml, xht, xhtml, mht, mhtml. Check the file extension, and make sure the plugin is enabled in "Plugin Center".

See the [project homepage](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer) for more details.
