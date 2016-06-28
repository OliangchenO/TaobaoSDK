package com.module.taobao;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.login.LoginService;
import com.alibaba.sdk.android.login.callback.LoginCallback;
import com.alibaba.sdk.android.login.callback.LogoutCallback;
import com.alibaba.sdk.android.session.model.Session;

import android.content.Intent;
import android.os.Bundle;


public class LoginActivity extends AbstractActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 登录服务
		LoginService loginService = AlibabaSDK.getService(LoginService.class);
		
		Intent data = getIntent();
		final Intent resultData = new Intent();
		final JSONObject json = new JSONObject();
		int option = data.getIntExtra("option", 1);
		switch (option) {
		case Constants.SHOWLOGIN_INDEX:
			showLogin(loginService, resultData, json);
			break;
		case Constants.LOGOUT_INDEX:
			logout(loginService, resultData, json);
			break;
		}

	}

	private void logout(LoginService loginService, final Intent resultData, final JSONObject json) {
		try {
			loginService.logout(LoginActivity.this, new LogoutCallback() {
				@Override
				public void onFailure(int code, String msg) {
					try {
						json.put("code", code);
						json.put("msg", msg);
					} catch (JSONException e) {
					}
					resultData.putExtra("result", json.toString());
					setResult(RESULT_CANCELED, resultData);
					finish();
				}

				@Override
				public void onSuccess() {
					try {
						json.put("msg", "登出成功");
					} catch (JSONException e) {
					}
					resultData.putExtra("result", json.toString());
					setResult(RESULT_OK, resultData);
					finish();
				}
			});
		} catch (Exception e) {
			try {
				json.put("code", ErrorEunm.UN_ASYNCINIT.getCode());
				json.put("msg", ErrorEunm.UN_ASYNCINIT.getMsg());
			} catch (JSONException e1) {
			}
			resultData.putExtra("result", json.toString());
			setResult(RESULT_CANCELED, resultData);
			finish();
		}
	}

	private void showLogin(LoginService loginService, final Intent resultData, final JSONObject json) {
		try {
			loginService.showLogin(LoginActivity.this, new InternalLoginCallback());
		} catch (Exception e2) {
			try {
				json.put("code", ErrorEunm.UN_ASYNCINIT.getCode());
				json.put("msg", ErrorEunm.UN_ASYNCINIT.getMsg());
			} catch (JSONException e1) {
			}
			resultData.putExtra("result", json.toString());
			setResult(RESULT_CANCELED, resultData);
			finish();
		}
	}

	private class InternalLoginCallback implements LoginCallback {

		@SuppressWarnings({ "rawtypes" })
		@Override
		public void onSuccess(Session session) {
			Intent resultData = new Intent();
			JSONObject json = new JSONObject();
			try {
				String AuthorizationCode = session.getAuthorizationCode();
				json.put("authorizationCode", AuthorizationCode);
				json.put("loginTime", session.getLoginTime());
				Map otherInfo = session.getOtherInfo();
				json.put("otherInfo", otherInfo == null ? "" : String.valueOf(otherInfo));
				json.put("nick", session.getUser().nick);
				json.put("avatarUrl", session.getUser().avatarUrl);
				json.put("userId", session.getUserId());
				json.put("isLogin", session.isLogin());
			} catch (JSONException e1) {
			}
//			CookieManager.getInstance().removeAllCookie();
//			CookieSyncManager.getInstance().sync();
//			Map<String, String[]> m = WebViewActivitySupport.getInstance().getCookies();
//			for (Entry<String, String[]> e : m.entrySet()) {
//				for (String s : e.getValue()) {
//					CookieManager.getInstance().setCookie(e.getKey(), s);
//				}
//			}
//			CookieSyncManager.getInstance().sync();
			resultData.putExtra("result", json.toString());
			setResult(RESULT_OK, resultData);
			finish();
		}

		@Override
		public void onFailure(int code, String message) {
			Intent resultData = new Intent();
			JSONObject json = new JSONObject();
			try {
				json.put("code", code);
				json.put("message", message);
			} catch (JSONException e) {
			}
			resultData.putExtra("result", json.toString());
			setResult(RESULT_CANCELED, resultData);
			finish();
		}
	}
}
