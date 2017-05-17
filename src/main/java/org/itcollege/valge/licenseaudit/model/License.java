package org.itcollege.valge.licenseaudit.model;

public class License {

  public final String url;
  public final String name;

  public License(org.apache.maven.model.License lic) {
    this.name = lic.getName();
    this.url = lic.getUrl();
  }

  public License(String name) {
    this.name = name;
    this.url = "";
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
