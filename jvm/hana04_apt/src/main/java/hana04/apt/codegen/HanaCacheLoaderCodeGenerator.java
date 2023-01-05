package hana04.apt.codegen;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import dagger.Binds;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import hana04.apt.util.Util;
import hana04.base.caching.HanaCacheLoader;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import java.util.List;

public class HanaCacheLoaderCodeGenerator implements CodeGenerator {
  private final Element element;
  private final List<String> protocols;

  public HanaCacheLoaderCodeGenerator(Element element, String[] protocol) {
    this.element = element;
    this.protocols = ImmutableList.copyOf(protocol);
    Util.checkInjectedConstructor(element,
      "A cache loader implementation must have a constructor annotated with @Inject. "
        + "(" + element.asType().toString() + ")");
  }

  @Override
  public void generateCode(Filer filer) {
    // NO-OP
  }

  private String getUniqueClassName() {
    return Util.getUniqueClassName(element);
  }

  @Override
  public List<BindingGenerator> getBindingGenerators() {
    return ImmutableList.of(new Generator());
  }

  class Generator implements BindingGenerator {

    @Override
    public void generate(TypeSpec.Builder moduleClassBuilder) {
      for (int i = 0; i < protocols.size(); i++) {
        String protocol = protocols.get(i);
        moduleClassBuilder.addMethod(MethodSpec
          .methodBuilder("provide__" + getUniqueClassName() + "__ReadableSerializerByClass_" + i)
          .addAnnotation(Binds.class)
          .addAnnotation(IntoMap.class)
          .addAnnotation(AnnotationSpec.builder(StringKey.class)
            .addMember("value", CodeBlock.builder().add("$S", protocol).build())
            .build())
          .addModifiers(Modifier.PUBLIC)
          .addModifiers(Modifier.ABSTRACT)
          .returns(ParameterizedTypeName.get(ClassName.get(HanaCacheLoader.class), TypeVariableName.get("?")))
          .addParameter(ParameterSpec.builder(ClassName.get(element.asType()), "cacheLoader").build())
          .build());
      }
    }

    @Override
    public String getBindingLocationPackageName() {
      return Util.getPackageName(element);
    }
  }
}
