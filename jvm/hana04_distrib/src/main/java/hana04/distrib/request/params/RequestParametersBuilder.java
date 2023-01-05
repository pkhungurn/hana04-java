package hana04.distrib.request.params;

public interface RequestParametersBuilder {
  RequestParametersBuilder parse(String[] args);
  void displayHelp(String className);
}
