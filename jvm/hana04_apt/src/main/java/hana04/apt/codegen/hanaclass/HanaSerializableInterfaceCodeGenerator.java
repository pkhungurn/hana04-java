package hana04.apt.codegen.hanaclass;

import com.google.common.collect.ImmutableList;
import hana04.apt.codegen.hanaclass.property.PropertySpec;

import javax.lang.model.element.Element;
import java.util.List;

public abstract class HanaSerializableInterfaceCodeGenerator extends HanaConcreteClassCodeGenerator {
  private final int typeId;

  private List<String> typeNames;

  public HanaSerializableInterfaceCodeGenerator(
    Element element,
    String packageName,
    int typeId,
    String[] typeNames,
    List<PropertySpec> propertySpecs) {
    super(element, packageName, propertySpecs);
    this.typeId = typeId;

    if (typeNames.length == 0) {
      this.typeNames = ImmutableList.of(element.getSimpleName().toString());
    } else {
      this.typeNames = ImmutableList.copyOf(typeNames);
    }
  }

  public int getTypeId() {
    return typeId;
  }

  public String getSerializedTypeName() {
    return typeNames.get(0);
  }

  public List<String> getTypeNames() {
    return typeNames;
  }
}