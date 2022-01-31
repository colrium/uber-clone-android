package com.tranxit.enterprise.ui.activity.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tranxit.enterprise.user.BuildConfig;
import com.tranxit.enterprise.user.R;
import com.tranxit.enterprise.base.BaseActivity;
import com.tranxit.enterprise.data.network.model.User;
import com.tranxit.enterprise.ui.activity.change_password.ChangePasswordActivity;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class ProfileActivity extends BaseActivity implements ProfileIView {

    @BindView(R.id.picture)
    CircleImageView picture;
    @BindView(R.id.first_name)
    EditText firstName;
    @BindView(R.id.last_name)
    EditText lastName;
    @BindView(R.id.mobile)
    EditText mobile;
    @BindView(R.id.email)
    EditText email;
    File imgFile = null;
    public static boolean isRefreshProfile = true;
    @BindView(R.id.emergency_mobile1)
    EditText emergencyMobile1;
    @BindView(R.id.emergency_mobile2)
    EditText emergencyMobile2;
    private ProfilePresenter<ProfileActivity> profilePresenter = new ProfilePresenter<>();
    String messsage = null;

    @Override
    public int getLayoutId() {
        return R.layout.activity_profile;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        profilePresenter.attachView(this);
        profilePresenter.profile();
        isRefreshProfile = true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, ProfileActivity.this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                e.printStackTrace();
            }

            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                imgFile = imageFiles.get(0);
                Glide.with(activity()).load(Uri.fromFile(imgFile)).apply(RequestOptions.placeholderOf(R.drawable.user).dontAnimate().error(R.drawable.user)).into(picture);
            }


            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {

            }
        });
    }

    @OnClick({R.id.picture, R.id.save, R.id.change_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.picture:
                if (hasPermission(Manifest.permission.CAMERA) && hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    pickImage();
                } else {
                    requestPermissionsSafely(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
                }
                break;
            case R.id.save:
                updateProfile();
                break;
            case R.id.change_password:
                startActivity(new Intent(this, ChangePasswordActivity.class));
                break;
        }
    }

    private void updateProfile() {
        if (firstName.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_first_name), Toast.LENGTH_SHORT).show();
            return;
        } else if (lastName.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_last_name), Toast.LENGTH_SHORT).show();
            return;
        } else if (mobile.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_mobile), Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("first_name", RequestBody.create(MediaType.parse("text/plain"), firstName.getText().toString()));
        map.put("last_name", RequestBody.create(MediaType.parse("text/plain"), lastName.getText().toString()));
        map.put("email", RequestBody.create(MediaType.parse("text/plain"), email.getText().toString()));
        map.put("mobile", RequestBody.create(MediaType.parse("text/plain"), mobile.getText().toString()));
        map.put("emergency_contact1", RequestBody.create(MediaType.parse("text/plain"), emergencyMobile1.getText().toString()));
        map.put("emergency_contact2",  RequestBody.create(MediaType.parse("text/plain"), emergencyMobile2.getText().toString()));

        MultipartBody.Part filePart = null;
        if (imgFile != null)
            filePart = MultipartBody.Part.createFormData("picture", imgFile.getName(), RequestBody.create(MediaType.parse("image*//*"), imgFile));

        showLoading();
        messsage = getString(R.string.profile_updated_successfuly);
        profilePresenter.update(map, filePart);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ASK_MULTIPLE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean permission1 = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean permission2 = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (permission1 && permission2) {
                        pickImage();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.please_give_permissions, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onSuccess(@NonNull User user) {
        hideLoading();
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        mobile.setText(user.getMobile());
        email.setText(user.getEmail());
        emergencyMobile1.setText(user.getEmergencyContact1());
        emergencyMobile2.setText(user.getEmergencyContact2());
        Glide.with(activity()).load(BuildConfig.BASE_IMAGE_URL + user.getPicture()).apply(RequestOptions.placeholderOf(R.drawable.user).dontAnimate().error(R.drawable.user)).into(picture);

        if (messsage != null) {
            Toast.makeText(this, messsage, Toast.LENGTH_SHORT).show();
            messsage = null;
        }
    }

    @Override
    public void onError(Throwable e) {
        hideLoading();
    }


}
