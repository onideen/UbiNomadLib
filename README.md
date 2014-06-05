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
If *UbiNomadLib* has a red exclamation mark, it may be a build problem:
1. Right-click on the project and press *Properties* 
2. Go to *Android* tab
3. If any library is marked with a red cross, remove it and add it again.
4. There should be 3 libraries included marked with a green v


## Implement in your application

[Work in progress]


## Extend the library

[WOrk in progress]

## Errors
- 2 versions of support-library
- solution right-click -> android tools -> add library
