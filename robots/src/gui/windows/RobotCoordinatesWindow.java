package gui.windows;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import api.states.SavableJInternalFrame;

public class RobotCoordinatesWindow extends SavableJInternalFrame
{
    private final JLabel coordinatesLabel;
    public RobotCoordinatesWindow()
    {
        super("robotCoordinatesWindow", "Координаты робота");
        coordinatesLabel = new JLabel("X: 0, Y: 0, Направление: 0");
        coordinatesLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(coordinatesLabel, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }
}
