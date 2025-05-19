package api.states;

public interface StateSavable
{
    String getWindowId();
    byte[] saveState();
    void restoreState(byte[] stateData);
}
