package api.localization;

import java.util.Locale;

public enum SupportedLocale
{
    RUSSIAN(new Locale("ru", "RU"), "Русский"),
    ENGLISH(Locale.ENGLISH, "English"),
    GERMAN(Locale.GERMAN, "Deutsch");

    private final Locale locale;
    private final String displayName;

    SupportedLocale(Locale locale, String displayName)
    {
        this.locale = locale;
        this.displayName = displayName;
    }

    public Locale getLocale()
    {
        return locale;
    }

    public String getDisplayName()
    {
        return displayName;
    }
}