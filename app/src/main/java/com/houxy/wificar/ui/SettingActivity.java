package com.houxy.wificar.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.houxy.wificar.R;
import com.houxy.wificar.utils.SPUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Houxy on 2016/11/16.
 */

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.etCommForward) EditText mEtCommForward;
    @BindView(R.id.etCommBackward) EditText mEtCommBackward;
    @BindView(R.id.etCommLeft) EditText mEtCommLeft;
    @BindView(R.id.etCommRight) EditText mEtCommRight;
    @BindView(R.id.etCommStop) EditText mEtCommStop;
    @BindView(R.id.etPort) EditText mEtPort;
    @BindView(R.id.etAddressUrl) EditText mEtAddressUrl;
    @BindView(R.id.etVideoUrl) EditText mEtVideoUrl;
    @BindView(R.id.btnConfirm) Button mBtnConfirm;
    @BindView(R.id.btnQuit) Button mBtnQuit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {

        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtil.setForwardComm(mEtCommForward.getText().toString());
                SPUtil.setBackwardComm(mEtCommBackward.getText().toString());
                SPUtil.setTurnLeftComm(mEtCommLeft.getText().toString());
                SPUtil.setTurnRightComm(mEtCommRight.getText().toString());
                SPUtil.setStopComm(mEtCommStop.getText().toString());
                SPUtil.setVideoAddress(mEtVideoUrl.getText().toString());
                SPUtil.setControlAddress(mEtAddressUrl.getText().toString());
                SPUtil.setControlPort(mEtPort.getText().toString());
                Toast.makeText(SettingActivity.this, R.string.comm_save_success, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        mBtnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingActivity.this, R.string.data_not_save, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }


}
