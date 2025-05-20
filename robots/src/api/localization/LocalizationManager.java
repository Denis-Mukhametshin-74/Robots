package api.localization;

import java.text.MessageFormat;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.UIManager;

import log.Logger;

public final class LocalizationManager
{
    private static ResourceBundle bundle;
    private static Locale currentLocale;
    private static final Map<String, MessageFormat> messageFormatCache = new HashMap<>();

    public static void setLocale(Locale locale)
    {
        try
        {
            currentLocale = locale;
            Locale.setDefault(locale);
            bundle = ResourceBundle.getBundle("localization.messages", locale);

            UIManagerConfigurator.configureUIManager();

            Logger.debug("Locale set to: " + locale);
        }
        catch (MissingResourceException e)
        {
            Logger.error("Resource bundle not found for locale: " + locale);
            bundle = ResourceBundle.getBundle("localization.messages", SupportedLocale.RUSSIAN.getLocale());
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

    public static void setRussianLocale()
    {
        try
        {
            Locale.setDefault(new Locale("ru", "RU"));
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            UIManager.put("OptionPane.yesButtonText", "Да");
            UIManager.put("OptionPane.noButtonText", "Нет");
            UIManager.put("OptionPane.cancelButtonText", "Отмена");
            UIManager.put("OptionPane.okButtonText", "ОК");

            UIManager.put("FileChooser.openButtonText", "Открыть");
            UIManager.put("FileChooser.saveButtonText", "Сохранить");
            UIManager.put("FileChooser.cancelButtonText", "Отмена");
            UIManager.put("FileChooser.fileNameLabelText", "Имя файла");
            UIManager.put("FileChooser.filesOfTypeLabelText", "Типы файлов");
            UIManager.put("FileChooser.openDialogTitleText", "Открыть");
            UIManager.put("FileChooser.saveDialogTitleText", "Сохранить");
            UIManager.put("FileChooser.lookInLabelText", "Папка");
            UIManager.put("FileChooser.upFolderToolTipText", "На уровень выше");
            UIManager.put("FileChooser.homeFolderToolTipText", "Домашняя папка");

            UIManager.put("ColorChooser.okText", "ОК");
            UIManager.put("ColorChooser.cancelText", "Отмена");
            UIManager.put("ColorChooser.previewText", "Предпросмотр");
        }
        catch (Exception e)
        {
            Logger.error("Ошибка при установке русского языка: " + e.getMessage());
        }
    }
}