package io.github.lxgaming.bukkitbootstrap.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class PatchingUtils {
    private static final String JREPATH = jre();

    public static void getandpatchjar() {
        try {
            downloadJarJarPatcher();
            downloadWithMD5("https://launcher.mojang.com/v1/objects/5fafba3f58c40dc51b5c3ca72a98f62dfdae1db7/server.jar", "./cache/original.jar", "a0671390aa0691e70a950155aab06ffb");
            downloadWithMD5("https://github.com/BurritoSpigot/TacoSpigot/releases/download/v1.0/TacoSpigot.patch", "./cache/TacoSpigot.patch", "850672d11680110d1886c63a86133b38");
            runCommand(Arrays.asList(JREPATH, "-jar", "./cache/JarJarPatcher.jar", "patchjar", "./cache/original.jar", "./cache/patched.jar", "./cache/TacoSpigot.patch"));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        
    }

    private static void downloadJarJarPatcher() throws IOException {
        download("https://github.com/CoolMineman/JarJarPatcher/releases/download/v1.0/JarJarPatcher-1.0.jar", "./cache/JarJarPatcher.jar");
    }

    private static void downloadWithMD5(String utlStr, String file, String md5) throws IOException {
        download(utlStr, file);
        runCommand(Arrays.asList(JREPATH, "-jar", "./cache/JarJarPatcher.jar", "verifymd5", file, md5));
    }

    private static void download(String urlStr, String file) throws IOException {
        URL url = new URL(urlStr);
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count = 0;
        while ((count = bis.read(buffer, 0, 1024)) != -1) {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
    }

    private static void runCommand(List<String> command) {
        try {
            ProcessBuilder b = new ProcessBuilder(command);
            Process p = b.start();
            p.waitFor();
            int exitStatus = p.exitValue();
            if (exitStatus != 0) {
                System.exit(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static String jre() {
        final String JAVA_HOME = System.getProperty("java.home");
        final File BIN = new File(JAVA_HOME, "bin");
        File exe = new File(BIN, "java");

        if (!exe.exists()) {
            // We might be on Windows, which needs an exe extension
            exe = new File(BIN, "java.exe");
        }

        if (exe.exists()) {
            return exe.getAbsolutePath();
        }

        try {
            // Just try invoking java from the system path; this of course
            // assumes "java[.exe]" is /actually/ Java
            final String NAKED_JAVA = "java";
            new ProcessBuilder(NAKED_JAVA).start();

            return NAKED_JAVA;
        }
        catch (IOException e)
        {
            return null;
        }
    }
}