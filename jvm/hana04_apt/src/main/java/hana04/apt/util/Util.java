package hana04.apt.util;

import com.google.common.base.Preconditions;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import java.io.IOException;

public class Util {
  public static void writeJavaFile(Filer filer, String packageName, TypeSpec typeSpec) {
    JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();
    try {
      javaFile.writeTo(filer);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String getPackageLevelClassName(Element element) {
    StringBuilder output = new StringBuilder();
    output.append(element.getSimpleName());
    Element current = element.getEnclosingElement();
    while (!current.getKind().equals(ElementKind.PACKAGE)) {
      output.insert(0, current.getSimpleName().toString() + "_");
      current = current.getEnclosingElement();
    }
    return output.toString();
  }

  public static String getPackageName(Element element) {
    Element current = element;
    while (!current.getKind().equals(ElementKind.PACKAGE)) {
      current = current.getEnclosingElement();
    }
    return element.toString();
  }

  /*
  public static ImmutableList<String> getUniqueClassNameParts(Element element) {
    ImmutableList.Builder<String> parts = ImmutableList.builder();
    parts.add(element.getSimpleName().toString());
    Element current = element.getEnclosingElement();
    while (!current.getKind().equals(ElementKind.PACKAGE)) {
      parts.add(current.getSimpleName().toString() + "_");
      current = current.getEnclosingElement();
    }
    parts.add(current.toString().replace(".", "_"));
    return parts.build();
  }
  */

  public static String getUniqueClassName(Element element) {
    StringBuilder output = new StringBuilder();
    output.append(element.getSimpleName());
    Element current = element.getEnclosingElement();
    while (!current.getKind().equals(ElementKind.PACKAGE)) {
      output.insert(0, current.getSimpleName().toString() + "_");
      current = current.getEnclosingElement();
    }
    output.insert(0, "____");
    output.insert(0, current.toString().replace(".", "_"));
    return output.toString();
  }

  public static void checkInjectedConstructor(Element element, String errorMessage) {
    boolean foundRightConstructor = false;
    for (Element enclosedElement : element.getEnclosedElements()) {
      if (!enclosedElement.getKind().equals(ElementKind.CONSTRUCTOR)) {
        continue;
      }
      if (enclosedElement.getAnnotation(Inject.class) == null) {
        continue;
      }
      foundRightConstructor = true;
    }
    Preconditions.checkArgument(foundRightConstructor, errorMessage);
  }
}
