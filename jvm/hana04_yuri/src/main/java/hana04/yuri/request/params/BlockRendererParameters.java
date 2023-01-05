package hana04.yuri.request.params;

import hana04.distrib.request.params.BlockSizeParameters;
import hana04.distrib.request.params.RequestParametersBuilder;
import hana04.distrib.request.params.SaveInterval;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.inferred.freebuilder.FreeBuilder;

import javax.inject.Inject;

@FreeBuilder
public interface BlockRendererParameters {
  /**
   * Whether to write the timing info to a file.
   */
  boolean writingTimingInfo();

  /**
   * Whether to show rendering progress window.
   */
  boolean showingRenderingProgressWindow();

  /**
   * Block size
   */
  int blockSize();

  /**
   * Regular interval for saving partial results. (default: empty = only save at the end)
   */
  SaveInterval saveInterval();

  class Builder extends BlockRendererParameters_Builder implements RequestParametersBuilder {
    @Inject
    public Builder() {
      writingTimingInfo(false);
      showingRenderingProgressWindow(false);
      blockSize(BlockSizeParameters.DEFAULT_BLOCK_SIZE);
      saveInterval(SaveInterval.atTheEnd());
    }

    @Override
    public Builder parse(String[] args) {
      for (int i = 0; i < args.length / 2; i++) {
        String paramName = args[2 * i];
        String value = args[2 * i + 1];
        switch (paramName) {
          case "t":
            writingTimingInfo(Boolean.valueOf(value));
            break;
          case "g":
            showingRenderingProgressWindow(Boolean.valueOf(value));
            break;
          case "b":
            int blockSize_ = Integer.valueOf(value);
            if (blockSize_ <= 0) {
              throw new RuntimeException("Invalid rendering block size: " + blockSize_);
            }
            blockSize(blockSize_);
            break;
          case "r":
            int intervalInSeconds = Integer.valueOf(value);
            if (intervalInSeconds <= 0) {
              saveInterval(SaveInterval.atTheEnd());
            } else {
              saveInterval(SaveInterval.every(intervalInSeconds));
            }
            break;
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

      options.addOption(Option.builder("Dg")
              .valueSeparator()
              .hasArg()
              .argName("true/false")
              .desc("Whether to show the rendering progress window.")
              .build());

      options.addOption(Option.builder("Db")
              .valueSeparator()
              .hasArg()
              .argName("res")
              .desc("Specify the block resolution used to split the request into parallel workloads.")
              .build());

      options.addOption(Option.builder("Dr")
              .valueSeparator()
              .hasArg()
              .argName("sec")
              .desc("Write (partial) output images every 'sec' seconds.")
              .build());

      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(className, options);
    }
  }
}
