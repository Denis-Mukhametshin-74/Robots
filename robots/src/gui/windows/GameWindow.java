package gui.windows;

import api.localization.LocalizationManager;
import api.states.SavableJInternalFrame;

import gui.components.GameVisualizer;

import model.RobotModel;

import java.awt.BorderLayout;

import javax.swing.JPanel;

public class GameWindow extends SavableJInternalFrame
{
    public GameWindow(RobotModel robotModel)
    {
        super("gameWindow", LocalizationManager.getString("window.game"));

        GameVisualizer gameVisualizer = new GameVisualizer(robotModel);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(gameVisualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    @Override
    public void updateLocalization()
    {
        setTitle(LocalizationManager.getString("window.game"));
    }
}
