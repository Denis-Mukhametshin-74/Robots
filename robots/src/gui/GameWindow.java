package gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import api.SavableJInternalFrame;

public class GameWindow extends SavableJInternalFrame
{
    private final GameVisualizer m_visualizer;
    public GameWindow() 
    {
        super("gameWindow");
        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }
}
