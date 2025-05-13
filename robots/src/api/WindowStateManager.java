package api;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Scanner;

import log.Logger;

import javax.swing.*;

public final class WindowStateManager
{
    private static final String WINDOW_STATE_FILE = System.getProperty("user.home") + "/.robot_window_state.cfg";

    public static void saveStates(Frame mainFrame, Collection<JInternalFrame> frames)
    {
        try (PrintWriter writer = new PrintWriter(new FileWriter(WINDOW_STATE_FILE)))
        {
            writer.printf("%d,%d,%d,%d,%b,%b%n",
                    mainFrame.getX(), mainFrame.getY(),
                    mainFrame.getWidth(), mainFrame.getHeight(),
                    (mainFrame.getExtendedState() & Frame.MAXIMIZED_BOTH) != 0,
                    (mainFrame.getExtendedState() & Frame.ICONIFIED) != 0);

            for (JInternalFrame frame : frames)
            {
                if (frame instanceof StateSavable)
                {
                    ((StateSavable)frame).saveState(writer);
                }
            }
        }
        catch (IOException e)
        {
            Logger.error("Ошибка сохранения состояния окон: " + e.getMessage());
        }
    }

    public static void restoreMainFrameState(Frame frame)
    {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds(inset, inset, screenSize.width - inset*2, screenSize.height - inset*2);

        try (Scanner scanner = new Scanner(new File(WINDOW_STATE_FILE)))
        {
            if (scanner.hasNextLine())
            {
                String[] state = scanner.nextLine().split(",");
                if (state.length == 6)
                {
                    frame.setBounds(
                            Integer.parseInt(state[0]),
                            Integer.parseInt(state[1]),
                            Integer.parseInt(state[2]),
                            Integer.parseInt(state[3]));

                    if (Boolean.parseBoolean(state[4]))
                    {
                        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
                    }
                }
            }
        }
        catch (Exception e)
        {
            Logger.debug("Не удалось восстановить состояние главного окна");
        }
    }

    public static void restoreWindowsState(Collection<? extends StateSavable> windows)
    {
        try (Scanner scanner = new Scanner(new File(WINDOW_STATE_FILE)))
        {
            if (scanner.hasNextLine())
            {
                scanner.nextLine();
            }

            windows.forEach(window -> {
                if (scanner.hasNextLine())
                {
                    window.restoreState(scanner);
                }
            });
        }
        catch (Exception e)
        {
            Logger.debug("Ошибка восстановления окон: " + e.getMessage());
        }
    }
}
