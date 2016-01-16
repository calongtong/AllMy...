package com.smartmadsoft.xposed.aio.tweaks;

import android.content.Context;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class S5TouchWizJunk {
    public static void hook(final XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            XposedHelpers.findAndHookMethod("com.sec.android.app.popupuireceiver.BatteryCover", lpparam.classLoader, "showPopupBatteryCover", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    param.setResult(null);
                }
            });

            XposedHelpers.findAndHookMethod("com.sec.android.app.popupuireceiver.PopupuiService", lpparam.classLoader, "showUSBCDetacheddDialog", Context.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    param.setResult(null);
                }
            });
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }

    public static void hookUI(final XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.phone.PhoneStatusBar", lpparam.classLoader, "showHideQConnectLayout", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    param.setResult(null);
                }
            });

            XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.phone.PhoneStatusBarPolicy", lpparam.classLoader, "updateVolumeZen", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    param.setResult(null);
                    //XposedBridge.log("updateVolumeZen");
                }
            });
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }
}
