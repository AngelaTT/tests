package com.software.videoplayer.activity.video;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.software.videoplayer.R;
import com.software.videoplayer.constants.MyConstants;
import com.software.videoplayer.util.SPHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {


    @BindView(R.id.ra_pa_f)
    EditText raPaF;
    @BindView(R.id.ra_pa_s)
    EditText raPaS;
    @BindView(R.id.ra_ok)
    Button raOk;
    @BindView(R.id.ra_pq)
    EditText raPq;
    private String passF, passS,passQ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        raPaF.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!raPaF.getText().toString().equals("")) {
                    raOk.setTextColor(getResources().getColor(R.color.colorPrimary));
                    raOk.setClickable(true);
                } else {
                    raOk.setTextColor(getResources().getColor(R.color.darkGray));
                    raOk.setClickable(false);
                }
            }
        });
        raPaS.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!raPaS.getText().toString().equals("")) {
                    raOk.setTextColor(getResources().getColor(R.color.colorPrimary));
                    raOk.setClickable(true);
                } else {
                    raOk.setTextColor(getResources().getColor(R.color.darkGray));
                    raOk.setClickable(false);
                }
            }
        });

        raPq.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!raPq.getText().toString().equals("")) {
                    raOk.setTextColor(getResources().getColor(R.color.colorPrimary));
                    raOk.setClickable(true);
                } else {
                    raOk.setTextColor(getResources().getColor(R.color.darkGray));
                    raOk.setClickable(false);
                }
            }
        });
        raOk.setOnClickListener(v -> {
            passF = raPaF.getText().toString();
            passS = raPaS.getText().toString();

            if (passF.equals(passS)) {
                passQ = raPq.getText().toString();
                SPHelper.putBooleanMessage(RegisterActivity.this, MyConstants.IS_FIRST, true);
                SPHelper.putStringMessage(RegisterActivity.this, MyConstants.LOCK_PASSWORD, passS);
                SPHelper.putStringMessage(RegisterActivity.this, MyConstants.LOCK_ANSWER, passQ);
                startActivity(new Intent(RegisterActivity.this, LockActivity.class));
                RegisterActivity.this.finish();
            } else {
                Toast.makeText(this, "两次输入的密码不一致！", Toast.LENGTH_SHORT).show();
            }

        });
    }
}
