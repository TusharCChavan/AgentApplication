package com.chqup.agent;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;


public class LoginCardActivity extends AppCompatActivity {

    private View parent_view;

    MaterialRippleLayout sign_in;
    ProgressBar progressbar;
    private RequestQueue mQueue;

    TextInputEditText username, password;
    AppCompatCheckBox check;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_card);

        if (checkOut().equals("0"))
        {
            startActivity(new Intent(getApplicationContext(), MainActivity.class).putExtra("comesfrom","no"));
            finish();
        }

        mQueue = Volley.newRequestQueue(this);

        check = findViewById(R.id.check);
        parent_view = findViewById(android.R.id.content);
        sign_in = findViewById(R.id.sign_in);
        progressbar = findViewById(R.id.progressbar);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check.isChecked())
                {
                    check.setChecked(true);
                }
                else
                {
                    check.setChecked(false);
                }
            }
        });

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                    login(username.getText().toString(), password.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "Please Complete All Field", Toast.LENGTH_SHORT).show();
                }
//
//                startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                finish();
            }
        });


//        ((View) findViewById(R.id.forgot_password)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(parent_view, "Forgot Password", Snackbar.LENGTH_SHORT).show();
//            }
//        });
    }


    public void login(String username, String pass) {
        progressbar.setVisibility(View.VISIBLE);
        sign_in.setVisibility(View.GONE);

        String url = "https://www.chqup.com/agent/agent_login.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressbar.setVisibility(View.GONE);
                sign_in.setVisibility(View.VISIBLE);

                if (response.length() != 0) {
                    if (response.charAt(0) != 'F') {

                        if(check.isChecked())
                        {
                            insertData(pass,username,"null","null");
                        }

                        Toast.makeText(LoginCardActivity.this, "Done", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class).putExtra("comesfrom","no"));
                        finish();
                    } else {
                        Toast.makeText(LoginCardActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressbar.setVisibility(View.GONE);
                sign_in.setVisibility(View.VISIBLE);
                Toast.makeText(LoginCardActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("username", username);
                params.put("password", pass);

                return params;
            }
        };

        mQueue.add(request);
    }




    SQLiteDatabase db;
    void insertData(String aid,String username,String usergmail,String userimage)
    {
        Cursor c = null;


        db = openOrCreateDatabase("UserData", MODE_PRIVATE, null);

        String sql = "create table if not exists userdata (indeval text,aid text,username text,usergmail text,userimage text);";
        db.execSQL(sql);

        ContentValues values = new ContentValues();
        values.put("indeval","0");
        values.put("aid","");
        values.put("username","");
        values.put("usergmail","");
        values.put("userimage","");

        db.insert("userdata",null,values);

        db.execSQL("update userdata set aid='" + aid + "' where indeval='" + "0" + "';");
        db.execSQL("update userdata set username='" + username + "' where indeval='" + "0" + "';");
        db.execSQL("update userdata set usergmail='" + usergmail + "' where indeval='" + "0" + "';");
        db.execSQL("update userdata set userimage='" + userimage + "' where indeval='" + "0" + "';");
    }
    String checkOut()
    {
        Cursor c = null;
        String i = "";
        db = openOrCreateDatabase("UserData", MODE_PRIVATE, null);
        String sql = "create table if not exists userdata (indeval text,aid text,username text,usergmail text,userimage text);";
        db.execSQL(sql);
        c = db.rawQuery("select * from userdata;", null);
        c.moveToFirst();
        for (int ii = 0; c.moveToPosition(ii); ii++) {
            i = c.getString(0);
        }
        return i;
    }

}
