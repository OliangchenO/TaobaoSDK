package com.module.taobao;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.login.LoginService;
import com.alibaba.sdk.android.session.SessionListener;
import com.alibaba.sdk.android.session.model.Session;
import com.alibaba.sdk.android.session.model.User;
import com.alibaba.sdk.android.trade.CartService;
import com.alibaba.sdk.android.trade.OrderService;
import com.alibaba.sdk.android.trade.callback.TradeProcessCallback;
import com.alibaba.sdk.android.trade.model.TradeResult;
import com.taobao.tae.sdk.callback.InitResultCallback;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

public class AliBaiChuan extends UZModule {
	private UZModuleContext mJsCallback;
	static final int ACTIVITY_REQUEST_CODE_A = 100;

	public AliBaiChuan(UZWebView webView) {
		super(webView);
	}

	public void jsmethod_asyncInit(final UZModuleContext moduleContext) {

		AlibabaSDK.asyncInit(moduleContext.getContext(), new InitResultCallback() {

			@Override
			public void onSuccess() {
				LoginService loginService = AlibabaSDK.getService(LoginService.class);
                loginService.setSessionListener(new SessionListener() {

                    @Override
                    public void onStateChanged(Session session) {
                        if (session != null) {
                            Toast.makeText(getContext(),
                                    "session状态改变" + session.getUserId() + session.getUser() + session.isLogin(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "session is null", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

	public void jsmethod_showLogin(final UZModuleContext moduleContext) {
		mJsCallback = moduleContext;
		Intent intent = new Intent(getContext(), LoginActivity.class);
		intent.putExtra("option", Constants.SHOWLOGIN_INDEX);
		startActivityForResult(intent, ACTIVITY_REQUEST_CODE_A);
	}

	public void jsmethod_getUserInfo(final UZModuleContext moduleContext) {
		LoginService loginService = AlibabaSDK.getService(LoginService.class);
		JSONObject ret = new JSONObject();
		JSONObject err = new JSONObject();
		try {
			Session session = loginService.getSession();
			User user = session.getUser();
			ret.put("nick", user.nick);
			ret.put("id", user.id);
			ret.put("avatarUrl", user.avatarUrl);
			moduleContext.success(ret, true);
		} catch (Exception e) {
			try {
				err.put("code", ErrorEunm.UN_ASYNCINIT.getCode());
				err.put("msg", ErrorEunm.UN_ASYNCINIT.getMsg());
				moduleContext.error(ret, err, true);
			} catch (JSONException e1) {
			}
		}
	}
	
	public void jsmethod_logout(UZModuleContext moduleContext) {
		mJsCallback = moduleContext;
		Intent intent = new Intent(getContext(), LoginActivity.class);
		intent.putExtra("option", Constants.LOGOUT_INDEX);
		startActivityForResult(intent, ACTIVITY_REQUEST_CODE_A);
	}

	public void jsmethod_showCart(final UZModuleContext moduleContext) {
		final JSONObject ret = new JSONObject();
		final JSONObject err = new JSONObject();
		try {
			CartService cartService = AlibabaSDK.getService(CartService.class);
			cartService.showCart(getContext(), new TradeProcessCallback() {

				@Override
				public void onFailure(int code, String message) {
					try {
						err.put("code", code);
						err.put("msg", message);
						moduleContext.error(ret, err, true);
					} catch (JSONException e) {
					}
					
				}

				@Override
				public void onPaySuccess(TradeResult tradeResult) {
					try {
						List<Long> payFailedOrders = tradeResult.payFailedOrders;
						List<Long> paySuccessOrders = tradeResult.paySuccessOrders;
						
						ret.put("payFailedOrders", DataUtils.toString(payFailedOrders));
						ret.put("paySuccessOrders", DataUtils.toString(paySuccessOrders));
						
						moduleContext.success(ret, true);
					} catch (JSONException e) {
					}
				}
			});
		} catch (Exception e) {
			try {
				err.put("code", ErrorEunm.UN_ASYNCINIT.getCode());
				err.put("msg", ErrorEunm.UN_ASYNCINIT.getMsg());
				moduleContext.error(ret, err, true);
			} catch (JSONException e1) {
			}
		}
	}
	
	public void jsmethod_showMyOrders(final UZModuleContext moduleContext) {
		//订单服务
		OrderService orderService = AlibabaSDK.getService(OrderService.class);
		//orderService.showOrder
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == ACTIVITY_REQUEST_CODE_A) {
			String result = data.getStringExtra("result");
			if (null != result && null != mJsCallback) {
				try {
					JSONObject ret = new JSONObject(result);
					mJsCallback.success(ret, true);
					mJsCallback = null;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} else {
			String result = data.getStringExtra("result");
			if (null != result && null != mJsCallback) {
				try {
					JSONObject ret = new JSONObject();
					JSONObject err = new JSONObject(result);
					mJsCallback.error(ret, err, true);
					mJsCallback = null;
				} catch (JSONException e) {
				}
			}
		}
	}
}
