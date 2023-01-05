package hana04.botan;

import hana04.botan.glasset.program.FileProgramAsset;
import hana04.botan.glasset.program.ResourceProgramAsset;

/**
 * This project's type IDs start with 60000.
 */
public final class TypeIds {
  /**
   * {@link FileProgramAsset}
   */
  public static final int TYPE_ID_FILE_PROGRAM_ASSET = 60001;

  /**
   * {@link ResourceProgramAsset}
   */
  public static final int TYPE_ID_RESOURCE_PROGRAM_ASSET = 60002;

  private TypeIds() {
    // NO-OP
  }
}
