package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;

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
    
    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.

        setRussianLocale();

        int inset = 50;        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
            screenSize.width  - inset*2,
            screenSize.height - inset*2);

        setContentPane(desktopPane);
        
        
        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmAndExit();
            }
        });
    }
    
    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }
    
    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
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

    private void setRussianLocale()
    {
        try
        {
            Locale.setDefault(new Locale("ru", "RU"));
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            UIManager.put("OptionPane.yesButtonText", "Да");
            UIManager.put("OptionPane.noButtonText", "Нет");
            UIManager.put("OptionPane.cancelButtonText", "Отмена");
            UIManager.put("OptionPane.okButtonText", "ОК");

            UIManager.put("FileChooser.openButtonText", "Открыть");
            UIManager.put("FileChooser.saveButtonText", "Сохранить");
            UIManager.put("FileChooser.cancelButtonText", "Отмена");
            UIManager.put("FileChooser.fileNameLabelText", "Имя файла");
            UIManager.put("FileChooser.filesOfTypeLabelText", "Типы файлов");
            UIManager.put("FileChooser.openDialogTitleText", "Открыть");
            UIManager.put("FileChooser.saveDialogTitleText", "Сохранить");
            UIManager.put("FileChooser.lookInLabelText", "Папка");
            UIManager.put("FileChooser.upFolderToolTipText", "На уровень выше");
            UIManager.put("FileChooser.homeFolderToolTipText", "Домашняя папка");

            UIManager.put("ColorChooser.okText", "ОК");
            UIManager.put("ColorChooser.cancelText", "Отмена");
            UIManager.put("ColorChooser.previewText", "Предпросмотр");

        }
        catch (Exception e)
        {
            Logger.error("Ошибка при установке русского языка: " + e.getMessage());
        }
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
            System.exit(0);
        }
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
