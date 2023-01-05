package hana04.distrib.request.workblock;

import java.util.UUID;

public abstract class WorkBlockRunnerState1D<T extends WorkBlock1D> extends WorkBlockRunnerState<T> {
  protected final int blockSize = -1;

  public WorkBlockRunnerState1D(UUID requestUuid) {
    super(requestUuid);
  }

  public abstract T createBlock(int start, int end);

  @Override
  public String getKey(T block) {
    return String.format("%d,%d", block.getStartIndex(), block.getEndIndex());
  }

  @Override
  public T createBlockFromKey(String key) {
    String[] comps = key.split(",");
    int start = Integer.valueOf(comps[0]);
    int end = Integer.valueOf(comps[1]);
    return createBlock(start, end);
  }

}
