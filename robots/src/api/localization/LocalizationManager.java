package api.localization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Properties;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import log.Logger;

public final class LocalizationManager
{
    private static ResourceBundle bundle;
    private static Locale currentLocale;

    private static final Map<String, MessageFormat> messageFormatCache = new ConcurrentHashMap<>();
    private static final List<Runnable> localeChangeListeners = new ArrayList<>();
    private static final Map<String, String> menuItemTextToKeyMap = new ConcurrentHashMap<>();

    private static final String CONFIG_FILE = System.getProperty("user.home") + "/.robot_lang_config.dat";
    private static final String LANG_KEY = "selected_language";

    private static volatile boolean initialized = false;

    private static synchronized void initialize()
    {
        Locale savedLocale = loadSelectedLanguage();
        setLocale(savedLocale != null ? savedLocale : SupportedLocale.RUSSIAN.getLocale());
    }

    public static void ensureInitialized()
    {
        if (!initialized)
        {
            synchronized (LocalizationManager.class)
            {
                if (!initialized)
                {
                    initialize();
                    initialized = true;
                }
            }
        }
    }

    private static void initMenuItemTextMap()
    {
        menuItemTextToKeyMap.clear();
        if (bundle == null) return;

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

    private static void saveSelectedLanguage()
    {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CONFIG_FILE)))
        {
            Properties props = new Properties();
            props.setProperty(LANG_KEY, currentLocale.toString());
            oos.writeObject(props);
        }
        catch (IOException e)
        {
            Logger.error("Failed to save language preference: " + e.getMessage());
        }
    }

    private static Locale loadSelectedLanguage()
    {
        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists())
        {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CONFIG_FILE)))
        {
            Properties props = (Properties) ois.readObject();
            String langValue = props.getProperty(LANG_KEY);

            if (langValue != null)
            {
                String[] parts = langValue.split("_");
                if (parts.length == 1)
                {
                    return new Locale(parts[0]);
                }
                else if (parts.length >= 2)
                {
                    return new Locale(parts[0], parts[1]);
                }
            }
        }
        catch (Exception e)
        {
            Logger.error("Failed to load language preference: " + e.getMessage());
        }
        return null;
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
            saveSelectedLanguage();

            Logger.debug("Locale set to: " + locale.getDisplayLanguage().toLowerCase());
        }
        catch (MissingResourceException e)
        {
            Logger.error("Failed to load bundle for locale: " + locale);
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
                Logger.error("Error in language change event listener: " + e.getMessage());
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
            ensureInitialized();
            return bundle.getString(key);
        }
        catch (MissingResourceException e)
        {
            Logger.error("Missing resource for key: " + key);
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