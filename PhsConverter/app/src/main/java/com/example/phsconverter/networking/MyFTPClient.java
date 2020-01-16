package com.example.phsconverter.networking;

import android.content.Context;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.FileInputStream;

import static com.example.phsconverter.utils.Constants.HOST;
import static com.example.phsconverter.utils.Constants.PASSWORD;
import static com.example.phsconverter.utils.Constants.PORT;
import static com.example.phsconverter.utils.Constants.TAG;
import static com.example.phsconverter.utils.Constants.USERNAME;

public class MyFTPClient {
    private static FTPClient mFTPClient = null;

    public static boolean ftpConnect() {
        try {
            mFTPClient = new FTPClient();
            // connecting to the host
            mFTPClient.connect(HOST, PORT);

            // now check the reply code, if positive mean connection success
            if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
                boolean status = mFTPClient.login(USERNAME, PASSWORD);
                mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                mFTPClient.enterLocalPassiveMode();
                Log.i(TAG, status + " ");
                return status;
            }
        } catch (Exception e) {
            Log.d(TAG, "Error: could not connect to host " + HOST + "\n" + e);
        }

        return false;
    }

    public static boolean ftpDisconnect() {
        try {
            mFTPClient.logout();
            mFTPClient.disconnect();
            return true;
        } catch (Exception e) {
            Log.d(TAG, "Error occurred while disconnecting from ftp server.");
        }

        return false;
    }

    public static String ftpGetCurrentWorkingDirectory() {
        try {
            return mFTPClient.printWorkingDirectory();
        } catch (Exception e) {
            Log.d(TAG, "Error: could not get current working directory.");
        }

        return null;
    }

    public static boolean ftpChangeDirectory(String directory_path) {
        try {
            mFTPClient.changeWorkingDirectory(directory_path);
        } catch (Exception e) {
            Log.d(TAG, "Error: could not change directory to " + directory_path);
        }

        return false;
    }

    public static String[] ftpPrintFilesList(String dir_path) {
        String[] fileList = null;
        try {
            FTPFile[] ftpFiles = mFTPClient.listFiles(dir_path);
            int length = ftpFiles.length;
            fileList = new String[length];
            for (int i = 0; i < length; i++) {
                String name = ftpFiles[i].getName();
                boolean isFile = ftpFiles[i].isFile();

                if (isFile) {
                    fileList[i] = "File :: " + name;
                    Log.i(TAG, "File : " + name);
                } else {
                    fileList[i] = "Directory :: " + name;
                    Log.i(TAG, "Directory : " + name);
                }
            }
            return fileList;
        } catch (Exception e) {
            e.printStackTrace();
            return fileList;
        }
    }

    public static boolean ftpMakeDirectory(String new_dir_path) {
        try {
            return mFTPClient.makeDirectory(new_dir_path);
        } catch (Exception e) {
            Log.d(TAG, "Error: could not create new directory named "
                    + new_dir_path);
        }

        return false;
    }

    public boolean ftpUpload(String srcFilePath, String desFileName,
                             String desDirectory, Context context) {
        boolean status = false;
        try {
            FileInputStream srcFileStream = new FileInputStream(srcFilePath);
            status = mFTPClient.storeFile(desFileName, srcFileStream);
            srcFileStream.close();
            return status;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "upload failed: " + e);
        }

        return status;
    }
}
