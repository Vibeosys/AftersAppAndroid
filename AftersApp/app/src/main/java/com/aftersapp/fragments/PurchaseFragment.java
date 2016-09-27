package com.aftersapp.fragments;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aftersapp.utils.AppConstants;
import com.android.vending.billing.IInAppBillingService;
import com.aftersapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by shrinivas on 24-09-2016.
 */
public class PurchaseFragment extends BaseFragment implements View.OnClickListener {
    private TextView mPurchaseTextView;
    IInAppBillingService mService;
    ServiceConnection mServiceConn;
    String mPrice;
    private UUID mTransactionId;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
            }
        };


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.purchase_fragment,container,false);
        mPurchaseTextView = (TextView) view.findViewById(R.id.purchase);
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        getActivity().bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
        mPurchaseTextView.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id)
        {
            case R.id.purchase:
                Toast toast = Toast.makeText(getContext(),"Purchase button is clicked",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                String packageName = getActivity().getPackageName();
                int isBillingSupported = -1;
                try {
                    isBillingSupported = mService.isBillingSupported(3, getActivity().getPackageName(), "inapp");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                if (isBillingSupported != 0) {
                    Log.d("Billing","NA");
                    Log.d("Billing","NA");
                }
                else
                {
                    getInAppPurchase();
                }

                break;
        }


    }

    private void getInAppPurchase() {

        try {
            Bundle purchaseItems = mService.getPurchases(3, getActivity().getPackageName(), "inapp", null);
            int responseCode = purchaseItems.getInt("RESPONSE_CODE");
            if (responseCode == 0) {
                ArrayList<String> items = purchaseItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                if (items.contains("com.package.name")) {
                    Toast.makeText(getContext(), "Product is already purchased", Toast.LENGTH_SHORT).show();
                } else {
                    purchaseAvailableProduct();
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void purchaseAvailableProduct() {

        ArrayList<String> purchaseList = new ArrayList<String>();
        purchaseList.add("com.inter.package.name");
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", purchaseList);
        AsynchGetPurchaseData purchaseBundle = new AsynchGetPurchaseData();
        purchaseBundle.execute(querySkus);
    }

    private class AsynchGetPurchaseData extends AsyncTask<Bundle, Void, Bundle> {
        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle bundle = null;
            try {
                bundle = mService.getSkuDetails(3,
                        getActivity().getPackageName(), "inapp", params[0]);

            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return bundle;
        }
        @Override
        protected void onPostExecute(Bundle bundle) {
            super.onPostExecute(bundle);
            try {
                int response = bundle.getInt("RESPONSE_CODE");
                if (response == 0) {
                    ArrayList<String> responseList
                            = bundle.getStringArrayList("DETAILS_LIST");

                    for (String thisResponse : responseList) {
                        JSONObject object = new JSONObject(thisResponse);
                        String productId = object.getString("productId");
                        String price = object.getString("price");
                        if (productId.equals("com.package.name.add"))

                mPrice = price;
                        mTransactionId = UUID.randomUUID();
                        Bundle buyIntentBundle = mService.getBuyIntent(3, getActivity().getPackageName(),
                                productId, "inapp", mTransactionId.toString());

                        PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");

                        getActivity().startIntentSenderForResult(pendingIntent.getIntentSender(), 1001,
                                null, Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
                    }

                }
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }catch (RemoteException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == 0) {
                try {
                    JSONObject jsonObj = new JSONObject(purchaseData);
                    String productId = jsonObj.getString("productId");
                    String payload = jsonObj.getString("developerPayload");
                    if (productId.equals("com.your.package.name")) {
                        Toast.makeText(getContext(), "You have purchase the ads free " +
                                "application. Thank you!", Toast.LENGTH_LONG).show();
                        mSessionManager.setIsPurchased(AppConstants.ITEM_PURCHASED);
                    }

                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Failed to parse purchase data.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            Toast toast = Toast.makeText(getContext(),"In else part of result activity",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }
}
