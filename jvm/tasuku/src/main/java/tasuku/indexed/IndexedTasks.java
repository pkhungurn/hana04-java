package tasuku.indexed;

public interface IndexedTasks extends PrefixedTasks {
  int[] getShape();
  int getArity();
}
