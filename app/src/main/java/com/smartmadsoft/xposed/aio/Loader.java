package com.smartmadsoft.xposed.aio;

import android.content.res.XResources;

import com.smartmadsoft.xposed.aio.tweaks.AlwaysSoftwareMenu;
import com.smartmadsoft.xposed.aio.tweaks.BatteryHistoryXXL;
import com.smartmadsoft.xposed.aio.tweaks.BatteryLightDisabler;
import com.smartmadsoft.xposed.aio.tweaks.CompactVolumePanel;
import com.smartmadsoft.xposed.aio.tweaks.DeskClockAlarm;
import com.smartmadsoft.xposed.aio.tweaks.DisableAdbNotification;
import com.smartmadsoft.xposed.aio.tweaks.DisableHorizontalScrollAR;
import com.smartmadsoft.xposed.aio.tweaks.DisableSuIndicator;
import com.smartmadsoft.xposed.aio.tweaks.DisableUsbNotification;
import com.smartmadsoft.xposed.aio.tweaks.GentleHapticFeedback;
import com.smartmadsoft.xposed.aio.tweaks.GentleHapticFeedbackTouchWiz;
import com.smartmadsoft.xposed.aio.tweaks.HideNetworkIndicators;
import com.smartmadsoft.xposed.aio.tweaks.MediaKeys;
import com.smartmadsoft.xposed.aio.tweaks.MediaStreamDefault;
import com.smartmadsoft.xposed.aio.tweaks.MinimumBrightnessMX;
import com.smartmadsoft.xposed.aio.tweaks.NativeFreezer;
import com.smartmadsoft.xposed.aio.tweaks.NoOverlayWarning;
import com.smartmadsoft.xposed.aio.tweaks.NoPasswordAfterBootTW;
import com.smartmadsoft.xposed.aio.tweaks.NoSafeVolumeWarning;
import com.smartmadsoft.xposed.aio.tweaks.NoToastIcons;
import com.smartmadsoft.xposed.aio.tweaks.OneWayBrightness;
import com.smartmadsoft.xposed.aio.tweaks.PocketFirst;
import com.smartmadsoft.xposed.aio.tweaks.ChromeTabsToolbarOnPhone;
import com.smartmadsoft.xposed.aio.tweaks.QuickUnlock;
import com.smartmadsoft.xposed.aio.tweaks.RemapVolume;
import com.smartmadsoft.xposed.aio.tweaks.K920Cardboard;
import com.smartmadsoft.xposed.aio.tweaks.NoWakeOnCharge;
import com.smartmadsoft.xposed.aio.tweaks.S5TouchWizJunk;
import com.smartmadsoft.xposed.aio.tweaks.S5ReaderMode;
import com.smartmadsoft.xposed.aio.tweaks.GMSWearNotificationDisable;
import com.smartmadsoft.xposed.aio.tweaks.S7TouchKeyLight;
//import com.smartmadsoft.xposed.aio.tweaks.Sandbox;
import com.smartmadsoft.xposed.aio.tweaks.S7sRGBVideo;
import com.smartmadsoft.xposed.aio.tweaks.VolumeKeysCursorControl;
import com.smartmadsoft.xposed.aio.tweaks.onehandzoomenabler.AR;
import com.smartmadsoft.xposed.aio.tweaks.onehandzoomenabler.FF;

import java.io.File;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Loader implements IXposedHookZygoteInit, IXposedHookLoadPackage, IXposedHookInitPackageResources {
    private static XSharedPreferences prefs;

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {
        prefs = new XSharedPreferences("com.smartmadsoft.xposed.aio", "tweaks");
        prefs.makeWorldReadable();

        int brightnessValue = Integer.parseInt(prefs.getString("tweak_minimumbrightness_list", "-1"));
        if (brightnessValue > 0) {
            // from https://github.com/GravityBox/GravityBox
            XResources.setSystemWideReplacement("android", "integer", "config_screenBrightnessSettingMinimum", brightnessValue);
            XResources.setSystemWideReplacement("android", "integer", "config_screenBrightnessDim", brightnessValue);
        }
        if (prefs.getBoolean("tweak_chrometabstoolbaronphone", false))
            ChromeTabsToolbarOnPhone.init();
        if (prefs.getBoolean("tweak_s5twjunk", false)) {
            XResources.setSystemWideReplacement("android", "bool", "show_ongoing_ime_switcher", false);
            //XResources.setSystemWideReplacement("android", "bool", "config_intrusiveNotificationLed", true);
        }
        if (prefs.getBoolean("tweak_nowakeoncharge", false))
            XResources.setSystemWideReplacement("android", "bool", "config_unplugTurnsOnScreen", false);

        //XResources.setSystemWideReplacement("android", "bool", "config_showNavigationBar", true);
        //XResources.setSystemWideReplacement("com.android.settings", "bool", "config_showNavigationBar", true);
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("android")) {
            if (prefs.getBoolean("tweak_nosafevolumewarning", false))
                NoSafeVolumeWarning.hook(lpparam);
            if (prefs.getBoolean("tweak_onewaybrightness", false))
                OneWayBrightness.hook(lpparam);
            if (prefs.getBoolean("tweak_batterylightdisabler", false))
                BatteryLightDisabler.hook(lpparam);
            if (prefs.getBoolean("tweak_alwayssoftwaremenu", false))
                AlwaysSoftwareMenu.hook(lpparam);
            int hapticValue = Integer.parseInt(prefs.getString("tweak_gentlehapticfeedback_list", "-1"));
            if (hapticValue > 0)
                GentleHapticFeedback.hook(lpparam, hapticValue);
            boolean tweakPlayPause = prefs.getBoolean("tweak_remapprevtoplaypause", false);
            boolean tweakTorch = prefs.getBoolean("tweak_remapnexttotorch", false);
            if (tweakPlayPause || tweakTorch)
                RemapVolume.hook(lpparam, tweakPlayPause, tweakTorch);
            if (prefs.getBoolean("tweak_nowakeoncharge", false))
                NoWakeOnCharge.hook(lpparam);
            if (prefs.getBoolean("tweak_s5readermode", false))
                S5ReaderMode.hook(lpparam);
            if (prefs.getBoolean("tweak_mediastreamdefault", false))
                MediaStreamDefault.hook(lpparam);
            if (prefs.getBoolean("tweak_t700gentlehapticfeedback", false))
                GentleHapticFeedbackTouchWiz.hook(lpparam);
            if (prefs.getBoolean("tweak_mediakeys", false))
                MediaKeys.hook(lpparam);
            if (prefs.getBoolean("tweak_disableusbnotification", false))
                DisableUsbNotification.hook(lpparam);
            if (prefs.getBoolean("tweak_disableadbnotification", false))
                DisableAdbNotification.hook(lpparam);
            if (prefs.getBoolean("tweak_k920cardboard", false))
                K920Cardboard.hookAndroid(lpparam);
            if (prefs.getBoolean("tweak_s7srgbvideo", false))
                S7sRGBVideo.hook(lpparam);

            //Sandbox.hook(lpparam);
        }
        if (lpparam.packageName.equals("com.android.server.telecom")) {
            if (prefs.getBoolean("tweak_k920cardboard", false))
                K920Cardboard.hookTelecom(lpparam);
        }
        if (lpparam.packageName.equals("com.android.systemui")) {
            if (prefs.getBoolean("tweak_disablesuindicator", false))
                DisableSuIndicator.hook(lpparam);
            if (prefs.getBoolean("tweak_nowakeoncharge", false))
                NoWakeOnCharge.hookUI(lpparam);
            if (prefs.getBoolean("tweak_s5twjunk", false))
                S5TouchWizJunk.hookUI(lpparam);
            if (prefs.getBoolean("tweak_s5twjunk_dnd", false))
                S5TouchWizJunk.hookUIDND(lpparam);
            if (prefs.getBoolean("tweak_k920cardboard", false))
                K920Cardboard.hookUI(lpparam);
            if (prefs.getBoolean("tweak_twnopasswordafterboot", false))
                NoPasswordAfterBootTW.hook(lpparam);
            if (prefs.getBoolean("tweak_quickunlock", false))
                QuickUnlock.hook(lpparam);
        }
        if (lpparam.packageName.equals("com.android.settings")) {
            if (prefs.getBoolean("tweak_batteryhistoryxxl", false))
                BatteryHistoryXXL.hook(lpparam);
            if (prefs.getBoolean("tweak_nativefreezer", false))
                NativeFreezer.hook(lpparam);
            if (prefs.getBoolean("tweak_s5twjunk", false))
                S5TouchWizJunk.hookSettings(lpparam);
            if (prefs.getBoolean("tweak_k920cardboard", false))
                K920Cardboard.hookSettings(lpparam);
            if (prefs.getBoolean("tweak_s7touchkeylight", false))
                S7TouchKeyLight.hook(lpparam);

            //Sandbox.hook(lpparam);
        }
        if (lpparam.packageName.equals("com.adobe.reader")) {
            if (prefs.getBoolean("tweak_onehandzoom_ar", false))
                AR.hook(lpparam);
            if (prefs.getBoolean("tweak_disablehorizontalscroll_ar", false))
                DisableHorizontalScrollAR.hook(lpparam);
        }
        if (prefs.getBoolean("tweak_notoasticons", false))
            NoToastIcons.hook(lpparam);
        if (prefs.getBoolean("tweak_pocketfirst", false))
            PocketFirst.hook(lpparam);
        if (prefs.getBoolean("tweak_deskclockalarm", false))
            DeskClockAlarm.hook(lpparam);
        if ((lpparam.packageName.equals("org.mozilla.firefox") || lpparam.packageName.equals("org.mozilla.firefox_beta") || lpparam.packageName.equals("org.mozilla.fennec") ) && prefs.getBoolean("tweak_onehandzoom_ff", false))
            FF.hook(lpparam);
        if ((lpparam.packageName.equals("com.mxtech.videoplayer.pro") ||  lpparam.packageName.equals("com.mxtech.videoplayer.ad")) && prefs.getBoolean("tweak_minimumbrightness_mx", false))
            MinimumBrightnessMX.hook(lpparam);
        if (ChromeTabsToolbarOnPhone.isChrome(lpparam.packageName) && prefs.getBoolean("tweak_chrometabstoolbaronphone", false))
            ChromeTabsToolbarOnPhone.hook(lpparam);
        if (prefs.getBoolean("tweak_k920cardboard", false))
            K920Cardboard.hookAll(lpparam);
        if (lpparam.packageName.equals("com.sec.android.app.popupuireceiver") && prefs.getBoolean("tweak_s5twjunk", false))
            S5TouchWizJunk.hook(lpparam);
        if (lpparam.packageName.equals("com.google.android.gms") && prefs.getBoolean("tweak_gmswearnotificationdisable", false))
            GMSWearNotificationDisable.hook(lpparam);
        if (prefs.getBoolean("tweak_volumekeyscursorcontrol", false))
            VolumeKeysCursorControl.hook(lpparam);
        if (prefs.getBoolean("tweak_nooverlaywarning", false) && lpparam.packageName.endsWith(".packageinstaller"))
            NoOverlayWarning.hook(lpparam);

        //Sandbox.hook(lpparam);
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam iprparam) throws Throwable {
        if (iprparam.packageName.equals("com.android.systemui")) {
            if (prefs.getBoolean("tweak_hidenetworkindicators", false))
                HideNetworkIndicators.hook(iprparam);
            if (prefs.getBoolean("tweak_compactvolumepanel", false))
                CompactVolumePanel.hook(iprparam);
        }
    }
}
