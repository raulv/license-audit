package org.itcollege.valge.licenseaudit.model;

import java.util.List;

import com.google.inject.internal.util.Lists;

public class License {

  public static final License UNKNOWN = new License();
  
  public final String url;
  public final String name;
  public final List<Dependency> dependencies;
  public final Boolean isUnknown;

  public License(org.apache.maven.model.License lic) {
    this.name = lic.getName();
    this.url = lic.getUrl();
    this.dependencies = Lists.newArrayList();
    this.isUnknown = null;
  }

  private License() {
    this.name = "Unknown license";
    this.url = null;
    this.isUnknown = true;
    this.dependencies = Lists.newArrayList();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    License other = (License) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    }
    else if (!name.equals(other.name))
      return false;
    return true;
  }

}
