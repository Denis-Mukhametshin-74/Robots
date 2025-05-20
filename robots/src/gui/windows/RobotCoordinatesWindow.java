package gui.windows;

import java.awt.BorderLayout;
import java.awt.Font;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;

import api.states.SavableJInternalFrame;
import model.RobotModel;

public class RobotCoordinatesWindow extends SavableJInternalFrame implements Observer
{
    private JLabel coordinatesLabel;
    private final RobotModel robotModel;

    public RobotCoordinatesWindow(RobotModel robotModel)
    {
        super("robotCoordinatesWindow", "Координаты робота");
        this.robotModel = robotModel;

        initComponents();
        robotModel.addObserver(this);
        updateCoordinates();
    }

    private void initComponents()
    {
        coordinatesLabel = new JLabel();
        coordinatesLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(coordinatesLabel, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    private void updateCoordinates()
    {
        String text = String.format("X: %.2f, Y: %.2f, Направление: %.2f",
                robotModel.getRobotPositionX(),
                robotModel.getRobotPositionY(),
                robotModel.getRobotDirection());
        coordinatesLabel.setText(text);
    }

    @Override
    public void update(Observable o, Object arg)
    {
        if (coordinatesLabel != null)
        {
            updateCoordinates();
        }
    }
}
