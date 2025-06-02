package gui.components;

import api.localization.LocalizationManager;
import api.robots.ExternalRobot;
import api.robots.RobotLoader;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class RobotMenuBuilder {
    private final GameVisualizer visualizer;

    public RobotMenuBuilder(GameVisualizer visualizer) {
        this.visualizer = visualizer;
    }

    public JMenu buildRobotMenu() {
        JMenu robotMenu = new JMenu(LocalizationManager.getString("menu.robots"));

        JMenuItem loadItem = new JMenuItem(LocalizationManager.getString("menu.robots.load"));
        loadItem.addActionListener(this::handleLoadRobot);
        robotMenu.add(loadItem);

        JMenuItem defaultItem = new JMenuItem(LocalizationManager.getString("menu.robots.default"));
        defaultItem.addActionListener(e -> visualizer.setExternalRobot(null));
        robotMenu.add(defaultItem);

        return robotMenu;
    }

    private void handleLoadRobot(ActionEvent e) {
        File jarFile = RobotLoader.selectJarFile();
        if (jarFile == null) return;

        try {
            Class<? extends ExternalRobot> robotClass = RobotLoader.loadFirstRobotClass(jarFile);
            if (robotClass != null) {
                ExternalRobot robot = robotClass.getDeclaredConstructor().newInstance();
                visualizer.setExternalRobot(robot);
                JOptionPane.showMessageDialog(null,
                        "Робот успешно загружен: " + robot.getRobotType(),
                        "Успех", JOptionPane.INFORMATION_MESSAGE);
            } else {
                showError("В выбранном JAR-файле не найдено подходящих роботов");
            }
        } catch (Exception ex) {
            showError("Ошибка загрузки робота: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(null,
                message,
                "Ошибка",
                JOptionPane.ERROR_MESSAGE);
    }
}