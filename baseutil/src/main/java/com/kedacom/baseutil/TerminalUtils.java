package com.kedacom.baseutil;

import android.text.TextUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by zhoutianjie on 2018/11/20.
 */

public class TerminalUtils {

    public static int getNumCores() {
        try {
            // Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            // Filter to only list the devices we care about
            File[] files = dir.listFiles(new FileFilter() {

                @Override
                public boolean accept(File pathname) {
                    // Check if filename is "cpu", followed by a single digit
                    // number
                    if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                        return true;
                    }
                    return false;
                }
            });
            // Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            // Default to return 1 core
            return 1;
        }
    }

    public static int getCPUFrequencyMax() {
        return readSystemFileAsInt("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
    }

    private static int readSystemFileAsInt(final String pSystemFile) {
        InputStream in = null;
        String content = "";
        try {
            final Process process = new ProcessBuilder(new String[] {
                    "/system/bin/cat", pSystemFile
            }).start();

            in = process.getInputStream();

            final StringBuilder sb = new StringBuilder();
            final Scanner sc = new Scanner(in);
            while (sc.hasNextLine()) {
                sb.append(sc.nextLine());
            }
            content = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(null!=in){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(TextUtils.isEmpty(content.trim())){
            return -1;
        }
        return Integer.parseInt(content);
    }
}
