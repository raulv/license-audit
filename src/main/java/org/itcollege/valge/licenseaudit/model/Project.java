package org.itcollege.valge.licenseaudit.model;

import java.util.List;

import org.apache.maven.project.MavenProject;

public class Project {

  public final String name;
  public final String artifactId;
  public final String groupId;
  public final String version;
  public final List<Dependency> dependencies;

  public Project(MavenProject project, List<Dependency> deps) {
    name = project.getName();
    artifactId = project.getArtifactId();
    groupId = project.getGroupId();
    version = project.getVersion();

    dependencies = deps;
  }

}
