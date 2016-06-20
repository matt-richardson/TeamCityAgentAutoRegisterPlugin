# TeamCity Agent Auto Authorise Plugin

A plugin for TeamCity to allow automatic agent authorization based on a token configured in agent's buildAgent.properties file.

Heavily based on the original [JetBrains agent magic authorise plugin] (https://confluence.jetbrains.com/display/TW/Agent+Custom+Token+Authorize+Plugin), which was originally based on [this feature request (TW-33377)](http://youtrack.jetbrains.com/issue/TW-33377)

## Status
Basic implementation completed. Original plugin noted that it can be unstable in non-basic use, though this has not been observed in the (basic) use that this plugin been put to so far.

## Usage
1. Set [internal property](https://confluence.jetbrains.com/display/TCD8/Configuring+TeamCity+Server+Startup+Properties#ConfiguringTeamCityServerStartupProperties-TeamCityinternalproperties) teamcity.agentAutoAuthorize.authorizationToken to a random token
For example:
teamcity.agentAutoAuthorize.authorizationToken=70d44d1e5007dd6b

2. On unauthorized agent, add "teamcity.agentAutoAuthorize.authorizationToken" configuration parameter into buildAgent.propertes file with the value of the token
For example:
teamcity.agentAutoAuthorize.authorizationToken=70d44d1e5007dd6b

3. Once connected, the agent is authorized (with a comment noting that it was authorised by this plugin).
Any number of agents can connect with the same token.

## Known Issues
* If an authorized agent is unauthorized, it will re-authorize when/if it disconnects and reconnects.
* authorizationToken is visible in agent properties, and therefore caution should be taken in using this plugin in an environment where not all users are fully trusted.
* as currently implemented (Jan 2015), this plugin works the same was as configuring the original plugin with `agent.authorize.tokens=unlimitedToken:70d44d1e5007dd6b:99999:0`. (This discovery was subsequent to the creation of this plugin, as the readme for the original plugin specifically says that a plugin can only be used once, however the implementation differs from that.) I am seriously considering removing all token references from this plugin, and having a basic plugin that will authorise any agent that connects.

 
## Download
Please see [releases](https://github.com/matt-richardson/TeamCityAgentAutoRegisterPlugin/releases).

## TeamCity Versions Compatibility
Compatible with TeamCity 8.0 and later.

## License

This is released under the Apache Licence Version 2, as was the original plugin this was based off. This derivitive work contains modifications by Matt Richardson.

## Installation
Either:

* Copy the plugin zip into the `TeamCity Data Directory`/plugins directory and restart the server, or
* Upload the plugin from  `Administration->Server Administration->Diagnostics->Browse Data Directory`

## Feedback
Everybody is encouraged to try the plugin and provide feedback in the forum or post bugs into the issue tracker.
Please make sure to note the plugin version that you use.


## Building the project

Pre-requisites:

* JDK 1.6
* IntelliJ IDEA (12+, Ultimate edition recommended)
* Ant 1.7+
* TeamCity 7.1+ .tar.gz distribution

The project is compatible with IntelliJ IDEA 12+ and TeamCity 8.0+
Sequence of IDEA setup:

- configure Path Variable "TeamCityDistribution" in IDEA Settings/Project Variables to point to the unpacked TeamCity .tar.gz distribution. Use the minimum version the plugin should be compatible with.
- configure Path Variable "TeamCityDataDirectory" in IDEA Settings/Project Variables to point to the data directory to be used.
- open the project (please make sure to configure path variables before first project opening)
- configure Project SDK the in "Project Structure"/Project to point to JDK 1.6 or greater (minimum JDK version applicable for TeamCity server and agent is recommended)

### To build with IntelliJ IDEA Ultimate:
- configure Application server in "Run TeamCity server with plugin" run configuration to point to the TeamCity distribution
- To run the TeamCity server with the plugin deployed from IDEA run "Run TeamCity server with plugin" run configuration

### To build with Ant:
- edit build.properties to configure path TeamCity Data Directory
- edit build.properties to configure path to TeamCity distribution and path to TeamCity Data Directory
- after any project structure changes (e.g. deleting or adding a module) make sure you invoke "Generate Ant Build" in IDEA with the only options selected: single file, Enable UI forms
