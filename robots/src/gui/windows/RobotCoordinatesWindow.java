package gui.windows;

import api.localization.LocalizationManager;
import api.states.SavableJInternalFrame;

import model.RobotModel;

import java.awt.BorderLayout;
import java.awt.Font;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class RobotCoordinatesWindow extends SavableJInternalFrame implements Observer
{
    private JLabel coordinatesLabel;
    private final RobotModel robotModel;

    public RobotCoordinatesWindow(RobotModel robotModel)
    {
        super("robotCoordinatesWindow", LocalizationManager.getString("window.coordinates"));

        this.robotModel = robotModel;
        this.robotModel.addObserver(this);

        initComponents();
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
        String text = LocalizationManager.getCachedFormattedString("coordinates.format",
                robotModel.getRobotPositionX(),
                robotModel.getRobotPositionY(),
                robotModel.getRobotDirection());
        coordinatesLabel.setText(text);
    }

    @Override
    public void updateLocalization()
    {
        setTitle(LocalizationManager.getString("window.coordinates"));
        updateCoordinates();
    }

    @Override
    public void update(Observable o, Object arg)
    {
        if (coordinatesLabel != null)
        {
            updateCoordinates();
        }
    }

    @Override
    public void dispose()
    {
        this.robotModel.deleteObserver(this);
        super.dispose();
    }
}
