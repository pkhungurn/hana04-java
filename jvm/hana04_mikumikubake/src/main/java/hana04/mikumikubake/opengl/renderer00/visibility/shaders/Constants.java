package hana04.mikumikubake.opengl.renderer00.visibility.shaders;

public final class Constants {
  private Constants() {
    // NO-OP
  }

  private static final String RESOURCES_PATH = "/hana/mikumikubake/renderer00/visibility/shaders";
  public static final String MMD_POSED_MESH_VERT_RESOURCE_NAME = RESOURCES_PATH + "/mmd_posed_mesh.vert";
  public static final String WORLD_POSITION_FRAG_RESOURCE_NAME = RESOURCES_PATH + "/world_position.frag";
  public static final String MMD_POSE_MESH_START_TO_END_VERT_RESOURCE_NAME = RESOURCES_PATH +
    "/mmd_posed_mesh_start_to_end.vert";
  public static final String VISIBILITY_FRAG_RESOURCE_NAME = RESOURCES_PATH + "/visibility.frag";
}
