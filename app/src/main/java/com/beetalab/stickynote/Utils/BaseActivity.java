package com.beetalab.stickynote.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.beetalab.stickynote.R;
import com.beetalab.stickynote.activity.AddNoteActivity;
import com.beetalab.stickynote.activity.HomeActivity;
import com.beetalab.stickynote.config.Constant;
import com.beetalab.stickynote.config.StickyNoteApplication;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public abstract class BaseActivity extends AppCompatActivity {

    private InterstitialAd mInterstitialAd;
    protected boolean is_pro_user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        is_pro_user = AppUtils.getSharedPreference(this).getBoolean(Constant.PREF_KEY_IS_PRO_USER,false);
        MobileAds.initialize(this);
    }

    protected void makeToolbarTitleCenter(Toolbar toolbar , TextView toolbarTitle) {
        if (toolbar != null && toolbarTitle != null)
            toolbar.post(() -> {
                float middleX = toolbar.getX() + toolbar.getWidth() / 2.0f;
                float txtX = toolbarTitle.getX();
                if (txtX < 0 || txtX > middleX)
                    toolbarTitle.setTranslationX(0);
                txtX = toolbarTitle.getX();
                float txtMiddleX = txtX + toolbarTitle.getWidth() / 2.0f;
                float gap = middleX - txtMiddleX;
                toolbarTitle.setTranslationX(gap);
            });
    }

    protected void loadInstratialAds(Activity activityCompat){

        if(is_pro_user){

            if(!(activityCompat instanceof HomeActivity)){
                 activityCompat.setResult(Activity.RESULT_OK);
                    activityCompat.finish();}
            return;
        }


        AdRequest adRequest = new AdRequest.Builder().build();
        if(mInterstitialAd == null) {

            InterstitialAd.load(this, Constant.ADMOB_INASTRATIAL_ID, adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(InterstitialAd interstitialAd) {
                    mInterstitialAd = interstitialAd;

                    if(mInterstitialAd != null) {
                        if(activityCompat instanceof AddNoteActivity &&  ((StickyNoteApplication) getApplication()).isAdShowed)

                            return;

                        if (activityCompat instanceof AddNoteActivity) {
                            ((StickyNoteApplication) getApplication()).isAdShowed = true;
                        }
                        mInterstitialAd.show(activityCompat);

                        if(!(activityCompat instanceof HomeActivity)){
                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdFailedToShowFullScreenContent(AdError adError) {
                                    super.onAdFailedToShowFullScreenContent(adError);
                                    activityCompat.setResult(Activity.RESULT_OK);
                                    activityCompat.finish();
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    super.onAdShowedFullScreenContent();
                                }

                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent();
                                    activityCompat.setResult(Activity.RESULT_OK);
                                    activityCompat.finish();
                                }
                            });
                        }
                    }
                }

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    mInterstitialAd = null;
                    super.onAdFailedToLoad(loadAdError);
                }
            });
        }
        else {
            mInterstitialAd.show(activityCompat);

            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    activityCompat.setResult(Activity.RESULT_OK);
                    activityCompat.finish();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    activityCompat.setResult(Activity.RESULT_OK);
                    activityCompat.finish();
                }
            });
        }


    }




    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
