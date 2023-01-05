package hana04.base;

import hana04.base.donothingobject.DoNothingObject__Module;
import hana04.base.filesystem.FileSystemModule;
import hana04.base.util.HanaMapEntry;

@dagger.Module(
    includes = {
        hana04.base.extension.placeholders.Module.class,
        DoNothingObject__Module.class,
        hana04.base.serialize.loader.Module.class,
        FileSystemModule.class,
        HanaMapEntry.Module.class,
    }
)
public abstract class Module {
  // NO-OP
}
