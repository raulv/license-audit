package org.itcollege.valge.licenseaudit.model;

public class Scope {

  public final String name;
  public final boolean isBundled;
  public int dependencyCount = 0;

  public Scope(String name, boolean isBundled) {
    this.name = name;
    this.isBundled = isBundled;
  }

}
