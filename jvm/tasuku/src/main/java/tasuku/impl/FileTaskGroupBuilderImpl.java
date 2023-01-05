package tasuku.impl;

import tasuku.FileTaskGroupBuilder;
import tasuku.TaskUtil;
import tasuku.Workspace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileTaskGroupBuilderImpl implements FileTaskGroupBuilder {
  private final Workspace ws;
  private final String resolvedPrefix;
  private final HashMap<String, ArrayList<String>> fileTaskGroups = new HashMap<>();

  public FileTaskGroupBuilderImpl(Workspace ws, String prefix) {
    this.ws = ws;
    this.resolvedPrefix = prefix;
  }

  @Override
  public void add(String fileTaskName, String group) {
    if (!fileTaskGroups.containsKey(group)) {
      fileTaskGroups.put(group, new ArrayList<>());
    }
    fileTaskGroups.get(group).add(fileTaskName);
  }

  @Override
  public void add(String fileTaskName, String... group) {
    for (String aGroup : group) {
      add(fileTaskName, aGroup);
    }
  }

  @Override
  public void add(String fileTaskName, List<String> group) {
    for (String aGroup : group) {
      add(fileTaskName, aGroup);
    }
  }

  @Override
  public String getResolvedPrefix() {
    return resolvedPrefix;
  }

  public void build() {
    for (Map.Entry<String, ArrayList<String>> entry : fileTaskGroups.entrySet()) {
      String name = entry.getKey();
      ArrayList<String> taskNames = entry.getValue();
      if (taskNames.size() > 0) {
        ws.newCommandTask(resolvedPrefix + "/" + name, taskNames, null);
        TaskUtil.createDeleteAllTask(ws, resolvedPrefix + "/" + name + "_clean", taskNames);
      }
    }
  }
}
