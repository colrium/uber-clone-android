package com.tranxit.enterprise.ui.activity.social;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.Scopes;
import com.tranxit.enterprise.user.BuildConfig;
import com.tranxit.enterprise.user.R;
import com.tranxit.enterprise.base.BaseActivity;
import com.tranxit.enterprise.data.SharedHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.tranxit.enterprise.data.network.model.Token;
import com.tranxit.enterprise.ui.activity.main.MainActivity;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class SocialLoginActivity extends BaseActivity implements SocialIView {

    private static final int RC_SIGN_IN = 9001;
    GoogleSignInClient mGoogleSignInClient;
    private SocialPresenter<SocialLoginActivity> presenter = new SocialPresenter<>();
    CallbackManager callbackManager;

    HashMap<String, Object> map = new HashMap<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_social_login;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
        callbackManager = CallbackManager.Factory.create();
        map.put("device_token", SharedHelper.getKey(this, "device_token"));
        map.put("device_id", SharedHelper.getKey(this, "device_id"));
        map.put("device_type", BuildConfig.DEVICE_TYPE);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.google_signin_server_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @OnClick({R.id.facebook, R.id.google})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.facebook:
                fbLogin();
                break;
            case R.id.google:
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
        }
    }

    void fbLogin() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            public void onSuccess(LoginResult loginResult) {
                if (AccessToken.getCurrentAccessToken() != null) {
                    map.put("login_by", "facebook");
                    map.put("accessToken", loginResult.getAccessToken().getToken());
                    //fbOtpVerify("");
                }
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                if (exception instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                }
                Log.e("Facebook", exception.getMessage());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            String TAG = "Google";
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //String token = account.getIdToken();
                map.put("login_by", "google");
                Runnable runnable = () -> {
                    try {
                        String scope = "oauth2:"+ Scopes.EMAIL+" "+ Scopes.PROFILE;
                        String accessToken = GoogleAuthUtil.getToken(getApplicationContext(), account.getAccount(), scope, new Bundle());
                        Log.d(TAG, "accessToken:"+accessToken);
                        map.put("accessToken", accessToken);
                        //fbOtpVerify("");
                    } catch (IOException | GoogleAuthException e) {
                        e.printStackTrace();
                    }
                };
                AsyncTask.execute(runnable);
            } catch (ApiException e) {
                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            }
        }
    }


    private void register() {
        map.put("mobile", SharedHelper.getKey(this, "mobile"));
        map.put("dial_code", SharedHelper.getKey(this, "dial_code"));
        if (map.get("login_by").equals("google")) {
            presenter.loginGoogle(map);
        } else {
            presenter.loginFacebook(map);
        }

        showLoading();
    }

    @Override
    public void onSuccess(Token token) {
        hideLoading();
        String accessToken = token.getTokenType() + " " + token.getAccessToken();
        SharedHelper.putKey(this, "access_token", accessToken);
        SharedHelper.putKey(this, "logged_in", true);
        finishAffinity();
        startActivity(new Intent(this, MainActivity.class));

    }

    @Override
    public void onError(Throwable e) {
        hideLoading();
    }
}
