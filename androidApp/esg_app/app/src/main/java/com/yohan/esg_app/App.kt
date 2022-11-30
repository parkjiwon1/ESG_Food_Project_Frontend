package com.yohan.esg_app

import android.app.Application
import org.conscrypt.Conscrypt
import java.security.Security

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Security.insertProviderAt(Conscrypt.newProvider(), 1);
    }
}