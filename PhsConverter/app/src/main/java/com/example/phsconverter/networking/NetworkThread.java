package com.example.phsconverter.networking;

import android.os.AsyncTask;

public class NetworkThread extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... voids) {
        boolean connected = FTPManager.ftpConnect();
        return null;
    }
}
