package app;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import api.localization.LocalizationManager;
import api.states.StateSavable;
import api.states.WindowStateManager;

import gui.components.MenuBuilder;
import gui.windows.GameWindow;
import gui.windows.LogWindow;
import gui.windows.RobotCoordinatesWindow;

import log.Logger;

public class MainApplication extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();

    public MainApplication()
    {
        initializeApplication();
        setupWindows();
        setupMenu();
        setupWindowClosingHandler();
    }

    private void initializeApplication()
    {
        LocalizationManager.setRussianLocale();
        WindowStateManager.restoreMainFrameState(this);
        setContentPane(desktopPane);
    }

    private void setupWindows()
    {
        GameWindow gameWindow = createGameWindow();
        LogWindow logWindow = createLogWindow();
        RobotCoordinatesWindow robotCoordinatesWindow = createRobotCoordinatesWindow();
        WindowStateManager.restoreWindowsState(Arrays.asList(gameWindow, logWindow, robotCoordinatesWindow));
    }

    private List<StateSavable> getAllSavableWindows() {
        List<StateSavable> windows = new ArrayList<>();
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame instanceof StateSavable) {
                windows.add((StateSavable) frame);
            }
        }
        return windows;
    }

    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        configureWindow(logWindow, 10, 120, 300, 730);
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected GameWindow createGameWindow()
    {
        GameWindow gameWindow = new GameWindow();
        configureWindow(gameWindow, 320, 10, 840, 840);
        return gameWindow;
    }

    protected RobotCoordinatesWindow createRobotCoordinatesWindow()
    {
        RobotCoordinatesWindow robotCoordinatesWindow = new RobotCoordinatesWindow();
        configureWindow(robotCoordinatesWindow, 10, 10, 300, 100);
        return robotCoordinatesWindow;
    }

    private void configureWindow(JInternalFrame window, int x, int y, int width, int height)
    {
        window.setLocation(x, y);
        window.setSize(width, height);
        window.setResizable(true);
        window.setClosable(true);
        window.setMaximizable(true);
        window.setIconifiable(true);
        addWindow(window);
    }

    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private void setupMenu()
    {
        MenuBuilder menuBuilder = new MenuBuilder(
                this::setLookAndFeel,
                this::confirmAndExit
        );
        setJMenuBar(menuBuilder.build());
    }

    private void setupWindowClosingHandler()
    {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                confirmAndExit();
            }
        });
    }

    private void confirmAndExit()
    {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Вы действительно хотите выйти?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION)
        {
            saveAndExit();
        }
    }

    private void saveAndExit()
    {
        try
        {
            WindowStateManager.saveStates(this, getAllSavableWindows());
            Arrays.stream(desktopPane.getAllFrames())
                    .forEach(frame -> {
                        frame.setVisible(false);
                        frame.dispose();
                    });
            System.exit(0);
        }
        catch (Exception e)
        {
            Logger.error("Ошибка при сохранении состояния: " + e.getMessage());
            System.exit(1);
        }
    }

    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (Exception e)
        {
            Logger.debug("Не удалось установить LookAndFeel: " + e.getMessage());
        }
    }
}
