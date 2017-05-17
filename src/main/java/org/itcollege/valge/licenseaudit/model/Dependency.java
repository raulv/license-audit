package org.itcollege.valge.licenseaudit.model;

import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;

public class Dependency {
  
  public final String artifactId;
  public final String groupId;
  public final String version;
  public final String scope;
  public final License license;

  @SuppressWarnings("rawtypes")
  public Dependency(Artifact artifact, MavenProject artifactProject) {
    artifactId = artifact.getArtifactId();
    groupId = artifact.getGroupId();
    version = artifact.getVersion();
    scope = artifact.getScope();
    
    List licList = artifactProject.getLicenses();
    if (licList.size() == 0) {
      this.license = new License("Unknown");
    }
    else {
      // TODO: list all licenses if there's more than one
      this.license = new License((org.apache.maven.model.License) licList.get(0));
    }
  }

}
