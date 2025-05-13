package api;

import java.beans.PropertyVetoException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.JInternalFrame;

import log.Logger;

public abstract class SavableJInternalFrame extends JInternalFrame implements StateSavable
{
    protected final String windowId;

    public SavableJInternalFrame(String windowId)
    {
        this.windowId = windowId;
    }

    @Override
    public void saveState(PrintWriter writer)
    {
        writer.printf("%s,%d,%d,%d,%d,%b,%b%n",
                windowId,
                getX(), getY(),
                getWidth(), getHeight(),
                isMaximum(), isIcon());
    }

    @Override
    public void restoreState(Scanner scanner)
    {
        while (scanner.hasNextLine())
        {
            String line = scanner.nextLine();
            if (line.startsWith(windowId))
            {
                String[] state = line.split(",");
                if (state.length >= 7)
                {
                    setBounds(
                            Integer.parseInt(state[1]),
                            Integer.parseInt(state[2]),
                            Integer.parseInt(state[3]),
                            Integer.parseInt(state[4]));

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
                break;
            }
        }
    }
}
