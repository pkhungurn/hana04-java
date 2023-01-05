package hana04.distrib.app;

import hana04.base.serialize.FileDeserializer;
import hana04.base.serialize.binary.BinarySerializer;
import hana04.base.util.TypeUtil;
import hana04.distrib.request.Request;
import hana04.distrib.request.params.TypeNameToRequestParametersBuilderMap;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.nio.file.FileSystem;
import java.util.List;

public class Main extends ClientApp {
  private final static Logger logger = LoggerFactory.getLogger(Main.class);
  /**
   * User-specified data.
   */
  private String outputFileName = null;
  /**
   * Dependencies
   */
  private final FileDeserializer fileDeserializer;
  private final FileSystem fileSystem;

  @Inject
  public Main(
    BinarySerializer.Factory binarySerializerFactory,
    TypeNameToRequestParametersBuilderMap typeNameToRequestParameterBuilder,
    FileDeserializer fileDeserializer,
    FileSystem fileSystem) {
    super(binarySerializerFactory, typeNameToRequestParameterBuilder);
    this.fileDeserializer = fileDeserializer;
    this.fileSystem = fileSystem;
  }

  public String getOutputFileName() {
    return outputFileName;
  }

  @Override
  protected Options getOptions() {
    Options options = super.getOptions();

    options.addOption(Option.builder("o").hasArg().argName("fname").desc(
      "Write the output image to the file denoted by \"fname\""
    ).build());

    return options;
  }

  protected void processCommandLine(CommandLine cmd, Options options) {
    super.processCommandLine(cmd, options);

    // The output file name
    if (cmd.hasOption('o')) {
      outputFileName = cmd.getOptionValue('o');
    } else {
      outputFileName = null;
    }
  }

  protected void printHelp() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("java Main [options] <json-file>", getOptions());
  }

  @Override
  protected void processInputFiles(List<String> inputFiles) {
    String pathStr = inputFiles.get(0);
    String extension = FilenameUtils.getExtension(pathStr).toLowerCase();
    if (!extension.equals("json")) {
      logger.error("Fatal error: unknown file " + pathStr + ", expected an extension of type .json");
    }
    Object deserialized = fileDeserializer.deserialize(pathStr);
    Request request = TypeUtil.cast(deserialized, Request.class,
      "Fatal error: the content of file " + pathStr + " is not a Request.");
    Request.Runner runner = request.getExtension(Request.Runner.Vv.class).value();
    if (outputFileName == null) {
      outputFileName = runner.getDefaultOutputFileName(pathStr);
    }
    try {
      uploadRequestToRemoteServers(request);
      runner.runMain(this);
      removeRequestFromRemoteServers(request);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
