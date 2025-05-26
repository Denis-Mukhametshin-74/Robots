package log;

public enum LogLevel
{
    TRACE(0),
    DEBUG(1),
    INFO(2),
    WARNING(3),
    ERROR(4),
    FATAl(5);
    
    private final int intLevel;

    LogLevel(int intLevel)
    {
        this.intLevel = intLevel;
    }

    public int getIntLevel()
    {
        return intLevel;
    }
}
