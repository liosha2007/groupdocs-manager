
package com.github.liosha2007.android.groupdocs.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.github.liosha2007.android.groupdocs.common.Handler;

//@ReportsCrashes(formKey = "DashboardFragment"/*, formUri = "http://www.yourselectedbackend.com/reportpath"*/)
public class GMApplication extends Application {
    protected static final String PREFERENCES_NAME = "application-preferences";
    protected static GMApplication application;

    public GMApplication() {
        super();
        application = this;
    }

    @Override
    public void onCreate() {
//        ACRA.init(this);
        super.onCreate();
        Handler.initialize();
    }

    public static GMApplication getInstance() {
        return application;
    }

    public SharedPreferences getSharedPreferences() {
        return this.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
}
