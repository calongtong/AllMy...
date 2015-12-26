package com.smartmadsoft.xposed.aio.tweaks;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.view.View;
import android.widget.Button;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class NativeFreezer {
    private static Object mAppEntry;
    private static Object mAppEntry_info;
    private static boolean mAppEntry_info_enabled;
    private static int mAppEntry_info_flags;
    private static boolean isSystem;

    private static String textEnable = "Enable";
    private static String textDisable = "Disable";

    public static void hook(final XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod("com.android.settings.applications.InstalledAppDetails", lpparam.classLoader, "initUninstallButtons", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                getAppInfo(param.thisObject);

                if (!isSystem) {
                    Button mSpecialDisableButton = (Button) XposedHelpers.getObjectField(param.thisObject, "mSpecialDisableButton");
                    XposedHelpers.callMethod(mSpecialDisableButton, "setOnClickListener", param.thisObject);

                    getResourceStrings(param.thisObject);

                    if (mAppEntry_info_enabled)
                        mSpecialDisableButton.setText(textDisable);
                    else
                        mSpecialDisableButton.setText(textEnable);

                    try {
                        View mMoreControlButtons = (View) XposedHelpers.getObjectField(param.thisObject, "mMoreControlButtons");
                        mMoreControlButtons.setVisibility(View.VISIBLE);
                    } catch (NoSuchFieldError e) {
                        // AOSPA
                        mSpecialDisableButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        final Class<?> ClassDisableChanger = XposedHelpers.findClass("com.android.settings.applications.InstalledAppDetails.DisableChanger", lpparam.classLoader);

        findAndHookMethod("com.android.settings.applications.InstalledAppDetails", lpparam.classLoader, "onClick", View.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) {
                Object v = param.args[0];
                Object mSpecialDisableButton = XposedHelpers.getObjectField(param.thisObject, "mSpecialDisableButton");

                if (v == mSpecialDisableButton) {
                    getAppInfo(param.thisObject);

                    if (!mAppEntry_info_enabled) {
                        Object DisableChanger = XposedHelpers.newInstance(ClassDisableChanger, param.thisObject, mAppEntry_info, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
                        XposedHelpers.callMethod(DisableChanger, "execute", (Object) null);
                        return null;
                    }
                }
                try {
                    return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });

        //findAndHookMethod("com.android.settings.applications.InstalledAppDetails", lpparam.classLoader, "showDialogInner", int.class, int.class, new XC_MethodHook() {
        findAndHookMethod("com.android.settings.applications.AppInfoBase", lpparam.classLoader, "showDialogInner", int.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) {
                // DLG_SPECIAL_DISABLE -> DLG_DISABLE
                if ((Integer) param.args[0] == 9)
                    param.args[0] = 7;
            }
        });
    }

    private static void getAppInfo(Object thisObject) {
        mAppEntry = XposedHelpers.getObjectField(thisObject, "mAppEntry");
        mAppEntry_info = XposedHelpers.getObjectField(mAppEntry, "info");
        mAppEntry_info_enabled = (Boolean) XposedHelpers.getObjectField(mAppEntry_info, "enabled");
        mAppEntry_info_flags = (Integer) XposedHelpers.getObjectField(mAppEntry_info, "flags");

        if ((mAppEntry_info_flags & ApplicationInfo.FLAG_SYSTEM) == 0)
            isSystem = false;
        else
            isSystem = true;
    }

    private static void getResourceStrings(Object thisObject) {
        try {
            Resources resources = (Resources) XposedHelpers.callMethod(thisObject, "getResources");
            textDisable = (String) resources.getText(resources.getIdentifier("disable_text", "string", "com.android.settings"));
            textEnable = (String) resources.getText(resources.getIdentifier("enable_text", "string", "com.android.settings"));
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
}
