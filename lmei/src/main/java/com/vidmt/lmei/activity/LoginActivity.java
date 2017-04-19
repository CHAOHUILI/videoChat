package com.vidmt.lmei.activity;

import android.app.Notification;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ta.annotation.TAInjectView;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.vidmt.lmei.Application;
import com.vidmt.lmei.R;
import com.vidmt.lmei.controller.Person_Service;
import com.vidmt.lmei.dialog.LoadingDialog;
import com.vidmt.lmei.entity.Persion;
import com.vidmt.lmei.util.rule.ManageDataBase;
import com.vidmt.lmei.util.rule.SharedPreferencesUtil;
import com.vidmt.lmei.util.think.JsonUtil;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class LoginActivity extends BaseActivity {
	@TAInjectView(id = R.id.headerthemeleft)
	RelativeLayout headerthemeleft;
	@TAInjectView(id = R.id.user)
	ImageView user;
	@TAInjectView(id = R.id.headerright)
	LinearLayout headerright;
	@TAInjectView(id = R.id.typelog)
	ImageView typelog;
	@TAInjectView(id = R.id.headercontent)
	TextView headercontent;
	@TAInjectView(id = R.id.headercontentv)
	View headercontentv;
	@TAInjectView(id = R.id.headercontent1)
	TextView headercontent1;
	@TAInjectView(id = R.id.headercontentv1)
	View headercontentv1;
	@TAInjectView(id = R.id.headconrel1)
	RelativeLayout headconrel1;
	private long exitTime = 0;

	@TAInjectView(id = R.id.logintel)
	EditText logintel;
	@TAInjectView(id = R.id.person_pwd)
	EditText person_pwd;
	@TAInjectView(id = R.id.loginbtn)
	TextView loginbtn;
	@TAInjectView(id = R.id.forget)
	TextView forget;
	@TAInjectView(id = R.id.logreglin)
	LinearLayout logreglin;
	@TAInjectView(id = R.id.wb)
	LinearLayout wb;// 微博
	@TAInjectView(id = R.id.qq)
	LinearLayout qq;// qq
	@TAInjectView(id = R.id.wx)
	LinearLayout wxreg;// 微信
	String name;
	String pwd;
	String icon;
	String uid = "";
	String sex;
	String birthday = "";
	String photo = "";
	int gender = 0;
	LoadingDialog dialog = null;
	IWXAPI mWeixinAPI = null;
	Tencent mTencent = null;
	protected static UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		themes();
		InitView();
	}

	@Override
	public void InitView() {
		// TODO Auto-generated method stub
		super.InitView();
		headerthemeleft.setVisibility(View.GONE);
		headerright.setVisibility(View.VISIBLE);
		headconrel1.setVisibility(View.GONE);
		headercontentv.setVisibility(View.GONE);
		headercontent.setText("登录");
		Drawable drawable = context.getResources().getDrawable(R.drawable.header_left);
		user.setBackgroundDrawable(drawable);
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(LoginActivity.this, "wx9c4eb94a79c7fa79",
				"3f0f88171dff91e336769611b6fedd4e");
		wxHandler.addToSocialSDK();
		wxHandler.setRefreshTokenAvailable(false);
		// 添加微博平台
		// mController.getConfig().setSsoHandler(new SinaSsoHandler());
		// UMSocialService mController =
		// UMServiceFactory.getUMSocialService("com.umeng.login");
		// 添加QQ平台
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(LoginActivity.this, "1105693336", "UJ25WWlq0srw5i9z");
		qqSsoHandler.addToSocialSDK();

		if (mWeixinAPI == null) {
			mWeixinAPI = WXAPIFactory.createWXAPI(this, "微信号", true);
			mWeixinAPI.registerApp("微信号");
		}
		if (mTencent == null) {
			mTencent = Tencent.createInstance("QQ号", this);
		}
	}

	@Override
	protected void onAfterSetContentView() {
		// TODO Auto-generated method stub
		super.onAfterSetContentView();
		OnClickListener onClickListener = new OnClickListener() {
			public static final int MIN_CLICK_DELAY_TIME = 1000;
			private long lastClickTime = 0;
			long currentTime;
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.headerthemeleft:
					finish();
					break;
				case R.id.loginbtn:
					Validation();
					break;
				case R.id.logreglin:
					// 注册
					finish();
					StartActivity(RegisterActivity.class);
					break;
				case R.id.forget:

					StartActivity(ChangePasswordActivity.class);

					break;

				case R.id.wb:
					//loadingDialog.show();
					mController.getConfig().setSsoHandler(new SinaSsoHandler());
					currentTime = Calendar.getInstance().getTimeInMillis();
					if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
						lastClickTime = currentTime;
						mController.doOauthVerify(LoginActivity.this, SHARE_MEDIA.SINA, new UMAuthListener() {
							@Override
							public void onError(SocializeException e, SHARE_MEDIA platform) {
								loadingDialog.dismiss();
							}

							@Override
							public void onComplete(Bundle value, SHARE_MEDIA platform) {
								if (value != null && !TextUtils.isEmpty(value.getString("uid"))) {
									// Toast.makeText(LoginActivity.this,
									// "授权成功.",
									// Toast.LENGTH_SHORT).show();

									mController.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.SINA,
											new UMDataListener() {
										@Override
										public void onStart() {
											// Toast.makeText(LoginActivity.this,
											// "获取平台数据开始...",
											// Toast.LENGTH_SHORT).show();
										}

										@Override
										public void onComplete(int status, Map<String, Object> info) {
											if (status == 200 && info != null) {
												/*
												 * StringBuilder sb = new
												 * StringBuilder(); Set<String>
												 * keys = info.keySet(); for
												 * (String key : keys) {
												 * sb.append(key + "=" +
												 * info.get(key).toString() +
												 * "\r\n"); }
												 */
												uid = info.get("uid").toString();
												name = info.get("screen_name").toString();
												icon = info.get("profile_image_url").toString();
												gender =( (Integer) info.get("gender")).intValue();
												if(gender==0){
													gender=2;
												}
												ThirdLogin();
                                                
												// String token =
												// info.get("access_token").toString();
												// Log.d("TestData",
												// sb.toString());
											} else {
												loadingDialog.dismiss();
												Log.d("TestData", "发生错误：" + status);
											}
										}
									});
								} else {

									Toast.makeText(LoginActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
									loadingDialog.dismiss();
								}
							}

							@Override
							public void onCancel(SHARE_MEDIA platform) {
								loadingDialog.dismiss();
							}

							@Override
							public void onStart(SHARE_MEDIA platform) {
								loadingDialog.show();
							}
						});
					}
					break;
				case R.id.qq:
					if (mTencent.isSupportSSOLogin(activity)) {
						mController.doOauthVerify(LoginActivity.this, SHARE_MEDIA.QQ, new UMAuthListener() {
							@Override
							public void onStart(SHARE_MEDIA platform) {
								// Toast.makeText(LoginActivity.this, "授权开始",
								// Toast.LENGTH_SHORT).show();
								loadingDialog.show();
							}

							@Override
							public void onError(SocializeException e, SHARE_MEDIA platform) {
								// Toast.makeText(LoginActivity.this, "授权错误",
								// Toast.LENGTH_SHORT).show();
								loadingDialog.dismiss();
							}

							@Override
							public void onComplete(Bundle value, SHARE_MEDIA platform) {
								// loadingDialog.show();
								// Toast.makeText(LoginActivity.this, "授权完成",
								// Toast.LENGTH_SHORT).show();
								uid = value.get("uid").toString();
								// 获取相关授权信息
								mController.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.QQ, new UMDataListener() {
									@Override
									public void onStart() {

										// Toast.makeText(LoginActivity.this,
										// "获取平台数据开始...",
										// Toast.LENGTH_SHORT).show();

										// dialog = new
										// LoadingDialog(LoginActivity.this);
										// dialog.show();
										// ThirdLogin();
									}

									@Override
									public void onComplete(int status, Map<String, Object> info) {
										if (status == 200 && info != null) {
											name = info.get("screen_name").toString();
											icon = info.get("profile_image_url").toString();
											sex = info.get("gender").toString();
											if (sex.equals("男")) {
												gender = 1;
											} else {
												gender = 2;
											}
											ThirdLogin();
										} else {
											Log.d("TestData", "发生错误：" + status);
											loadingDialog.dismiss();
										}
									}
								});
							}

							@Override
							public void onCancel(SHARE_MEDIA platform) {
								// Toast.makeText(LoginActivity.this, "授权取消",
								// Toast.LENGTH_SHORT).show();
								loadingDialog.dismiss();
							}
						});
					} else {
						loadingDialog.dismiss();
						Toast.makeText(LoginActivity.this, "请先安装QQ", Toast.LENGTH_SHORT).show();
					}

					break;
				case R.id.wx:
				    currentTime = Calendar.getInstance().getTimeInMillis();
					if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
						lastClickTime = currentTime;
						if (mWeixinAPI.isWXAppInstalled()) {
							mController.doOauthVerify(LoginActivity.this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
								@Override
								public void onStart(SHARE_MEDIA platform) {

									// Toast.makeText(LoginActivity.this,
									// "授权开始",
									// Toast.LENGTH_SHORT).show();
									loadingDialog.show();
								}

								@Override
								public void onError(SocializeException e, SHARE_MEDIA platform) {
									// Toast.makeText(LoginActivity.this,
									// "授权错误",
									// Toast.LENGTH_SHORT).show();
									loadingDialog.dismiss();
								}

								@Override
								public void onComplete(Bundle value, SHARE_MEDIA platform) {
									// Toast.makeText(LoginActivity.this,
									// "授权完成",
									// Toast.LENGTH_SHORT).show();
									// 获取相关授权信息
									uid = value.get("uid").toString();
									// ToastShow("Uid="+uid);

									mController.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.WEIXIN,
											new UMDataListener() {
										@Override
										public void onStart() {
											// Toast.makeText(LoginActivity.this,
											// "获取平台数据开始...",
											// Toast.LENGTH_SHORT).show();

										}

										@Override
										public void onComplete(int status, Map<String, Object> info) {
											if (status == 200 && info != null) {
												/*
												 * StringBuilder sb = new
												 * StringBuilder(); Set<String>
												 * keys = info.keySet();
												 * for(String key : keys){
												 * sb.append(key+"="+info.get(
												 * key). toString()+"\r\n"); }
												 */

												name = info.get("nickname").toString();
												icon = info.get("headimgurl").toString();
												gender = Integer.valueOf(info.get("sex").toString());
												ThirdLogin();

												// Log.d("TestData",sb.toString());
											} else {
												Log.d("TestData", "发生错误：" + status);
												loadingDialog.dismiss();
											}
										}
									});
								}

								@Override
								public void onCancel(SHARE_MEDIA platform) {
									// Toast.makeText(LoginActivity.this,
									// "授权取消",
									// Toast.LENGTH_SHORT).show();
									loadingDialog.dismiss();
								}
							});
						} else {
							loadingDialog.dismiss();
							Toast.makeText(LoginActivity.this, "请先安装微信", Toast.LENGTH_SHORT).show();
						}

					}

					break;
				default:
					break;
				}
			}
		};
		headerthemeleft.setOnClickListener(onClickListener);
		forget.setOnClickListener(onClickListener);
		wb.setOnClickListener(onClickListener);
		qq.setOnClickListener(onClickListener);
		wxreg.setOnClickListener(onClickListener);
		logreglin.setOnClickListener(onClickListener);
		loginbtn.setOnClickListener(onClickListener);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		/** 使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	public void Validation() {
		// //关闭小键盘
		// InputMethodManager imm = (InputMethodManager)getSystemService(
		// Context.INPUT_METHOD_SERVICE);
		// imm.hideSoftInputFromWindow(logintel.getWindowToken(), 0);
		name = logintel.getText().toString().trim();
		pwd = person_pwd.getText().toString().trim();
		if ("".equals(name)) // ||tel.equals("请输入您的手机号")
		{
			ToastShow("请输入您的手机号");
		} else if ("".equals(pwd)) // ||pwd.equals("请输入密码")
		{
			ToastShow("请输入您的密码");
		} else {
			loadingDialog.show();

			LoadData();
		}
	}

	public void LoadData() {
		super.LoadData();
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String jhuser = Person_Service.login(name, pwd, uid);
				Message msg = mUIHandler.obtainMessage(1);
				msg.obj = jhuser;
				msg.sendToTarget();
			}
		}).start();
	}

	public void ThirdLogin() {
		// loadingDialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String Third = uid;

				String jhuser = Person_Service.third_login(Third, name, birthday, gender, icon);
				Message msg = mUIHandler.obtainMessage(2);
				msg.obj = jhuser;
				msg.sendToTarget();
			}
		}).start();
	}
	Persion p_pe;
	private Handler mUIHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				if (msg.obj != null) {
					String mes = (String) msg.obj;

					if (mes.equals(JsonUtil.ObjToJson("error"))) {

						ToastShow("网络错误请重新登录");
						loadingDialog.dismiss();
					} else if (mes.equals(JsonUtil.ObjToJson("user_not_exist"))) {

						ToastShow("还没有该用户");
						loadingDialog.dismiss();
					} else if (mes.equals(JsonUtil.ObjToJson("fail"))) {

						ToastShow("用户名或密码输入有误");
						loadingDialog.dismiss();
					} else if (mes.equals("")) {

						ToastShow("请求超时");
						loadingDialog.dismiss();
					} else if (mes.equals(JsonUtil.ObjToJson("warning"))) {

						ToastShow("您的账户已被管理员禁用");
						loadingDialog.dismiss();
					} else {

						p_pe = JsonUtil.JsonToObj(mes, Persion.class);

						if (p_pe.getUse_state() != null && p_pe.getUse_state() == 2) {
							ToastShow("您的账户已被管理员禁用");
							loadingDialog.dismiss();
						} else {
							
							inityunong(p_pe);
						}

						// dbutil.insertData(p, Persion.class);
						// Intent intent = new Intent(LoginActivity.this,
						// FooterPageActivity.class);
						// startActivity(intent);
						// finish();
					}
					// loadingDialog.dismiss();
				}

				break;
			// 第三方
			case 2:
				if (msg.obj != null) {
					String mes = (String) msg.obj;
					if (mes.equals("fail")) {
						ToastShow("登录失败请重新登录");
						loadingDialog.dismiss();
					} else if (mes.equals(JsonUtil.ObjToJson("warning"))) {
						ToastShow("您的账户已被管理员禁用");
						loadingDialog.dismiss();
					} else {
						p_pe = JsonUtil.JsonToObj(mes, Persion.class);
						if (p_pe.getUse_state() != null && p_pe.getUse_state() == 2) {
							ToastShow("您的账户已被管理员禁用");
							loadingDialog.dismiss();
						} else {
							/*initJpush(p);
							SharedPreferencesUtil.putInt(LoginActivity.this, "editor", 0);
							ManageDataBase.Delete(dbutil, Persion.class, null);
							ManageDataBase.Insert(dbutil, Persion.class, p);*/
							inityunong(p_pe);
						}
					}
				}

				break;
			default:
				break;
			}
		}
	};

	// 设置别名用来发消息
	private void initJpush(Persion p) {
		// 设置消息栏样式
		BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(getApplicationContext());
		builder.statusBarDrawable = R.drawable.ic_launcher;
		builder.notificationFlags = Notification.FLAG_AUTO_CANCEL; // 设置为点击后自动消失
		builder.notificationDefaults = Notification.DEFAULT_SOUND; // 设置为铃声（
																	// Notification.DEFAULT_SOUND）或者震动（
																	// Notification.DEFAULT_VIBRATE）
		JPushInterface.setPushNotificationBuilder(1, builder);
		// 设置接受信息的数量
		JPushInterface.setLatestNotificationNumber(getApplicationContext(), 3);
		// 设置别名，用户id为别名
		Set<String> set = new HashSet<String>();
		set.add("all"); // 这指定all 是给所有安装爱狗之家发信息 set是TAG
		int phone = p.getId(); // 别名 指定给某个用户发
		JPushInterface.setAliasAndTags(getApplicationContext(), String.valueOf(phone), set, mAliasCallback);

	}

	private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			String logs;
			switch (code) {
			case 0:
				logs = "Set tag and alias success";
				Log.i("jpush success", logs);
				break;
			case 6002:
				logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
				Log.i("jpush Failed", logs);
				break;
			default:
				logs = "Failed with errorCode = " + code;
				Log.i("jpush Failed", logs);
			}
		}

	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			exit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 
	 * 融云连接
	 * 
	 **/
	private void inityunong(final Persion persion) {

		if (RongIM.getInstance().getRongIMClient() != null) {
			String as;

		}

		if (getApplicationInfo().packageName.equals(Application.getApplication().getPackageName())) {
			// ToastShow("connect");
			/**
			 * IMKit SDK调用第二步,建立与服务器的连接
			 */
			RongIM.connect(persion.getRongyuntoken(), new RongIMClient.ConnectCallback() {

				/**
				 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的
				 * Token
				 */
				@Override
				public void onTokenIncorrect() {
					loadingDialog.dismiss();
					Looper.prepare();
					try {
						Toast.makeText(LoginActivity.this, "连接融云失败", Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					Looper.loop();
					Log.d("LoginActivity", "--onTokenIncorrect");				
					// StartActivity(HomePageActivity.class);
					//
					// finish();
				}

				/**
				 * 连接融云成功
				 * 
				 * @param userid
				 *            当前 token
				 */
				@Override
				public void onSuccess(String userid) {

					Log.d("LoginActivity", "--onSuccess" + userid);
					/**
					 * 刷新用户缓存数据。
					 *
					 * @param userInfo
					 *            需要更新的用户缓存数据。
					 */
					// ToastShow("onSuccess");

					initJpush(p_pe);
					// ToastShow(p.getRongyuntoken());
					SharedPreferencesUtil.putInt(LoginActivity.this, "editor", 0);
					ManageDataBase.Delete(dbutil, Persion.class, null);
					ManageDataBase.Insert(dbutil, Persion.class, p_pe);
					RongIM.getInstance().refreshUserInfoCache(
							new UserInfo(persion.getId() + "", persion.getNick_name(), Uri.parse(persion.getPhoto())));
					RongIM.getInstance().setMessageAttachedUserInfo(true);

					StartActivity(FooterPageActivity.class);
					finish();
					loadingDialog.dismiss();
				}

				/**
				 * 连接融云失败
				 * 
				 * @param errorCode
				 *            错误码，可到官网 查看错误码对应的注释
				 */
				@Override
				public void onError(RongIMClient.ErrorCode errorCode) {
					loadingDialog.dismiss();
					//ToastShow("连接融云失败");
					Looper.prepare();
					Toast.makeText(LoginActivity.this, "连接融云失败", Toast.LENGTH_SHORT).show();
					Looper.loop();
					Log.d("LoginActivity", "--onError" + errorCode);
					//loadingDialog.dismiss();
					// StartActivity(HomePageActivity.class);
					// finish();
				}
			});
		}else{
			Toast.makeText(LoginActivity.this, "连接融云失败", 11).show();
		}
	}

	public void exit() {
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(getApplicationContext(), this.getResources().getString(R.string.exit), Toast.LENGTH_SHORT)
					.show();
			exitTime = System.currentTimeMillis();
		} else {
			/*
			 * int currentVersion = android.os.Build.VERSION.SDK_INT; if
			 * (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
			 * Intent startMain = new Intent(Intent.ACTION_MAIN);
			 * startMain.addCategory(Intent.CATEGORY_HOME);
			 * startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 * startActivity(startMain); System.exit(0); } else {// android2.1
			 * ActivityManager am = (ActivityManager)
			 * getSystemService(ACTIVITY_SERVICE);
			 * am.restartPackage(getPackageName()); }
			 */
			exitApp();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}