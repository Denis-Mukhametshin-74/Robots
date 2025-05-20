package api.localization;

import java.text.MessageFormat;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import log.Logger;

public final class LocalizationManager
{
    private static ResourceBundle bundle;
    private static Locale currentLocale;
    private static final Map<String, MessageFormat> messageFormatCache = new HashMap<>();

    static
    {
        setLocale(SupportedLocale.ENGLISH.getLocale());
    }

    public static void setLocale(Locale locale)
    {
        try
        {
            currentLocale = locale;
            Locale.setDefault(locale);
            bundle = ResourceBundle.getBundle("localization.messages", locale, LocalizationManager.class.getClassLoader());

            UIManagerConfigurator.configureUIManager();

            Logger.debug("Locale set to: " + locale);
        }
        catch (MissingResourceException e)
        {
            Logger.error("Resource bundle not found for locale: " + locale);
            try
            {
                bundle = ResourceBundle.getBundle("localization/messages", SupportedLocale.ENGLISH.getLocale(), LocalizationManager.class.getClassLoader());
            }
            catch (MissingResourceException ex)
            {
                Logger.error("Fallback locale also not found");
                throw new RuntimeException("No localization files found", ex);
            }
        }
    }

    public static Locale getCurrentLocale()
    {
        return currentLocale;
    }

    public static String getString(String key)
    {
        try
        {
            return bundle.getString(key);
        }
        catch (MissingResourceException e)
        {
            Logger.error("Missing resource for key: " + key);
            return "!" + key + "!";
        }
    }

    public static String getFormattedString(String key, Object... args)
    {
        String pattern = getString(key);
        return MessageFormat.format(pattern, args);
    }

    public static String getCachedFormattedString(String key, Object... args)
    {
        String pattern = getString(key);

        MessageFormat format = messageFormatCache.get(pattern);
        if (format == null)
        {
            format = new MessageFormat(pattern, currentLocale);
            messageFormatCache.put(pattern, format);
        }

        return format.format(args);
    }

    public static SupportedLocale[] getSupportedLocales()
    {
        return SupportedLocale.values();
    }
}