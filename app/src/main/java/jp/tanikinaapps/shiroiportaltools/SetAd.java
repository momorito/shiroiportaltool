package jp.tanikinaapps.shiroiportaltools;

import android.content.Context;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class SetAd {
    AdView adView;
    Context context;
    String adCode;

    public SetAd(Context context,AdView adView){
        this.context = context;
        this.adView = adView;
        adCode = context.getResources().getString(R.string.ad_code);
    }

    public void AdSetting(){
        MobileAds.initialize(context,adCode);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}
