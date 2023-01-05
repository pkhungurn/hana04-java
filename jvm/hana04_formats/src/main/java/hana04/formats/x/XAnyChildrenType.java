package hana04.formats.x;

public class XAnyChildrenType implements XChildrenType
{
    @Override
    public boolean isAllowedAsChild(XTemplate template)
    {
        return true;
    }

    private XAnyChildrenType()
    {
        // NO-OP
    }
    private static final XAnyChildrenType instance = new XAnyChildrenType();

    public static XAnyChildrenType v()
    {
        return instance;
    }
}
