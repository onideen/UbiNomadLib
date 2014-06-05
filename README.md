UbiNomad Library
================

Middelware for location sharing on social networks



## Getting started

### Install Google Play Services

1. Open Android SDK Manager
2. Install *Extras* -> *Google Play Services*


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
5. Press finish
5. Congratulation the IDE is ready

#### Errors
If *UbiNomadLib* has a red x, it may be a build problem:
1. Right-click on the project and press *Properties* 
2. Go to *Android* tab
3. If any library is marked with a red x, remove it and add it again.
4. There should be 3 libraries included marked with a green checkmark


## Implement in your application

To have nice setup for a UbiNomad application, this section will go through the steps to set it up.  

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



## Extend the library





## Errors
- 2 versions of support-library
- solution right-click -> android tools -> add library
