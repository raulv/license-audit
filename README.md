# Maven plugin for auditing 3rd party licenses

The plugin was created as part of Raul Valge's bachelor's thesis for "Auditing System for 3rd Party Licenses in Software Projects".

### Installation
The plugin is currently not available from any repositories so it needs to be manually installed.
Download the source and run ***mvn install***.

The build also runs the license audit on itself. You can view the report in **target/license-audit/report.html**

### Using the plugin
Add the plugin to your project's pom.xml
```xml
...
<dependency>
    <groupId>org.itcollege.valge</groupId>
    <artifactId>license-audit-maven-plugin</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
...
```

Run ***mvn license-audit:run*** in your project to generate the report.

Open the report from **target/license-audit/report.html**

Use the UI provided in the report to approve, reject, merge and override licenses. To preserve your changes, click 'Download configuration' in the UI and download the **license-configuration.json** file into your project's root directory.
