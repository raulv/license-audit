package org.itcollege.valge.licenseaudit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.itcollege.valge.licenseaudit.model.ConfigData;
import org.itcollege.valge.licenseaudit.model.Dependency;
import org.itcollege.valge.licenseaudit.model.License;
import org.itcollege.valge.licenseaudit.model.Project;
import org.itcollege.valge.licenseaudit.model.ReportData;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;

@Mojo(name = "run", requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM)
public class LicenseAuditMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  MavenProject project;

  @Parameter(property = "localRepository", required = true, readonly = true)
  private ArtifactRepository localRepository;

  @Parameter(property = "project.remoteArtifactRepositories", required = true, readonly = true)
  private List<ArtifactRepository> remoteRepositories;

  @Component
  private MavenProjectBuilder mavenProjectBuilder;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    List<Dependency> deps = this.loadDependencies(project, localRepository, remoteRepositories, mavenProjectBuilder);
    Set<License> uniqueLicenses = this.findUniqueLicenses(deps);
    try {
      ConfigData config = new ConfigData();

      String content = getHtmlContent(
          new ReportData(new Project(this.project, deps), uniqueLicenses),
          config);
      
      Files.write(content, new File(getOutputDir(), "report.html"), Charset.defaultCharset());
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private Set<License> findUniqueLicenses(List<Dependency> dependencies) {
    Set<License> lics = Sets.newHashSet();
    for (Dependency dep: dependencies) {
      lics.add(dep.license);
    }
    return lics;
  }

  String getHtmlContent(ReportData reportData, ConfigData configData) throws IOException, JsonProcessingException {
    InputStream in = getClass().getResourceAsStream("/report.html");
    String templateContent = CharStreams.toString(new InputStreamReader(in));
    
    ObjectMapper mapper = new ObjectMapper();
    return templateContent
        .replace("<REPORT_DATA>", mapper.writeValueAsString(reportData))
        .replace("<CONFIG_DATA>", mapper.writeValueAsString(configData));
  }

  private File getOutputDir() throws IOException {
    File dir = new File(project.getBuild().getDirectory(), "license-audit");
    if (!dir.exists()) {
      dir.mkdir();
    }
    return dir;
  }

  @SuppressWarnings("unchecked")
  public List<Dependency> loadDependencies(MavenProject project, ArtifactRepository localRepository,
      List<ArtifactRepository> remoteRepositories, MavenProjectBuilder mavenProjectBuilder) {

    Set<Artifact> artifacts = project.getArtifacts();

    List<Dependency> deps = Lists.newArrayList();
    for (Artifact artifact : artifacts) {
      try {
        MavenProject depMavenProject = mavenProjectBuilder.buildFromRepository(artifact, remoteRepositories,
            localRepository, true);
        depMavenProject.getArtifact().setScope(artifact.getScope());
        deps.add(new Dependency(artifact, depMavenProject));
      }
      catch (ProjectBuildingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    return deps;
  }
}
