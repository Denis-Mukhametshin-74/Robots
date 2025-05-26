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
import api.states.SavableJInternalFrame;
import api.states.StateSavable;
import api.states.WindowStateManager;

import gui.components.MenuBuilder;
import gui.windows.GameWindow;
import gui.windows.LogWindow;
import gui.windows.RobotCoordinatesWindow;

import model.RobotModel;

import log.Logger;

public class MainApplication extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final RobotModel robotModel = new RobotModel();
    private MenuBuilder menuBuilder;

    public MainApplication()
    {
        initializeApplication();
        setupWindows();
        setupMenu();
        setupWindowClosingHandler();
        LocalizationManager.addLocaleChangeListener(this::updateUIOnLocaleChange);
    }

    private void initializeApplication()
    {
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

    private List<StateSavable> getAllSavableWindows()
    {
        List<StateSavable> windows = new ArrayList<>();
        for (JInternalFrame frame : desktopPane.getAllFrames())
        {
            if (frame instanceof StateSavable)
            {
                windows.add((StateSavable) frame);
            }
        }
        return windows;
    }

    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        configureWindow(logWindow, 10, 120, 300, 730);
        Logger.debug(LocalizationManager.getString("log.working"));
        return logWindow;
    }

    protected GameWindow createGameWindow()
    {
        GameWindow gameWindow = new GameWindow(robotModel);
        configureWindow(gameWindow, 320, 10, 840, 840);
        return gameWindow;
    }

    protected RobotCoordinatesWindow createRobotCoordinatesWindow()
    {
        RobotCoordinatesWindow robotCoordinatesWindow = new RobotCoordinatesWindow(robotModel);
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
        menuBuilder = new MenuBuilder(
                this::setLookAndFeel,
                this::confirmAndExit
        );
        setJMenuBar(menuBuilder.build());
    }

    public void updateUIOnLocaleChange()
    {
        menuBuilder.updateMenuLocalization();
        SwingUtilities.updateComponentTreeUI(this);

        for (JInternalFrame frame : desktopPane.getAllFrames())
        {
            if (frame instanceof SavableJInternalFrame)
            {
                ((SavableJInternalFrame) frame).updateLocalization();
            }
            SwingUtilities.updateComponentTreeUI(frame);
        }
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
        UIManager.put("OptionPane.yesButtonText", LocalizationManager.getString("option.yes"));
        UIManager.put("OptionPane.noButtonText", LocalizationManager.getString("option.no"));

        int result = JOptionPane.showConfirmDialog(
                this,
                LocalizationManager.getString("exit.confirm.message"),
                LocalizationManager.getString("exit.confirm.title"),
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
            Logger.error(LocalizationManager.getString("Ошибка при сохранении состояния: ") + e.getMessage());
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
            Logger.debug(LocalizationManager.getString("Не удалось установить LookAndFeel: ") + e.getMessage());
        }
    }

    @Override
    public void dispose()
    {
        LocalizationManager.removeLocaleChangeListener(this::updateUIOnLocaleChange);
        super.dispose();
    }
}
