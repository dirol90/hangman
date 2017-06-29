package com.tsimbalyukstudio.hangman;

import android.app.Activity;

import com.appodeal.ads.Appodeal;

public class ADDS {
    String appKey = "ef81e7ce0c0014d366d72e56b13eae0f9a50e62df1d79ec6";
    static int addCounter = 0;

    ADDS(Activity a) {
        Appodeal.disableNetwork(a, "cheetah");
        Appodeal.disableLocationPermissionCheck();
        Appodeal.disableWriteExternalStoragePermissionCheck();
        Appodeal.initialize(a, appKey, Appodeal.BANNER | Appodeal.INTERSTITIAL);
        Appodeal.setTesting(false);
    }

    public void showBanner(Activity a) {
        if (Appodeal.isLoaded(Appodeal.BANNER_BOTTOM)) {
            Appodeal.show(a, Appodeal.BANNER_BOTTOM);
        }
    }


    public void showInterstitial(Activity a) {
        addCounter++;
        if (Appodeal.isLoaded(Appodeal.INTERSTITIAL) && addCounter % 3 == 0) {
            Appodeal.show(a, Appodeal.INTERSTITIAL);
            addCounter = 0;
        }
    }

}
