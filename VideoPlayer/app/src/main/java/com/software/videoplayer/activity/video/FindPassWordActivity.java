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
import butterknife.OnClick;

public class FindPassWordActivity extends AppCompatActivity {

    @BindView(R.id.fp_as)
    EditText fpAs;
    @BindView(R.id.fp_ok)
    Button fpOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pass_word);
        ButterKnife.bind(this);
        fpOk.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!fpAs.getText().toString().equals("")) {
                    fpOk.setTextColor(getResources().getColor(R.color.colorPrimary));
                    fpOk.setClickable(true);
                } else {
                    fpOk.setTextColor(getResources().getColor(R.color.darkGray));
                    fpOk.setClickable(false);
                }
            }
        });
    }

    @OnClick(R.id.fp_ok)
    public void onViewClicked() {
        if (fpAs.getText().toString().equals(SPHelper.getStringnMessage(this, MyConstants.LOCK_ANSWER))) {
            startActivity(new Intent(FindPassWordActivity.this, RegisterActivity.class));
            this.finish();
        } else {
            Toast.makeText(this, "密保问题错误！", Toast.LENGTH_SHORT).show();
        }
    }

}
