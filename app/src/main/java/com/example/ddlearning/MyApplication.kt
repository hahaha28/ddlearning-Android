package com.example.ddlearning

import android.app.Application
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.kongzue.dialog.util.DialogSettings
import com.tencent.mmkv.MMKV

class MyApplication :Application(){

    override fun onCreate() {
        super.onCreate()
        initDialogSetting()

        MMKV.initialize(this)
    }

    private fun initDialogSetting(){
        DialogSettings.style = DialogSettings.STYLE.STYLE_MIUI
        DialogSettings.autoShowInputKeyboard = true
        DialogSettings.okButtonDrawable = ResourcesCompat.getDrawable(resources,R.drawable.dialog_ok_btn_drawable,null)
    }
}