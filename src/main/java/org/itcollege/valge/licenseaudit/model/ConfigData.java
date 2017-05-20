package org.itcollege.valge.licenseaudit.model;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.inject.internal.util.Lists;

public class ConfigData {

  public List<String> approved = Lists.newArrayList();
  public List<String> rejected = Lists.newArrayList();
  public Map<String, String> alias = Maps.newHashMap();
  public Map<String, License> licenseOverride = Maps.newHashMap();

}
