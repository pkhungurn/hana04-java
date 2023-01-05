package hana04.apt.codegen.hanaclass;

import com.squareup.javapoet.ClassName;
import hana04.apt.codegen.hanaclass.mixin.LateDeserializableMixin;
import hana04.apt.codegen.hanaclass.property.PropertySpec;

import javax.lang.model.element.Element;
import java.util.List;

public class HanaLateDeserializableCodeGenerator extends HanaObjectCodeGenerator {
  public HanaLateDeserializableCodeGenerator(
      Element element,
      String packageName,
      ClassName superClass,
      int typeId,
      String[] typeNames,
      List<PropertySpec> propertySpecs) {
    super(element, packageName, superClass, typeId, typeNames, propertySpecs);
    mixins.add(new LateDeserializableMixin(this));
  }
}
