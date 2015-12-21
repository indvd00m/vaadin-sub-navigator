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
		<version>0.9.4</version>
	</dependency>

## Vaadin directory

[SubNavigator](https://vaadin.com/directory#!addon/subnavigator)

## General information

_Sorry for my english._

SubNavigator - is a server-side addon for [Vaadin 7] (https://vaadin.com/) which extends the capabilities of the standard object [Navigator] (https://vaadin.com/book/-/page/advanced.navigator.html) and allows easier to organize a hierarchical multilevel structure of vaadin-application with support of browser bookmarks, navigation history, Forward/Back buttons, etc.

Standard [Navigator] (https://vaadin.com/book/-/page/advanced.navigator.html) allows you to register for a specific View special URI Fragment and when the user accesses the required URL-address Navigator call `enter()` method from the corresponding View. All is well until there is a need to organize a sub-View. [Navigator] (https://vaadin.com/book/-/page/advanced.navigator.html) allows you to pass in `View.enter()` any parameters, ie, it can be easy to arrange two-level hierarchy, /main/view1 and /main/view2 for example. For large nesting it will require additional action.

[SubNavigator](https://github.com/indvd00m/vaadin-sub-navigator/blob/master/sub-navigator-api/src/main/java/com/indvd00m/vaadin/navigator/api/ISubNavigator.java) allows you to explicitly specify a hierarchy of objects, and when the user moves from one address (URI Fragment, to be precise) to another SubNavigator will notify the appropriate objects on the need to clean/refresh the data in that prioritization as they are in the hierarchy.

### Description

Two main interfaces in [SubNavigator](https://github.com/indvd00m/vaadin-sub-navigator/blob/master/sub-navigator-api/src/main/java/com/indvd00m/vaadin/navigator/api/ISubNavigator.java) - is a [ISubView](https://github.com/indvd00m/vaadin-sub-navigator/blob/master/sub-navigator-api/src/main/java/com/indvd00m/vaadin/navigator/api/view/ISubView.java) and [ISubContainer](https://github.com/indvd00m/vaadin-sub-navigator/blob/master/sub-navigator-api/src/main/java/com/indvd00m/vaadin/navigator/api/view/ISubContainer.java).

```
public interface ISubView extends Component {
	String getRelativePath();
	void clean();
	void build();
}
```
```
public interface ISubContainer extends ISubView {
	ISubView getSelectedView();
	void setSelectedView(ISubView view);
	void deselectView(ISubView view);
}
```
As you can see, `ISubContainer` is a container, and generally can contain any other` ISubContainer`, or `ISubView`. Let's look in situation where you will need to display some data at address `#!/path1/path2/path3`. Both `path1` and `path2` is a `ISubContainer` implementation, `path3` can be either `ISubView` or `ISubContainer`. The method `getRelativePath()` of these objects determine their relative path `path1`, `path2`, `path3`. `path1` is a root element which contains other elements. For example, `path1` - it could be `Panel` element with nested `TabSheet`, `path2` - Tab, `path3` - `VerticalLayout`. 

An example of the implementation of the element at `path3`:
```
public class SubView3 extends VerticalLayout implements ISubView {

	@Override
	public String getRelativePath() {
		return "path3";
	}

	@Override
	public void clean() {
		removeAllComponents();
	}

	@Override
	public void build() {
		addComponent(new Label("Hello, world!"));
	}

}
```
This is example of implementation of the element at `path2`:
```
public class SimpleSubContainer2 extends VerticalLayout implements ISubContainer {

	@Override
	public String getRelativePath() {
		return "path2";
	}

	@Override
	public void clean() {
		removeAllComponents();
	}

	@Override
	public void build() {
		((MyUI) getUI()).getSubNavigator().addView(this, new SubView3());
	}

	@Override
	public ISubView getSelectedView() {
		return (ISubView) getComponent(0);
	}

	@Override
	public void setSelectedView(ISubView view) {
		addComponent(view);
	}

	@Override
	public void deselectView(ISubView view) {
		removeComponent(view);
	}

}
```

**Views registering.** To define the object tree in the application it can be used `addView(ISubContainer container, ISubView view)` method from [ISubNavigator](https://github.com/indvd00m/vaadin-sub-navigator/blob/master/sub-navigator-api/src/main/java/com/indvd00m/vaadin/navigator/api/ISubNavigator.java) interface:
```
// registering views
ISubNavigator subNavigator = new SubNavigator(ui, path1View); // path1View - root view
subNavigator.addView(path1View, path2View); // path2View contained in path1View
subNavigator.addView(path2View, path3View);
subNavigator.addView(path1View, path4View);
subNavigator.addView(path4View, path5View);
```

**Situation 1** - a user for the first time passed the link to the application. For each object from the root to the last (`path1`,` path2`, `path3`) SubNavigator will invoke methods `build()` (for `ISubView`) and `setSelectedView(ISubView view)` (for `ISubContainer`), starting from the root:
```
// navigating to #!/path1/path2/path3
path1View.build()
path1View.setSelectedView(path2View)
path2View.build()
path2View.setSelectedView(path3View)
path3View.build()
```

**Situation 2** - user from the address `#!/path1/path2/path3` navigates to `#!/path1/path4/path5`. For objects with relative path `path3` and `path2` SubNavigator call methods `clean()` and `deselectView(ISubView view)`, then for objects with path `path4` and` path5` call methods `build()` and `setSelectedView(ISubView view ) `:
```
// navigating from #!/path1/path2/path3 to #!/path1/path4/path5
path3View.clean()
path2View.deselectView(path3View)
path2View.clean()
path1View.deselectView(path2View)
path1View.setSelectedView(path4View)
path4View.build()
path4View.setSelectedView(path5View)
path5View.build()
```

### Dynamic containers
In addition to the static method of view registration (`ISubNavigator.addView`), you can use  [ISubDynamicContainer](https://github.com/indvd00m/vaadin-sub-navigator/blob/master/sub-navigator-api/src/main/java/com/indvd00m/vaadin/navigator/api/view/ISubDynamicContainer.java):
```
public interface ISubDynamicContainer extends ISubContainer {
	ISubView createView(String viewPathAndParameters);
}
```
This container can create a nested `ISubView` without special registration. In the previous example, if `path3` is `ISubDynamicContainer` in the path `#!/path1/path2/path3`, navigating to `#!/path1/path2/path3/123` will cause of calling method `createView("123")` in `path3` object. Dynamic containers can contain other dynamic containers.

Example of dynamic container which can create windows:
```
public class DynamicContainer1 extends VerticalLayout implements ISubDynamicContainer, ISubTitled, CloseListener {

	protected ISubNavigator subNavigator;
	SimpleView selectedView;
	DynamicContainer1 thisView = this;

	Label info;
	TextField id;
	Button button;

	@Override
	public ISubView createView(String viewPathAndParameters) {
		if (!viewPathAndParameters.matches("\\d+"))
			return null;
		SimpleView view = new SimpleView(viewPathAndParameters);
		return view;
	}

	@Override
	public ISubView getSelectedView() {
		return selectedView;
	}

	@Override
	public void setSelectedView(ISubView view) {
		selectedView = (SimpleView) view;
		Window window = new Window();
		window.setModal(true);
		window.setWidth(300, Unit.PIXELS);
		window.setHeight(500, Unit.PIXELS);
		window.setContent(selectedView);
		window.setCaption("Dynamically created window");
		window.addCloseListener(this);
		getUI().addWindow(window);
	}

	@Override
	public void deselectView(ISubView view) {
		Window window = (Window) selectedView.getParent();
		window.removeCloseListener(this);
		window.close();
		selectedView = null;
	}

	@Override
	public void windowClose(CloseEvent e) {
		selectedView = null;
		subNavigator.notifySelectedChangeDirected(this);
	}

	@Override
	public void clean() {
		removeAllComponents();
	}

	@Override
	public String getRelativePath() {
		return "dynamic-container";
	}

	@Override
	public void build() {
		subNavigator = ((SubNavigatorUI) getUI()).getSubNavigator();

		setSizeUndefined();
		setSpacing(true);
		setMargin(true);

		info = new Label("This is dynamic container");
		addComponent(info);

		id = new TextField("Enter object id");
		id.setValue("123");
		id.setImmediate(true);
		addComponent(id);

		button = new Button("Click to open object");
		button.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				String sId = id.getValue().replaceAll("\\s+", "");
				subNavigator.navigateTo(thisView, sId);
			}
		});
		addComponent(button);
	}

	@Override
	public String getRelativeTitle() {
		return "Dynamic Container";
	}

}
```

### Exceptions handling

To catch errors you can implement [ISubErrorContainer](https://github.com/indvd00m/vaadin-sub-navigator/blob/master/sub-navigator-api/src/main/java/com/indvd00m/vaadin/navigator/api/view/ISubErrorContainer.java):
```
public interface ISubErrorContainer extends ISubContainer {
	ISubView createErrorView(String viewPath, String errorPath);
	ISubView createErrorView(String viewPath, Throwable t);
}
```
Method `createErrorView(String viewPath, String errorPath)` will be called if the `SubNavigator` could not find any data at the entered by user address. For example if the user enter a non-existent path `#!/path1/path9/path12` and` path1` implements `ISubErrorContainer`, SubNavigator call `createErrorView("error", "path1/path9/path12")` method on `path1` object. "error" is a relative path to display the created object `ISubView`, ie it will be located at the `#!/path1/error`.

Showing view with error info:
```
	@Override
	public ISubView createErrorView(String viewPath, String errorPath) {
		return new ErrorView(viewPath, errorPath);
	}

	@Override
	public ISubView createErrorView(String viewPath, Throwable t) {
		return new ErrorView(viewPath, t);
	}
```

### Page title

To display hierarchical page title (eg "Page1 - Inner Page2 - Inner Page3") you can implement interface [ISubTitled](https://github.com/indvd00m/vaadin-sub-navigator/blob/master/sub-navigator-api/src/main/java/com/indvd00m/vaadin/navigator/api/view/ISubTitled.java):
```
public interface ISubTitled {
	String getRelativeTitle();
}
```
To enable this feature, use the `ISubNavigator.setEnabledSubTitles (true)` method.

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

### Version 0.9.3
- Hierarchical page titles.
- Added deselectView() method for containers.
- Now containers can handle view exceptions.
- Fixed cleaning of dynamic containers.

### Version 0.9.3.1
- Allow redirect during the build process.

### Version 0.9.4
- Add addon building for deploy to vaadin directory.


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
