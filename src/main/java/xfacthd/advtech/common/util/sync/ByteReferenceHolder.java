package xfacthd.advtech.common.util.sync;

public class ByteReferenceHolder
{
    private byte lastKnownValue = 0;
    private byte value = 0;

    public byte get() { return value; }

    public void set(byte val) { value = val; }

    public boolean isDirty()
    {
        byte val = get();
        boolean flag = val != lastKnownValue;
        lastKnownValue = val;
        return flag;
    }
}