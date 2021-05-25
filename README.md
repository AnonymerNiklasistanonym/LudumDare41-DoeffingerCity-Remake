# LudumDare41-DoeffingerCity-Remake

A remake of the original https://github.com/AnonymerNiklasistanonym/LudumDare41-DoeffingerCity

## Setup

### libGDX project template generation

*(The following part is based on [this article by libgdx](https://libgdx.com/dev/project-generation/))*

To setup the project the *[libGDX PROJECT SETUP](https://libgdx.com/dev/project-generation/)* application was used with the following settings:

![Screenshot of the libGDX PROJECT SETUP configuration](screenshots/libgdx_setup.png)

### Importing the project into the Android Studio IDE

*(The following part is based on [this article by libgdx](https://libgdx.com/dev/import-and-running/))*

To import the generated project into [Android Studio](https://developer.android.com/studio) (which was chosen to easily test the game for Android devices but can also be extended for desktop testing) you just click `Open File or Project` and select the directory that was specified in the `Destination` text field during the libGDX project setup.

To easily test this application run configurations can be added:

Per default an `android` run configuration is already there which runs out of the box which can be run by clicking the green triangle (emulator or local Android device is both possible). When clicking on the entry select `Edit Configurations`, then click the plus symbol (`Add New Configuration`) and select `Application`. Give it the name `DesktopLauncher` and select an Android version, the desktop main class and append to the working directory `android/assets` so that resources in this directory can be found.

![Screenshot of the Android Studio new run configuration for desktop testing](screenshots/android_studio_desktop_launch_configuration_setup.png)

After clicking `OK` you should be able to press the green triangle while having the `DesktopLauncher` run configuration selected and a native desktop build should be compiled and run which means you are ready to build and test the game for desktop and Android devices.

These configurations need to be done each time this repository is cloned because every device will most likely have different parameters/paths.

### Export and run the project via the command line

To build, run and export the project using the command line you use `gradle`.

For ease of use (on Linux) you can create a [`Makefile`](td-racing-remake/Makefile) that contains multiple targets for each use case:

```makefile
.PHONY: clean clean_desktop clean_html build_desktop build_html

VERSION=1.0

all: export_desktop export_html

clean: clean_desktop clean_html

clean_desktop:
	# Run the gradle command to clean the desktop build files
	./gradlew desktop:clean
	# Remove the created JAR file
	rm -f bin/desktop-$(VERSION).jar

clean_html:
	# Run the gradle command to clean the html build files
	./gradlew html:clean
	# Remove the created HTML directory
	rm -rf bin/html-$(VERSION)

build_desktop:
	# Run the gradle command to build the desktop exectuable (JAR file)
	./gradlew desktop:dist

build_html:
	# Run the gradle command to build the html directory (can be hosted)
	./gradlew html:dist

export_desktop: build_desktop
	# After building desktop copy the created jar into a new bin directory
	mkdir -p bin
	cp desktop/build/libs/desktop-$(VERSION).jar bin/desktop-$(VERSION).jar

export_html: build_html
	# After building html copy the created directory into a new bin directory
	mkdir -p bin
	cp -R html/build/dist bin/html-$(VERSION)
```

You can either just run the command `make` in the directory to export a desktop and HTML version into a new `bin` directory or run `make TARGET` to only run a specific command (like for example `make clean` to clean all build files or `make export_desktop` to only export a desktop executable).

### Run the exported files

**Linux:**

- To run the created `.jar` file either double click it or type in the terminal:

  ```sh
  cd bin
  java -jar NameOfTheJar.jar
  ```

- To run the web app you can create a local web server in the terminal:

  ```sh
  # The following command only works if you have Python of version 3 or greater installed!
  cd bin
  python -m http.server
  # Now click or copy the listed URL to open it in your browser
  # (most likely http://0.0.0.0:8000/) and append the name of the html directory
  # (for example http://0.0.0.0:8000/html-1.0/) to view it in your browser.
  ```
