package hana04.apt.codegen;

import javax.annotation.processing.Filer;
import java.util.List;

public interface CodeGenerator {
  void generateCode(Filer filer);
  List<BindingGenerator> getBindingGenerators();
}
