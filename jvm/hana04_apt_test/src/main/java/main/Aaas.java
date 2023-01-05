package main;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.AbstractVersionedSubject;

import javax.inject.Inject;
import java.nio.file.FileSystem;

public final class Aaas {
  private Aaas() {
    // NO-OP
  }

  @HanaDeclareBuilder(Aaa.class)
  public static class AaaBuilder extends Aaa__Impl__Builder<AaaBuilder> {
    @Inject
    public AaaBuilder(Aaa__ImplFactory factory) {
      super(factory);
    }
  }

  public static class AaaExtension01 extends AbstractVersionedSubject implements Extension01 {
    @HanaDeclareExtension(extensionClass = Extension01.class, extensibleClass = Aaa.class)
    public AaaExtension01(Aaa aaa, FileSystem fileSystem) {
      // NO-OP
    }

    @Override
    protected long updateInternal() {
      return versionManager.getVersion() + 1;
    }

    @Override
    public void sayHello() {
      System.out.println("Hello!");
    }
  }
}
