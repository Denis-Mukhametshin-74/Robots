package api;

import log.Logger;

import java.util.Locale;
import javax.swing.UIManager;

public final class LocalizationManager
{
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
