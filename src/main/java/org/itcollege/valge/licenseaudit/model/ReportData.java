package org.itcollege.valge.licenseaudit.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class ReportData {

  public final Project project;
  public final String timestamp;
  public final List<License> licenses;
  public final Set<String> bundledScopes;

  public ReportData(Project project, List<License> licenses, Set<String> bundledScopes) {
    this.project = project;
    this.licenses = licenses;
    this.bundledScopes = bundledScopes;
    this.timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
  }

}
