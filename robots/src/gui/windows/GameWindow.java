package gui.windows;

import api.localization.LocalizationManager;
import api.states.SavableJInternalFrame;

import gui.components.GameVisualizer;

import api.robots.RobotModel;

import java.awt.BorderLayout;

import javax.swing.JPanel;

public class GameWindow extends SavableJInternalFrame
{
    private final GameVisualizer visualizer;

    public GameWindow(RobotModel robotModel)
    {
        super("gameWindow", LocalizationManager.getString("window.game"));

        this.visualizer = new GameVisualizer(robotModel);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    public GameVisualizer getVisualizer()
    {
        return visualizer;
    }

    @Override
    public void updateLocalization()
    {
        setTitle(LocalizationManager.getString("window.game"));
    }
}
