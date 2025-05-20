package api.states;

import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;

import log.Logger;

public abstract class SavableJInternalFrame extends JInternalFrame implements StateSavable
{
    protected final String windowId;
    private static final String DELIMITER = ",";

    public SavableJInternalFrame(String windowId, String title)
    {
        super(title, true, true, true, true);
        this.windowId = windowId;
    }

    @Override
    public String getWindowId()
    {
        return windowId;
    }

    @Override
    public byte[] saveState()
    {
        String state = String.join(DELIMITER,
                Integer.toString(getX()),
                Integer.toString(getY()),
                Integer.toString(getWidth()),
                Integer.toString(getHeight()),
                Boolean.toString(isMaximum()),
                Boolean.toString(isIcon()));
        return state.getBytes();
    }

    @Override
    public void restoreState(byte[] stateData)
    {
        String stateStr = new String(stateData);
        String[] state = stateStr.split(DELIMITER);

        if (state.length >= 6)
        {
            try
            {
                setBounds(
                        Integer.parseInt(state[0]),
                        Integer.parseInt(state[1]),
                        Integer.parseInt(state[2]),
                        Integer.parseInt(state[3])
                );

                if (Boolean.parseBoolean(state[4]))
                {
                    try
                    {
                        setMaximum(true);
                    }
                    catch (PropertyVetoException e)
                    {
                        Logger.debug("Не удалось развернуть окно " + windowId);
                    }
                }
                if (Boolean.parseBoolean(state[5]))
                {
                    try
                    {
                        setIcon(true);
                    }
                    catch (PropertyVetoException e)
                    {
                        Logger.debug("Не удалось свернуть окно " + windowId);
                    }
                }
            }
            catch (NumberFormatException e)
            {
                Logger.debug("Ошибка восстановления состояния окна " + windowId);
            }
        }
    }
}
