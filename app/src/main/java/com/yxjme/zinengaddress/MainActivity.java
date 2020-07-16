package com.yxjme.zinengaddress;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ed_input = findViewById(R.id.ed_input);
        ed_output = findViewById(R.id.ed_output);

       new SmartAddressUtil(this,ed_input) {
            @Override
            public void onAddressResult(final AddressBean addressBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ed_output.setText(new Gson().toJson(addressBean));
                    }
                });
            }
        };
    }


    EditText ed_input ;
    TextView ed_output;

    /**
     * @param view
     */
    public void check(View view){
    }
}