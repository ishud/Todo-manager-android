package com.beetalab.stickynote.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.usage.ConfigurationStats;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.PriceChangeConfirmationListener;
import com.android.billingclient.api.PriceChangeFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryResponseListener;

import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.beetalab.stickynote.R;
import com.beetalab.stickynote.Utils.AppUtils;
import com.beetalab.stickynote.Utils.BaseActivity;
import com.beetalab.stickynote.config.Constant;
import com.beetalab.stickynote.model.BaseModel;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import static com.beetalab.stickynote.config.Constant.PREF_KEY_IS_PRO_USER;
import static com.beetalab.stickynote.config.Constant.PRODUCT_UPGRADE_PREMIUM;

public class PremiumActivity extends BaseActivity implements PurchasesUpdatedListener{

    public static final int REQ_CODE_UPGRADE_PREMIUM = 2005;

    private ImageView premImage,closeImgage;
    private Button purchesBtn;

    private BillingClient billingClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);

        BillingClient.Builder builder =  BillingClient.newBuilder(this);
        builder.setListener(this);
        builder.enablePendingPurchases();
        billingClient = builder.build();

        initView();

    }

    private void initView() {
        premImage = findViewById(R.id.premium_image);
        closeImgage = findViewById(R.id.close_btn);
        purchesBtn = findViewById(R.id.upgrade_premium_btn);
        Glide.with(this).asGif().load(R.drawable.premium_icon).into(premImage);

        closeImgage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0,0);
            }
        });

        purchesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectPlayConsole();
            }
        });

    }

    private void connectPlayConsole(){

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {

                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    setPerchusParams();
                }
                else{
                    Toast.makeText(PremiumActivity.this, "Purches fail, Try again", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(PremiumActivity.this, "Purches fail, Try again", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setPerchusParams(){

        List<String> params = new ArrayList<>();
        params.add(PRODUCT_UPGRADE_PREMIUM);
        final SkuDetailsParams.Builder builder = SkuDetailsParams.newBuilder();

        final SkuDetailsParams.Builder builder1 = builder.setSkusList(params).setType(BillingClient.SkuType.INAPP);

        billingClient.querySkuDetailsAsync(builder1.build(), (billingResult, list) -> {

            if(list != null)
                launchPaymentFlow(list);

        });
    }

    private void launchPaymentFlow(List<SkuDetails> list){

        if(list.size() == 0)
            return;

        BillingFlowParams.Builder flowParams = BillingFlowParams.newBuilder();
        flowParams.setSkuDetails(list.get(0));
        final BillingFlowParams build = flowParams.build();

        final int responseCode = billingClient.launchBillingFlow(this, build).getResponseCode();

        if(responseCode == BillingClient.BillingResponseCode.OK){

        }

    }


    @Override
    public void onPurchasesUpdated( BillingResult billingResult, List<Purchase> list) {

        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null){
            handleSuccessPurches(list.get(0));
        }
        else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED){

        }
        else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED){
            AppUtils.getSharedPreference(PremiumActivity.this).edit().putBoolean(PREF_KEY_IS_PRO_USER,true).apply();
            closeActivity();
        }
        else
            Toast.makeText(this, "Purchase fail, Try again", Toast.LENGTH_SHORT).show();

    }

//    private void handlePurches(Purchase purchase) {
//
//        ConsumeParams consumeParams = ConsumeParams.newBuilder()
//            .setPurchaseToken(purchase.getPurchaseToken())
//                .build();
//
//        billingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
//            @Override
//            public void onConsumeResponse( BillingResult billingResult,  String s) {
//                System.out.println("========================= " + billingResult.getDebugMessage());
//                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
//                    handleSuccessPurches(purchase);
//
//                }
//            }
//        });
//
//    }

    private void handleSuccessPurches(Purchase purchase) {

        if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){
            if(!purchase.isAcknowledged()){
                AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken()).build();

                billingClient.acknowledgePurchase(params,acknowledgePurchaseResponseListener);
            }
            AppUtils.getSharedPreference(PremiumActivity.this).edit().putBoolean(PREF_KEY_IS_PRO_USER,true).apply();
            closeActivity();

        }
    }

    private void closeActivity(){
        setResult(RESULT_OK);
        finish();
    }

    private AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
        @Override
        public void onAcknowledgePurchaseResponse( BillingResult billingResult) {

          if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
              closeActivity();
          }

        }
    };
}