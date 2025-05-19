package api;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Iterator;

import javax.swing.JInternalFrame;

import log.Logger;

public final class WindowStateManager
{
    private static final String WINDOW_STATE_FILE = System.getProperty("user.home") + "/.robot_window_state.cfg";

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

    public static void saveStates(Frame mainFrame, Collection<JInternalFrame> frames)
    {
        List<byte[]> states = new ArrayList<>();

        String mainState = String.format("%d,%d,%d,%d,%b,%b",
                mainFrame.getX(), mainFrame.getY(),
                mainFrame.getWidth(), mainFrame.getHeight(),
                (mainFrame.getExtendedState() & Frame.MAXIMIZED_BOTH) != 0,
                (mainFrame.getExtendedState() & Frame.ICONIFIED) != 0);
        states.add(mainState.getBytes());

        for (JInternalFrame frame : frames)
        {
            if (frame instanceof StateSavable)
            {
                states.add(((StateSavable)frame).saveState());
            }
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(WINDOW_STATE_FILE)))
        {
            oos.writeObject(states);
        }
        catch (IOException e)
        {
            Logger.error("Ошибка сохранения состояния окон: " + e.getMessage());
        }
    }

    public static void restoreWindowsState(Collection<? extends StateSavable> windows)
    {
        try
        {
            List<byte[]> states = loadStates();
            if (states.size() > 1)
            {
                Iterator<? extends StateSavable> windowIter = windows.iterator();
                Iterator<byte[]> stateIter = states.subList(1, states.size()).iterator();

                while (windowIter.hasNext() && stateIter.hasNext())
                {
                    windowIter.next().restoreState(stateIter.next());
                }
            }
        }
        catch (Exception e)
        {
            Logger.debug("Ошибка восстановления окон: " + e.getMessage());
        }
    }

    private static List<byte[]> loadStates() throws IOException, ClassNotFoundException
    {
        Path path = Paths.get(WINDOW_STATE_FILE);
        if (!Files.exists(path))
        {
            return Collections.emptyList();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(WINDOW_STATE_FILE)))
        {
            return (List<byte[]>) ois.readObject();
        }
    }
}
