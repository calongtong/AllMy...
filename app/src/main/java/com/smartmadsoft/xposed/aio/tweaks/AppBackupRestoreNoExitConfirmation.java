package com.smartmadsoft.xposed.aio.tweaks;

import android.view.KeyEvent;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AppBackupRestoreNoExitConfirmation {
    public static void hook(final XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            XposedHelpers.findAndHookMethod("mobi.infolife.appbackup.ui.screen.mainpage.ActivityBrPage", lpparam.classLoader, "onKeyDown", int.class, KeyEvent.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    if (((KeyEvent) param.args[1]).getKeyCode() == KeyEvent.KEYCODE_BACK) {
                        XposedHelpers.callMethod(param.thisObject, "finish");
                        param.setResult(true);
                    }
                }
            });
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }
}
