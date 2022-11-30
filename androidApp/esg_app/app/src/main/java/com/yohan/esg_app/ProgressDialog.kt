package com.yohan.esg_app

import android.app.Dialog
import android.content.Context
import android.view.Window
import androidx.core.app.DialogCompat

class ProgressDialog:Dialog{

    constructor(context:Context): super(context){

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_progress)


    }

}