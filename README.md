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
