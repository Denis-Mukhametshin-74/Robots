package api.localization;

import javax.swing.UIManager;

import log.Logger;

public final class UIManagerConfigurator
{
    public static void configureUIManager()
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            if (LocalizationManager.getCurrentLocale().equals(SupportedLocale.RUSSIAN.getLocale()))
            {
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
            else
            {
                UIManager.put("OptionPane.yesButtonText", "Yes");
                UIManager.put("OptionPane.noButtonText", "No");
                UIManager.put("OptionPane.cancelButtonText", "Cancel");
                UIManager.put("OptionPane.okButtonText", "OK");

                UIManager.put("FileChooser.openButtonText", "Open");
                UIManager.put("FileChooser.saveButtonText", "Save");
                UIManager.put("FileChooser.cancelButtonText", "Cancel");
                UIManager.put("FileChooser.fileNameLabelText", "File Name");
                UIManager.put("FileChooser.filesOfTypeLabelText", "File Type");
                UIManager.put("FileChooser.openDialogTitleText", "Open");
                UIManager.put("FileChooser.saveDialogTitleText", "Save");
                UIManager.put("FileChooser.lookInLabelText", "Folder");
                UIManager.put("FileChooser.upFolderToolTipText", "Up Folder");
                UIManager.put("FileChooser.homeFolderToolTipText", "Home Folder");

                UIManager.put("ColorChooser.okText", "OK");
                UIManager.put("ColorChooser.cancelText", "Cancel");
                UIManager.put("ColorChooser.previewText", "Preview");

            }
        }
        catch (Exception e)
        {
            Logger.error("Error configuring UI for locale: " + e.getMessage());
        }
    }
}
