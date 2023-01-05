
package hana04.formats.unreal;

import hana04.base.util.BinaryIo;
import org.apache.commons.io.input.SwappedDataInputStream;

import java.io.IOException;

/**
 *
 * @author Pramook Khungurn
 */
public class VChunkHeader
{
    public String chunkId;
    public int typeFlag;
    public int dataSize;
    public int dataCount;
    
    public void read(SwappedDataInputStream fin) throws IOException
    {        
        chunkId = BinaryIo.readUtfString(fin, 20);
        typeFlag = fin.readInt();
        dataSize = fin.readInt();
        dataCount = fin.readInt();
    }
}
