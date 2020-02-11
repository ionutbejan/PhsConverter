package com.example.phsconverter.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static android.Manifest.permission.READ_PHONE_STATE;

public class FileUtils {
    private final static String CONVERTED = "Converted";
    public final static String PHS_EXTENSION = ".phs";
    public final static String LAB_EXTENSION = ".lab";
    private final static String ZIP_EXTENSION = ".zip";
    private static final int BUFFER = 1024;

    public static String getApplicationDataPath(Context context) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getApplicationName(context);
    }

    private static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    private static String getConvertedDirPath(Context context) {
        File phsDir = new File(Environment.getExternalStorageDirectory().getPath(), getApplicationName(context));

        File convertedDir = new File(phsDir, CONVERTED);
        if (!convertedDir.exists())
            convertedDir.mkdirs();
        return convertedDir.getPath();
    }

    public static String writeToFile(String data, Context context, String extension, long time) {
        File fileToWrite = new File(getConvertedDirPath(context), generateFileName(context, time) + extension);
        try (FileOutputStream fOut = new FileOutputStream(fileToWrite)) {
            fOut.write(data.getBytes());
            return fileToWrite.getPath();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(Constants.TAG, "File write failed: " + e.toString());
            return null;
        }
    }

    private static String generateFileName(Context context, long time) {
        int phone = ContextCompat.checkSelfPermission(context,
                READ_PHONE_STATE);

        String fileName = "";

        if (phone == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    fileName = telephonyManager.getImei();
                } else
                    fileName = Settings.Secure.ANDROID_ID;
        } else {
            fileName = "unidentified";
        }

        return fileName + "_" + getFormattedDate(time);
    }

    private static String getFormattedDate(long time) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm", Locale.getDefault());
        return dateFormat.format(time);
    }

    public static boolean zip(Context context, String[] files, long time) {
        File zipFile = new File(getConvertedDirPath(context), getFormattedDate(time) + ZIP_EXTENSION);
        try (FileOutputStream dest = new FileOutputStream(zipFile)) {
            BufferedInputStream origin;
            try (ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest))) {
                byte[] data = new byte[BUFFER];

                for (String file : files) {
                    FileInputStream fi = new FileInputStream(file);
                    origin = new BufferedInputStream(fi, BUFFER);

                    ZipEntry entry = new ZipEntry(file.substring(file.lastIndexOf("/") + 1));
                    out.putNextEntry(entry);
                    int count;

                    while ((count = origin.read(data, 0, BUFFER)) != -1) {
                        out.write(data, 0, count);
                    }
                    origin.close();
                }
                out.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<File> getConvertedFiles(Context context) {
        ArrayList<File> inFiles = new ArrayList<>();
        File phsDir = new File(getConvertedDirPath(context));
        if (phsDir.exists()) {
            File[] files = phsDir.listFiles();
            for (File file : files) {
                if (file.getName().endsWith(".zip")) {
                    inFiles.add(file);
                }
            }
        }
        Collections.reverse(inFiles);
        return inFiles;
    }
}
