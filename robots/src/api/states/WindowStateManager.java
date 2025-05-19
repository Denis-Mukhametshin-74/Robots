package api.states;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import log.Logger;

public final class WindowStateManager
{
    private static final String WINDOW_STATE_FILE = System.getProperty("user.home") + "/.robot_window_state.dat";

    public static void restoreMainFrameState(Frame frame)
    {
        Path path = Paths.get(WINDOW_STATE_FILE);
        if (!Files.exists(path))
        {
            setDefaultFrameBounds(frame);
            return;
        }

        try (DataInputStream dis = new DataInputStream(new FileInputStream(WINDOW_STATE_FILE)))
        {
            if (dis.available() > 0 && dis.readUTF().equals("main"))
            {
                frame.setBounds(dis.readInt(), dis.readInt(), dis.readInt(), dis.readInt());
                if (dis.readBoolean())
                {
                    frame.setExtendedState(Frame.MAXIMIZED_BOTH);
                }
            }
        }
        catch (Exception e)
        {
            Logger.error("Ошибка восстановления главного окна: " + e.getMessage());
            setDefaultFrameBounds(frame);
        }
    }

    public static void saveStates(Frame mainFrame, Collection<? extends StateSavable> windows)
    {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(WINDOW_STATE_FILE)))
        {
            saveFrameState(dos, "main",
                    mainFrame.getX(), mainFrame.getY(),
                    mainFrame.getWidth(), mainFrame.getHeight(),
                    (mainFrame.getExtendedState() & Frame.MAXIMIZED_BOTH) != 0);

            for (StateSavable window : windows)
            {
                byte[] state = window.saveState();
                dos.writeUTF(window.getWindowId());
                dos.writeInt(state.length);
                dos.write(state);
            }
        }
        catch (IOException e)
        {
            Logger.error("Ошибка сохранения состояния окон: " + e.getMessage());
        }
    }

    private static void saveFrameState(DataOutputStream dos, String id, int x, int y, int width, int height, boolean isMaximized) throws IOException
    {
        dos.writeUTF(id);
        dos.writeInt(x);
        dos.writeInt(y);
        dos.writeInt(width);
        dos.writeInt(height);
        dos.writeBoolean(isMaximized);
    }

    public static void restoreWindowsState(Collection<? extends StateSavable> windows)
    {
        if (windows.isEmpty()) return;

        Map<String, StateSavable> windowMap = new HashMap<>();
        windows.forEach(w -> windowMap.put(w.getWindowId(), w));

        try (DataInputStream dis = new DataInputStream(new FileInputStream(WINDOW_STATE_FILE)))
        {
            if (dis.available() > 0 && dis.readUTF().equals("main"))
            {
                dis.readInt(); dis.readInt(); dis.readInt(); dis.readInt(); dis.readBoolean();
            }

            while (dis.available() > 0)
            {
                String windowId = dis.readUTF();
                int length = dis.readInt();
                byte[] state = new byte[length];
                dis.readFully(state);

                StateSavable window = windowMap.get(windowId);
                if (window != null)
                {
                    window.restoreState(state);
                }
            }
        }
        catch (Exception e)
        {
            Logger.debug("Ошибка восстановления окон: " + e.getMessage());
        }
    }

    private static void setDefaultFrameBounds(Frame frame)
    {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds(inset, inset, screenSize.width - inset*2, screenSize.height - inset*2);
    }
}
