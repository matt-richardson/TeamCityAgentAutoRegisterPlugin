A plugin for TeamCity to allow automatic agent authorization based on a token configured in agent's buildAgent.properties file.
Related feature request: http://youtrack.jetbrains.com/issue/TW-33377


-- How to use and build the project --

Pre-requisites:
- JDK 1.6
- IntelliJ IDEA (12+, Ultimate edition recommended)
- Ant 1.7+
- TeamCity 7.1+ .tar.gz distribution

The project is compatible with IntelliJ IDEA 12+ and TeamCity 8.0+
Sequence of IDEA setup:
- configure Path Variable "TeamCityDistribution" in IDEA Settings/Project Variables to point to the unpacked TeamCity .tar.gz distribution. Use the minimum version the plugin should be compatible with.
- configure Path Variable "TeamCityDataDirectory" in IDEA Settings/Project Variables to point to the data directory to be used.
- open the project (please make sure to configure path variables before first project opening)
- configure Project SDK the in "Project Structure"/Project to point to JDK 1.6 or greater (minimum JDK version applicable for TeamCity server and agent is recommended)
- (IntelliJ IDEA Ultimate only) configure Application server in "Run TeamCity server with plugin" run configuration to point to the TeamCity distribution

- (optional) consider changing code style to have 2 spaces tabs and indents

(IntelliJ IDEA Ultimate only) To run the TeamCity server with the plugin deployed from IDEA run "Run TeamCity server with plugin" run configuration

To build with Ant:
- edit build.properties to configure path TeamCity Data Directory
- edit build.properties to configure path to TeamCity distribution and path to TeamCity Data Directory
- after any project structure changes (e.g. deleting or adding a module) make sure you invoke "Generate Ant Build" in IDEA with the only options selected: single file, Enable UI forms
