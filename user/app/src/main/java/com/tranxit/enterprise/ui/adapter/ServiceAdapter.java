package com.tranxit.enterprise.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tranxit.enterprise.MvpApplication;
import com.tranxit.enterprise.user.R;
import com.tranxit.enterprise.data.network.model.Service;
import com.tranxit.enterprise.ui.activity.main.MainActivity;
import com.tranxit.enterprise.ui.fragment.service.RateCardFragment;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by santhosh@appoets.com on 08-05-2018.
 */
public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.MyViewHolder> {

    private List<Service> list;
    private TextView capacity;
    private Context context;
    private int lastCheckedPos = 0;
    private Animation zoomIn;
    NumberFormat numberFormat;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout itemView;
        private TextView serviceName, price;
        private ImageView image;

        MyViewHolder(View view) {
            super(view);
            serviceName = view.findViewById(R.id.service_name);
            price = view.findViewById(R.id.price);
            image = view.findViewById(R.id.image);
            itemView = view.findViewById(R.id.item_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Service object = list.get(position);
            if (view.getId() == R.id.item_view) {
                if (lastCheckedPos == position) {
                    RateCardFragment.SERVICE = object;
                    ((MainActivity) context).changeFragment(new RateCardFragment());
                }
                lastCheckedPos = position;
                notifyDataSetChanged();
            }

        }
    }

    public ServiceAdapter(Context context, List<Service> list, TextView capacity) {
        this.context = context;
        this.list = list;
        this.capacity = capacity;
        numberFormat = MvpApplication.getInstance().getNumberFormat();
        zoomIn = AnimationUtils.loadAnimation(this.context, R.anim.zoom_in_animation);
        zoomIn.setFillAfter(true);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_service, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Service obj = list.get(position);
        holder.serviceName.setText(obj.getName());
        holder.price.setText(numberFormat.format(obj.getFixed()));
        Glide.with(context).load(obj.getImage()).into(holder.image);
        if (position == lastCheckedPos) {
            capacity.setText(String.valueOf(obj.getCapacity()));
            holder.serviceName.setTextColor(context.getResources().getColor(R.color.colorPrimaryText));
            holder.price.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.information_button, 0);
            holder.price.setVisibility(View.VISIBLE);
            holder.itemView.startAnimation(zoomIn);
            ((MainActivity)context).showSelectedProviders(obj.getId());
        } else {
            holder.serviceName.setTextColor(context.getResources().getColor(R.color.colorSecondaryText));
            holder.price.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            holder.price.setVisibility(View.GONE);

        }
        holder.itemView.setAlpha(obj.getStatus() == 1 ? 1 : 0.5f);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public Service getSelectedService() {
        if (list.size() > 0) {
            return list.get(lastCheckedPos);
        } else {
            return null;
        }

    }

}
