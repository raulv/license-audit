package org.itcollege.valge.licenseaudit.model;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Dependency {
  
  public final String artifactId;
  public final String groupId;
  public final String version;
  public final String scope;
  
  @JsonIgnore
  public final MavenProject mavenProject;

  public Dependency(Artifact artifact, MavenProject mavenProject) {
    artifactId = artifact.getArtifactId();
    groupId = artifact.getGroupId();
    version = artifact.getVersion();
    scope = artifact.getScope();
    this.mavenProject = mavenProject;
  }

}
