package com.tranxit.enterprise.ui.fragment.rate;


import android.content.Intent;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tranxit.enterprise.user.R;
import com.tranxit.enterprise.base.BaseBottomSheetDialogFragment;
import com.tranxit.enterprise.data.network.model.Datum;
import com.tranxit.enterprise.data.network.model.Provider;
import com.tranxit.enterprise.ui.activity.main.MainActivity;

import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.tranxit.enterprise.base.BaseActivity.DATUM;

public class RatingDialogFragment extends BaseBottomSheetDialogFragment implements RatingIView {

    @BindView(R.id.avatar)
    CircleImageView avatar;
    @BindView(R.id.provider_name)
    TextView providerName;
    @BindView(R.id.rating)
    RatingBar rating;
    @BindView(R.id.comment)
    EditText comment;
    @BindView(R.id.submit)
    Button submit;
    private RatingPresenter<RatingDialogFragment> presenter = new RatingPresenter<>();

    public RatingDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_rating_dialog;
    }

    @Override
    public void initView(View view) {
        setCancelable(false);
        getDialog().setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            View bottomSheetInternal = d.findViewById(android.support.design.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        getDialog().setCanceledOnTouchOutside(false);
        ButterKnife.bind(this, view);
        presenter.attachView(this);

        if (DATUM != null) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(String.valueOf(DATUM.getId()));
            Provider provider = DATUM.getProvider();
            if (provider != null) {
                providerName.setText(getString(R.string.rate_your_service_with, provider.getFirstName()));
                Glide.with(activity()).load(provider.getAvatar()).apply(RequestOptions.placeholderOf(R.drawable.user).dontAnimate().error(R.drawable.user)).into(avatar);
            }
        }
    }

    @Override
    public void onSuccess(Object object) {
        hideLoading();
        dismiss();
        Intent intent = new Intent("INTENT_FILTER");
        ((MainActivity) Objects.requireNonNull(getActivity())).sendBroadcast(intent);
        //activity().sendBroadcast(intent);
    }

    @Override
    public void onError(Throwable e) {
        Log.d("DD", "dd");
        hideLoading();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick(R.id.submit)
    public void onViewClicked() {
        if (DATUM != null) {
            Datum datum = DATUM;
            HashMap<String, Object> map = new HashMap<>();
            map.put("request_id", datum.getId());
            map.put("rating", Math.round(rating.getRating()));
            map.put("comment", comment.getText().toString());
            showLoading();
            presenter.rate(map);

        }
    }
}
