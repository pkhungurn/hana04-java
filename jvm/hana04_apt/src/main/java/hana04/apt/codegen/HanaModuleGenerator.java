package hana04.apt.codegen;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import dagger.Module;
import hana04.apt.processor.HanaAnnotationProcessor;

import javax.annotation.Generated;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import java.util.List;

public class HanaModuleGenerator {
  private final Element element;
  private final String packageName;

  public HanaModuleGenerator(Element element, String packageName) {
    this.element = element;
    this.packageName = packageName;
  }

  public String getPackageName() {
    return packageName;
  }

  public String className() {
    return element.getSimpleName() + "__GeneratedHanaModule";
  }

  public ClassName poetClassName() {
    return ClassName.bestGuess(packageName + "." + className());
  }

  public JavaFile generateFile(List<BindingGenerator> generators) {
    TypeSpec.Builder moduleClass = TypeSpec.classBuilder(className())
      .addAnnotation(AnnotationSpec.builder(Generated.class)
        .addMember("value", "$S", HanaAnnotationProcessor.class)
        .build())
      .addModifiers(Modifier.PUBLIC)
      .addAnnotation(Module.class)
      .addModifiers(Modifier.ABSTRACT);

    generators.forEach(generator -> generator.generate(moduleClass));

    return JavaFile.builder(packageName, moduleClass.build()).build();
  }
}
