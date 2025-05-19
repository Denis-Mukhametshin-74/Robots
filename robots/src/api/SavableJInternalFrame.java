package api;

import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;

import log.Logger;

public abstract class SavableJInternalFrame extends JInternalFrame implements StateSavable
{
    protected final String windowId;
    private static final String DELIMITER = ",";

    public SavableJInternalFrame(String windowId)
    {
        super(windowId, true, true, true, true);
        this.windowId = windowId;
    }

    @Override
    public byte[] saveState()
    {
        String state = String.join(DELIMITER,
                windowId,
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

        if (state.length >= 7 && state[0].equals(windowId))
        {
            try
            {
                setBounds(
                        Integer.parseInt(state[1]),
                        Integer.parseInt(state[2]),
                        Integer.parseInt(state[3]),
                        Integer.parseInt(state[4])
                );

                if (Boolean.parseBoolean(state[5]))
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
                if (Boolean.parseBoolean(state[6]))
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
                Logger.error("Ошибка формата данных состояния: " + e.getMessage());
            }
        }
    }
}
