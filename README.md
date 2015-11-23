# SubNavigator Add-on for Vaadin 7

SubNavigator is a server side add-on for Vaadin 7.

## Online demo

Try the add-on demo at <http://indvd00m.com/sub-navigator-demo>

## Using
Add maven repository to your POM:

	<repository>
		<id>indvd00m-github-repo</id>
		<url>https://github.com/indvd00m/maven-repo/raw/master/repository</url>
	</repository>

Add dependency to your project:

	<dependency>
		<groupId>com.indvd00m.vaadin</groupId>
		<artifactId>sub-navigator</artifactId>
		<version>0.9.2</version>
	</dependency>

## Download release

https://github.com/indvd00m/vaadin-sub-navigator/releases

## Building and running demo
```
git clone https://github.com/indvd00m/vaadin-sub-navigator/
cd vaadin-sub-navigator
mvn clean install
cd sub-navigator-demo
mvn jetty:run
```
To see the demo, navigate to <http://localhost:8080/>

## Development with Eclipse IDE

For further development of this add-on, the following tool-chain is recommended:
- Eclipse IDE
- m2e wtp plug-in (install it from Eclipse Marketplace)
- Vaadin Eclipse plug-in (install it from Eclipse Marketplace)
- JRebel Eclipse plug-in (install it from Eclipse Marketplace)
- Chrome browser

### Importing project

Choose File > Import... > Existing Maven Projects

Note that Eclipse may give "Plugin execution not covered by lifecycle configuration" errors for pom.xml. Use "Permanently mark goal resources in pom.xml as ignored in Eclipse build" quick-fix to mark these errors as permanently ignored in your project. Do not worry, the project still works fine.

### Debugging server-side

If you have not already compiled the widgetset, do it now by running vaadin:install Maven target for sub-navigator-root project.

If you have a JRebel license, it makes on the fly code changes faster. Just add JRebel nature to your sub-navigator-demo project by clicking project with right mouse button and choosing JRebel > Add JRebel Nature

To debug project and make code modifications on the fly in the server-side, right-click the sub-navigator-demo project and choose Debug As > Debug on Server. Navigate to http://localhost:8080/sub-navigator-demo/ to see the application.

 
## Release notes

### Version 0.8.0
- First beta version.

### Version 0.9.0
- Rework, rewrite, refactor.

### Version 0.9.1
- Now there is no need for extra NPE checks in sub-views.

### Version 0.9.2
- Clean views only on deselecting.
- Added ISubErrorContainer for error page generating.
- Some API refactoring.
- Demo enhancement.


## Roadmap

This component is developed as a hobby with no public roadmap or any guarantees of upcoming releases. That said, the following features are planned for upcoming releases:
- Hierarchical page title
- ...

## Issue tracking

The issues for this add-on are tracked on its github.com page. All bug reports and feature requests are appreciated. 

## Contributions

Contributions are welcome, but there are no guarantees that they are accepted as such. Process for contributing is the following:
- Fork this project
- Create an issue to this project about the contribution (bug or feature) if there is no such issue about it already. Try to keep the scope minimal.
- Develop and test the fix or functionality carefully. Only include minimum amount of code needed to fix the issue.
- Refer to the fixed issue in commit
- Send a pull request for the original project
- Comment on the original issue that you have implemented a fix for it

## License & Author

Add-on is distributed under Apache License 2.0. For license terms, see LICENSE.txt.

SubNavigator is written by David E. Veliev.
