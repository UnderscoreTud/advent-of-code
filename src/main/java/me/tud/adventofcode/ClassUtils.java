package me.tud.adventofcode;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarFile;

public final class ClassUtils {
    
    private static final File sourceLocation;
    
    static {
        URL location = Main.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation();
        File fileLocation;
        try {
            fileLocation = new File(location.toURI());
        } catch (URISyntaxException e) {
            fileLocation = new File(location.getFile());
        }
        sourceLocation = fileLocation;
    }

    private ClassUtils() {
        throw new UnsupportedOperationException();
    }

    public static List<String> getSubpackages(String basePackage) throws IOException {
        return getPackages().stream()
                .filter(pkg -> pkg.startsWith(basePackage + '.'))
                .toList();
    }

    public static List<String> getPackages() throws IOException {
        String basePackage = Main.class.getPackageName();
        if (sourceLocation.getName().startsWith(".jar"))
            return getJarPackages(basePackage);
        return getDirPackages(basePackage);
    }
    
    private static List<String> getJarPackages(String basePackage) throws IOException {
        try (JarFile jar = new JarFile(sourceLocation)) {
            List<String> packages = new ArrayList<>();
            jar.entries().asIterator().forEachRemaining(entry -> {
                String name;
                if (entry.isDirectory() && (name = entry.getName().replace('/', '.')).startsWith(basePackage))
                    packages.add(name);
            });
            return packages;
        }
    }

    private static List<String> getDirPackages(String basePackage) {
        File parentDirectory = new File(sourceLocation, basePackage.replace('.', '/'));
        List<String> packages = new ArrayList<>();
        Optional.ofNullable(parentDirectory.listFiles(File::isDirectory)).stream()
                .flatMap(Arrays::stream)
                .peek(file -> packages.addAll(getDirPackages(basePackage + '.' + file.getName())))
                .map(File::getName)
                .map(fileName -> basePackage + '.' + fileName)
                .forEach(packages::add);
        return packages;
    }

}
