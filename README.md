# Wall-T

Wall-T is a build radiator to monitor build status for [Teamcity Jetbrains](https://www.jetbrains.com/teamcity/) continuous integration server.

Feel free to send any issue or suggestion by opening a report on the Issue tab.

## Overview

![Overview](https://lh3.googleusercontent.com/0qznI3iLbYAniiQu3D9g-d1N_jog4S30XNoPocrRAjeaXQ7vJsp5a3v22Anvdj_R5guk7foF0NPJqmJU0whxz7nGd5KiKflPRKINMC2mMX58awR14VN1OgaXTfWkSch6F90bVwcLP1RHAPV08eHb55AjJS-Wmbwb0-JamviH1pSnCJ_QmWgGEpN6FY3ko4ZV4_g_RxBannvegLDr9R_8wRoC8YWKk38cKGhtFMlo9l4AKrHGFPhiR9N162zi51RKmOgu11Jcfgy1aDcRT5UOAum4DuZEnPCB6WcXQ4QPsof-V5trU_IwQwHCXfQ9p3r6YkJ0IQakg51TU2umxoBV6_oFXnStf-SBm4HiSd9ttlZSkwEo-mpy8j-icz0V1ymRA8mnqbXzSFaSIamW4xvoEw_bgMwEwo3IFsyz7iy_Uvk6cpExkjKuOom0q0pUVP38RSrd6_8hSAczychjfCf0jUbg129Ft57ZuMg59N_149t9_5AaEeZvk5QbxCGsKsiJCHxsHjfPkfGqF53yR_BPcolscAkqKY7sAary-l4XP4qFfSfV9qaJSKwE9txCGPbFBGP6uZPz=w1366-h613)

## Quick Start

1. [Download the latest zip archive](https://github.com/u2032/wall-t/releases/)
1. Unzip the archive
1. Edit the included start script or define JAVA8_HOME environment variable to a JRE 8+
1. Launch the start script
1. Enjoy !

––––––––––––––––––––––––––––––––––––

## Details

### Requirements
Wall-T is written using JavaFX and Java 8. You need a Java Runtime Environment version 8 or higher.

You can donwload [Java 8+ on this link](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html).

### Server Configuration
When the application starts, a configuration view is available to configure or customize the radiator view (we also talk about wall view).

First, you must configure the teamcity server url and credentials. You can use use the guest mode by keeping empty the username or setting it at the value 'guest'. Note that the guest mode let you connect to the server only if the server authorize the guest mode. You can also use a specific user login to connect to the server. Only HTTP and HTTPS protocol are currently supported: the server url must start with either of them.

Moreover, the application also supports the use of a http proxy that you can configure.

Finally, you should set the api version that you want to use, according to your teamcity server version. You can not use an API version greater than your server version, but you can use a API version less than the server version if it is still supported.

If the connection is impossible, please check the configuration. The error message and the log file can help you what the issue is.

![Configuration view](https://lh3.googleusercontent.com/u/0/d/0B5cKQDFfHPOmSndrSkU0NWhSRTg=s1600-k-iv1)

### View Preferences
One connected, the list of all projects and build types are displayed. You can select the ones you wish display on the wall view. By default, the name displayed on the wall view is the name defined on the server for the build type or project, but you can also for each of them choose an alias name to better the display.

You can configure the maximum number of tiles on the wall view, by column and by row. If you have selected more items that the wall can displayed in one screen according to this setting, the wall view will use several screens by switching from one to another alternately.

You can reorder selected items on the configuration view. Please note that right now, you can not mix build types and projects: projects are always displayed at the end of wall view.

### Tile of a Build Type
For each selected build type, the alias name (or build type name if alias is not set) is displayed on a colored background. The color of the background represents the status of the project: grey when no data, green when last build is successful, red when last build is failure.

When a build is running for a build type, the tile is animated and a progress bar appears in the backgound.

At the right, a context part displays some information: the finish date of the last build if there is no running build or the time left for the current running build. A weather icon is also present: a sun if the 3 last builds are successful, cloudy sun if one of them, cloud if two of them, and rain if all of them.

![Tile Successful](https://lh3.googleusercontent.com/u/0/d/0B5cKQDFfHPOmZERfOGtXS2F3b00=s1600-k-iv1)
![Tile Running](https://lh3.googleusercontent.com/u/0/d/0B5cKQDFfHPOmUm10TUc2TEJmbzA=s1600-k-iv1)
![Tile Failure](https://lh3.googleusercontent.com/u/0/d/0B5cKQDFfHPOmMUZsM2tDc1NBMFk=s1600-k-iv1)

### Tile of a Project
Project are rendered in a different way: you have the name, and the number of success and failure of build types included in this project.

Please keep in mind that selecting a project implies to monitor indirectly many other build types (all the children) and may increase significantly the number of requests to the server.

![Tile Project](https://lh3.googleusercontent.com/u/0/d/0B5cKQDFfHPOmTDZUMkRkNmxMR2c=s1600-k-iv1)

### Light mode
If you prefer less information on wall view, you can opt for the light mode, which removes all context part on tiles and keep only the name and background status.

![Light Mode](https://lh3.googleusercontent.com/u/0/d/0B5cKQDFfHPOmZXR1LWY1VlRBcjQ=s1600-k-iv1)

### Tips
Press F11 to switch to fullscreen mode.

Press ESC to switch to configuration view.

### Api notes
Application currently supports TeamCity API version from 6 to 8.1 (which are all included in the last version of TeamCity)  

#### 6.0 -> 8.0
* Support of build types
* Support of projects, but project status only reflects status of the direct children
#### 8.1
* Support of queued status: an icon appears when a build is in queue
* Project status reflects now status of all child build types (ie also build type included in a subchild project)

### Known "Issues"
* API 9.1 adds support of build which failed to start **(not used by this project yet)**
* API 10.0 adds support of hanging build **(not used by this project yet)**
* Right now, application doesn't support branch feature of teamcity server.
* When modification is applied to server side (addind new projet, new build configuration, or deleting one, etc.), the radiator ignores it. You can press "Connect to server" button on configuration view to refresh data.

## Downloads
Go to the [Download Page](https://github.com/u2032/wall-t/releases/) 

