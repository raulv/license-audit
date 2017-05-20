package org.itcollege.valge.licenseaudit.model;

import java.util.List;

import com.google.inject.internal.util.Lists;

public class ConfigData {

  public List<String> approved = Lists.newArrayList();
  public List<String> rejected = Lists.newArrayList();

}
