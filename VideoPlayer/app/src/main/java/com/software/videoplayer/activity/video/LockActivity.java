package com.software.videoplayer.activity.video;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.nightonke.blurlockview.BlurLockView;
import com.nightonke.blurlockview.Password;
import com.software.videoplayer.R;
import com.software.videoplayer.constants.MyConstants;
import com.software.videoplayer.data.Stroe;
import com.software.videoplayer.model.LockInfo;
import com.software.videoplayer.util.SPHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LockActivity extends AppCompatActivity {

    //    @BindView(R.id.pass_et)
//    EditText passEt;
//    @BindView(R.id.pass_bt)
//    Button passBt;
    @BindView(R.id.blur_lock)
    BlurLockView blurLock;
    @BindView(R.id.lock_img)
    ImageView lockImg;

    private List<LockInfo> video = new ArrayList<>();
    private List<LockInfo> image = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        ButterKnife.bind(this);


        getData();
        initView();
    }

    private void initView() {

        blurLock.setBlurredView(lockImg);
        blurLock.setCorrectPassword(SPHelper.getStringnMessage(this, MyConstants.LOCK_PASSWORD));

        blurLock.setType(Password.NUMBER, false);
        blurLock.setOnPasswordInputListener(new BlurLockView.OnPasswordInputListener() {
            @Override
            public void correct(String inputPassword) {
                Intent intent = new Intent(LockActivity.this, PrivateActivity.class);
                intent.putParcelableArrayListExtra(MyConstants.LOCK_VIDEO, (ArrayList<? extends Parcelable>) video);
                intent.putParcelableArrayListExtra(MyConstants.LOCK_IMAGE, (ArrayList<? extends Parcelable>) image);
                startActivity(intent);
                LockActivity.this.finish();
            }

            @Override
            public void incorrect(String inputPassword) {
                Toast.makeText(LockActivity.this, "密码输入错误！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void input(String inputPassword) {

            }
        });
        blurLock.getLeftButton().setText("忘记密码？");
        blurLock.getLeftButton().setOnClickListener(v -> startActivity(new Intent(LockActivity.this, FindPassWordActivity.class)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("unchecked")
    public void getData() {
        if (Stroe.getData(this, MyConstants.LOCK_VIDEO, LockInfo.class) != null) {
            video = (List<LockInfo>) Stroe.getData(this, MyConstants.LOCK_VIDEO, LockInfo.class);
        }
        if (Stroe.getData(this, MyConstants.LOCK_IMAGE, LockInfo.class) != null) {
            image = (List<LockInfo>) Stroe.getData(this, MyConstants.LOCK_IMAGE, LockInfo.class);
        }

    }
}
