package gui.windows;

import api.localization.LocalizationManager;
import api.states.SavableJInternalFrame;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;

import javax.swing.JPanel;

public class LogWindow extends SavableJInternalFrame implements LogChangeListener
{
    private final LogWindowSource logSource;
    private final TextArea logContent;

    public LogWindow(LogWindowSource logSource) 
    {
        super("logWindow", LocalizationManager.getString("window.log"));

        this.logSource = logSource;
        logSource.registerListener(this);

        logContent = new TextArea("");
        logContent.setSize(200, 500);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();

        updateLogContent();
    }

    private void updateLogContent()
    {
        StringBuilder content = new StringBuilder();

        for (LogEntry entry : logSource.all())
        {
            content.append(entry.strMessage()).append("\n");
        }

        logContent.setText(content.toString());
        logContent.invalidate();
    }

    @Override
    public void updateLocalization()
    {
        setTitle(LocalizationManager.getString("window.log"));
    }
    
    @Override
    public void onLogChanged()
    {
        EventQueue.invokeLater(this::updateLogContent);
    }

    @Override
    public void dispose()
    {
        logSource.unregisterListener(this);
        super.dispose();
    }
}
