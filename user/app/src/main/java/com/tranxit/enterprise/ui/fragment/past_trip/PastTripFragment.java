package com.tranxit.enterprise.ui.fragment.past_trip;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tranxit.enterprise.user.R;
import com.tranxit.enterprise.base.BaseActivity;
import com.tranxit.enterprise.base.BaseFragment;
import com.tranxit.enterprise.data.network.model.Datum;
import com.tranxit.enterprise.data.network.model.Provider;
import com.tranxit.enterprise.ui.activity.past_trip_detail.PastTripDetailActivity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PastTripFragment extends BaseFragment implements PastTripIView {


    @BindView(R.id.past_trip_rv)
    RecyclerView pastTripRv;
    @BindView(R.id.error_layout)
    LinearLayout errorLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    Unbinder unbinder;

    List<Datum> list = new ArrayList<>();
    TripAdapter adapter;

    NumberFormat numberFormat = getNumberFormat();

    private PastTripPresenter<PastTripFragment> presenter = new PastTripPresenter<>();


    public PastTripFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_past_trip;
    }

    @Override
    public View initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        presenter.attachView(this);
        adapter = new TripAdapter(getActivity(), list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        pastTripRv.setLayoutManager(mLayoutManager);
        pastTripRv.setItemAnimator(new DefaultItemAnimator());
        pastTripRv.setAdapter(adapter);


        progressBar.setVisibility(View.VISIBLE);
        presenter.pastTrip();

        return view;
    }

    @Override
    public void onSuccess(List<Datum> datumList) {
        progressBar.setVisibility(View.GONE);

        list.clear();
        list.addAll(datumList);
        adapter.notifyDataSetChanged();

        if (list.isEmpty()) {
            errorLayout.setVisibility(View.VISIBLE);
        } else {
            errorLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onError(Throwable e) {
        progressBar.setVisibility(View.GONE);
    }

    private class TripAdapter extends RecyclerView.Adapter<TripAdapter.MyViewHolder> {

        private List<Datum> list;
        private Context context;

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private CardView itemView;
            private TextView bookingId, payable, finishedAt, lblCarType;
            private ImageView staticMap;
            CircleImageView avatar;

            MyViewHolder(View view) {
                super(view);
                itemView = (CardView) view.findViewById(R.id.item_view);
                bookingId = (TextView) view.findViewById(R.id.booking_id);
                payable = (TextView) view.findViewById(R.id.payable);
                lblCarType = (TextView) view.findViewById(R.id.lblCarType);
                finishedAt = (TextView) view.findViewById(R.id.finished_at);
                staticMap = (ImageView) view.findViewById(R.id.static_map);
                avatar = (CircleImageView) view.findViewById(R.id.avatar);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                if (view.getId() == R.id.item_view) {
                    BaseActivity.DATUM = list.get(position);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity(), staticMap, ViewCompat.getTransitionName(staticMap));
                    Intent intent = new Intent(getActivity(), PastTripDetailActivity.class);
                    startActivity(intent, options.toBundle());
                }

            }
        }


        private TripAdapter(Context context, List<Datum> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_past_trip, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Datum datum = list.get(position);
            holder.finishedAt.setText(datum.getFinishedAt());
            holder.bookingId.setText(datum.getBookingId());
            if(datum.getPayment()!= null){
                holder.payable.setText(numberFormat.format(datum.getPayment().getTotal()));
            }
            if(datum.getServiceType() != null){
                holder.lblCarType.setText(datum.getServiceType().getName());
            }
            Glide.with(activity()).load(datum.getStaticMap()).apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background).dontAnimate().error(R.drawable.ic_launcher_background)).into(holder.staticMap);

            Provider provider = datum.getProvider();
            if (provider != null) {
                Glide.with(activity()).load(provider.getAvatar()).apply(RequestOptions.placeholderOf(R.drawable.user).dontAnimate().error(R.drawable.user)).into(holder.avatar);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}
