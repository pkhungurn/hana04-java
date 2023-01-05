package hana04.distrib.request.params;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.inferred.freebuilder.FreeBuilder;

import javax.inject.Inject;

@FreeBuilder
public interface TimingInfoParameters {
  /**
   * Whether to write the timing info to a file.
   */
  boolean writingTimingInfo();

  class Builder extends TimingInfoParameters_Builder implements RequestParametersBuilder {
    @Inject
    public Builder() {
      writingTimingInfo(false);
    }

    @Override
    public Builder parse(String[] args) {
      for (int i = 0; i < args.length / 2; i++) {
        String paramName = args[2 * i];
        String value = args[2 * i + 1];
        if (paramName.equals("t")) {
          writingTimingInfo(Boolean.valueOf(value));
        }
      }
      return this;
    }

    @Override
    public void displayHelp(String className) {
      Options options = new Options();

      options.addOption(Option.builder("Dt")
              .valueSeparator()
              .hasArg()
              .argName("true/false")
              .desc("Whether to write the timing info into a file.")
              .build());

      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(className, options);
    }
  }
}
