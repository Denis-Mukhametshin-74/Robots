package api.robots;

import javax.swing.JFileChooser;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class RobotLoader
{
    public static List<Class<? extends ExternalRobot>> loadRobotsFromJar(File jarFile) throws Exception
    {
        if (!jarFile.getName().endsWith(".jar"))
        {
            throw new IllegalArgumentException("File must be a JAR archive");
        }

        List<Class<? extends ExternalRobot>> robotClasses = new ArrayList<>();
        JarFile jar = new JarFile(jarFile);
        URL[] urls = { new URL("jar:file:" + jarFile.getAbsolutePath() + "!/") };

        try (URLClassLoader cl = URLClassLoader.newInstance(urls))
        {
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class"))
                {
                    try
                    {
                        String className = entry.getName()
                                .replace("/", ".")
                                .replace(".class", "");

                        Class<?> loadedClass = cl.loadClass(className);
                        if (ExternalRobot.class.isAssignableFrom(loadedClass))
                        {
                            @SuppressWarnings("unchecked")
                            Class<? extends ExternalRobot> robotClass = (Class<? extends ExternalRobot>) loadedClass;
                            robotClasses.add(robotClass);
                        }
                    }
                    catch (Exception e)
                    {
                        System.err.println("Error loading class: " + e.getMessage());
                    }
                }
            }
        }

        return robotClasses;
    }

    public static File selectJarFile()
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select robot JAR file");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter()
        {
            @Override
            public boolean accept(File f)
            {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".jar");
            }

            @Override
            public String getDescription()
            {
                return "JAR files (*.jar)";
            }
        });

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
        {
            return fileChooser.getSelectedFile();
        }
        return null;
    }
}
