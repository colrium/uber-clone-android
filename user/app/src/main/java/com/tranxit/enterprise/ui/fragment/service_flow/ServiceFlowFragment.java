package com.tranxit.enterprise.ui.fragment.service_flow;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tranxit.enterprise.user.R;
import com.tranxit.enterprise.base.BaseFragment;
import com.tranxit.enterprise.chat.ChatActivity;
import com.tranxit.enterprise.common.CancelRequestInterface;
import com.tranxit.enterprise.common.fcm.MyFirebaseMessagingService;
import com.tranxit.enterprise.data.SharedHelper;
import com.tranxit.enterprise.data.network.model.DataResponse;
import com.tranxit.enterprise.data.network.model.Datum;
import com.tranxit.enterprise.data.network.model.Provider;
import com.tranxit.enterprise.data.network.model.ProviderService;
import com.tranxit.enterprise.data.network.model.ServiceType;
import com.tranxit.enterprise.ui.activity.main.MainActivity;
import com.tranxit.enterprise.ui.fragment.cancel_ride.CancelRideDialogFragment;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.tranxit.enterprise.MvpApplication.PERMISSIONS_REQUEST_PHONE;
import static com.tranxit.enterprise.base.BaseActivity.DATUM;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServiceFlowFragment extends BaseFragment implements ServiceFlowIView, CancelRequestInterface, DirectionCallback {

    Unbinder unbinder;
    @BindView(R.id.sos)
    TextView sos;
    @BindView(R.id.otp)
    TextView otp;
    @BindView(R.id.avatar)
    CircleImageView avatar;
    @BindView(R.id.first_name)
    TextView firstName;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.rating)
    RatingBar rating;
    @BindView(R.id.cancel)
    Button cancel;
    @BindView(R.id.share_ride)
    Button sharedRide;
    Unbinder unbinder1;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.service_type_name)
    TextView serviceTypeName;
    @BindView(R.id.service_number)
    TextView serviceNumber;
    @BindView(R.id.service_model)
    TextView serviceModel;
    @BindView(R.id.call)
    Button call;
    @BindView(R.id.lblgeo)
    TextView lblgeo_value;

    String providerPhoneNumber = null;
    FirebaseDatabase database;
    DatabaseReference myRef;
    String shareRideText = "";
    private ServiceFlowPresenter<ServiceFlowFragment> presenter = new ServiceFlowPresenter<>();
    private CancelRequestInterface callback;
    LatLng origin, providerLatLng;
    AlertDialog otpDialog;

    public ServiceFlowFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_service_flow;
    }

    @Override
    public View initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        activity().registerReceiver(myReceiver, new IntentFilter(MyFirebaseMessagingService.INTENT_PROVIDER));
        callback = this;
        presenter.attachView(this);
        database = FirebaseDatabase.getInstance();
        //presenter.checkStatus();
        ((MainActivity)activity()).initialProcess = true;
        if (DATUM != null) {
            initView(DATUM);
        }


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activity().unregisterReceiver(myReceiver);
    }

    @OnClick({R.id.sos, R.id.cancel, R.id.share_ride, R.id.call, R.id.chat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sos:
                sos();
                break;
            case R.id.cancel:
                CancelRideDialogFragment cancelRideDialogFragment = new CancelRideDialogFragment(callback);
                cancelRideDialogFragment.show(activity().getSupportFragmentManager(), cancelRideDialogFragment.getTag());
                break;
            case R.id.share_ride:
                sharedRide();
                break;
            case R.id.call:
                callPhoneNumber(providerPhoneNumber);
                break;
            case R.id.chat:
                if (DATUM != null) {
                    Intent i = new Intent(activity(), ChatActivity.class);
                    i.putExtra("request_id", String.valueOf(DATUM.getId()));
                    i.putExtra("provider_id", String.valueOf(DATUM.getProviderId()));
                    startActivity(i);
                }
                break;
        }
    }

    private void initView(Datum datum) {
        FirebaseMessaging.getInstance().subscribeToTopic(String.valueOf(datum.getId()));
        Provider provider = datum.getProvider();
        if (provider != null) {
            firstName.setText(String.format("%s %s", provider.getFirstName(), provider.getLastName()));
            rating.setRating(Float.parseFloat(provider.getRating()));
            Glide.with(activity()).load(provider.getAvatar()).apply(RequestOptions.placeholderOf(R.drawable.user).dontAnimate().error(R.drawable.user)).into(avatar);
            providerPhoneNumber = provider.getMobile();
        }

        ServiceType serviceType = datum.getServiceType();
        if (serviceType != null) {
            serviceTypeName.setText(serviceType.getName());
            Glide.with(activity()).load(serviceType.getImage()).into(image);
        }

        ProviderService providerService = datum.getProviderService();
        if (providerService != null) {
            serviceNumber.setText(providerService.getServiceNumber());
            serviceModel.setText(providerService.getServiceModel());
        }

        if (datum.getOtp() != null) {
            otp.setVisibility(View.VISIBLE);
            otp.setText(getString(R.string.otp_, datum.getOtp()));
        }else{
            otp.setVisibility(View.GONE);
        }
        shareRideText = getString(R.string.app_name) + "- I would like to share a ride with you at " + "http://maps.google.com/maps?q=loc:" + datum.getDLatitude() + "," + datum.getDLongitude() + "\n" +
                "Pickup Location: " + datum.getSAddress() + "\n\n" +
                "Drop Location: " + datum.getDAddress() + "\n\n" +
                "Driver Name: " + firstName.getText().toString() + "\n\n" +
                "Vehicle Number: " + serviceNumber.getText().toString() + "\n" +
                "Model: " + serviceModel.getText().toString() + "\n" +
                "Phone: " + providerPhoneNumber +
                "";

        /*if (datum.getSLatitude() != null && datum.getSLongitude() != null && datum.getDLatitude() != null && datum.getDLongitude() != null) {
            LatLng origin = new LatLng(datum.getSLatitude(), datum.getSLongitude());
            LatLng destination = new LatLng(datum.getDLatitude(), datum.getDLongitude());
            ((MainActivity) Objects.requireNonNull(getActivity())).drawRoute(origin, destination);
        }*/

        ((MainActivity) Objects.requireNonNull(getActivity())).drawDirectionToStop(datum.getRouteKey());

        switch (datum.getStatus()) {
            case "STARTED":
                if(provider != null){
                    LatLng providerLat = new LatLng(provider.getLatitude(), provider.getLongitude());
                    origin = new LatLng(datum.getSLatitude(), datum.getSLongitude());
                    getDistance(providerLat, origin);
                    ((MainActivity) Objects.requireNonNull(getActivity())).drawRoute(providerLat, origin, false);
                    status.setText(R.string.driver_accepted_your_request);
                }
                break;
            case "ARRIVED":
                status.setText(R.string.driver_has_arrived_your_location);
                break;
            case "PICKEDUP":
                status.setText(R.string.you_are_on_ride);
                cancel.setVisibility(View.GONE);
                sharedRide.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    private void sos() {
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.sos_alert))
                .setMessage(R.string.are_sure_you_want_to_emergency_alert)
                .setCancelable(true)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    String sos = SharedHelper.getKey(activity(), "sos", "");
                    ServiceFlowFragment.this.callPhoneNumber(sos);
                }).setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.cancel())
                .show();
    }

    private void callPhoneNumber(String mobileNumber) {
        if (mobileNumber != null && !mobileNumber.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobileNumber));
            startActivity(intent);
        }
    }

    private void sharedRide() {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareRideText);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch (Exception e) {
            Toast.makeText(activity(), "applications not found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuccess(DataResponse dataResponse) {

        DATUM = dataResponse.getData().get(0);

        lblgeo_value.setText("geo_fencing_distance =" + DATUM.getGeoFencingDistance());
        /*if (!dataResponse.getData().isEmpty()) {
            Datum datum = dataResponse.getData().get(0);
            initView(datum);
        }*/
    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(activity(), "Permission Granted. Try Again!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void cancelRequestMethod() {

    }

    public void getDistance(LatLng source, LatLng destination) {
        GoogleDirection.withServerKey(getString(R.string.google_map_key))
                .from(source)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (isAdded()) {
            if (direction.isOK()) {
                Route route = direction.getRouteList().get(0);
                if (!route.getLegList().isEmpty()) {
                    Leg leg = route.getLegList().get(0);
                    status.setText(getString(R.string.driver_accepted_your_request_, leg.getDuration().getText()));
                    lblgeo_value.setText("geo_fencing_distance =" + DATUM.getGeoFencingDistance());
                }
            } else {
                //Toast.makeText(activity(), direction.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {

    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


//            presenter.checkStatus();

            Log.d("latitude", "" + intent.getDoubleExtra("latitude", 0));
            Log.d("longitude", "" + intent.getDoubleExtra("longitude", 0));
            providerLatLng = new LatLng(intent.getDoubleExtra("latitude", 0), intent.getDoubleExtra("longitude", 0));
            ((MainActivity) context).addCar(providerLatLng);

            if (origin != null) {
                getDistance(providerLatLng, origin);
            }
        }
    };
}
