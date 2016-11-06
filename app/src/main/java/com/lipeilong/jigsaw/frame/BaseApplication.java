package com.lipeilong.jigsaw.frame;

import android.app.Application;

import com.lipeilong.jigsaw.env.Env;
import com.lipeilong.jigsaw.log.CrashHandler;

/**
 * Created by lipeilong on 16/10/29.
 */

public class BaseApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Env.setContext(getApplicationContext());

        CrashHandler.getInstance().init(getApplicationContext());
    }
}
