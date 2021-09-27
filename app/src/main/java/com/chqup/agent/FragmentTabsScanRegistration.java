package com.chqup.agent;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FragmentTabsScanRegistration extends Fragment {
    TextInputEditText  username,full_name,dob,address;

    TextInputEditText mobile,pincode,otp,city,state,amount;
    RadioButton scanner;
    MaterialRippleLayout register;

    private RequestQueue mQueue;

    ProgressBar progressBar;




    public FragmentTabsScanRegistration() {
    }

    public static FragmentTabsScanRegistration newInstance() {
        FragmentTabsScanRegistration fragment = new FragmentTabsScanRegistration();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tabs_scan_registration, container, false);

        username = root.findViewById(R.id.username);
        full_name = root.findViewById(R.id.full_name);
        dob = root.findViewById(R.id.dob);
        address = root.findViewById(R.id.address);

        mobile = root.findViewById(R.id.mobile);
        pincode = root.findViewById(R.id.pincode);
        progressBar = root.findViewById(R.id.progressbar);

        otp = root.findViewById(R.id.otp);
        register = root.findViewById(R.id.register);
        scanner = root.findViewById(R.id.radio_one);
        city = root.findViewById(R.id.city);
        state = root.findViewById(R.id.state);
        amount = root.findViewById(R.id.amount);
        amount.setFocusable(false);

        getAmount();
        mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(charSequence.toString().length() == 10)
                {

                   // Toast.makeText(getContext(), ""+charSequence.toString(), Toast.LENGTH_SHORT).show();

                    verifyMobileno(charSequence.toString());

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

        if(username.getText().toString().isEmpty() ||
                full_name.getText().toString().isEmpty() ||
                dob.getText().toString().isEmpty() ||
                address.getText().toString().isEmpty() ||
                mobile.getText().toString().isEmpty() ||
                pincode.getText().toString().isEmpty() ||
                otp.getText().toString().isEmpty() ||
                city.getText().toString().isEmpty() ||
                state.getText().toString().isEmpty()
        ) {
            Toast.makeText(getActivity(), "Please complete all fields", Toast.LENGTH_SHORT).show();
        }
        else{

            String uname = username.getText().toString();
            String fname = full_name.getText().toString();
            String date = dob.getText().toString();
            String add = address.getText().toString();
            String mob = mobile.getText().toString();
            String pin = pincode.getText().toString();
            String ot = otp.getText().toString();
            String ci = city.getText().toString();
            String st = state.getText().toString();



            if(ot.equalsIgnoreCase(foundotp))
            {

              //  Toast.makeText(getContext(), ""+pin+" "+ot+" "+ci+" "+st, Toast.LENGTH_SHORT).show();
                registerUser(mob,uname,fname,date,add,pin,ci,st,ot);
            }
            else
            {
                Toast.makeText(getContext(), "wrong otp", Toast.LENGTH_SHORT).show();
            }

        }
            }
        });



        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),QRScanner.class));
                getActivity().finish();

            }
        });



        if(getActivity().getIntent().getStringExtra("comesfrom").toString().equalsIgnoreCase("scanner"))
        {
            username.setText(getActivity().getIntent().getStringExtra("username") == null ? "" : getActivity().getIntent().getStringExtra("username"));
            full_name.setText(getActivity().getIntent().getStringExtra("username") == null ? "" : getActivity().getIntent().getStringExtra("username"));
            dob.setText(getActivity().getIntent().getStringExtra("dob") == null ? "" : getActivity().getIntent().getStringExtra("dob"));
            address.setText(getActivity().getIntent().getStringExtra("address") == null ? "" : getActivity().getIntent().getStringExtra("address"));
            state.setText(getActivity().getIntent().getStringExtra("state") == null ? "" : getActivity().getIntent().getStringExtra("state"));
            pincode.setText(getActivity().getIntent().getStringExtra("pc") == null ? "" : getActivity().getIntent().getStringExtra("pc"));

            if(!username.getText().toString().isEmpty())
            {
                username.setFocusable(false);
            }
            if(!full_name.getText().toString().isEmpty())
            {
                full_name.setFocusable(false);
            }

            if(!dob.getText().toString().isEmpty())
            {
                dob.setFocusable(false);
            }
            if(!address.getText().toString().isEmpty())
            {
                address.setFocusable(false);
            }

            if(!state.getText().toString().isEmpty())
            {
                state.setFocusable(false);
            }
            if(!pincode.getText().toString().isEmpty())
            {
                pincode.setFocusable(false);
            }


        }

        if(dob.getText().toString().isEmpty())
        {

            dob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar cur_calender = Calendar.getInstance();
                    DatePickerDialog datePicker = DatePickerDialog.newInstance(
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.set(Calendar.YEAR, year);
                                    calendar.set(Calendar.MONTH, monthOfYear);
                                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    long date_ship_millis = calendar.getTimeInMillis();
                                    dob.setText(Tools.getFormattedDateSimple(date_ship_millis));

                                }
                            },
                            cur_calender.get(Calendar.YEAR),
                            cur_calender.get(Calendar.MONTH),
                            cur_calender.get(Calendar.DAY_OF_MONTH)
                    );
                    //set dark light
                    datePicker.setThemeDark(false);
                    datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
                    datePicker.setMaxDate(cur_calender);
                    datePicker.show(getActivity().getFragmentManager(), "Datepickerdialog");
                }
            });
        }

        return root;
    }


    String foundotp = "";

    void verifyMobileno(String mobileno)
    {


        mQueue = Volley.newRequestQueue(getContext());


        progressBar.setVisibility(View.VISIBLE);
        register.setVisibility(View.GONE);

        String url = "https://www.chqup.com/agent/registration_user.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() != 0) {
                    foundotp = response;
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

                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("comes", "send");
                params.put("mobile_no", mobileno);

                return params;
            }
        };

        mQueue.add(request);
    }



    void registerUser(String mobi,String username1,String fullname,String dateof,String address1,String pincode1,String city1,String state1,String otp1)
    {

        mQueue = Volley.newRequestQueue(getContext());

        progressBar.setVisibility(View.VISIBLE);
        register.setVisibility(View.GONE);

        String url = "https://www.chqup.com/agent/registration_user.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() != 0) {
                    Toast.makeText(getActivity(), ""+response, Toast.LENGTH_SHORT).show();
                    mobile.setText("");
                    full_name.setText("");
                    username.setText("");
                    dob.setText("");
                    address.setText("");
                    state.setText("");
                    pincode.setText("");
                    city.setText("");
                    otp.setText("");

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

                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("comes", "other");
                params.put("mobile_no", mobi);
                params.put("username", username1);
                params.put("fullname", fullname);
                params.put("dob", dateof);
                params.put("address", address1);
                params.put("state", state1);
                params.put("city", city1);
                params.put("pincode", pincode1);
                params.put("otp", otp1);



                return params;
            }
        };

        mQueue.add(request);
    }


    void getAmount()
    {
        mQueue = Volley.newRequestQueue(getContext());

        progressBar.setVisibility(View.VISIBLE);
        register.setVisibility(View.GONE);

        String url = "https://www.chqup.com/agent/registration_user.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() != 0) {

                    amount.setText(response);
                   // Toast.makeText(getActivity(), ""+response, Toast.LENGTH_SHORT).show();
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

                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("comes", "money");

                return params;
            }
        };

        mQueue.add(request);
    }
}