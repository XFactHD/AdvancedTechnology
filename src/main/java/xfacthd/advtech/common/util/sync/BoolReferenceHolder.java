package xfacthd.advtech.common.util.sync;

public class BoolReferenceHolder
{
    private boolean lastKnownValue = false;
    private boolean value = false;

    public boolean get() { return value; }

    public void set(boolean val) { value = val; }

    public boolean isDirty()
    {
        boolean val = get();
        boolean flag = val != lastKnownValue;
        lastKnownValue = val;
        return flag;
    }
}