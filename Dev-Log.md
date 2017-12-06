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
1. wangEditor
2. editor.md
3. gitweb
4. lighttpd
5. javafx
6. google web toolkit
7. gitlab
8. docker
9. VM
10. google cloud platform
11. GitHub API for Java
12. JGit
13. jsoup
14. JNI

### Function Features
1. Integrated with rich text editor, which is open source, web based, dependent on html.
2. Cloud sync with server using gitweb
3. Version control using gitweb
3. Markdown support
4. To-do list
5. PDF convertion

### Developing Plan
- [x] A javaFx Webview Integrated with web rich text editor
- [x] Single note process. new, save, open, export
- [x] Deal with multiple notes
- [ ] One display view and multiple ditor windows
- [ ] Record the opened notes with URL
- [ ] Communicate with the server
- [ ] Specify a new file extension


### Code Log
- [x] right after load, `getEngine.getDocument() == null`
- [ ] Webview Font, CSS
- [x] `webEngine.loadContent` ?
- [x] save html as string using `w3cDom.asString` or `jSoupDoc.outerHtml`
- [ ] name of the html file, the title element, the title class field, the title of the tab to be unified
- [ ] height exception when open files
- [ ] how to bind the tab and the note object? use id? use envent handler of the selectproperty to select the table?
