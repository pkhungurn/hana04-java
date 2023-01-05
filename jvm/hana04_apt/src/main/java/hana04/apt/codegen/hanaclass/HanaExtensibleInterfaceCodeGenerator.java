package hana04.apt.codegen.hanaclass;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import hana04.apt.codegen.hanaclass.mixin.ExtensibleInterfaceMixin;

import javax.lang.model.element.Element;

public class HanaExtensibleInterfaceCodeGenerator extends HanaClassCodeGenerator {

  public HanaExtensibleInterfaceCodeGenerator(Element element, String packageName, ClassName superClass) {
    super(element, packageName, /* propertySpecs= */ ImmutableList.of());
    mixins.add(new ExtensibleInterfaceMixin(this, superClass));
  }
}
