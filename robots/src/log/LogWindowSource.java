package log;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LogWindowSource
{
    private final int queueLimit;
    private final Deque<LogEntry> messages;
    private final List<LogChangeListener> listeners = new CopyOnWriteArrayList<>();

    public LogWindowSource(int queueLimit)
    {
        this.queueLimit = queueLimit;
        this.messages = new ArrayDeque<>(queueLimit);
    }

    public void registerListener(LogChangeListener listener)
    {
        listeners.add(listener);
    }

    public void unregisterListener(LogChangeListener listener)
    {
        listeners.remove(listener);
    }

    public void append(LogLevel logLevel, String message)
    {
        LogEntry entry = new LogEntry(logLevel, message);

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
                System.err.println("Error notifying listener: " + e.getMessage());
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

    public Iterable<LogEntry> range(int startFrom, int count)
    {
        synchronized (messages) {
            if (startFrom < 0 || startFrom >= messages.size())
            {
                return Collections.emptyList();
            }

            List<LogEntry> result = new ArrayList<>();
            int index = 0;
            int added = 0;

            for (LogEntry entry : messages)
            {
                if (index++ >= startFrom)
                {
                    result.add(entry);
                    if (++added >= count)
                    {
                        break;
                    }
                }
            }
            return result;
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
            messages.clear();
        }
    }
}
