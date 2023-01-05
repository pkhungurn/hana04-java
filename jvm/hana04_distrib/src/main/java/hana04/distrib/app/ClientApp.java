package hana04.distrib.app;

import hana04.base.serialize.binary.BinarySerializer;
import hana04.distrib.request.params.RequestParametersBuilder;
import hana04.distrib.request.params.TypeNameToRequestParametersBuilderMap;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.util.List;

public abstract class ClientApp extends App {
  /**
   * The command line arguments to the program.
   */
  private String[] arguments;
  /**
   * The parameters to the request.
   */
  private String[] requestParameters;
  /**
   * A mapping from request type names to the {@link RequestParametersBuilder} of that request type.
   */
  private final TypeNameToRequestParametersBuilderMap typeNameToRequestParameterBuilder;

  ClientApp(
    BinarySerializer.Factory binarySerializerFactory,
    TypeNameToRequestParametersBuilderMap typeNameToRequestParameterBuilder) {
    super(binarySerializerFactory);
    this.typeNameToRequestParameterBuilder = typeNameToRequestParameterBuilder;
  }

  @Override
  protected Options getOptions() {
    Options options = super.getOptions();

    options.addOption(Option.builder("i").hasArg().argName("className").desc(
      "Class name of the class register to use."
    ).build());

    Option requestSpecificParameters = Option.builder("D").argName("parameter=value")
      .hasArgs()
      .valueSeparator()
      .desc("Define a request-specific parameter.")
      .build();
    options.addOption(requestSpecificParameters);

    return options;
  }

  protected void processCommandLine(CommandLine cmd, Options options) {
    super.processCommandLine(cmd, options);

    requestParameters = cmd.getOptionValues('D');
    if (requestParameters == null) {
      requestParameters = new String[0];
    }
  }

  public String[] getRequestParameters() {
    return requestParameters;
  }

  public String[] getArguments() {
    return arguments;
  }

  protected abstract void printHelp();

  private void printRequestParameters() {
    typeNameToRequestParameterBuilder.printRequestParameters();
  }

  public void run(String[] args) {
    arguments = args;
    CommandLineParser commandLineParser = new DefaultParser();
    CommandLine cmd = null;
    Options options = getOptions();
    try {
      cmd = commandLineParser.parse(options, args);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }

    processCommandLine(cmd, options);

    List<String> inputFileList = cmd.getArgList();
    if (cmd.hasOption('h')) {
      printHelp();
      System.exit(0);
    } else if (cmd.hasOption('H')) {
      if (cmd.getOptionValue('H') == null) {
        printRequestParameters();
      } else {
        typeNameToRequestParameterBuilder.printRequestParameter(cmd.getOptionValue('H'), false);
      }
      System.exit(0);
    } else if (inputFileList.size() == 0) {
      printHelp();
      System.exit(0);
    }

    processInputFiles(inputFileList);
  }

  protected abstract void processInputFiles(List<String> inputFileList);
}
