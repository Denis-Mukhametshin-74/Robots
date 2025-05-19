package gui.components;

import java.awt.event.KeyEvent;
import java.util.function.Consumer;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

import log.Logger;

public class MenuBuilder
{
    private final JMenuBar menuBar;
    private final Consumer<String> lookAndFeelSetter;
    private final Runnable exitHandler;

    public MenuBuilder(Consumer<String> lookAndFeelSetter, Runnable exitHandler)
    {
        this.menuBar = new JMenuBar();
        this.lookAndFeelSetter = lookAndFeelSetter;
        this.exitHandler = exitHandler;
    }

    public JMenuBar build()
    {
        createLookAndFeelMenu();
        createTestMenu();
        createExitMenu();
        return menuBar;
    }

    private void createLookAndFeelMenu()
    {
        JMenu menu = new JMenu("Режим отображения");
        menu.setMnemonic(KeyEvent.VK_V);
        menu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        addMenuItem(menu, "Системная схема", KeyEvent.VK_S,
                () -> lookAndFeelSetter.accept(UIManager.getSystemLookAndFeelClassName()));

        addMenuItem(menu, "Универсальная схема", KeyEvent.VK_U,
                () -> lookAndFeelSetter.accept(UIManager.getCrossPlatformLookAndFeelClassName()));

        menuBar.add(menu);
    }

    private void createTestMenu()
    {
        JMenu menu = new JMenu("Тесты");
        menu.setMnemonic(KeyEvent.VK_T);
        menu.getAccessibleContext().setAccessibleDescription("Тестовые команды");

        addMenuItem(menu, "Сообщение в лог", KeyEvent.VK_S,
                () -> Logger.debug("Новая строка"));

        menuBar.add(menu);
    }

    private void createExitMenu()
    {
        JMenu menu = new JMenu("Выход");
        menu.setMnemonic(KeyEvent.VK_Q);

        addMenuItem(menu, "Закрыть приложение", KeyEvent.VK_C, exitHandler);

        menuBar.add(menu);
    }

    private void addMenuItem(JMenu parentMenu, String text, int mnemonic, Runnable action)
    {
        JMenuItem menuItem = new JMenuItem(text, mnemonic);
        menuItem.addActionListener(e -> action.run());
        parentMenu.add(menuItem);
    }
}