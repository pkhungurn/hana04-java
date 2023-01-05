package hana04.distrib.request.workblock;

import org.inferred.freebuilder.FreeBuilder;

@FreeBuilder
public interface Block2DSpecification {
  int offsetX();
  int offsetY();
  int sizeX();
  int sizeY();
  class Builder extends Block2DSpecification_Builder {}
}
