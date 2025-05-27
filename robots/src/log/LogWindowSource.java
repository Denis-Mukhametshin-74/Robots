package log;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class LogWindowSource
{
    private final int queueLimit;
    private final Deque<LogEntry> messages;
    private final List<LogChangeListener> listeners;

    public LogWindowSource(int queueLimit)
    {
        this.queueLimit = queueLimit;
        this.messages = new ArrayDeque<>(queueLimit);
        this.listeners = new CopyOnWriteArrayList<>();
    }

    public void registerListener(LogChangeListener listener)
    {
        listeners.add(listener);
    }

    public void unregisterListener(LogChangeListener listener)
    {
        listeners.remove(listener);
    }

    public void append(LogLevel logLevel, String strMessage)
    {
        LogEntry entry = new LogEntry(logLevel, strMessage);

        synchronized (messages)
        {
            if (messages.size() >= queueLimit)
            {
                messages.removeFirst();
            }
            messages.addLast(entry);
        }

        for (LogChangeListener listener : listeners)
        {
            try
            {
                listener.onLogChanged();
            }
            catch (Exception e)
            {
                Logger.error("Error notifying listener: " + e.getMessage());
            }
        }
    }

    public int size()
    {
        synchronized (messages)
        {
            return messages.size();
        }
    }

    public Iterable<LogEntry> all()
    {
        synchronized (messages)
        {
            return new ArrayList<>(messages);
        }
    }

    public void clear()
    {
        synchronized (messages)
        {
            Logger.debug("Clearing log, current size: " + messages.size());
            messages.clear();
        }
    }
}
