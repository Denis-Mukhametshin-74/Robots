package gui.components;

import java.awt.event.KeyEvent;
import java.util.function.Consumer;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

import api.localization.LocalizationManager;
import api.localization.SupportedLocale;
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
        createLanguageMenu();
        return menuBar;
    }

    private void createLookAndFeelMenu()
    {
        JMenu menu = new JMenu(LocalizationManager.getString("menu.view"));
        menu.setMnemonic(KeyEvent.VK_V);
        menu.getAccessibleContext().setAccessibleDescription(
                LocalizationManager.getString("menu.view.description"));

        addMenuItem(menu, LocalizationManager.getString("menu.view.system"), KeyEvent.VK_S,
                () -> lookAndFeelSetter.accept(UIManager.getSystemLookAndFeelClassName()));

        addMenuItem(menu, LocalizationManager.getString("menu.view.cross"), KeyEvent.VK_U,
                () -> lookAndFeelSetter.accept(UIManager.getCrossPlatformLookAndFeelClassName()));

        menuBar.add(menu);
    }

    private void createTestMenu()
    {
        JMenu menu = new JMenu(LocalizationManager.getString("menu.tests"));
        menu.setMnemonic(KeyEvent.VK_T);
        menu.getAccessibleContext().setAccessibleDescription(
                LocalizationManager.getString("menu.tests.description"));

        addMenuItem(menu, LocalizationManager.getString("menu.tests.log"), KeyEvent.VK_S,
                () -> Logger.debug(LocalizationManager.getString("menu.tests.log.message")));

        menuBar.add(menu);
    }

    private void createExitMenu()
    {
        JMenu menu = new JMenu(LocalizationManager.getString("menu.exit"));
        menu.setMnemonic(KeyEvent.VK_Q);

        addMenuItem(menu, LocalizationManager.getString("menu.exit.close"), KeyEvent.VK_C, exitHandler);

        menuBar.add(menu);
    }

    private void createLanguageMenu()
    {
        JMenu menu = new JMenu(LocalizationManager.getString("menu.language"));
        menu.setMnemonic(KeyEvent.VK_L);

        for (SupportedLocale locale : LocalizationManager.getSupportedLocales()) {
            JMenuItem item = new JMenuItem(locale.getDisplayName());
            item.addActionListener(e -> {
                LocalizationManager.setLocale(locale.getLocale());
                updateMenuLocalization();
            });
            menu.add(item);
        }

        menuBar.add(menu);
    }

    private void updateMenuLocalization() {
        // Можно добавить логику обновления меню при смене языка
        // В текущей реализации меню пересоздается при каждом вызове build()
    }

    private void addMenuItem(JMenu parentMenu, String text, int mnemonic, Runnable action)
    {
        JMenuItem menuItem = new JMenuItem(text, mnemonic);
        menuItem.addActionListener(e -> action.run());
        parentMenu.add(menuItem);
    }
}