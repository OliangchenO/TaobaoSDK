package com.module.taobao;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.login.LoginService;
import com.alibaba.sdk.android.login.callback.LoginCallback;
import com.alibaba.sdk.android.session.model.Session;
import com.alibaba.sdk.android.ui.support.WebViewActivitySupport;
import com.taobao.tae.sdk.callback.InitResultCallback;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;


public class AliBaiChuan extends UZModule {

	public AliBaiChuan(UZWebView webView) {
		super(webView);
	}
	
	
	public void jsmethod_asyncInit(final UZModuleContext moduleContext) {
		
		AlibabaSDK.asyncInit(moduleContext.getContext(), new InitResultCallback() {
			 
            @Override
            public void onSuccess() {
            	JSONObject ret = new JSONObject();
            	try {
					ret.put("msg", "初始化成功");
					moduleContext.success(ret, true);
				} catch (JSONException e) {
				}
            }
 
            @Override
            public void onFailure(int code, String message) {
            	JSONObject ret = new JSONObject();
            	JSONObject err = new JSONObject();
            	try {
					err.put("code", code);
					err.put("msg", message);
					moduleContext.error(ret, err, true);
				} catch (JSONException e) {
				}
            }
        });
	}
	
	public void jsmethod_showLogin(final UZModuleContext moduleContext){
		Toast.makeText(
        		mContext,
                "显示成功", Toast.LENGTH_SHORT).show();
		try {
			AlibabaSDK.getService(LoginService.class).showLogin(mContext, new InternalLoginCallback());
			Toast.makeText(
	        		mContext,
	                "成功", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(
	        		mContext,
	               "error", Toast.LENGTH_SHORT).show();
		}
	}
	
	private class InternalLoginCallback implements LoginCallback {

        @Override
        public void onSuccess(Session session) {
            Toast.makeText(
            		mContext,
                    "授权成功" + session.getUserId() + session.getUser().nick + session.isLogin() + session.getLoginTime()
                            + session.getUser().avatarUrl, Toast.LENGTH_SHORT).show();
            CookieManager.getInstance().removeAllCookie();
            CookieSyncManager.getInstance().sync();
            Map<String, String[]> m = WebViewActivitySupport.getInstance().getCookies();
            for (Entry<String, String[]> e : m.entrySet()) {
                for (String s : e.getValue()) {
                    CookieManager.getInstance().setCookie(e.getKey(), s);
                    System.out.println("key: " + e.getKey() + " value: " + s);
                }
            }
            CookieSyncManager.getInstance().sync();
            System.out.println("------------" + CookieManager.getInstance().getCookie("http://taobao.com"));
        }

        @Override
        public void onFailure(int code, String message) {
            Toast.makeText(mContext, "授权取消" + code, Toast.LENGTH_SHORT).show();
        }
    }
}
