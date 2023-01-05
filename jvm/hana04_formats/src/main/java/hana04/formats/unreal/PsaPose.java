
package hana04.formats.unreal;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.util.HashMap;

public class PsaPose
{
    public HashMap<String, Pair<Vector3f, Quat4f>> jointXforms = new HashMap<String, Pair<Vector3f, Quat4f>>();
    
    public void get(String boneName, Vector3f displacement, Quat4f rotation)
    {
        if (jointXforms.containsKey(boneName))
        {
            Pair<Vector3f, Quat4f> joint = jointXforms.get(boneName);
            displacement.set(joint.getLeft());
            rotation.set(joint.getRight());
        }
        else
        {
            displacement.set(0,0,0);
            rotation.set(0,0,0,1);
        }
    }
    
    public void set(String boneName, Vector3f displacement, Quat4f rotation)
    {
        jointXforms.put(boneName, new MutablePair<Vector3f, Quat4f>(displacement, rotation));
    }
    
    public void clear()
    {
        jointXforms.clear();
    }
}
