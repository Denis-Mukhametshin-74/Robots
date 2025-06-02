package api.robots;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;

import javax.swing.JFileChooser;

public class RobotLoader {
    public static File selectJarFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Выберите файл робота");
        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".jar") || f.isDirectory();
            }
            public String getDescription() {
                return "JAR файлы (*.jar)";
            }
        });

        return chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION
                ? chooser.getSelectedFile()
                : null;
    }

    @SuppressWarnings("unchecked")
    public static Class<? extends ExternalRobot> loadFirstRobotClass(File jarFile) throws Exception {
        URL jarUrl = new URL("jar:file:" + jarFile.getAbsolutePath() + "!/");
        try (URLClassLoader loader = new URLClassLoader(new URL[]{jarUrl});
             JarFile jar = new JarFile(jarFile)) {

            return jar.stream()
                    .filter(entry -> entry.getName().endsWith(".class"))
                    .map(entry -> toClassName(entry.getName()))
                    .map(className -> {
                        try {
                            Class<?> clazz = loader.loadClass(className);
                            return ExternalRobot.class.isAssignableFrom(clazz)
                                    ? (Class<? extends ExternalRobot>) clazz
                                    : null;
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(clazz -> clazz != null)
                    .findFirst()
                    .orElse(null);
        }
    }

    private static String toClassName(String entryName) {
        return entryName.replace("/", ".")
                .replace(".class", "");
    }
}