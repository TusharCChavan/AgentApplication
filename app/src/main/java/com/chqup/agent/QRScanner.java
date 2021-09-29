package com.chqup.agent;

import java.util.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {


    ZXingScannerView zXingScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
            }
        }

        zXingScannerView = new ZXingScannerView(this);
        setContentView(zXingScannerView);
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }

    @Override
    public void handleResult(Result result) {
        Log.d("daat", result.getText());
        processScannedData(result.getText());
    }


    @Override
    protected void onPause() {
        super.onPause();

        zXingScannerView.stopCamera();
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();


        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
    }

    private static final int MY_CAMERA_REQUEST_CODE = 100;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void processScannedData(String scanData) {
        Log.d("sahil", scanData);
        XmlPullParserFactory pullParserFactory;
        try {
            // init the parserfactory
            pullParserFactory = XmlPullParserFactory.newInstance();
            // get the parser
            XmlPullParser parser = pullParserFactory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new StringReader(scanData));
            // parse the XML
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    Log.d("sahil", "Start document");
                } else if (eventType == XmlPullParser.START_TAG) {
                    // extract data from tag
                    //name

                    try {
                        name = parser.getAttributeValue(null, DataAttributes.AADHAR_NAME_ATTR) == null ? parser.getAttributeValue(null, "n") == null ? "" : parser.getAttributeValue(null, "n") : parser.getAttributeValue(null, DataAttributes.AADHAR_NAME_ATTR);
                        //gender
                        gender = parser.getAttributeValue(null, DataAttributes.AADHAR_GENDER_ATTR) == null ? parser.getAttributeValue(null, "g") == null ? "" : parser.getAttributeValue(null, "g") : parser.getAttributeValue(null, DataAttributes.AADHAR_GENDER_ATTR);
                        //address
                        address = parser.getAttributeValue(null, DataAttributes.AADHAR_ADDRESS_ATTR) == null ? parser.getAttributeValue(null, "a") == null ? "" : parser.getAttributeValue(null, "a") : parser.getAttributeValue(null, DataAttributes.AADHAR_ADDRESS_ATTR);
                        //dob
                        dob = parser.getAttributeValue(null, DataAttributes.AADHAR_DOB_ATTR) == null ? parser.getAttributeValue(null, "d") == null ? "" : parser.getAttributeValue(null, "d") : parser.getAttributeValue(null, DataAttributes.AADHAR_DOB_ATTR);
                        //state
                        state = parser.getAttributeValue(null, DataAttributes.AADHAR_STATE_ATTR) == null ? "" : parser.getAttributeValue(null, DataAttributes.AADHAR_STATE_ATTR);
                        //dist
                        dist = parser.getAttributeValue(null, DataAttributes.AADHAR_DIS_ATTR) == null ? "" : parser.getAttributeValue(null, DataAttributes.AADHAR_DIS_ATTR);
                        //dist
                        pc = parser.getAttributeValue(null, DataAttributes.AADHAR_PC_ATTR) == null ? "" : parser.getAttributeValue(null, DataAttributes.AADHAR_PC_ATTR);

                        if (address.isEmpty()) {
                            address = parser.getAttributeValue(null, "loc") == null ? "location" : parser.getAttributeValue(null, "loc");
                        }


                        Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();


                        startActivity(new Intent(getApplicationContext(), MainActivity.class)
                                .putExtra("comesfrom", "scanner")
                                .putExtra("username", name)
                                .putExtra("dob", dob)
                                .putExtra("address", address)
                                .putExtra("gender", gender)
                                .putExtra("state", state)
                                .putExtra("dist", dist)
                                .putExtra("pc", pc)
                        );

                        finish();


                    } catch (Exception ex) {


                        Toast.makeText(getApplicationContext(), "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    } finally {

                    }


                } else if (eventType == XmlPullParser.END_TAG) {
                    Log.d("sahil", "End tag " + parser.getName());
                } else if (eventType == XmlPullParser.TEXT) {
                    Log.d("sahil", "Text " + parser.getText());
                }

                // update eventType
                eventType = parser.next();
            }
            // display the data on screen
            // displayScannedData();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }// EO function

    String name = "", gender = "", address = "", dob = "", state = "", dist = "", pc = "";

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(getApplicationContext(), MainActivity.class)
                .putExtra("comesfrom", "no")
        );
    }
}


class DataAttributes {
    // declare xml attributes of aadhar card QR code xml response
    public static final String AADHAAR_DATA_TAG = "PrintLetterBarcodeData",
            AADHAR_NAME_ATTR = "name",
            AADHAR_GENDER_ATTR = "gender",
            AADHAR_DOB_ATTR = "dob",
            AADHAR_ADDRESS_ATTR = "address",
            AADHAR_STATE_ATTR = "state",
            AADHAR_DIS_ATTR = "dist",
            AADHAR_PC_ATTR = "pc";
}