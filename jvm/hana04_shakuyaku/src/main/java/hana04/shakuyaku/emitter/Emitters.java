package hana04.shakuyaku.emitter;

public class Emitters {
  private Emitters() {
    // NO-OP
  }

  public interface EmitterInfo {
    EmitterType getType();
  }
}
