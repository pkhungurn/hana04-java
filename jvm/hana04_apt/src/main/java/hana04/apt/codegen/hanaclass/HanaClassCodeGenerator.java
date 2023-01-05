package hana04.apt.codegen.hanaclass;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import hana04.apt.codegen.BindingGenerator;
import hana04.apt.codegen.CodeGenerator;
import hana04.apt.codegen.hanaclass.mixin.ClassMixin;
import hana04.apt.codegen.hanaclass.property.PropertySpec;
import hana04.apt.util.Util;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class HanaClassCodeGenerator implements CodeGenerator {
  public final Element element;
  public final String packageName;
  public final List<PropertySpec> propertySpecs;
  public Optional<Element> userDefinedBuilderElement = Optional.empty();
  public final ArrayList<ClassMixin> mixins = new ArrayList<>();

  public HanaClassCodeGenerator(Element element, String packageName, List<PropertySpec> propertySpecs) {
    this.element = element;
    this.packageName = packageName;
    this.propertySpecs = propertySpecs;
  }

  @Override
  public void generateCode(Filer filer) {
    for (ClassMixin mixin : mixins) {
      mixin.generateCode(filer);
    }
  }

  public String getClassName() {
    return element.getSimpleName().toString();
  }

  public String getUniqueClassName() {
    return Util.getUniqueClassName(element);
  }

  public ClassName getPoetClassName() {
    return ClassName.get(packageName, getClassName());
  }

  @Override
  public List<BindingGenerator> getBindingGenerators() {
    ImmutableList.Builder<BindingGenerator> bindingGenerators = ImmutableList.builder();
    for (ClassMixin mixin : mixins) {
      bindingGenerators.addAll(mixin.getBindingGenerators());
    }
    return bindingGenerators.build();
  }
}
