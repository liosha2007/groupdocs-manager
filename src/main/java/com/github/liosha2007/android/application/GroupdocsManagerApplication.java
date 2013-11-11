
package com.github.liosha2007.android.application;

import android.app.Application;
import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(formKey = "MainActivity"/*, formUri = "http://www.yourselectedbackend.com/reportpath"*/)
public class GroupdocsManagerApplication
    extends Application
{

    @Override
    public void onCreate() {
        ACRA.init(this);
        super.onCreate();
    }

}
