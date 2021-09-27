package com.chqup.agent;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentTabsRecharge extends Fragment {
    private String[] array_recharge;
    private String[] usernamedata;
    TextInputEditText recharge_type,mobile,amount,otp,username;
    private RequestQueue mQueue;
    ProgressBar progressBar;
    TextView verify;
    MaterialRippleLayout register;



    public FragmentTabsRecharge() {
    }

    public static FragmentTabsRecharge newInstance() {
        FragmentTabsRecharge fragment = new FragmentTabsRecharge();


        return fragment;
    }


    private void showStateChoiceDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setSingleChoiceItems(array_recharge, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                recharge_type.setTextColor(Color.BLACK);
                recharge_type.setText(array_recharge[which]);
            }
        });
        builder.show();
    }

    private void showUsernameDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setSingleChoiceItems(usernamedata, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                username.setTextColor(Color.BLACK);
                username.setText(usernamedata[which]);

                sendOtp(usernamedata[which]);
            }
        });
        builder.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tabs_recharge, container, false);

        recharge_type = root.findViewById(R.id.recharge_type);
        mobile = root.findViewById(R.id.mobile);
        amount = root.findViewById(R.id.amount);
        otp = root.findViewById(R.id.otp);
        verify = root.findViewById(R.id.verify);
        register = root.findViewById(R.id.register);
        progressBar = root.findViewById(R.id.progressbar);
        username = root.findViewById(R.id.username);

        array_recharge = getResources().getStringArray(R.array.recharge);

        getAmmount();


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.getText().toString().isEmpty()
                        || recharge_type.getText().toString().isEmpty()
                        || mobile.getText().toString().isEmpty()
                        || amount.getText().toString().isEmpty()
                        || otp.getText().toString().isEmpty()
                )
                {
                    Toast.makeText(getContext(), "complete all fields", Toast.LENGTH_SHORT).show();
                }
                else
                {
                   // Toast.makeText(getContext(), username.getText().toString(), Toast.LENGTH_SHORT).show();
                    rechargeNow(recharge_type.getText().toString(),username.getText().toString(),
                            amount.getText().toString(),otp.getText().toString());
                }
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mobile.getText().toString().length() == 10)
                {
                    verifyMobileno(mobile.getText().toString());
                }
            }
        });

        recharge_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStateChoiceDialog();
            }
        });

        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUsernameDialog();
            }
        });

        return root;
    }

    void verifyMobileno(String mobileno)
    {

        mQueue = Volley.newRequestQueue(getContext());


        progressBar.setVisibility(View.VISIBLE);
        register.setVisibility(View.GONE);

        String url = "https://www.chqup.com/agent/renewal_user.php";



        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() != 0) {

                    List<HashMap<String,String>> hashMap = convertToHashMap(response);

                //    Toast.makeText(getActivity(), ""+hashMap.toString(), Toast.LENGTH_SHORT).show();


                    usernamedata = new String[hashMap.size()];

                    for(int i=0;i<hashMap.size();i++)
                    {
                     usernamedata[i] = hashMap.get(i).get("username");
                    }


                    //Toast.makeText(getContext(), ""+response, Toast.LENGTH_SHORT).show();

                }

                progressBar.setVisibility(View.GONE);
                register.setVisibility(View.VISIBLE);


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressBar.setVisibility(View.GONE);
                register.setVisibility(View.VISIBLE);

                Toast.makeText(getActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("comes", "send_mobile");
                params.put("mobile_no", mobileno);

                return params;
            }
        };

        mQueue.add(request);
    }

    public List<HashMap<String, String>> convertToHashMap(String jsonString) {
        List<HashMap<String, String>> myHashMapList = new ArrayList<>();
        try {
            JSONArray jArray = new JSONArray(jsonString);
            JSONObject jObject = null;
            String keyString=null;
            for (int i = 0; i < jArray.length(); i++) {
                jObject = jArray.getJSONObject(i);

          //      Toast.makeText(getContext(), ""+jObject.toString(), Toast.LENGTH_LONG).show();
                // beacuse you have only one key-value pair in each object so I have used index 0
                HashMap<String, String> myHashMap = new HashMap<String, String>();

                keyString = (String)jObject.names().get(0);
                myHashMap.put(keyString, jObject.getString(keyString));

                keyString = (String)jObject.names().get(1);
                myHashMap.put(keyString, jObject.getString(keyString));

                myHashMapList.add(myHashMap);


              //  Toast.makeText(getContext(), ""+myHashMapList.toString(), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return myHashMapList;
    }

    String getOtp = "";

    void sendOtp(String name)
    {

        mQueue = Volley.newRequestQueue(getContext());


        progressBar.setVisibility(View.VISIBLE);
        register.setVisibility(View.GONE);

        String url = "https://www.chqup.com/agent/renewal_user.php";



        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() != 0) {
                    getOtp = response;
                    Toast.makeText(getActivity(), "Please wait for otp", Toast.LENGTH_SHORT).show();
                }

                progressBar.setVisibility(View.GONE);
                register.setVisibility(View.VISIBLE);


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressBar.setVisibility(View.GONE);
                register.setVisibility(View.VISIBLE);

                Toast.makeText(getActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("comes", "send_username");
                params.put("username", name);

                return params;
            }
        };

        mQueue.add(request);

    }

    void rechargeNow(String selectedplan,String user,String amount,String otp1)
    {
        if(otp1.equalsIgnoreCase(getOtp))
        {

            mQueue = Volley.newRequestQueue(getContext());


            progressBar.setVisibility(View.VISIBLE);
            register.setVisibility(View.GONE);

            String url = "https://www.chqup.com/agent/renewal_user.php";



            StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.length() != 0) {
                        Toast.makeText(getContext(), ""+response, Toast.LENGTH_SHORT).show();

                        username.setText("");
                        mobile.setText("");
                        otp.setText("");
                        recharge_type.setText("");

                    }

                    progressBar.setVisibility(View.GONE);
                    register.setVisibility(View.VISIBLE);


                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    progressBar.setVisibility(View.GONE);
                    register.setVisibility(View.VISIBLE);

                    Toast.makeText(getActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("comes", "other");
                    params.put("selected_plan", selectedplan);
                    params.put("username", user);
                    params.put("amount", amount);
                    params.put("otp", otp1);

                    return params;
                }
            };

            mQueue.add(request);
        }
        else
        {
            Toast.makeText(getContext(), "Wrong otp", Toast.LENGTH_SHORT).show();
        }
    }


    void getAmmount()
    {
        mQueue = Volley.newRequestQueue(getContext());


        progressBar.setVisibility(View.VISIBLE);
        register.setVisibility(View.GONE);

        String url = "https://www.chqup.com/agent/renewal_user.php";



        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() != 0) {
                    amount.setText(response);
                }

                progressBar.setVisibility(View.GONE);
                register.setVisibility(View.VISIBLE);


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressBar.setVisibility(View.GONE);
                register.setVisibility(View.VISIBLE);

                Toast.makeText(getActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("comes", "send_renewal");

                return params;
            }
        };

        mQueue.add(request);
    }
}