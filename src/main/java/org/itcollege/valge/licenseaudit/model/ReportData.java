package org.itcollege.valge.licenseaudit.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class ReportData {

  public final Project project;
  public final String timestamp;
  public final Set<License> licenses;

  public ReportData(Project project, Set<License> licenses) {
    this.project = project;
    this.licenses = licenses;
    this.timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
  }

}
