package hana04.apt.util;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import java.util.Map;

public class AnnotationUtil {
  public static String extractClassField(Element element, Class<?> annotationClass, String fieldName) {
    for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
      if (annotationMirror
          .getAnnotationType()
          .asElement()
          .asType()
          .toString()
          .equals(annotationClass.getCanonicalName())) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();
        for (ExecutableElement ee : elementValues.keySet()) {
          if (ee.getSimpleName().toString().equals(fieldName)) {
            return elementValues.get(ee).getValue().toString();
          }
        }
      }
    }
    throw new RuntimeException(String.format("Cannot find field '%s' of annotation class '%s' in element %s.",
        fieldName, annotationClass.getCanonicalName(), element));
  }
}
