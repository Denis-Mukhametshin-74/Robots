package app;

import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import api.LocalizationManager;
import api.WindowStateManager;
import gui.GameWindow;
import gui.LogWindow;
import log.Logger;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается. 
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    
    public MainApplicationFrame()
    {
        LocalizationManager.setRussianLocale();
        WindowStateManager.restoreMainFrameState(this);

        setContentPane(desktopPane);
        
        LogWindow logWindow = createLogWindow();
        GameWindow gameWindow = createGameWindow();

        WindowStateManager.restoreWindowsState(Arrays.asList(logWindow, gameWindow));

        setJMenuBar(generateMenuBar());
        setupWindowClosingHandler();
    }
    
    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        configureWindow(logWindow, 10, 10, 300, 800);
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected GameWindow createGameWindow()
    {
        GameWindow gameWindow = new GameWindow();
        configureWindow(gameWindow, 320, 10, 400, 400);
        return gameWindow;
    }
    
    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
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
            WindowStateManager.saveStates(this, Arrays.asList(desktopPane.getAllFrames()));
            for (JInternalFrame frame : desktopPane.getAllFrames())
            {
                frame.setVisible(false);
                frame.dispose();
            }
            System.exit(0);
        }
    }
    
    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        addLookAndFeelMenuItem(lookAndFeelMenu, "Системная схема",
                KeyEvent.VK_S, UIManager.getSystemLookAndFeelClassName());

        addLookAndFeelMenuItem(lookAndFeelMenu, "Универсальная схема",
                KeyEvent.VK_U, UIManager.getCrossPlatformLookAndFeelClassName());

        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        addTestMenuItem(testMenu, "Сообщение в лог", KeyEvent.VK_S,
                () -> Logger.debug("Новая строка"));

        JMenu exitMenu = new JMenu("Выход");
        exitMenu.setMnemonic(KeyEvent.VK_Q);

        addExitMenuItem(exitMenu, "Закрыть приложение", KeyEvent.VK_C);

        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        menuBar.add(exitMenu);
        return menuBar;
    }

    private void addLookAndFeelMenuItem(JMenu menu, String text, int mnemonic, String lookAndFeel)
    {
        JMenuItem menuItem = new JMenuItem(text, mnemonic);
        menuItem.addActionListener((event) -> {
            setLookAndFeel(lookAndFeel);
            this.invalidate();
        });
        menu.add(menuItem);
    }

    private void addTestMenuItem(JMenu menu, String text, int mnemonic, Runnable action)
    {
        JMenuItem menuItem = new JMenuItem(text, mnemonic);
        menuItem.addActionListener((event) -> action.run());
        menu.add(menuItem);
    }

    private void addExitMenuItem(JMenu menu, String text, int mnemonic)
    {
        JMenuItem menuItem = new JMenuItem(text, mnemonic);
        menuItem.addActionListener((event) -> confirmAndExit());
        menu.add(menuItem);
    }
    
    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }
}
