package api.localization;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import log.Logger;

public final class LocalizationManager
{
    private static ResourceBundle bundle;
    private static Locale currentLocale;
    private static final Map<String, MessageFormat> messageFormatCache = new HashMap<>();
    private static final List<Runnable> localeChangeListeners = new ArrayList<>();
    private static final Map<String, String> menuItemTextToKeyMap = new HashMap<>();

    static
    {
        setLocale(SupportedLocale.RUSSIAN.getLocale());
        initMenuItemTextMap();
    }

    private static void initMenuItemTextMap()
    {
        menuItemTextToKeyMap.clear();
        Set<String> allKeys = bundle.keySet();

        for (String key : allKeys)
        {
            if (!key.startsWith("menu.") && !key.startsWith("option."))
            {
                continue;
            }

            for (SupportedLocale supportedLocale : SupportedLocale.values())
            {
                try
                {
                    ResourceBundle localeBundle = ResourceBundle.getBundle(
                            "localization.messages",
                            supportedLocale.getLocale(),
                            LocalizationManager.class.getClassLoader()
                    );

                    String localizedText = localeBundle.getString(key);
                    menuItemTextToKeyMap.put(localizedText, key);
                }
                catch (MissingResourceException e)
                {
                    Logger.debug("Key '" + key + "' not found for locale: " + supportedLocale);
                }
            }
        }
    }

    public static String getLocalizedMenuItemText(String originalText) {
        String key = menuItemTextToKeyMap.get(originalText);

        if (key != null) {
            return getString(key);
        }

        Logger.debug("No localization key found for menu item text: " + originalText);
        return originalText;
    }

    public static void setLocale(Locale locale)
    {
        try
        {
            currentLocale = locale;
            Locale.setDefault(locale);
            bundle = ResourceBundle.getBundle("localization.messages", locale, LocalizationManager.class.getClassLoader());

            initMenuItemTextMap();
            notifyLocaleChangeListeners();

            Logger.debug("Язык сменён на: " + locale.getDisplayLanguage().toLowerCase());
        }
        catch (MissingResourceException e)
        {
            Logger.error("Пакет ресурсов языка не найден:" + locale);
        }
    }

    public static void addLocaleChangeListener(Runnable listener)
    {
        localeChangeListeners.add(listener);
    }

    public static void removeLocaleChangeListener(Runnable listener)
    {
        localeChangeListeners.remove(listener);
    }

    private static void notifyLocaleChangeListeners()
    {
        for (Runnable listener : localeChangeListeners)
        {
            try
            {
                listener.run();
            }
            catch (Exception e)
            {
                Logger.error("Ошибка в слушателе событий смены языка: " + e.getMessage());
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
            Logger.error("Отсутствует ресурс для ключа: " + key);
            return key;
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