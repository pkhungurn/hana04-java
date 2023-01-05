package hana04.distrib.request.params;

import javax.inject.Inject;

public class NoParameters {
  public static class Builder implements RequestParametersBuilder {
    @Inject
    public Builder() {
    }

    @Override
    public Builder parse(String[] args) {
      return this;
    }

    @Override
    public void displayHelp(String className) {
      System.out.println("usage: " + className);
      System.out.println(" This request does not have any option.");
    }
  }
}
