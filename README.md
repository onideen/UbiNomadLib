UbiNomad Library
================

Middelware for location-based applications with social network integration



## Getting started

### Install Google Play Services

1. Open Android SDK Manager
2. Install *Extras* -> *Google Play Services*
   
   ![Play Services image](https://github.com/vegaen/UbiNomadLib/raw/master/screenshots/AndroidSDK.png)


### Prepare IDE
This section will present a short tutorial on how to make Eclipse ready for UbiNomad Library

1. Import an existing Android application
2. Select root directory
	- Google Play Services:  *[android-sdk]*/extras/google/google_play_services/libproject/google-play-services_lib
	- Other dependencies: *[UbiNomadLib]*

3. Select projects to import 
	- Google Play Services:  google-play-services_lib
	- Other dependencies: UbiNomadLib, Facebook SDK, and foursquare-oauth-library 

4. Make sure "Copy project into workspace" is **unchecked**
   ![Import project](https://github.com/vegaen/UbiNomadLib/raw/master/screenshots/ImportLibraries.png)

5. Press finish
5. Congratulation the IDE is ready

#### Errors
If *UbiNomadLib* is marked with a red x, it may be a build problem:

![Project Error](https://github.com/vegaen/UbiNomadLib/raw/master/screenshots/ProjectErrorX.png)

1. Right-click on the project and press *Properties* 
2. Go to *Android* tab
   ![Android Tab](https://github.com/vegaen/UbiNomadLib/raw/master/screenshots/AndroidTab.png)

3. If any library is marked with a red x, remove it and add it again.
4. There should be 3 libraries included marked with a green checkmark


## Implement in your application

To have clean setup for a UbiNomad application, this section will go through the steps to set it up.  

1. Import an existing Android application
2. Select root directory: *UbiNomadLib*/samples
3. Import *HelloUbiNomad*, and set a name for your application
4. **Check** the "Copy project into workspace" option
5. Press *Finish*
6. A project with your chosen name should be visible in the project explorer. It will also be marked with a red x
7. Right-click on the project and choose *Properties*
8. There should be a red x on the UbiNomadLib project. Remove the project and replace it with UbiNomadLib.
9. You should now be able to run the application.
10. (Optional) Change package name to your liking. If you change the package name, you will need to change it to the same in AndroidManifest.

11. (Optional) To be able to use Facebook, create an application on developers.facebook.com. Add the application id to *res/values/strings.xml*. Follow Facebooks getting started guide to be able to get key hashes

12. (Optional) To be able to use Foursquare, create an application on developers.foursquare.com. Add client_id and client_secret to the constructor when register an oauth key.

### Errors
If you get the error message "2 versions of support-library" it can be fixed by:

- Right-click the project and choose *Android Tools*->*Add Support Library...*

   ![Android Tab](https://github.com/vegaen/UbiNomadLib/raw/master/screenshots/AddSupport.png)


## Extend the library
There are two ways in which you are able to extend the library: You may add additional place providers, or you may add functionality. This section will describe how to start.

### Additional Place Provider

1. Extend *ExternalProvider*
2. Implement each method the provider supports. If it does not support a function return null
3. Extend *ProviderConnector*
4. Implement the methods in the extended ProviderConnector


### Add Functionality
1. Create a new method in the *ExternalProvider* interface
2. Implement the method in each existing Place Provider



## Errors
- 2 versions of support-library
- solution right-click -> android tools -> add library
