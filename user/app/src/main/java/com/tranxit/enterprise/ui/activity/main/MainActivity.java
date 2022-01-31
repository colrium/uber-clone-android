package com.tranxit.enterprise.ui.activity.main;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tranxit.enterprise.data.network.model.SettingsResponse;
import com.tranxit.enterprise.user.BuildConfig;
import com.tranxit.enterprise.MvpApplication;
import com.tranxit.enterprise.user.R;
import com.tranxit.enterprise.base.BaseActivity;
import com.tranxit.enterprise.common.ConnectivityReceiver;
import com.tranxit.enterprise.common.InfoWindowData;
import com.tranxit.enterprise.common.PolyUtils;
import com.tranxit.enterprise.common.fcm.MyFirebaseMessagingService;
import com.tranxit.enterprise.data.SharedHelper;
import com.tranxit.enterprise.data.network.model.DataResponse;
import com.tranxit.enterprise.data.network.model.Datum;
import com.tranxit.enterprise.data.network.model.Provider;
import com.tranxit.enterprise.data.network.model.User;
import com.tranxit.enterprise.ui.activity.about_us.AboutActivity;
import com.tranxit.enterprise.ui.activity.coupon.CouponActivity;
import com.tranxit.enterprise.ui.activity.help.HelpActivity;
import com.tranxit.enterprise.ui.activity.location_pick.LocationPickActivity;
import com.tranxit.enterprise.ui.activity.outstation.OutstationBookingActivity;
import com.tranxit.enterprise.ui.activity.passbook.PassbookActivity;
import com.tranxit.enterprise.ui.activity.payment.PaymentActivity;
import com.tranxit.enterprise.ui.activity.profile.ProfileActivity;
import com.tranxit.enterprise.ui.activity.rental.RentalActivity;
import com.tranxit.enterprise.ui.activity.setting.SettingsActivity;
import com.tranxit.enterprise.ui.activity.splash.SplashActivity;
import com.tranxit.enterprise.ui.activity.wallet.WalletActivity;
import com.tranxit.enterprise.ui.activity.your_trips.YourTripActivity;
import com.tranxit.enterprise.ui.fragment.book_ride.BookRideFragment;
import com.tranxit.enterprise.ui.fragment.invoice.InvoiceFragment;
import com.tranxit.enterprise.ui.fragment.rate.RatingDialogFragment;
import com.tranxit.enterprise.ui.fragment.schedule.ScheduleFragment;
import com.tranxit.enterprise.ui.fragment.searching.SearchingFragment;
import com.tranxit.enterprise.ui.fragment.service.RateCardFragment;
import com.tranxit.enterprise.ui.fragment.service.ServiceFragment;
import com.tranxit.enterprise.ui.fragment.service_flow.ServiceFlowFragment;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.HttpException;
import retrofit2.Response;

import static com.tranxit.enterprise.MvpApplication.DEFAULT_ZOOM;
import static com.tranxit.enterprise.MvpApplication.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.tranxit.enterprise.MvpApplication.PICK_LOCATION_REQUEST_CODE;
import static com.tranxit.enterprise.MvpApplication.isCCAvenueEnabled;
import static com.tranxit.enterprise.MvpApplication.mLastKnownLocation;
import static com.tranxit.enterprise.ui.activity.profile.ProfileActivity.isRefreshProfile;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener, DirectionCallback, MainIView, ConnectivityReceiver.ConnectivityReceiverListener {

    @BindView(R.id.container)
    FrameLayout container;
    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.gps)
    ImageView gps;
    @BindView(R.id.source)
    TextView sourceTxt;
    @BindView(R.id.destination)
    TextView destinationTxt;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.top_layout)
    LinearLayout topLayout;
    @BindView(R.id.pick_location_layout)
    LinearLayout pickLocationLayout;
    boolean doubleBackToExitPressedOnce = false;
    PolylineOptions polylineOptions;

    private final LatLng mDefaultLocation = new LatLng(13.083143, 80.254997);
    @BindView(R.id.menu_app)
    ImageView menuApp;
    TextView lblOutstation;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.rdDaily)
    RadioButton rdDaily;
    @BindView(R.id.rdRentals)
    RadioButton rdRentals;
    @BindView(R.id.rdOutstation)
    RadioButton rdOutstation;
    @BindView(R.id.toggle)
    RadioGroup toggle;
    private boolean mLocationPermissionGranted;
    SupportMapFragment mapFragment;
    GoogleMap googleMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private String alternateApiKey;
    private LatLng sourceLatLng;
    private LatLng destLatLng;
    BottomSheetBehavior bottomSheetBehavior;
    private MainPresenter<MainActivity> mainPresenter = new MainPresenter<>();

    CircleImageView picture;
    TextView name;
    String STATUS = "";

    public boolean initialProcess = true;
    private LatLng oldPosition = null, newPosition = null;
    private Marker marker;

    private HashMap<Integer, Marker> mHashMap = new HashMap<>();
    ConnectivityReceiver internetReceiver = new ConnectivityReceiver();
    @BindView(R.id.lnrNoInternet)
    LinearLayout lnrNoInternet;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        alternateApiKey = SharedHelper.getKey(this, SharedHelper.GOOGLE_API_KEY);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        ButterKnife.bind(this);

        mainPresenter.attachView(this);


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetReceiver, intentFilter);
        registerReceiver(myReceiver, new IntentFilter(MyFirebaseMessagingService.INTENT_FILTER));
        // register connection status listener
        MvpApplication.getInstance().setConnectivityListener(this);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerview = navigationView.getHeaderView(0);
        picture = headerview.findViewById(R.id.picture);
        name = headerview.findViewById(R.id.name);
        headerview.setOnClickListener(v -> {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, picture, ViewCompat.getTransitionName(picture));
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent, options.toBundle());
        });

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        bottomSheetBehavior = BottomSheetBehavior.from(container);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        mainPresenter.profile();

        toggle.setOnCheckedChangeListener((group, checkedId) -> {
            View radioButton = group.findViewById(checkedId);
            int index = group.indexOfChild(radioButton);

            switch (index) {
                case 0:
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0)
                        getSupportFragmentManager().popBackStack();
                    if (RIDE_REQUEST.containsKey("s_address") && RIDE_REQUEST.containsKey("d_address")) {
                        changeFlow("SERVICE");
                        LatLng origin = new LatLng((Double) RIDE_REQUEST.get("s_latitude"), (Double) RIDE_REQUEST.get("s_longitude"));
                        LatLng destination = new LatLng((Double) RIDE_REQUEST.get("d_latitude"), (Double) RIDE_REQUEST.get("d_longitude"));
                        drawRoute(origin, destination, false);
                    } else {
                        changeFlow("EMPTY");
                    }
                    break;
                case 1:
                    startActivity(new Intent(this, OutstationBookingActivity.class));
                    break;
                case 2:
                    startActivity(new Intent(this, RentalActivity.class));
                    break;
            }

        });

        new AppUpdater(activity())
                .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                .setDisplay(Display.DIALOG)
                .setButtonDoNotShowAgain(null)
                .setButtonDismiss(null)
                .setCancelable(false)
                .start();


        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
                mainPresenter.getNavigationSettings();
            }

            @Override
            public void onDrawerClosed(@NonNull View view) {

            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });

        navigationView.getMenu().findItem(R.id.nav_payment).setVisible(isCCAvenueEnabled);

    }

    @Override
    public void onResume() {
        super.onResume();
        rdDaily.setChecked(true);
        if (!STATUS.equals("SERVICE")) {
            mainPresenter.checkStatus();
        }
        if (isRefreshProfile) {
            mainPresenter.profile();
            isRefreshProfile = false;
        }

        checkConnection();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
        unregisterReceiver(internetReceiver);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
                if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                    mainPresenter.checkStatus();
                }
            } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, getString(R.string.please_click_back_again_to_exit), Toast.LENGTH_SHORT).show();
            }
        }

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_payment:
                startActivity(new Intent(this, PaymentActivity.class));
                break;
            case R.id.nav_your_trips:
                startActivity(new Intent(this, YourTripActivity.class));
                break;
            case R.id.nav_coupon:
                startActivity(new Intent(this, CouponActivity.class));
                break;
            case R.id.nav_wallet:
                startActivity(new Intent(this, WalletActivity.class));
                break;
            case R.id.nav_passbook:
                startActivity(new Intent(this, PassbookActivity.class));
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_help:
                startActivity(new Intent(this, HelpActivity.class));
                break;
            case R.id.nav_faq:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(BuildConfig.BASE_URL + "faq"));
                startActivity(i);
                break;
            case R.id.nav_share:
                shareApp();
                break;
            case R.id.nav_logout:
                alertLogout();
                break;
            case R.id.nav_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @OnClick({R.id.menu, R.id.gps, R.id.source, R.id.destination, R.id.menu_app})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.menu:
            case R.id.menu_app:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(Gravity.START);
                }
                break;
            case R.id.gps:
                getDeviceLocation();
                break;
            case R.id.source:
                Intent sourceIntent = new Intent(this, LocationPickActivity.class);
                startActivityForResult(sourceIntent, PICK_LOCATION_REQUEST_CODE);
                break;
            case R.id.destination:
                Intent destinationIntent = new Intent(this, LocationPickActivity.class);
                destinationIntent.putExtra("desFocus", true);
                startActivityForResult(destinationIntent, PICK_LOCATION_REQUEST_CODE);
                break;


        }
    }

    @Override
    public void onCameraIdle() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        getProviders();
    }

    private void getProviders() {
        if (STATUS.equals("EMPTY")) try {
            CameraPosition cameraPosition = googleMap.getCameraPosition();
            HashMap<String, Object> map = new HashMap<>();
            map.put("latitude", cameraPosition.target.latitude);
            map.put("longitude", cameraPosition.target.longitude);
            mainPresenter.providers(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCameraMove() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        } catch (Resources.NotFoundException e) {
            Log.d("Map:Style", "Can't find style. Error: ");
        }
        this.googleMap = googleMap;
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();

        /*GoogleDirection.withServerKey(getString(R.string.google_api_key))
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(this);*/
    }

    public void changeFragment(Fragment fragment) {
        if (isFinishing()) {
            return;
        }

        if (fragment != null) {
            if (fragment instanceof BookRideFragment || fragment instanceof ServiceFlowFragment) {
                container.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            } else {
                container.setBackgroundColor(getResources().getColor(R.color.white));
            }

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (fragment instanceof RateCardFragment)
                fragmentTransaction.addToBackStack(fragment.getTag());
            else if (fragment instanceof ScheduleFragment)
                fragmentTransaction.addToBackStack(fragment.getTag());
            else if (fragment instanceof ServiceFragment)
                fragmentTransaction.addToBackStack(fragment.getTag());
            else if (fragment instanceof BookRideFragment)
                fragmentTransaction.addToBackStack(fragment.getTag());
            fragmentTransaction.replace(R.id.container, fragment, fragment.getTag());
            fragmentTransaction.commit();
        } else {

            for (Fragment fragmentd : getSupportFragmentManager().getFragments()) {
                if (fragmentd instanceof ServiceFlowFragment) {
                    getSupportFragmentManager().beginTransaction().remove(fragmentd).commit();
                }
            }
            container.removeAllViews();
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            if (googleMap != null && mLastKnownLocation != null) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
            }
        }

    }

    public void changeFlow(String status) {
        STATUS = status;
        dismissDialog("SEARCHING");
        dismissDialog("INVOICE");
        dismissDialog("RATING");
        System.out.println("From: " + status);
        switch (status) {
            case "EMPTY":
                try {
                    if (googleMap != null) googleMap.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mHashMap.clear();
                changeFragment(null);
                getProviders();
                break;
            case "SERVICE":
                changeFragment(new ServiceFragment());
                break;
            case "SEARCHING":
                SearchingFragment searchingFragment = new SearchingFragment();
                searchingFragment.show(getSupportFragmentManager(), "SEARCHING");
                break;
            case "STARTED":
                if (DATUM != null) {
                    initialProcess = true;
                    FirebaseMessaging.getInstance().subscribeToTopic(String.valueOf(DATUM.getId()));
                }
                changeFragment(new ServiceFlowFragment());
                break;
            case "ARRIVED":
                changeFragment(new ServiceFlowFragment());
                break;
            case "PICKEDUP":
                changeFragment(new ServiceFlowFragment());
                break;
            case "DROPPED":
                InvoiceFragment invoiceFragment = new InvoiceFragment();
                invoiceFragment.show(getSupportFragmentManager(), "INVOICE");
                break;
            case "COMPLETED":
                InvoiceFragment invoiceFragment1 = new InvoiceFragment();
                invoiceFragment1.show(getSupportFragmentManager(), "INVOICE");
                break;
            case "RATING":
                mainPresenter.profile();

                RIDE_REQUEST.clear();
                sourceTxt.setText("");
                destinationTxt.setText("");
                getDeviceLocation();


                if (DATUM != null) {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(String.valueOf(DATUM.getId()));
                }
                RatingDialogFragment ratingDialogFragment = new RatingDialogFragment();
                ratingDialogFragment.show(getSupportFragmentManager(), "RATING");

                break;
            default:
                break;
        }
    }

    void dismissDialog(String tag) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment instanceof SearchingFragment) {
            SearchingFragment df = (SearchingFragment) fragment;
            df.dismissAllowingStateLoss();
        }
        if (fragment instanceof InvoiceFragment) {
            InvoiceFragment df = (InvoiceFragment) fragment;
            df.dismissAllowingStateLoss();
        }
        if (fragment instanceof RatingDialogFragment) {
            RatingDialogFragment df = (RatingDialogFragment) fragment;
            df.dismissAllowingStateLoss();
        }
    }

    void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        String s_address = getAddress(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
                        if (s_address != null) {
                            sourceTxt.setText(s_address);
                            RIDE_REQUEST.put("s_address", s_address);
                            RIDE_REQUEST.put("s_latitude", mLastKnownLocation.getLatitude());
                            RIDE_REQUEST.put("s_longitude", mLastKnownLocation.getLongitude());
                        }

                    } else {
                        Log.d("Map", "Current location is null. Using defaults.");
                        Log.e("Map", "Exception: %s", task.getException());
                        googleMap.animateCamera(CameraUpdateFactory
                                .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void updateLocationUI() {
        if (googleMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                googleMap.getUiSettings().setCompassEnabled(false);
                googleMap.setOnCameraMoveListener(this);
                googleMap.setOnCameraIdleListener(this);
            } else {
                googleMap.setMyLocationEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    updateLocationUI();
                    getDeviceLocation();
                }
            }
        }
    }

    public void drawRoute(LatLng source, LatLng destination, boolean changeApiKey) {

        String apiKey = changeApiKey ? alternateApiKey :
                getResources().getString(R.string.google_directions_key);

        //Fix
        if (TextUtils.isEmpty(alternateApiKey))
            apiKey = getResources().getString(R.string.google_directions_key);

        sourceLatLng = source;
        destLatLng = destination;

        GoogleDirection.withServerKey(apiKey)
                .from(source)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (direction.isOK()) {
            initialProcess = true;
            googleMap.clear();
            Route route = direction.getRouteList().get(0);
            if (!route.getLegList().isEmpty()) {

                Leg leg = route.getLegList().get(0);
                InfoWindowData originLeg = new InfoWindowData();
                originLeg.setAddress(leg.getStartAddress());
                originLeg.setArrival_time(null);
                originLeg.setDistance(leg.getDistance().getText());

                InfoWindowData destinationLeg = new InfoWindowData();
                destinationLeg.setAddress(leg.getEndAddress());
                destinationLeg.setArrival_time(leg.getDuration().getText());
                destinationLeg.setDistance(leg.getDistance().getText());

                LatLng origin = new LatLng(leg.getStartLocation().getLatitude(), leg.getStartLocation().getLongitude());
                LatLng destination = new LatLng(leg.getEndLocation().getLatitude(), leg.getEndLocation().getLongitude());
                googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.src_icon)).position(origin)).setTag(originLeg);
                googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.des_icon)).position(destination)).setTag(destinationLeg);


                /*CustomInfoWindowAdapter customInfoWindow = new CustomInfoWindowAdapter(this);
                googleMap.setInfoWindowAdapter(customInfoWindow);*/
            }

            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
            polylineOptions = DirectionConverter.createPolyline(this, directionPositionList, 5, getResources().getColor(R.color.colorAccent));
            googleMap.addPolyline(polylineOptions);
            setCameraWithCoordinationBounds(route);

        } else {

            //Toast.makeText(this, direction.getErrorMessage(), Toast.LENGTH_SHORT).show();
            if (alternateApiKey != null) {
//                routeRequestedAgain = true;
                drawRoute(sourceLatLng, destLatLng, true);
            } else {
                Log.d("", direction.getErrorMessage());
                Toast.makeText(getApplicationContext(), direction.getErrorMessage(),
                        Toast.LENGTH_SHORT).show();
                changeFlow("EMPTY");
            }
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    public void addCar(LatLng latLng) {

        if (isFinishing()) {
            return;
        }

        if (latLng != null && latLng.latitude != 0 && latLng.longitude != 0) {
            if (newPosition != null)
                oldPosition = newPosition;
            newPosition = latLng;
            if (initialProcess) {
                initialProcess = false;

                int vehicleIcon = R.drawable.car_icon;
                Datum datum = DATUM;
                if (datum != null) {
                    if (datum.getProviderService() != null) {
                        vehicleIcon = datum.getProviderService().getServiceTypeId() == 5 ? R.drawable.auto_icon : R.drawable.car_icon;
                    }
                }

                marker = googleMap.addMarker(new MarkerOptions().position(latLng)
                        .anchor(0.5f, 0.75f)
                        .icon(BitmapDescriptorFactory.fromResource(vehicleIcon)));

            } else {
                animateMarker(oldPosition, newPosition, marker);
                marker.setRotation(bearingBetweenLocations(oldPosition, newPosition));
            }
        }
    }

    @Override
    public void onSuccess(@NonNull User user) {
        name.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
        Glide.with(activity()).load(BuildConfig.BASE_IMAGE_URL + user.getPicture()).apply(RequestOptions.placeholderOf(R.drawable.user).dontAnimate().error(R.drawable.user)).into(picture);
        SharedHelper.putKey(this, "wallet_amount", user.getWalletBalance());

        SharedHelper.putKey(this, "user_id", user.getId());
        SharedHelper.putKey(this, "rental_content", user.getRental_content());
        SharedHelper.putKey(this, "outstation_content", user.getOutstation_content());
        SharedHelper.putKey(this, "sos", user.getSos());

        if (!user.getDeviceToken().equals(SharedHelper.getKey(this, "device_token"))) {
            SharedHelper.clearSharedPreferences(activity());
            finishAffinity();
            startActivity(new Intent(activity(), SplashActivity.class));
        }


    }

    @Override
    public void onSuccess(DataResponse dataResponse) {
        if (!dataResponse.getData().isEmpty()) {
            DATUM = dataResponse.getData().get(0);
            changeFlow(DATUM.getStatus());
            pickLocationLayout.setVisibility(View.GONE);
            menu.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.GONE);
        } else {
            menu.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
            changeFlow("EMPTY");
            pickLocationLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSuccessLogout(Object object) {

    }

    @Override
    public void onSuccess(List<Provider> providerList) {
        for (Provider provider : providerList) {
            if (mHashMap.containsKey(provider.getId())) {
                Marker marker = mHashMap.get(provider.getId());
                LatLng startPosition = marker.getPosition();
                LatLng newPos = new LatLng(provider.getLatitude(), provider.getLongitude());
                marker.setPosition(newPos);
                animateMarker(startPosition, newPos, marker);
                marker.setRotation(bearingBetweenLocations(startPosition, newPos));
            } else {
                int vehicleIcon = R.drawable.car_icon;
                Integer serviceId = 0;
                if (provider.getProviderService() != null) {
                    serviceId = provider.getProviderService().getServiceTypeId();
                    vehicleIcon = provider.getProviderService().getServiceTypeId() == 5 ? R.drawable.auto_icon : R.drawable.car_icon;
                }

                MarkerOptions markerOptions = new MarkerOptions()
                        .anchor(0.5f, 0.5f)
                        .position(new LatLng(provider.getLatitude(), provider.getLongitude()))
                        .rotation(0.0f)
                        .snippet(serviceId + "")
                        .icon(BitmapDescriptorFactory.fromResource(vehicleIcon));
                mHashMap.put(provider.getId(), googleMap.addMarker(markerOptions));

            }
        }
    }

    @Override
    public void onError(Throwable e) {
        Log.d("Error", "My Error" + e.getLocalizedMessage());

        if (e instanceof HttpException) {
            Response response = ((HttpException) e).response();
            Log.e("onError", response.code() + "");
        }
    }

    @Override
    public void onSuccess(SettingsResponse response) {
        SharedHelper.putKey(this, SharedHelper.GOOGLE_API_KEY, response.getApiKey());
        alternateApiKey = response.getApiKey();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == PICK_LOCATION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (RIDE_REQUEST.containsKey("s_address")) {
                    sourceTxt.setText(String.valueOf(RIDE_REQUEST.get("s_address")));
                } else {
                    sourceTxt.setText("");
                }
                if (RIDE_REQUEST.containsKey("d_address")) {
                    destinationTxt.setText(String.valueOf(RIDE_REQUEST.get("d_address")));
                } else {
                    destinationTxt.setText("");
                }
                if (RIDE_REQUEST.containsKey("s_address") && RIDE_REQUEST.containsKey("d_address")) {
                    changeFlow("SERVICE");
                    LatLng origin = new LatLng((Double) RIDE_REQUEST.get("s_latitude"), (Double) RIDE_REQUEST.get("s_longitude"));
                    LatLng destination = new LatLng((Double) RIDE_REQUEST.get("d_latitude"), (Double) RIDE_REQUEST.get("d_longitude"));
                    drawRoute(origin, destination, false);
                } else {
                    changeFlow("EMPTY");
                }
            }
        }
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean removeDAddress = intent.getBooleanExtra("removeDAddress", false);
            if (removeDAddress) {
                destinationTxt.setText("");
            }

            mainPresenter.checkStatus();
        }
    };


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);

    }


    private void showSnack(boolean isConnected) {
        if (isConnected) {
            lnrNoInternet.setVisibility(View.GONE);
        } else {
            lnrNoInternet.setVisibility(View.VISIBLE);
        }
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    public void showSelectedProviders(Integer id) {
        googleMap.clear();
        List<Marker> providers = new ArrayList<>();
        for (Map.Entry<Integer, Marker> item : mHashMap.entrySet()) {
            Marker marker = item.getValue();
            if (marker.getSnippet().equals(id + "")) {
                providers.add(item.getValue());
            }
        }

        for (Marker item : providers) {
            int vehicleIcon = id == 5 ? R.drawable.auto_icon : R.drawable.car_icon;
            MarkerOptions markerOptions = new MarkerOptions()
                    .anchor(0.5f, 0.5f)
                    .position(item.getPosition())
                    .rotation(0.0f)
                    .snippet("" + item.getId())
                    .icon(BitmapDescriptorFactory.fromResource(vehicleIcon));
            googleMap.addMarker(markerOptions);
        }

        if (polylineOptions != null) {
            LatLng origin = polylineOptions.getPoints().get(0);
            LatLng destination = polylineOptions.getPoints().get(polylineOptions.getPoints().size() - 1);
            googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.src_icon)).position(origin));
            googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.des_icon)).position(destination));
            googleMap.addPolyline(polylineOptions);
        }
        //System.out.format("key: %s, value: %d%n", item.getKey(), item.getValue());
    }

    public void drawDirectionToStop(String overviewPolyline) {
        if (overviewPolyline != null) {
            googleMap.clear();
            PolyUtils polyUtils = new PolyUtils(this, googleMap, overviewPolyline);
            polyUtils.start();
        }
    }
}
