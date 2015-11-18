package sk.fei.mobv.pivarci.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import sk.fei.mobv.pivarci.auth.MyAuthenticator;

public class MyAuthenticatorService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        MyAuthenticator authenticator = new MyAuthenticator(this);
        return authenticator.getIBinder();
    }
}
