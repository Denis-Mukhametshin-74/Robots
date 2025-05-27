package gui.components;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;

import api.localization.LocalizationManager;
import api.localization.SupportedLocale;
import app.MainApplication;
import log.Logger;

public class MenuBuilder
{
    private final JMenuBar menuBar;
    private final Consumer<String> lookAndFeelSetter;
    private final Runnable exitHandler;
    private final List<JMenu> menus = new ArrayList<>();

    private final RobotMenuBuilder robotMenuBuilder;

    public MenuBuilder(Consumer<String> lookAndFeelSetter, Runnable exitHandler, GameVisualizer visualizer)
    {
        this.menuBar = new JMenuBar();
        this.lookAndFeelSetter = Objects.requireNonNull(lookAndFeelSetter);
        this.exitHandler = Objects.requireNonNull(exitHandler);
        this.robotMenuBuilder = new RobotMenuBuilder(visualizer);
    }

    public JMenuBar build()
    {
        menuBar.removeAll();
        menus.clear();

        createLookAndFeelMenu();
        createTestMenu();
        createLanguageMenu();
        menuBar.add(robotMenuBuilder.buildRobotMenu());
        createExitMenu();

        return menuBar;
    }

    public void updateMenuLocalization()
    {
        if (menus.size() < 4)
        {
            Logger.error("Not enough menus to update localization");
            return;
        }

        try
        {
            updateMenuTexts();
            updateMenuItemsTexts();
        }
        catch (Exception e)
        {
            Logger.error("Error updating menu localization: " + e.getMessage());
        }
    }

    private void updateMenuTexts()
    {
        String[] menuKeys = {"menu.view", "menu.tests", "menu.language", "menu.exit"};
        for (int i = 0; i < Math.min(menus.size(), menuKeys.length); i++)
        {
            menus.get(i).setText(LocalizationManager.getString(menuKeys[i]));
        }
    }

    private void updateMenuItemsTexts()
    {
        for (JMenu menu : menus)
        {
            for (int i = 0; i < menu.getItemCount(); i++)
            {
                JMenuItem item = menu.getItem(i);
                if (item != null)
                {
                    updateMenuItemText(item);
                }
            }
        }
    }

    private void updateMenuItemText(JMenuItem item)
    {
        String text = item.getText();
        String newText = LocalizationManager.getLocalizedMenuItemText(text);
        if (!text.equals(newText))
        {
            item.setText(newText);
        }
    }

    private void createLookAndFeelMenu()
    {
        JMenu menu = createMenu("menu.view", KeyEvent.VK_V);

        addLocalizedMenuItem(menu, "menu.view.system", KeyEvent.VK_S,
                () -> setLookAndFeel(UIManager.getSystemLookAndFeelClassName()));

        addLocalizedMenuItem(menu, "menu.view.cross", KeyEvent.VK_U,
                () -> setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()));

        addMenu(menu);
    }

    private void createTestMenu()
    {
        JMenu menu = createMenu("menu.tests", KeyEvent.VK_T);

        addLocalizedMenuItem(menu, "menu.tests.log", KeyEvent.VK_S,
                () -> Logger.debug(LocalizationManager.getString("menu.tests.log.message")));

        addMenu(menu);
    }

    private void createExitMenu()
    {
        JMenu menu = createMenu("menu.exit", KeyEvent.VK_Q);
        addLocalizedMenuItem(menu, "menu.exit.close", KeyEvent.VK_C, exitHandler);
        addMenu(menu);
    }

    private void createLanguageMenu()
    {
        JMenu menu = createMenu("menu.language", KeyEvent.VK_L);

        for (SupportedLocale locale : LocalizationManager.getSupportedLocales())
        {
            JMenuItem item = new JMenuItem(locale.getDisplayName());
            item.addActionListener(e -> changeLocale(locale));
            menu.add(item);
        }

        addMenu(menu);
    }

    private JMenu createMenu(String localizationKey, int mnemonic)
    {
        JMenu menu = new JMenu(LocalizationManager.getString(localizationKey));
        menu.setMnemonic(mnemonic);
        return menu;
    }

    private void addLocalizedMenuItem(JMenu parentMenu, String localizationKey, int mnemonic, Runnable action)
    {
        JMenuItem item = new JMenuItem(LocalizationManager.getString(localizationKey), mnemonic);
        item.addActionListener(e -> action.run());
        parentMenu.add(item);
    }

    private void addMenu(JMenu menu)
    {
        menuBar.add(menu);
        menus.add(menu);
    }

    private void setLookAndFeel(String className)
    {
        try
        {
            lookAndFeelSetter.accept(className);
        }
        catch (Exception e)
        {
            Logger.error("Failed to set look and feel: " + e.getMessage());
        }
    }

    private void changeLocale(SupportedLocale locale)
    {
        LocalizationManager.setLocale(locale.getLocale());
        MainApplication mainApp = (MainApplication) SwingUtilities.getWindowAncestor(menuBar);

        if (mainApp != null)
        {
            mainApp.updateUIOnLocaleChange();
        }
        else
        {
            Logger.error("Main application window not found");
        }
    }
}