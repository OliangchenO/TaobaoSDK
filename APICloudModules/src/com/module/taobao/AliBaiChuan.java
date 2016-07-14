package com.module.taobao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.login.LoginService;
import com.alibaba.sdk.android.session.SessionListener;
import com.alibaba.sdk.android.session.model.Session;
import com.alibaba.sdk.android.trade.*;
import com.alibaba.sdk.android.trade.callback.TradeProcessCallback;
import com.alibaba.sdk.android.trade.model.TaokeParams;
import com.alibaba.sdk.android.trade.model.TradeResult;
import com.alibaba.sdk.android.trade.page.*;
import com.ta.utdid2.android.utils.StringUtils;
import com.taobao.tae.sdk.callback.InitResultCallback;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import android.app.Activity;
import android.content.Intent;

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
					}
				});
				JSONObject ret = new JSONObject();
				try {
					ret.put("msg", StatusEunm.SUCCESS.getMsg());
					ret.put("code", StatusEunm.SUCCESS.getCode());
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
		// loginService.showLogin(getContext(), new InternalLoginCallback());
		mJsCallback = moduleContext;
		Intent intent = new Intent(getContext(), LoginActivity.class);
		intent.putExtra("option", Constants.SHOWLOGIN_INDEX);
		startActivityForResult(intent, ACTIVITY_REQUEST_CODE_A);
	}

	@SuppressWarnings("rawtypes")
	public void jsmethod_getUserInfo(final UZModuleContext moduleContext) {
		LoginService loginService = AlibabaSDK.getService(LoginService.class);
		JSONObject ret = new JSONObject();
		JSONObject err = new JSONObject();
		try {
			Session session = loginService.getSession();
			String AuthorizationCode = session.getAuthorizationCode();
			ret.put("authorizationCode", AuthorizationCode);
			ret.put("loginTime", session.getLoginTime());
			Map otherInfo = session.getOtherInfo();
			ret.put("otherInfo", otherInfo == null ? "" : String.valueOf(otherInfo));
			ret.put("nick", session.getUser().nick);
			ret.put("avatarUrl", session.getUser().avatarUrl);
			ret.put("userId", session.getUserId());
			ret.put("isLogin", session.isLogin());
			ret.put("msg", StatusEunm.SUCCESS.getMsg());
			ret.put("code", StatusEunm.SUCCESS.getCode());
			moduleContext.success(ret, true);
		} catch (Exception e) {
			try {
				err.put("code", StatusEunm.UN_ASYNCINIT.getCode());
				err.put("msg", StatusEunm.UN_ASYNCINIT.getMsg());
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

	public void jsmethod_showItemDetailPage(final UZModuleContext moduleContext) {
		final JSONObject ret = new JSONObject();
		final JSONObject err = new JSONObject();
		try {
			Map<String, String> exParams = new HashMap<String, String>();
			String isv_code = moduleContext.optString("isv_code");
			if (!StringUtils.isEmpty(isv_code)) {
				exParams.put(TradeConstants.ISV_CODE, isv_code);
				exParams.put(TradeConstants.ITEM_DETAIL_VIEW_TYPE, TradeConstants.TAOBAO_H5_VIEW);
			}
			String itemId = moduleContext.optString("itemId");
			ItemDetailPage itemDetailPage = null;
			if (!StringUtils.isEmpty(itemId)) {
				itemDetailPage = new ItemDetailPage(itemId, exParams);
			} else {
				err.put("code", StatusEunm.ILLEGAL_ARGUMENT.getCode());
				err.put("msg", StatusEunm.ILLEGAL_ARGUMENT.getMsg());
				moduleContext.error(ret, err, true);
			}
			TaokeParams taokeParams = new TaokeParams();
			String pid = moduleContext.optString("pid");
			if (!StringUtils.isEmpty(pid)) {
				taokeParams.pid = pid;
			}
			TradeService tradeService = AlibabaSDK.getService(TradeService.class);
			tradeService.show(itemDetailPage, taokeParams, mContext, null, new TradeProcessCallback() {

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
						ret.put("msg", StatusEunm.SUCCESS.getMsg());
						ret.put("code", StatusEunm.SUCCESS.getCode());
						ret.put("payFailedOrders", DataUtils.toString(payFailedOrders));
						ret.put("paySuccessOrders", DataUtils.toString(paySuccessOrders));

						moduleContext.success(ret, true);
					} catch (JSONException e) {
					}
				}
			});
		} catch (Exception e) {
			try {
				err.put("code", StatusEunm.UN_ASYNCINIT.getCode());
				err.put("msg", StatusEunm.UN_ASYNCINIT.getMsg());
				moduleContext.error(ret, err, true);
			} catch (JSONException e1) {
			}
		}

	}

	public void jsmethod_showMyCartsPage(final UZModuleContext moduleContext) {
		final JSONObject ret = new JSONObject();
		final JSONObject err = new JSONObject();
		try {
			MyCartsPage myCartsPage = new MyCartsPage();
			String isv_code = moduleContext.optString("isv_code");
			if (!StringUtils.isEmpty(isv_code)) {
				TradeConfigs.defaultISVCode = isv_code; // 传入isv_code
			}
			TaokeParams taokeParams = new TaokeParams();
			String pid = moduleContext.optString("pid");
			if (!StringUtils.isEmpty(pid)) {
				taokeParams.pid = pid;
			}
			TradeService tradeService = AlibabaSDK.getService(TradeService.class);
			tradeService.show(myCartsPage, taokeParams, mContext, null, new TradeProcessCallback() {

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
						ret.put("msg", StatusEunm.SUCCESS.getMsg());
						ret.put("code", StatusEunm.SUCCESS.getCode());
						ret.put("payFailedOrders", DataUtils.toString(payFailedOrders));
						ret.put("paySuccessOrders", DataUtils.toString(paySuccessOrders));

						moduleContext.success(ret, true);
					} catch (JSONException e) {
					}
				}
			});
		} catch (Exception e) {
			try {
				err.put("code", StatusEunm.UN_ASYNCINIT.getCode());
				err.put("msg", StatusEunm.UN_ASYNCINIT.getMsg());
				moduleContext.error(ret, err, true);
			} catch (JSONException e1) {
			}
		}
	}

	public void jsmethod_showMyOrdersPage(final UZModuleContext moduleContext) {
		final JSONObject ret = new JSONObject();
		final JSONObject err = new JSONObject();
		int type = moduleContext.optInt("type", 0);// 0：全部；1：待付款；2：待发货；3：待收货；4：待评价。若传入的不是这几个数字，则跳转到“全部”
		boolean allOrder = moduleContext.optBoolean("allOrder", false);
		MyOrdersPage myOrdersPage = new MyOrdersPage(type, allOrder);
		TaokeParams taokeParams = new TaokeParams();
		String pid = moduleContext.optString("pid");
		if (!StringUtils.isEmpty(pid)) {
			taokeParams.pid = pid;
		}
		TradeService tradeService = AlibabaSDK.getService(TradeService.class);
		tradeService.show(myOrdersPage, taokeParams, mContext, null, new TradeProcessCallback() {

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
					ret.put("msg", StatusEunm.SUCCESS.getMsg());
					ret.put("code", StatusEunm.SUCCESS.getCode());
					ret.put("payFailedOrders", DataUtils.toString(payFailedOrders));
					ret.put("paySuccessOrders", DataUtils.toString(paySuccessOrders));

					moduleContext.success(ret, true);
				} catch (JSONException e) {
				}
			}
		});
	}

	/**
	 * 打开我的卡券包页
	 * 
	 * @param moduleContext
	 */
	public void jsmethod_showMyCardCouponsPage(final UZModuleContext moduleContext) {
		final JSONObject ret = new JSONObject();
		final JSONObject err = new JSONObject();
		MyCardCouponsPage myCardCouponsPage = new MyCardCouponsPage();
		TaokeParams taokeParams = new TaokeParams();
		String pid = moduleContext.optString("pid");
		if (!StringUtils.isEmpty(pid)) {
			taokeParams.pid = pid;
		}
		TradeService tradeService = AlibabaSDK.getService(TradeService.class);
		tradeService.show(myCardCouponsPage, taokeParams, mContext, null, new TradeProcessCallback() {

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
					ret.put("msg", StatusEunm.SUCCESS.getMsg());
					ret.put("code", StatusEunm.SUCCESS.getCode());
					ret.put("payFailedOrders", DataUtils.toString(payFailedOrders));
					ret.put("paySuccessOrders", DataUtils.toString(paySuccessOrders));

					moduleContext.success(ret, true);
				} catch (JSONException e) {
				}
			}
		});
	}

	// 打开优惠券页
	public void jsmethod_showPromotionsPage(final UZModuleContext moduleContext) {
		final JSONObject ret = new JSONObject();
		final JSONObject err = new JSONObject();
		String shop = moduleContext.optString("shopId");
		PromotionsPage promotionsPage = null;
		if (StringUtils.isEmpty(shop)) {
			promotionsPage = new PromotionsPage("shop", shop);
		} else {
			try {
				err.put("code", StatusEunm.ILLEGAL_ARGUMENT.getCode());
				err.put("msg", StatusEunm.ILLEGAL_ARGUMENT.getMsg());
				moduleContext.error(ret, err, true);
			} catch (JSONException e) {
			}
		}
		TaokeParams taokeParams = new TaokeParams();
		String pid = moduleContext.optString("pid");
		if (!StringUtils.isEmpty(pid)) {
			taokeParams.pid = pid;
		}
		TradeService tradeService = AlibabaSDK.getService(TradeService.class);
		tradeService.show(promotionsPage, taokeParams, mContext, null, new TradeProcessCallback() {

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
					ret.put("msg", StatusEunm.SUCCESS.getMsg());
					ret.put("code", StatusEunm.SUCCESS.getCode());
					ret.put("payFailedOrders", DataUtils.toString(payFailedOrders));
					ret.put("paySuccessOrders", DataUtils.toString(paySuccessOrders));

					moduleContext.success(ret, true);
				} catch (JSONException e) {
				}
			}
		});
	}

	/**
	 * 根据商品链接打开商品页面
	 * 
	 * @param moduleContext
	 *            url：必填 打开的链接地址 isv_code： 选填， pid：选填
	 */
	public void jsmethod_showPageByUrl(final UZModuleContext moduleContext) {
		final JSONObject ret = new JSONObject();
		final JSONObject err = new JSONObject();

		String url = moduleContext.optString("url");
		Page page = null;
		if (!StringUtils.isEmpty(url)) {
			String isv_code = moduleContext.optString("isv_code");
			Map<String, Object> exParams = new HashMap<String, Object>();
			if (!StringUtils.isEmpty(isv_code)) {
				exParams.put(TradeConstants.ISV_CODE, isv_code);
			}
			page = new Page(url, exParams);
		} else {
			try {
				err.put("code", StatusEunm.ILLEGAL_ARGUMENT.getCode());
				err.put("msg", StatusEunm.ILLEGAL_ARGUMENT.getMsg());
				moduleContext.error(ret, err, true);
			} catch (JSONException e) {
			}
		}

		TaokeParams taokeParams = new TaokeParams();
		String pid = moduleContext.optString("pid");
		if (!StringUtils.isEmpty(pid)) {
			taokeParams.pid = pid;
		}
		TradeService tradeService = AlibabaSDK.getService(TradeService.class);
		tradeService.show(page, taokeParams, mContext, null, new TradeProcessCallback() {

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
					ret.put("msg", StatusEunm.SUCCESS.getMsg());
					ret.put("code", StatusEunm.SUCCESS.getCode());
					ret.put("payFailedOrders", DataUtils.toString(payFailedOrders));
					ret.put("paySuccessOrders", DataUtils.toString(paySuccessOrders));

					moduleContext.success(ret, true);
				} catch (JSONException e) {
				}
			}
		});
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
