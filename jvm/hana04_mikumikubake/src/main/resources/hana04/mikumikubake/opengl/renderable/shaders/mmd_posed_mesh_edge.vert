#version 150

#define MORPH_DISPLACEMENT_TEXTURE_WIDTH 4096
#define BONE_TRANSFORM_TEXTURE_WIDTH 256
#define MORPH_WEIGHT_TEXTURE_WIDTH 256
#define SDEF_PARAMS_TEXTURE_WIDTH 256

#define LINEAR_BLEND 0
#define SDEF 1
#define DUAL_QUATERNION 2

#define quatNorm length

in vec3 vert_position;
in vec3 vert_normal;
in vec2 vert_texCoord;
in vec4 vert_tangent;
in vec2 vert_morphStartAndCount;
in vec4 vert_boneIndex;
in vec4 vert_boneWeight;
in vec2 vert_skinningInfo;

uniform mat4 sys_modelMatrix;
uniform mat4 sys_normalMatrix;
uniform mat4 sys_viewMatrix;
uniform mat4 sys_projectionMatrix;

uniform sampler2DRect mesh_morphDisplacement;
uniform sampler2DRect mesh_sdefParams;

uniform sampler2DRect mesh_boneTransform;
uniform sampler2DRect mesh_morphWeight;

uniform float mat_edgeSize;

struct Morph {
    vec3 displacement;
    int index;
};

vec4 quatMul(vec4 a, vec4 b) {
    return vec4(cross(a.xyz, b.xyz) + a.w * b.xyz + b.w * a.xyz, a.w * b.w - dot(a.xyz, b.xyz));
}

vec4 quatConj(vec4 q) {
    return vec4(-q.xyz, q.w);
}

vec2 dualNumMul(vec2 a, vec2 b) {
    return vec2(a.x * b.x, a.x * b.y + a.y * b.x);
}

vec2 dualNumConj(vec2 a) {
    return vec2(a.x, -a.y);
}

vec2 dualNumSqrt(vec2 a) {
    return vec2(sqrt(a.x), a.y / (2*sqrt(a.x)));
}

vec2 dualNumInv(vec2 a) {
    return vec2(1.0 / a.x, -a.y / (a.x * a.x));
}

struct DualQuat {
    vec4 q0;
    vec4 qe;
};

DualQuat dualQuatAdd(DualQuat a, DualQuat b) {
    return DualQuat(a.q0 + b.q0, a.qe + b.qe);
}

DualQuat dualQuatScale(DualQuat a, float c) {
    return DualQuat(a.q0 * c, a.qe * c);
}

DualQuat dualQuatMul(DualQuat a, DualQuat b) {
    return DualQuat(quatMul(a.q0, b.q0), quatMul(a.q0, b.qe) + quatMul(a.qe, b.q0));
}

vec2 dualQuatNorm(DualQuat a) {
    float q0Norm = quatNorm(a.q0);
    return vec2(q0Norm, dot(a.q0, a.qe) / q0Norm);
}

DualQuat dualQuatFromDualNum(vec2 a) {
    return DualQuat(vec4(0,0,0,a.x), vec4(0,0,0,a.y));
}

DualQuat dualQuatNormalize(DualQuat a) {
    vec2 norm = dualQuatNorm(a);
    vec2 normInv = dualNumInv(norm);
    return dualQuatMul(a, dualQuatFromDualNum(normInv));
}

Morph getMorph(int morphStart, int morphOrder) {
    int index = morphStart + morphOrder;
    float u = index - MORPH_DISPLACEMENT_TEXTURE_WIDTH * (index / MORPH_DISPLACEMENT_TEXTURE_WIDTH) + 0.5;
    float v = index / MORPH_DISPLACEMENT_TEXTURE_WIDTH + 0.5;
    vec4 disp = texture(mesh_morphDisplacement, vec2(u, v));
    Morph morph;
    morph.displacement = disp.xyz;
    morph.index = int(round(disp.w));
    return morph;
}

float getMorphWeight(int morphIndex, sampler2DRect morphWeightTexture) {
    float u = morphIndex - MORPH_WEIGHT_TEXTURE_WIDTH * (morphIndex / MORPH_WEIGHT_TEXTURE_WIDTH) + 0.5;
    float v = morphIndex / MORPH_WEIGHT_TEXTURE_WIDTH + 0.5;
    float weight = texture(morphWeightTexture, vec2(u, v)).x;
    return weight;
}

vec4 readBoneTexture(int pixelIndex, sampler2DRect boneTransformTexture) {
    float u = pixelIndex - BONE_TRANSFORM_TEXTURE_WIDTH * (pixelIndex / BONE_TRANSFORM_TEXTURE_WIDTH) + 0.5;
    float v = pixelIndex / BONE_TRANSFORM_TEXTURE_WIDTH + 0.5;
    return texture(boneTransformTexture, vec2(u, v));
}

struct RigidBodyXform {
    vec3 disp;
    vec4 rot;
};

DualQuat dualQuatFromTranslation(vec3 t) {
    return DualQuat(vec4(0,0,0,1), vec4(t*0.5, 0));
}

DualQuat dualQuatFromQuat(vec4 q) {
    return DualQuat(q, vec4(0,0,0,0));
}

DualQuat dualQuatFromRigidBodyXform(RigidBodyXform xform) {
    return dualQuatMul(
    dualQuatFromTranslation(xform.disp),
    dualQuatFromQuat(xform.rot));
}

RigidBodyXform getBoneRigidBodyXform(int boneIndex, sampler2DRect boneTransformTexture) {
    RigidBodyXform xform;
    xform.disp = readBoneTexture(2*boneIndex+1, boneTransformTexture).xyz;
    xform.rot = readBoneTexture(2*boneIndex, boneTransformTexture);
    return xform;
}

RigidBodyXform rigidBodyXformFromDualQuat(DualQuat a) {
    return RigidBodyXform(
    2*quatMul(a.qe, quatConj(a.q0)).xyz,
    a.q0);
}

// This code is taken from
// http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToMatrix/index.htm
mat4 getRotationMatrix(vec4 q) {
    mat4 m;
    m[3] = vec4(0, 0, 0, 1);

    float sqw = q.w*q.w;
    float sqx = q.x*q.x;
    float sqy = q.y*q.y;
    float sqz = q.z*q.z;

    // invs (inverse square length) is only required if quaternion is not already normalised
    float invs = 1 / (sqx + sqy + sqz + sqw);
    m[0][0] = (sqx - sqy - sqz + sqw)*invs;// since sqw + sqx + sqy + sqz =1/invs*invs
    m[1][1] = (-sqx + sqy - sqz + sqw)*invs;
    m[2][2] = (-sqx - sqy + sqz + sqw)*invs;

    float tmp1 = q.x*q.y;
    float tmp2 = q.z*q.w;
    m[0][1] = 2.0 * (tmp1 + tmp2)*invs;
    m[1][0] = 2.0 * (tmp1 - tmp2)*invs;

    tmp1 = q.x*q.z;
    tmp2 = q.y*q.w;
    m[0][2] = 2.0 * (tmp1 - tmp2)*invs;
    m[2][0] = 2.0 * (tmp1 + tmp2)*invs;
    tmp1 = q.y*q.z;
    tmp2 = q.x*q.w;
    m[1][2] = 2.0 * (tmp1 + tmp2)*invs;
    m[2][1] = 2.0 * (tmp1 - tmp2)*invs;

    m[0][3] = 0;
    m[1][3] = 0;
    m[2][3] = 0;
    m[3][3] = 1;

    return m;
}

mat4 getMatrix(RigidBodyXform xform) {
    mat4 m = getRotationMatrix(xform.rot);
    m[3] = vec4(xform.disp, 1);
    return m;
}

mat4 getInterverseMatrix(RigidBodyXform xform) {
    mat4 m = getRotationMatrix(xform.rot);
    m = transpose(m);
    vec4 t = vec4(-xform.disp, 1);
    t = m * t;
    m[3] = t;
    return m;
}

mat4 getBoneMatrix(int boneIndex, sampler2DRect boneTransformTexture) {
    return getMatrix(getBoneRigidBodyXform(boneIndex, boneTransformTexture));
}

struct VertexConfig {
    vec3 p;
    vec3 t;
    vec3 n;
};

vec3 getBitangent(VertexConfig vc) {
    return normalize(cross(vc.n,vc.t));
}

VertexConfig transformVertexConfig(mat4 M, VertexConfig vc) {
    vec3 p = (M * vec4(vc.p, 1)).xyz;
    vec3 t = normalize(M * vec4(vc.t, 0)).xyz;
    vec3 n = normalize(transpose(inverse(M)) * vec4(vc.n, 0)).xyz;
    return VertexConfig(p, t, n);
}

VertexConfig skinWithLinearBlending(VertexConfig vc, sampler2DRect boneTransformTexture) {
    mat4 M = mat4(0);
    for (int i=0;i<4;i++) {
        int boneIndex = int(vert_boneIndex[i]);
        if (boneIndex < 0) {
            continue;
        }
        mat4 m = getBoneMatrix(boneIndex, boneTransformTexture);
        M += vert_boneWeight[i] * m;
    }
    M /= M[3][3];
    return transformVertexConfig(M, vc);
}

VertexConfig skinWithDualQuat(VertexConfig vc, sampler2DRect boneTransformTexture) {
    DualQuat sum = DualQuat(vec4(0,0,0,0), vec4(0,0,0,0));
    for (int i=0;i<4;i++) {
        int boneIndex = int(vert_boneIndex[i]);
        if (boneIndex < 0) {
            continue;
        }
        RigidBodyXform xform = getBoneRigidBodyXform(boneIndex, boneTransformTexture);
        DualQuat Q = dualQuatFromRigidBodyXform(xform);
        if (dot(Q.q0, sum.q0) < 0) {
            Q = dualQuatScale(Q, -1);
        }
        sum = dualQuatAdd(sum, dualQuatScale(Q, vert_boneWeight[i]));
    }
    DualQuat Q = dualQuatNormalize(sum);
    mat4 M = getMatrix(rigidBodyXformFromDualQuat(Q));
    return transformVertexConfig(M, vc);
}

struct SdefParams {
    vec3 C;
    vec3 R0;
    vec3 R1;
};

vec3 readSdefParamsTexture(int pixelIndex) {
    float u = pixelIndex - SDEF_PARAMS_TEXTURE_WIDTH * (pixelIndex / SDEF_PARAMS_TEXTURE_WIDTH) + 0.5;
    float v = pixelIndex / SDEF_PARAMS_TEXTURE_WIDTH + 0.5;
    return texture(mesh_sdefParams, vec2(u, v)).xyz;
}

SdefParams getSdefParams(int sdefVertexIndex) {
    SdefParams params;
    params.C = readSdefParamsTexture(3 * sdefVertexIndex);
    params.R0 = readSdefParamsTexture(3 * sdefVertexIndex + 1);
    params.R1 = readSdefParamsTexture(3 * sdefVertexIndex + 2);
    return params;
}

vec4 quatBlend(vec4 q1, float w1, vec4 q2, float w2) {
    float dotProd = dot(q1, q2);
    if (dotProd < 0) {
        q2 = -q2;
    }
    vec4 q = w1*q1 + w2*q2;
    return normalize(q);
}

// SDEF code from https://gist.github.com/nagadomi/aa39745ae6716b50c2a60288b093d14b
VertexConfig skinWithSdef(VertexConfig vc, sampler2DRect boneTransformTexture) {
    int boneIndex0 = int(round(vert_boneIndex[0]));
    int boneIndex1 = int(round(vert_boneIndex[1]));
    RigidBodyXform rbXform0 = getBoneRigidBodyXform(boneIndex0, boneTransformTexture);
    RigidBodyXform rbXform1 = getBoneRigidBodyXform(boneIndex1, boneTransformTexture);

    float bw0 = vert_boneWeight.x;
    float bw1 = vert_boneWeight.y;
    mat4 M0 = getMatrix(rbXform0);
    mat4 M1 = getMatrix(rbXform1);
    mat4 blendedM = bw0*M0 + bw1*M1;
    vec4 qBlended = quatBlend(rbXform0.rot, bw0, rbXform1.rot, bw1);
    mat4 qBlendedMat = getRotationMatrix(qBlended);

    SdefParams params = getSdefParams(int(round(vert_skinningInfo.y)));
    vec3 R0 = params.R0;
    vec3 R1 = params.R1;
    vec3 C = params.C;

    vec3 Cpos = vc.p - C;
    vec3 blendedR = bw0 * R0 + bw1 * R1;
    vec3 p =
    (
    blendedM * vec4(C,1)
    + qBlendedMat * vec4(Cpos,0)
    + bw0 * bw1 * (M0 - M1) * vec4(R0 - R1, 0) / 2
    ).xyz;
    vec3 t = (qBlendedMat * vec4(vc.t, 0)).xyz;
    vec3 n = (qBlendedMat * vec4(vc.n, 0)).xyz;
    return VertexConfig(p, t, n);
}

VertexConfig getPosedVertexConfig(sampler2DRect boneTransformTexture, sampler2DRect morphWeightTexture) {
    vec3 morphedPosition = vert_position;
    int morphCount = int(vert_morphStartAndCount.y);
    int morphStart = int(vert_morphStartAndCount.x);
    if (morphCount > 0) {
        for (int morphOrder=0; morphOrder < morphCount; morphOrder++) {
            Morph morph = getMorph(morphStart, morphOrder);
            float weight = getMorphWeight(morph.index, morphWeightTexture);
            morphedPosition += weight * morph.displacement;
        }
    }

    VertexConfig inVc = VertexConfig(morphedPosition, vert_tangent.xyz, vert_normal);
    VertexConfig posedVc;
    if (vert_skinningInfo.x == LINEAR_BLEND) {
        posedVc = skinWithLinearBlending(inVc, boneTransformTexture);
    } else if (vert_skinningInfo.x == SDEF) {
        posedVc = skinWithSdef(inVc, boneTransformTexture);
    } else {
        posedVc = skinWithDualQuat(inVc, boneTransformTexture);
    }
    return posedVc;
}

void main() {
    VertexConfig posedVc = getPosedVertexConfig(mesh_boneTransform, mesh_morphWeight);

    vec3 worldNormal = normalize((sys_normalMatrix * vec4(posedVc.n, 0.0)).xyz);
    vec4 worldPosition = sys_modelMatrix * vec4(posedVc.p, 1) + vec4(worldNormal * mat_edgeSize, 0);
    vec4 cameraPosition = sys_viewMatrix * vec4(worldPosition);
    vec4 clipPosition = sys_projectionMatrix * vec4(cameraPosition);

    gl_Position = clipPosition;
}