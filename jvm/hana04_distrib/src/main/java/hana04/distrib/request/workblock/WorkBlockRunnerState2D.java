package hana04.distrib.request.workblock;

import java.util.Iterator;
import java.util.UUID;

public abstract class WorkBlockRunnerState2D<T extends WorkBlock2D> extends WorkBlockRunnerState<T> {
  public WorkBlockRunnerState2D(UUID requestUuid) {
    super(requestUuid);
  }

  public abstract T createBlock(int offsetX, int offsetY, int sizeX, int sizeY);

  @Override
  public String getKey(T block) {
    return String.format("%d,%d,%d,%d",
            block.getOffsetX(), block.getOffsetY(), block.getSizeX(), block.getSizeY());
  }

  @Override
  public T createBlockFromKey(String key) {
    String[] comps = key.split(",");
    int offsetX = Integer.valueOf(comps[0]);
    int offsetY = Integer.valueOf(comps[1]);
    int sizeX = Integer.valueOf(comps[2]);
    int sizeY = Integer.valueOf(comps[3]);
    return createBlock(offsetX, offsetY, sizeX, sizeY);
  }

  public static <T extends WorkBlock2D> int generateAndDepositBlocks(
          WorkBlockRunnerState2D<T> state,
          Iterator<Block2DSpecification> blockGenerator) {
    int count = 0;
    while (blockGenerator.hasNext()) {
      Block2DSpecification spec = blockGenerator.next();
      state.requestedBlocks.deposit(state.createBlock(spec.offsetX(), spec.offsetY(), spec.sizeX(), spec.sizeY()));
      count++;
    }
    return count;
  }
}
