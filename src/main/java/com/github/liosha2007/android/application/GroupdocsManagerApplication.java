
package com.github.liosha2007.android.application;

import android.app.Application;

import com.github.liosha2007.android.common.Handler;

//@ReportsCrashes(formKey = "DashboardFragment"/*, formUri = "http://www.yourselectedbackend.com/reportpath"*/)
public class GroupdocsManagerApplication
        extends Application {


    @Override
    public void onCreate() {
//        ACRA.init(this);
        super.onCreate();
        Handler.initialize();
    }

}
