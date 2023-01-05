package hana04.distrib.request.params;

import com.google.common.base.Preconditions;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Singleton
public class TypeNameToRequestParametersBuilderMap {
  private final Map<String, RequestParametersBuilder> typeNameToRequestParametersBuilder;

  @Inject
  public TypeNameToRequestParametersBuilderMap(
    Map<String, RequestParametersBuilder> typeNameToRequestParametersBuilder) {
    this.typeNameToRequestParametersBuilder = typeNameToRequestParametersBuilder;
  }

  public RequestParametersBuilder get(String typeName) {
    return Preconditions.checkNotNull(typeNameToRequestParametersBuilder.get(typeName),
      "RequestParametersBuilder for " + typeName + " does not exist.");
  }

  public boolean containsKey(String typeName) {
    return typeNameToRequestParametersBuilder.containsKey(typeName);
  }

  public void printRequestParameter(String className, boolean skipIfNotRequest) {
    if (typeNameToRequestParametersBuilder.containsKey(className)) {
      typeNameToRequestParametersBuilder.get(className).displayHelp(className);
      System.out.println();
    } else {
      if (!skipIfNotRequest) {
        System.out.println("Class with name \"" + className + "\" has no associated command-line " +
          "request parameters.");
      }
    }
  }

  public void printRequestParameters() {
    Set<String> classNames = typeNameToRequestParametersBuilder.keySet();
    ArrayList<String> classNameList = new ArrayList<>(classNames);
    Collections.sort(classNameList);
    for (String className : classNameList) {
      printRequestParameter(className, true);
    }
  }

}
