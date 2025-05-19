package api;

public interface StateSavable
{
    byte[] saveState();
    void restoreState(byte[] stateData);
}
