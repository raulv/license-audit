package org.itcollege.valge.licenseaudit.model;

import java.util.List;

import org.apache.maven.project.MavenProject;

public class Project {

  public final String name;
  public final String artifactId;
  public final String groupId;
  public final String version;
  public final List<Scope> scopes;

  public Project(MavenProject project, List<Scope> scopes) {
    this.name = project.getName();
    this.artifactId = project.getArtifactId();
    this.groupId = project.getGroupId();
    this.version = project.getVersion();
    this.scopes = scopes;
  }

}
