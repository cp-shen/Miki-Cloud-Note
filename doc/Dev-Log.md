# Curriculum Requirements of The Project

### Compulsory Requirements
1. Use Java SE
2. Client with GUI and Server
3. Real time interation using network
4. Multi-thread
5. File I/O

### Alternatives
1. GUI with MenuBar, ToolBar, StatusBar
7. Picture drawing
8. DataBase
9. Vedio and Audio effect
1. Export data as Excel or PDF File
2. Others permitted

***

# My Project

### A Brief Introduction - Miki Cloud Note
A desktop cloud note application

### Dependents
1. [wangEditor](https://github.com/wangfupeng1988/wangEditor)
12. [org.eclipse.egit.github.core](https://github.com/eclipse/egit-github/tree/master/org.eclipse.egit.github.core)
13. [jsoup](https://jsoup.org/)
15. [wkhtmltopdf](https://wkhtmltopdf.org/)
16. [jhonnymertz/java-wkhtmltopdf-wrapper](https://github.com/jhonnymertz/java-wkhtmltopdf-wrapper)

### Function Features
1. Integrated with rich text editor **wangEditor**, which is open source and web based
2. Cloud sync with github gist
5. PDF convertion

### Developing Schedule
- [x] A javaFx Webview Integrated with web rich text editor
- [x] Single note process.
	- new, edit, save
	- open, export
	- remove
- [x] Deal with multiple notes
- [x] One display mainView and multiple editor windows
- [x] convert note to pdf file and export it
- [x] Github credential configuration and Remember the credential until the app exits
- [ ] Communicate with the gist server and deal with online content
- [ ] Record the opened notes with URL


### Trouble Shooting while coding
- [x] right after load, `getEngine.getDocument() == null`
    ```java
    final WebView webView = new WebView();
    final WebEngine webEngine = webView.getEngine();
    webEngine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
        if (newState == State.SUCCEEDED) {
            Document doc = webEngine.getDocument();
        }
    });
    webEngine.loadContent("<h1>hello</h1>");
    //webEngine.load("http://google.ch"); // This works too
    ```
- [x] save html as string using `jSoupDoc.outerHtml` not `w3cDom.asString`
- [x] rewrite content operation code -> editor operator class -> utility method which returns html string
- [x] how to disable editor in read-only mode
- [x] how to remove toolbar in read-only mode and pdf
- [x] `Jsoup.pase(URL url, int timeout)` do not support files (java.net.MalformedURLException: Only http & https protocols supported)
- [x] when disaplay note, when url is null, display nothing
- [x] `webEngine.loadContent` need to handle relative path manually
- [x] when closing the editor window and canceling export new note, url is still null
- [x] Auto refresh displaying after closing a editor window
- [x] repetition of the js scripts makes multiple toolbars
- [x] Where is muti-thread?
	```java
	public byte[] getPDF() throws IOException, InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            Process process = Runtime.getRuntime().exec(getCommandAsArray());

            Future<byte[]> inputStreamToByteArray = executor.submit(streamToByteArrayTask(process.getInputStream()));
            Future<byte[]> errorStreamToByteArray = executor.submit(streamToByteArrayTask(process.getErrorStream()));

            process.waitFor();

            if (process.exitValue() != 0) {
                throw new RuntimeException("Process (" + getCommand() + ") exited with status code " + process.exitValue() + ":\n" + new String(getFuture(errorStreamToByteArray)));
            }

            return getFuture(inputStreamToByteArray);
        } finally {
            executor.shutdownNow();
            cleanTempFiles();
        }
    }
	```
- [x]
	```java
	getUrl.equals("null");
	```
> - [ ] Webview Font. Problem of Chinese characters. Bold and italic failure.