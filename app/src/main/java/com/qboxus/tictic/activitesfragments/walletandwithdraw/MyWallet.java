package com.qboxus.tictic.activitesfragments.walletandwithdraw;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.qboxus.tictic.activitesfragments.profile.SettingAndPrivacyA;
import com.qboxus.tictic.activitesfragments.profile.settings.AppThemA;
import com.qboxus.tictic.simpleclasses.AppCompatLocaleActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.volley.plus.VPackages.VolleyRequest;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.volley.plus.interfaces.Callback;
import com.qboxus.tictic.models.UserModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.DataParsing;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MyWallet extends AppCompatLocaleActivity  implements PurchasesUpdatedListener {

    RecyclerView recyclerView;
    MyWalletAdapter adapter ;
    ArrayList<WalletModel> datalist = new ArrayList<>();
    TextView tvCoins;
    WalletModel selectedWalletModel;
    BillingClient billingClient;
    ArrayList<ProductDetails> inAppProductList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(MyWallet.this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE)
                , this, getClass(),false);
        setContentView(R.layout.activity_my_wallet);

        selectedWalletModel =new WalletModel();
        tvCoins =findViewById(R.id.coins_txt);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.tab_cashout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWithdrawCoins();
            }
        });

        initalizeBill();
        init_views();
    }


    private void openWithdrawCoins() {
        Intent intent=new Intent(MyWallet.this, WithdrawCoinsA.class);
        resultCallback.launch(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }


    ActivityResultLauncher<Intent> resultCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK ) {
                        Intent data = result.getData();
                        if (data.getBooleanExtra("isShow",false))
                        {
                            SetUpScreenData();
                        }

                    }
                }
            });


    public void initalizeBill(){
        billingClient=BillingClient.newBuilder(MyWallet.this)
                .setListener(this)
                .enablePendingPurchases()
                .build();
        startBillingConnection();
    }

    private void startBillingConnection() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                Log.d(Constants.tag,"Not Connected Connect Again");
                startBillingConnection();
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                Log.d(Constants.tag,"startConnection: "+billingResult.getResponseCode());
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    InitPurchases();
                    MyWallet.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            populateDataList();
                        }
                    });
                }
            }
        });
    }

    private void populateDataList() {
        datalist.clear();
        datalist.add(new WalletModel(Constants.Product_ID0,"",Constants.COINS0,Constants.PRICE0));
        datalist.add(new WalletModel(Constants.Product_ID1,"",Constants.COINS1,Constants.PRICE1));
        datalist.add(new WalletModel(Constants.Product_ID2,"",Constants.COINS2,Constants.PRICE2));
        datalist.add(new WalletModel(Constants.Product_ID3,"",Constants.COINS3,Constants.PRICE3));
        datalist.add(new WalletModel(Constants.Product_ID4,"",Constants.COINS4,Constants.PRICE4));
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onResume() {
        super.onResume();
        SetUpScreenData();
    }

    private void SetUpScreenData() {
        tvCoins.setText(""+Functions.getSharedPreference(MyWallet.this).getString(Variables.U_WALLET, "0"));
    }




    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase:purchases) {
                MyWallet.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handlePurchase(purchase);
                    }
                });
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d(Constants.tag,"" + billingResult.getResponseCode() + "--" + BillingClient.BillingResponseCode.USER_CANCELED);
        } else {
            Log.d(Constants.tag,"" + billingResult.getResponseCode());
        }
    }

    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams.Builder acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken());

                billingClient.acknowledgePurchase(acknowledgePurchaseParams.build(), new AcknowledgePurchaseResponseListener() {
                    @Override
                    public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            Log.d(Constants.tag,"Billing : Call API Fo Success "+purchase.getOriginalJson());
                            callAPIUpdateWallet(selectedWalletModel.coins+" coins", selectedWalletModel.coins, selectedWalletModel.price,purchase.getPurchaseToken());
                        }
                        else
                        {
                            Log.d(Constants.tag,"ResponseCode : "+billingResult.getResponseCode());
                        }
                    }
                });
            }
        }
    }


    // when we click the continue btn this method will call
    public void PurchaseItem(int postion) {
        ProductDetails selectedProduct=null;
        Log.d(Constants.tag,"inAppProductList Size: "+inAppProductList.size());
        for(ProductDetails item :inAppProductList)
        {
            if (item.getProductId().equals(""+datalist.get(postion).getId()))
            {
                selectedProduct=item;
            }
        }
        if (selectedProduct!=null)
        {
            List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList=new ArrayList<>();
            productDetailsParamsList.add(BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(selectedProduct)
                    .build());
            BillingFlowParams billingFlowParams=BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build();
            billingClient.launchBillingFlow(this, billingFlowParams);
        }
    }


    private void InitPurchases() {
        QueryProductDetailsParams.Builder queryProductDetailsParams=QueryProductDetailsParams
                .newBuilder()
                .setProductList(getInAppProducts());
        billingClient.queryProductDetailsAsync(queryProductDetailsParams.build(), new ProductDetailsResponseListener() {
            @Override
            public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> productDetailsList) {
                Log.d(Constants.tag,"queryProductDetailsAsync: "+billingResult.getResponseCode());
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    populateRegisterInAppProducts(productDetailsList);
                }
            }
        });
    }

    private void populateRegisterInAppProducts(List<ProductDetails> productDetailsList) {
        Log.d(Constants.tag,"populateRegisterInAppProducts: "+productDetailsList);
        inAppProductList.clear();
        for(ProductDetails item:productDetailsList)
        {
            Log.d(Constants.tag,"productDetails: "+item.getProductId());
            inAppProductList.add(item);
        }
    }

    private List<QueryProductDetailsParams.Product> getInAppProducts() {
        List<QueryProductDetailsParams.Product> productList=new ArrayList<>();
        productList.add(QueryProductDetailsParams
                .Product.newBuilder()
                .setProductId(Constants.Product_ID0)
                .setProductType(BillingClient.ProductType.INAPP)
                .build());
        productList.add(QueryProductDetailsParams
                .Product.newBuilder()
                .setProductId(Constants.Product_ID1)
                .setProductType(BillingClient.ProductType.INAPP)
                .build());
        productList.add(QueryProductDetailsParams
                .Product.newBuilder()
                .setProductId(Constants.Product_ID2)
                .setProductType(BillingClient.ProductType.INAPP)
                .build());
        productList.add(QueryProductDetailsParams
                .Product.newBuilder()
                .setProductId(Constants.Product_ID3)
                .setProductType(BillingClient.ProductType.INAPP)
                .build());
        productList.add(QueryProductDetailsParams
                .Product.newBuilder()
                .setProductId(Constants.Product_ID4)
                .setProductType(BillingClient.ProductType.INAPP)
                .build());
        return productList;
    }



    private void init_views() {
        recyclerView = findViewById(R.id.recylerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new MyWalletAdapter(this, datalist, new AdapterClickListener() {
            @Override
            public void onItemClick(View view, int pos, Object object) {
                selectedWalletModel =(WalletModel) object;
                PurchaseItem(pos);
            }
        });
        recyclerView.setAdapter(adapter);
    }






    public void callAPIUpdateWallet(String name, String coins, String price, String Tid){

        JSONObject params=new JSONObject();
        try {
            params.put("user_id",Functions.getSharedPreference(MyWallet.this).getString(Variables.U_ID,""));
            params.put("coin",coins);
            params.put("title",name);
            params.put("price",price.replace(Constants.CURRENCY,""));
            params.put("transaction_id",Tid);
            params.put("device","android");
        } catch (Exception e) {
            Log.d(Constants.tag,"Exception : "+e);
        }

      MyWallet.super.runOnUiThread(new Runnable() {
          @Override
          public void run() {
              Functions.showLoader(MyWallet.this,false,false);
              VolleyRequest.JsonPostRequest(MyWallet.this, ApiLinks.purchaseCoin, params,Functions.getHeaders(MyWallet.this), new Callback() {
                  @Override
                  public void onResponce(String resp) {
                Functions.checkStatus(MyWallet.this,resp);
                      Functions.cancelLoader();
                      Log.d(Constants.tag,"get coins before run \n"+params);
                      try {
                          JSONObject jsonObject=new JSONObject(resp);

                          String code=jsonObject.optString("code");
                          if(code!=null && code.equals("200")){
                              JSONObject msgObj=jsonObject.getJSONObject("msg");
                              UserModel userDetailModel= DataParsing.getUserDataModel(msgObj.optJSONObject("User"));
                              SharedPreferences.Editor editor = Functions.getSharedPreference(MyWallet.this).edit();
                              editor.putString(Variables.U_WALLET, ""+userDetailModel.getWallet());
                              editor.commit();
                              isNotifyCallback=true;
                              SetUpScreenData();
                          }
                      } catch (Exception e) {
                          Log.d(Constants.tag,"Exception : "+e);
                      }


                  }
              });
          }
      });
    }


    @Override
    protected void onDestroy() {
        billingClient.endConnection();
        super.onDestroy();
    }



    boolean isNotifyCallback=false;
    @Override
    public void onBackPressed() {
        if (isNotifyCallback)
        {
            Intent intent = new Intent();
            intent.putExtra("isShow", true);
            setResult(RESULT_OK, intent);
            finish();
        }
        else
        {
            finish();
        }
    }
}
