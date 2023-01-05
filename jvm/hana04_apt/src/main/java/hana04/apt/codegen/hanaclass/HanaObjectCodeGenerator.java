package hana04.apt.codegen.hanaclass;

import com.squareup.javapoet.ClassName;
import hana04.apt.codegen.hanaclass.mixin.ExtensibleInterfaceMixin;
import hana04.apt.codegen.hanaclass.mixin.ExtensibleMixin;
import hana04.apt.codegen.hanaclass.mixin.SerializableMixin;
import hana04.apt.codegen.hanaclass.property.PropertySpec;

import javax.lang.model.element.Element;
import java.util.List;

public class HanaObjectCodeGenerator extends HanaSerializableInterfaceCodeGenerator {
  public HanaObjectCodeGenerator(
      Element element,
      String packageName,
      ClassName superClass,
      int typeId,
      String[] typeNames,
      List<PropertySpec> propertySpecs) {
    super(element, packageName, typeId, typeNames, propertySpecs);
    mixins.add(new ExtensibleInterfaceMixin(this, superClass));
    mixins.add(new ExtensibleMixin(this));
    mixins.add(new SerializableMixin(this));
  }
}
