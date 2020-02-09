package com.example.phsconverter.utils;

import com.example.phsconverter.converter.Data;

import java.util.ArrayList;

public class TextUtils {
    public static String toPlainText(ArrayList<Data> data) {
        StringBuilder plainText = new StringBuilder();
        for (Data dataToConvert : data) {
            plainText.append(dataToConvert.toString());
        }

        return plainText.toString();
    }
}
