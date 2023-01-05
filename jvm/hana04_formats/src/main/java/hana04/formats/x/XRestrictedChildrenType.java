
package hana04.formats.x;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class XRestrictedChildrenType implements XChildrenType {
  private static final Set<XTemplate> allowedTemplates =
    new HashSet<XTemplate>();

  public XRestrictedChildrenType() {
    // NO-OP
  }

  public XRestrictedChildrenType(Collection<XTemplate> allowed) {
    allowedTemplates.addAll(allowed);
  }

  @Override
  public boolean isAllowedAsChild(XTemplate template) {
    return allowedTemplates.contains(template);
  }

  Set<XTemplate> getAllowedTemplateSet() {
    return allowedTemplates;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof XRestrictedChildrenType)) {
      return false;
    } else {
      XRestrictedChildrenType other = (XRestrictedChildrenType) o;
      return allowedTemplates.equals(other.allowedTemplates);
    }
  }
}
