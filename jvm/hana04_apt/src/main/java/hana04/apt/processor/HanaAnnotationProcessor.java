package hana04.apt.processor;

import com.google.auto.service.AutoService;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import hana04.apt.annotation.HanaDeclareBinaryDeserializer;
import hana04.apt.annotation.HanaDeclareBinarySerializerByClass;
import hana04.apt.annotation.HanaDeclareBinarySerializerByTypeId;
import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareCacheLoader;
import hana04.apt.annotation.HanaDeclareExtensible;
import hana04.apt.annotation.HanaDeclareExtensibleInterface;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.apt.annotation.HanaDeclareLateDeserializable;
import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaDeclareReadableDeserializer;
import hana04.apt.annotation.HanaDeclareReadableSerializerByClass;
import hana04.apt.annotation.HanaDeclareReadableSerializerByTypeName;
import hana04.apt.annotation.HanaModule;
import hana04.apt.annotation.HanaProperty;
import hana04.apt.annotation.HanaProvideExtension;
import hana04.apt.codegen.BindingGenerator;
import hana04.apt.codegen.CodeGenerator;
import hana04.apt.codegen.HanaBinaryDeserializerCodeGenerator;
import hana04.apt.codegen.HanaBinarySerializerByClassCodeGenerator;
import hana04.apt.codegen.HanaBinarySerializerByTypeIdCodeGenerator;
import hana04.apt.codegen.HanaBuilderCodeGenerator;
import hana04.apt.codegen.HanaCacheLoaderCodeGenerator;
import hana04.apt.codegen.HanaExtensionCodeGenerator;
import hana04.apt.codegen.HanaModuleGenerator;
import hana04.apt.codegen.HanaProvidesExtensionCodeGenerator;
import hana04.apt.codegen.HanaReadableDeserializerCodeGenerator;
import hana04.apt.codegen.HanaReadableSerializerByClassCodeGenerator;
import hana04.apt.codegen.HanaReadableSerializerByTypeNameCodeGenerator;
import hana04.apt.codegen.hanaclass.HanaClassCodeGenerator;
import hana04.apt.codegen.hanaclass.HanaExtensibleCodeGenerator;
import hana04.apt.codegen.hanaclass.HanaExtensibleInterfaceCodeGenerator;
import hana04.apt.codegen.hanaclass.HanaLateDeserializableCodeGenerator;
import hana04.apt.codegen.hanaclass.HanaObjectCodeGenerator;
import hana04.apt.codegen.hanaclass.property.PropertySpec;
import hana04.apt.codegen.hanaclass.property.PropertyType;
import hana04.apt.util.AnnotationUtil;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.stream.Collectors.toSet;

@AutoService(Processor.class)
public class HanaAnnotationProcessor extends AbstractProcessor {

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    HashSet<String> result = new HashSet<>();
    result.add(HanaModule.class.getCanonicalName());
    result.add(HanaDeclareExtensible.class.getCanonicalName());
    result.add(HanaDeclareObject.class.getCanonicalName());
    result.add(HanaProperty.class.getCanonicalName());
    result.add(HanaDeclareReadableDeserializer.class.getCanonicalName());
    result.add(HanaDeclareReadableSerializerByClass.class.getCanonicalName());
    result.add(HanaDeclareReadableSerializerByTypeName.class.getCanonicalName());
    result.add(HanaDeclareExtension.class.getCanonicalName());
    result.add(HanaDeclareExtensibleInterface.class.getCanonicalName());
    result.add(HanaDeclareCacheLoader.class.getCanonicalName());
    result.add(HanaDeclareLateDeserializable.class.getCanonicalName());
    result.add(HanaProvideExtension.class.getCanonicalName());
    result.add(HanaDeclareBinarySerializerByClass.class.getCanonicalName());
    result.add(HanaDeclareBinarySerializerByTypeId.class.getCanonicalName());
    result.add(HanaDeclareBinaryDeserializer.class.getCanonicalName());
    result.add(HanaDeclareBuilder.class.getCanonicalName());
    return result;
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latest();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    List<CodeGenerator> codeGenerators = collectCodeGenerators(roundEnv);
    for (CodeGenerator generator : codeGenerators) {
      generator.generateCode(processingEnv.getFiler());
    }

    ImmutableList<HanaModuleGenerator> hanaModuleGenerators = collectHanaModuleGenerators(roundEnv);
    ImmutableList<String> hanaModulePackageNames =
        hanaModuleGenerators.stream().map(HanaModuleGenerator::getPackageName).collect(toImmutableList());
    ImmutableList<ArrayList<BindingGenerator>> bindingGenerators = IntStream
        .range(0, hanaModuleGenerators.size())
        .mapToObj(x -> new ArrayList<BindingGenerator>())
        .collect(toImmutableList());
    for (CodeGenerator generator : codeGenerators) {
      for (BindingGenerator bindingGenerator : generator.getBindingGenerators()) {
        String bindingPackageName = bindingGenerator.getBindingLocationPackageName();
        Optional<Integer> index = longestPackageIndex(bindingPackageName, hanaModulePackageNames);
        index.ifPresent(i -> bindingGenerators.get(i).add(bindingGenerator));
      }
    }
    for (int i = 0; i < hanaModuleGenerators.size(); i++) {
      HanaModuleGenerator hanaModuleGenerator = hanaModuleGenerators.get(i);
      JavaFile moduleFile = hanaModuleGenerator.generateFile(bindingGenerators.get(i));
      try {
        moduleFile.writeTo(processingEnv.getFiler());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    return true;
  }

  private Optional<Integer> longestPackageIndex(String bindingPackageName, List<String> packageNames) {
    int maxIndex = -1;
    int maxLength = 0;
    for (int i = 0; i < packageNames.size(); i++) {
      String packageName = packageNames.get(i);
      if (!bindingPackageName.startsWith(packageName)) {
        continue;
      }
      if (maxLength < packageName.length()) {
        maxLength = packageName.length();
        maxIndex = i;
      }
    }
    return maxIndex < 0 ? Optional.empty() : Optional.of(maxIndex);
  }

  private List<CodeGenerator> collectCodeGenerators(RoundEnvironment roundEnv) {
    ImmutableList.Builder<CodeGenerator> codeGenerators = ImmutableList.builder();

    for (Element element : roundEnv.getElementsAnnotatedWith(HanaDeclareObject.class)) {
      if (!element.getEnclosingElement().getKind().equals(ElementKind.PACKAGE)) {
        throw new RuntimeException("@HanaNode is attached to an inner class.");
      }
      if (!element.getKind().equals(ElementKind.INTERFACE)) {
        throw new RuntimeException("@HanaNode is attached to a non-interface.");
      }
      List<PropertySpec> propertySpecs = collectHanaProperties(element);
      String nodePackageName = processingEnv.getElementUtils().getPackageOf(element).toString();

      ClassName superClass;
      try {
        superClass = ClassName.bestGuess(
            AnnotationUtil.extractClassField(element, HanaDeclareObject.class, "parent"));
      } catch (Exception e) {
        throw new RuntimeException(
            element.toString() + " " +
                AnnotationUtil.extractClassField(element, HanaDeclareObject.class, "parent"));
      }

      HanaClassCodeGenerator nodeSpec = new HanaObjectCodeGenerator(
          element,
          nodePackageName,
          superClass,
          element.getAnnotation(HanaDeclareObject.class).typeId(),
          element.getAnnotation(HanaDeclareObject.class).typeNames(),
          propertySpecs);
      findAndRecordInnerBuilder(element, nodeSpec);
      codeGenerators.add(nodeSpec);
    }

    for (Element element : roundEnv.getElementsAnnotatedWith(HanaDeclareExtensible.class)) {
      if (!element.getEnclosingElement().getKind().equals(ElementKind.PACKAGE)) {
        throw new RuntimeException("@HanaExtensible is attached to an inner class.");
      }
      if (!element.getKind().equals(ElementKind.INTERFACE)) {
        throw new RuntimeException("@HanaExtensible is attached to a non-interface.");
      }
      List<PropertySpec> propertySpecs = collectHanaProperties(element);
      String nodePackageName = processingEnv.getElementUtils().getPackageOf(element).toString();
      ClassName superClass;
      try {
        superClass = ClassName.bestGuess(
            AnnotationUtil.extractClassField(element, HanaDeclareExtensible.class, "value"));
      } catch (Exception e) {
        throw new RuntimeException(
            element.toString() + " " +
                AnnotationUtil.extractClassField(element, HanaDeclareExtensible.class, "value"));
      }
      HanaClassCodeGenerator extensibleSpec =
          new HanaExtensibleCodeGenerator(element, nodePackageName, superClass, propertySpecs);
      findAndRecordInnerBuilder(element, extensibleSpec);
      codeGenerators.add(extensibleSpec);
    }

    for (Element element : roundEnv.getElementsAnnotatedWith(HanaDeclareExtensibleInterface.class)) {
      if (!element.getEnclosingElement().getKind().equals(ElementKind.PACKAGE)) {
        throw new RuntimeException("@HanaExtensibleInterface is attached to an inner class.");
      }
      if (!element.getKind().equals(ElementKind.INTERFACE)) {
        throw new RuntimeException("@HanaExtensibleInterface is attached to a non-interface.");
      }
      String nodePackageName = processingEnv.getElementUtils().getPackageOf(element).toString();
      ClassName superClass;
      try {
        superClass = ClassName.bestGuess(
            AnnotationUtil.extractClassField(element, HanaDeclareExtensibleInterface.class, "value"));
      } catch (Exception e) {
        throw new RuntimeException(
            element.toString() + " " + AnnotationUtil.extractClassField(
                element,
                HanaDeclareExtensibleInterface.class,
                "value"));
      }
      HanaClassCodeGenerator nodeSpec = new HanaExtensibleInterfaceCodeGenerator(element, nodePackageName, superClass);
      codeGenerators.add(nodeSpec);
    }

    for (Element element : roundEnv.getElementsAnnotatedWith(HanaDeclareLateDeserializable.class)) {
      if (!element.getEnclosingElement().getKind().equals(ElementKind.PACKAGE)) {
        throw new RuntimeException("@HanaLateDeserializable is attached to an inner class.");
      }
      if (!element.getKind().equals(ElementKind.INTERFACE)) {
        throw new RuntimeException("@HanaLateDeserializable is attached to a non-interface.");
      }
      List<PropertySpec> propertySpecs = collectHanaProperties(element);
      String packageName = processingEnv.getElementUtils().getPackageOf(element).toString();

      ClassName superClass;
      try {
        superClass = ClassName.bestGuess(
            AnnotationUtil.extractClassField(element, HanaDeclareLateDeserializable.class, "parent"));
      } catch (Exception e) {
        throw new RuntimeException(
            element.toString() + " " +
                AnnotationUtil.extractClassField(element, HanaDeclareLateDeserializable.class, "parent"));
      }

      HanaClassCodeGenerator serializableSpec = new HanaLateDeserializableCodeGenerator(
          element,
          packageName,
          superClass,
          element.getAnnotation(HanaDeclareLateDeserializable.class).typeId(),
          element.getAnnotation(HanaDeclareLateDeserializable.class).typeNames(),
          propertySpecs);
      findAndRecordInnerBuilder(element, serializableSpec);
      codeGenerators.add(serializableSpec);
    }

    for (Element element : roundEnv.getElementsAnnotatedWith(HanaDeclareExtension.class)) {
      ClassName extensibleClassName;
      try {
        extensibleClassName = ClassName.bestGuess(AnnotationUtil.extractClassField(
            element,
            HanaDeclareExtension.class,
            "extensibleClass"));
      } catch (Exception e) {
        throw new RuntimeException(
            element.toString() + " " +
                AnnotationUtil.extractClassField(
                    element,
                    HanaDeclareExtension.class,
                    "extensibleClass"));
      }
      ClassName extensionClassName;
      try {
        extensionClassName = ClassName.bestGuess(AnnotationUtil.extractClassField(
            element,
            HanaDeclareExtension.class,
            "extensionClass"));
      } catch (Exception e) {
        throw new RuntimeException(
            element.toString() + " " +
                AnnotationUtil.extractClassField(
                    element,
                    HanaDeclareExtension.class,
                    "extensionClass"));
      }
      ClassName implClassName;
      try {
        implClassName = ClassName.bestGuess(element.getEnclosingElement().asType().toString());
      } catch (Exception e) {
        throw new RuntimeException(
            element.toString() + " " +
                element.getEnclosingElement().asType().toString());
      }
      codeGenerators.add(new HanaExtensionCodeGenerator(
          (ExecutableElement) element,
          processingEnv.getElementUtils().getPackageOf(element).toString(),
          extensibleClassName,
          extensionClassName,
          implClassName
      ));
    }

    for (Element element : roundEnv.getElementsAnnotatedWith(HanaProvideExtension.class)) {
      ClassName extensibleClassName;
      try {
        extensibleClassName = ClassName.bestGuess(AnnotationUtil.extractClassField(
            element,
            HanaProvideExtension.class,
            "extensibleClass"));
      } catch (Exception e) {
        throw new RuntimeException(
            element.toString() + " " +
                AnnotationUtil.extractClassField(
                    element,
                    HanaProvideExtension.class,
                    "extensibleClass"));
      }
      ClassName extensionClassName;
      try {
        extensionClassName = ClassName.bestGuess(AnnotationUtil.extractClassField(
            element,
            HanaProvideExtension.class,
            "extensionClass"));
      } catch (Exception e) {
        throw new RuntimeException(
            element.toString() + " " +
                AnnotationUtil.extractClassField(
                    element,
                    HanaProvideExtension.class,
                    "extensionClass"));
      }
      codeGenerators.add(new HanaProvidesExtensionCodeGenerator(
          (ExecutableElement) element,
          processingEnv.getElementUtils().getPackageOf(element).toString(),
          extensibleClassName,
          extensionClassName
      ));
    }

    for (Element element : roundEnv.getElementsAnnotatedWith(HanaDeclareReadableDeserializer.class)) {
      HanaDeclareReadableDeserializer annotation = element.getAnnotation(HanaDeclareReadableDeserializer.class);
      codeGenerators.add(new HanaReadableDeserializerCodeGenerator(element, annotation.value()));
    }

    for (Element element : roundEnv.getElementsAnnotatedWith(HanaDeclareReadableSerializerByClass.class)) {
      ClassName className;
      try {
        className = ClassName.bestGuess(AnnotationUtil.extractClassField(
            element,
            HanaDeclareReadableSerializerByClass.class,
            "value"));
      } catch (Exception e) {
        throw new RuntimeException(
            element.toString() + " " +
                AnnotationUtil.extractClassField(
                    element,
                    HanaDeclareReadableSerializerByClass.class,
                    "value"));
      }
      codeGenerators.add(new HanaReadableSerializerByClassCodeGenerator(element, className));
    }

    for (Element element : roundEnv.getElementsAnnotatedWith(HanaDeclareReadableSerializerByTypeName.class)) {
      HanaDeclareReadableSerializerByTypeName
          annotation = element.getAnnotation(HanaDeclareReadableSerializerByTypeName.class);
      codeGenerators.add(new HanaReadableSerializerByTypeNameCodeGenerator(
          element, annotation.value()));
    }

    for (Element element : roundEnv.getElementsAnnotatedWith(HanaDeclareBinarySerializerByClass.class)) {
      ClassName className;
      try {
        className = ClassName.bestGuess(AnnotationUtil.extractClassField(
            element,
            HanaDeclareBinarySerializerByClass.class,
            "value"));
      } catch (Exception e) {
        throw new RuntimeException(
            element.toString() + " " +
                AnnotationUtil.extractClassField(
                    element,
                    HanaDeclareBinarySerializerByClass.class,
                    "value"));
      }
      codeGenerators.add(new HanaBinarySerializerByClassCodeGenerator(
          element,
          className));
    }

    for (Element element : roundEnv.getElementsAnnotatedWith(HanaDeclareBinarySerializerByTypeId.class)) {
      codeGenerators.add(new HanaBinarySerializerByTypeIdCodeGenerator(
          element,
          element.getAnnotation(HanaDeclareBinarySerializerByTypeId.class).value()));
    }

    for (Element element : roundEnv.getElementsAnnotatedWith(HanaDeclareBinaryDeserializer.class)) {
      codeGenerators.add(new HanaBinaryDeserializerCodeGenerator(
          element,
          element.getAnnotation(HanaDeclareBinaryDeserializer.class).value()));
    }

    for (Element element : roundEnv.getElementsAnnotatedWith(HanaDeclareCacheLoader.class)) {
      String[] names = element.getAnnotation(HanaDeclareCacheLoader.class).value();
      codeGenerators.add(new HanaCacheLoaderCodeGenerator(element, names));
    }

    for (Element element : roundEnv.getElementsAnnotatedWith(HanaDeclareBuilder.class)) {
      /*
      if (!element.getEnclosingElement().getKind().equals(ElementKind.PACKAGE)) {
        throw new RuntimeException("@HanaDeclareBuilder is attached to an inner class.");
      }
       */
      if (!element.getKind().equals(ElementKind.CLASS)) {
        throw new RuntimeException("@HanaDeclareBuilder is attached to a non-class.");
      }
      String builderPackageName = processingEnv.getElementUtils().getPackageOf(element).toString();
      ClassName classToBeBuilt;
      try {
        classToBeBuilt =
            ClassName.bestGuess(AnnotationUtil.extractClassField(element, HanaDeclareBuilder.class, "value"));
      } catch (Exception e) {
        throw new RuntimeException(
            element + " " +
                AnnotationUtil.extractClassField(element, HanaDeclareBuilder.class, "value"));
      }
      codeGenerators.add(new HanaBuilderCodeGenerator(element, builderPackageName, classToBeBuilt));
    }

    return codeGenerators.build();
  }

  private ImmutableList<PropertySpec> collectHanaProperties(Element element) {
    ImmutableList.Builder<PropertySpec> propertySpecBuilder = ImmutableList.builder();
    for (Element enclosedElement : element.getEnclosedElements()) {
      if (!enclosedElement.getKind().equals(ElementKind.METHOD)) {
        continue;
      }
      if (enclosedElement.getAnnotation(HanaProperty.class) == null) {
        continue;
      }
      HanaProperty annotation = enclosedElement.getAnnotation(HanaProperty.class);
      ExecutableElement ee = (ExecutableElement) enclosedElement;
      String propertyName = ee.getSimpleName().toString();
      PropertyType propertyType = null;
      try {
        propertyType = PropertyType.parse(ee.getReturnType(), processingEnv.getTypeUtils());
      } catch (Exception e) {
        throw new IllegalArgumentException(String.format(
            "Could not parse property '%s' of interface '%s'",
            propertyName,
            element.getSimpleName()), e);
      }
      PropertySpec propertySpec = new PropertySpec(propertyName, propertyType, annotation.value());
      propertySpecBuilder.add(propertySpec);
    }
    ImmutableList<PropertySpec> propertySpecs = propertySpecBuilder.build();
    Set<Integer> tags = propertySpecs.stream().map(spec -> spec.tag).collect(toSet());
    Preconditions.checkState(
        tags.size() == propertySpecs.size(),
        "There are duplicated tags: " + element.getSimpleName());
    return propertySpecs;
  }

  private void findAndRecordInnerBuilder(Element element, HanaClassCodeGenerator classSpec) {
    // See if there's an inner factory class.
    for (Element enclosedElement : element.getEnclosedElements()) {
      if (!enclosedElement.getKind().equals(ElementKind.CLASS)) {
        continue;
      }
      if (enclosedElement.getSimpleName().toString().equals("Builder")) {
        classSpec.userDefinedBuilderElement = Optional.of(enclosedElement);
        break;
      }
    }
  }

  private ImmutableList<HanaModuleGenerator> collectHanaModuleGenerators(RoundEnvironment roundEnv) {
    ImmutableList.Builder<HanaModuleGenerator> generators = ImmutableList.builder();
    for (Element element : roundEnv.getElementsAnnotatedWith(HanaModule.class)) {
      if (!element.getKind().equals(ElementKind.CLASS)) {
        throw new RuntimeException("@HanaModule is not attached to a class.");
      }
      if (!element.getEnclosingElement().getKind().equals(ElementKind.PACKAGE)) {
        throw new RuntimeException("@HanaModule cannot be attached to an inner class");
      }
      /*
      if (!element.getSimpleName().toString().equals("Module")) {
        throw new RuntimeException(String.format(
            "@HanaModule must be attached to a class named 'Module'. Actual name = '%s'", element.getSimpleName()));
      }
      */
      HanaModuleGenerator hanaModuleGenerator = new HanaModuleGenerator(
          element,
          processingEnv.getElementUtils().getPackageOf(element).getQualifiedName().toString());
      generators.add(hanaModuleGenerator);
    }
    return generators.build();
  }
}
