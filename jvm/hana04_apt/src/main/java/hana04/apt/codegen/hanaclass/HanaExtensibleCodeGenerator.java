package hana04.apt.codegen.hanaclass;

import com.squareup.javapoet.ClassName;
import hana04.apt.codegen.hanaclass.mixin.ExtensibleInterfaceMixin;
import hana04.apt.codegen.hanaclass.mixin.ExtensibleMixin;
import hana04.apt.codegen.hanaclass.property.PropertySpec;

import javax.lang.model.element.Element;
import java.util.List;

public class HanaExtensibleCodeGenerator extends HanaConcreteClassCodeGenerator {
  public HanaExtensibleCodeGenerator(Element element, String packageName, ClassName superClass, List<PropertySpec> propertySpecs) {
    super(element, packageName, propertySpecs);
    mixins.add(new ExtensibleInterfaceMixin(this, superClass));
    mixins.add(new ExtensibleMixin(this));
  }
}
