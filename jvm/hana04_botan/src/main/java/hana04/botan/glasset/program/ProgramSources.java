package hana04.botan.glasset.program;

import hana04.base.changeprop.VersionedSubject;

import java.util.Optional;

public interface ProgramSources extends VersionedSubject {
  String getVertexShaderSource();

  String getFragmentShaderSource();

  Optional<String> getVertexShaderFileName();

  Optional<String> getFragmentShaderFileName();
}
