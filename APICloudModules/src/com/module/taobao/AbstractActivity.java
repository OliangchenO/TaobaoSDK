package com.module.taobao;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.alibaba.sdk.android.callback.CallbackContext;

public class AbstractActivity extends Activity {

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // 返回成功，淘宝将返回正确的信息，信息保存在intent中的result项中.
            Toast.makeText(this, data.getStringExtra("result"), Toast.LENGTH_SHORT).show();
        } else if (resultCode == RESULT_CANCELED) {
            // 用户主动取消操作.
        } else if (resultCode == -2) {
            // error,淘宝将返回错误码，同样解析intent中的result项，形式如下
            Toast.makeText(this, data.getStringExtra("result"), Toast.LENGTH_SHORT).show();
        }
        CallbackContext.onActivityResult(requestCode, resultCode, data);
    }
}
