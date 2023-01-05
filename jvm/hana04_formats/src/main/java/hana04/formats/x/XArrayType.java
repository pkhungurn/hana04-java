
package hana04.formats.x;

public class XArrayType implements XType
{
    protected XType elementType;
    protected int numericalLength;
    protected String lengthFieldName;
    protected boolean fixedLength;
    
    public static XArrayType createFixedLengthArrayType(
            XType elementType, int length)
    {
        XArrayType result = new XArrayType();        
        result.elementType = elementType;
        result.numericalLength = length;
        result.fixedLength = true;
        return result;
    }
    
    public static XArrayType createFieldLengthArrayType(
            XType elementType, String lengthFieldName)
    {
        XArrayType result = new XArrayType();        
        result.elementType = elementType;
        result.lengthFieldName = lengthFieldName;
        result.fixedLength = false;
        return result;
    }
    

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof XArrayType))
        {
            return false;
        }
        else
        {
            XArrayType other = (XArrayType) o;
            if (!elementType.equals(other.elementType))
            {
                return false;
            }
            if (fixedLength != other.fixedLength)
            {
                return false;
            }
            if (fixedLength)
            {
                return numericalLength == other.numericalLength;
            }
            else
            {
                return lengthFieldName.equals(other.lengthFieldName); 
            }
        }
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 17 * hash + (this.elementType != null ? this.elementType.hashCode() : 0);
        hash = 17 * hash + this.numericalLength;
        hash = 17 * hash + (this.lengthFieldName != null ? this.lengthFieldName.hashCode() : 0);
        hash = 17 * hash + (this.fixedLength ? 1 : 0);
        return hash;
    }
}
