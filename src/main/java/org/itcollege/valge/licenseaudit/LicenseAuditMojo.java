package org.itcollege.valge.licenseaudit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.itcollege.valge.licenseaudit.model.Scope;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.inject.internal.util.Maps;

@Mojo(name = "run", requiresDependencyResolution = ResolutionScope.TEST)
public class LicenseAuditMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  MavenProject project;

  @Parameter(property = "localRepository", required = true, readonly = true)
  private ArtifactRepository localRepository;

  @Parameter(property = "project.remoteArtifactRepositories", required = true, readonly = true)
  private List<ArtifactRepository> remoteRepositories;

  @Component
  private MavenProjectBuilder mavenProjectBuilder;

  // TODO: Make the bundled scopes configurable
  private Set<String> bundledScopes = Sets.newHashSet("compile");

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      List<Dependency> allDeps = this.loadAllDependencies(project, localRepository, remoteRepositories, mavenProjectBuilder);
      List<Scope> scopes = this.getScopesInfo(allDeps);
      List<Dependency> bundledDeps = this.filterScopes(allDeps);

      ConfigData config = loadConfiguration();
      List<License> licenses = this.groupByLicenses(bundledDeps, config);

      for (License lic: licenses) {
        if (config.approved.contains(lic.name)) {
          lic.status = "approved";
        }
        else if (config.rejected.contains(lic.name)) {
          lic.status = "rejected";
        }
      }
      
      String content = getHtmlContent(
          new ReportData(new Project(this.project, scopes), licenses, bundledScopes),
          config);

      Files.write(content, new File(getOutputDir(), "report.html"), Charset.defaultCharset());
    }
    catch (Exception e) {
      throw new MojoExecutionException("License audit failed", e);
    }
  }

  private ConfigData loadConfiguration() throws MojoExecutionException {
    File configFile = new File(project.getBasedir(), "license-configuration.json");
    if (configFile.exists()) {
      try {
        return new ObjectMapper().readValue(configFile, ConfigData.class);
      }
      catch (IOException e) {
        throw new MojoExecutionException("Failed to read license-audit.json - the file seems to be corrupted", e);
      }
    }

    return new ConfigData();
  }

  private List<Dependency> filterScopes(List<Dependency> dependencies) {
    List<Dependency> result = Lists.newArrayList();
    for (Dependency dep : dependencies) {
      if (dep.scope != null && bundledScopes.contains(dep.scope.toLowerCase())) {
        result.add(dep);
      }
    }
    return result;
  }

  private List<Scope> getScopesInfo(List<Dependency> dependencies) {
    Map<String, Scope> scopes = Maps.newHashMap();

    for (Dependency dep : dependencies) {
      if (dep.scope == null) {
        continue;
      }
      String scopeName = dep.scope.toLowerCase();

      Scope sc = null;
      if (scopes.containsKey(scopeName)) {
        sc = scopes.get(scopeName);
      }
      else {
        sc = new Scope(scopeName, bundledScopes.contains(scopeName));
        scopes.put(scopeName, sc);
      }

      sc.dependencyCount++;
    }

    return Lists.newArrayList(scopes.values());
  }

  @SuppressWarnings("rawtypes")
  private List<License> groupByLicenses(List<Dependency> deps, ConfigData config) {
    Map<String, License> result = Maps.newHashMap();

    for (Dependency dep : deps) {
      List licenses = dep.mavenProject.getLicenses();
      License lic = null;
      if (licenses == null || licenses.size() == 0) {
        
        lic = config.licenseOverride.get(dep.groupId + ":" + dep.artifactId);
        if (lic == null) {
          lic = License.UNKNOWN;
        }
        
      }
      else {
        // TODO: handle dependencies with multiple licenses
        // Currently only the first entry is used
        org.apache.maven.model.License mavenLic = (org.apache.maven.model.License) dep.mavenProject.getLicenses().get(0);
        
        lic = new License(mavenLic);
      }

      String name = lic.name.toLowerCase();

      if (result.containsKey(name)) {
        lic = result.get(name);
      }
      else {
        result.put(name, lic);
      }

      lic.dependencies.add(dep);
    }
    
    for (Entry<String, String> entry: config.alias.entrySet()) {
      String from = entry.getKey().toLowerCase();
      String to = entry.getValue().toLowerCase();
      if (result.containsKey(from) && result.containsKey(to)) {
        License lic = result.remove(from);
        result.get(to).dependencies.addAll(lic.dependencies);
      }
    }

    return Lists.newArrayList(result.values());
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
    dir.mkdirs();
    return dir;
  }

  @SuppressWarnings("unchecked")
  public List<Dependency> loadAllDependencies(MavenProject project, ArtifactRepository localRepository,
      List<ArtifactRepository> remoteRepositories, MavenProjectBuilder mavenProjectBuilder) throws MojoExecutionException {

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
        throw new MojoExecutionException("Failed to load dependencies", e);
      }
    }

    return deps;
  }
}
