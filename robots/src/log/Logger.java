package log;

public final class Logger
{
    private static final LogWindowSource defaultLogSource = new LogWindowSource(100);

    public static void debug(String strMessage)
    {
        defaultLogSource.append(LogLevel.DEBUG, strMessage);
    }
    
    public static void error(String strMessage)
    {
        defaultLogSource.append(LogLevel.ERROR, strMessage);
    }

    public static LogWindowSource getDefaultLogSource()
    {
        return defaultLogSource;
    }
}
