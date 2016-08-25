package com.wangdaye.mysplash._common.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.data.Me;
import com.wangdaye.mysplash._common.data.service.UserService;
import com.wangdaye.mysplash._common.data.tools.AuthManager;
import com.wangdaye.mysplash._common.ui.toast.MaterialToast;
import com.wangdaye.mysplash._common.ui.widget.StatusBarView;
import com.wangdaye.mysplash._common.utils.ThemeUtils;
import com.wangdaye.mysplash._common.utils.TypefaceUtils;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Update me activity.
 * */

public class UpdateMeActivity extends MysplashActivity
        implements View.OnClickListener, UserService.OnRequestMeProfileListener {
    // widget
    private CircularProgressView progressView;
    private LinearLayout contentView;

    private EditText usernameTxt;
    private EditText firstNameTxt;
    private EditText lastNameTxt;
    private EditText emailTxt;
    private EditText portfolioTxt;
    private EditText locationTxt;
    private EditText bioTxt;

    // data
    private UserService service;

    private int state;
    private final int INPUT_STATE = 0;
    private final int UPDATE_STATE = 1;

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_me);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            initData();
            initWidget();
        }
    }

    @Override
    protected void setTheme() {
        if (ThemeUtils.getInstance(this).isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Common);
        } else {
            setTheme(R.style.MysplashTheme_dark_Common);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        service.cancel();
    }

    /** <br> UI. */

    private void initWidget() {
        StatusBarView statusBar = (StatusBarView) findViewById(R.id.activity_update_me_statusBar);
        if (ThemeUtils.getInstance(this).isNeedSetStatusBarMask()) {
            statusBar.setMask(true);
        }

        ImageButton closeBtn = (ImageButton) findViewById(R.id.activity_update_me_closeBtn);
        if (ThemeUtils.getInstance(this).isLightTheme()) {
            closeBtn.setImageResource(R.drawable.ic_close_light);
        } else {
            closeBtn.setImageResource(R.drawable.ic_close_dark);
        }
        closeBtn.setOnClickListener(this);

        this.progressView = (CircularProgressView) findViewById(R.id.container_update_me_progressView);
        progressView.setVisibility(View.GONE);

        this.contentView = (LinearLayout) findViewById(R.id.container_update_me_textContainer);
        contentView.setVisibility(View.VISIBLE);

        this.usernameTxt = (EditText) findViewById(R.id.container_update_me_usernameTxt);
        TypefaceUtils.setTypeface(this, usernameTxt);
        usernameTxt.setText(AuthManager.getInstance().getMe().username);

        this.firstNameTxt = (EditText) findViewById(R.id.container_update_me_firstNameTxt);
        TypefaceUtils.setTypeface(this, firstNameTxt);
        firstNameTxt.setText(AuthManager.getInstance().getMe().first_name);

        this.lastNameTxt = (EditText) findViewById(R.id.container_update_me_lastNameTxt);
        TypefaceUtils.setTypeface(this, lastNameTxt);
        lastNameTxt.setText(AuthManager.getInstance().getMe().last_name);

        this.emailTxt = (EditText) findViewById(R.id.container_update_me_emailTxt);
        TypefaceUtils.setTypeface(this, emailTxt);
        emailTxt.setText(AuthManager.getInstance().getMe().email);

        this.portfolioTxt = (EditText) findViewById(R.id.container_update_me_portfolioTxt);
        TypefaceUtils.setTypeface(this, portfolioTxt);
        portfolioTxt.setText(AuthManager.getInstance().getMe().portfolio_url);

        this.locationTxt = (EditText) findViewById(R.id.container_update_me_locationTxt);
        TypefaceUtils.setTypeface(this, locationTxt);
        locationTxt.setText(AuthManager.getInstance().getMe().location);

        this.bioTxt = (EditText) findViewById(R.id.container_update_me_bioTxt);
        TypefaceUtils.setTypeface(this, bioTxt);
        bioTxt.setText(AuthManager.getInstance().getMe().bio);

        Button saveBtn = (Button) findViewById(R.id.container_update_me_saveBtn);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            saveBtn.setBackgroundResource(R.drawable.button_login);
        }
        saveBtn.setOnClickListener(this);
    }

    private void setState(int newState) {
        switch (newState) {
            case INPUT_STATE:
                if (state == UPDATE_STATE) {
                    animShow(contentView);
                    animHide(progressView);
                }
                break;

            case UPDATE_STATE:
                if (state == INPUT_STATE) {
                    animShow(progressView);
                    animHide(contentView);
                }
                break;
        }
        state = newState;
    }

    /** <br> data. */

    private void initData() {
        this.service = UserService.getService();
        this.state = INPUT_STATE;
    }

    private void updateProfile() {
        String username = usernameTxt.getText().toString();
        if (!TextUtils.isEmpty(username)) {
            service.updateMeProfile(
                    username,
                    firstNameTxt.getText().toString(),
                    lastNameTxt.getText().toString(),
                    emailTxt.getText().toString(),
                    portfolioTxt.getText().toString(),
                    locationTxt.getText().toString(),
                    bioTxt.getText().toString(),
                    this);
            setState(UPDATE_STATE);
        } else {
            MaterialToast.makeText(
                    this,
                    getString(R.string.feedback_name_is_required),
                    null,
                    MaterialToast.LENGTH_SHORT).show();
        }
    }

    /** <br> animator. */

    public void animShow(final View v) {
        if (v.getVisibility() == View.GONE) {
            v.setVisibility(View.VISIBLE);
        }
        ObjectAnimator
                .ofFloat(v, "alpha", 0, 1)
                .setDuration(300)
                .start();
    }

    public void animHide(final View v) {
        ObjectAnimator anim = ObjectAnimator
                .ofFloat(v, "alpha", 1, 0)
                .setDuration(300);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                v.setVisibility(View.GONE);
            }
        });
        anim.start();
    }

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_update_me_closeBtn:
                finish();
                break;

            case R.id.container_update_me_saveBtn:
                updateProfile();
                break;
        }
    }

    // on request me profile listener.

    @Override
    public void onRequestMeProfileSuccess(Call<Me> call, Response<Me> response) {
        if (response.isSuccessful()) {
            AuthManager.getInstance().writeUserInfo(response.body());
            finish();
        } else {
            setState(INPUT_STATE);
            MaterialToast.makeText(
                    this,
                    getString(R.string.feedback_update_profile_failed),
                    null,
                    MaterialToast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestMeProfileFailed(Call<Me> call, Throwable t) {
        setState(INPUT_STATE);
        MaterialToast.makeText(
                this,
                getString(R.string.feedback_update_profile_failed),
                null,
                MaterialToast.LENGTH_SHORT).show();
    }
}
