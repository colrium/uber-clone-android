package com.tranxit.enterprise.ui.fragment.wallet_history;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tranxit.enterprise.user.R;
import com.tranxit.enterprise.base.BaseFragment;
import com.tranxit.enterprise.data.network.model.Wallet;

import java.text.NumberFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class WalletHistoryFragment extends BaseFragment implements WalletHistoryIView {

    @BindView(R.id.wallet_history_rv)
    RecyclerView walletHistoryRv;
    @BindView(R.id.error_layout)
    LinearLayout errorLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    Unbinder unbinder;

    NumberFormat numberFormat;
    WalletHistoryAdapter adapter;
    private WalletHistoryPresenter<WalletHistoryFragment> presenter = new WalletHistoryPresenter<>();


    public WalletHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_wallet_history;
    }

    @Override
    public View initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        presenter.attachView(this);
        numberFormat = getNumberFormat();
        walletHistoryRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        walletHistoryRv.setItemAnimator(new DefaultItemAnimator());

        presenter.wallet();
        return view;
    }

    @Override
    public void onSuccess(List<Wallet> walletList) {
        progressBar.setVisibility(View.GONE);
        adapter = new WalletHistoryAdapter(getActivity(), walletList);
        walletHistoryRv.setAdapter(adapter);

        if (walletList.isEmpty()) {
            errorLayout.setVisibility(View.VISIBLE);
        } else {
            errorLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onError(Throwable e) {
        progressBar.setVisibility(View.GONE);

    }


    private class WalletHistoryAdapter extends RecyclerView.Adapter<WalletHistoryAdapter.MyViewHolder> {

        private List<Wallet> list;
        private Context context;

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private CardView itemView;
            private TextView createdAt, via, amount, status;

            MyViewHolder(View view) {
                super(view);
                itemView = (CardView) view.findViewById(R.id.item_view);
                createdAt = (TextView) view.findViewById(R.id.created_at);
                via = (TextView) view.findViewById(R.id.via);
                amount = (TextView) view.findViewById(R.id.amount);
                status = (TextView) view.findViewById(R.id.status);
                //itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {

            }
        }


        private WalletHistoryAdapter(Context context, List<Wallet> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_wallet_history, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Wallet item = list.get(position);
            holder.createdAt.setText(item.getCreatedAt());
            holder.amount.setText(numberFormat.format(item.getAmount()));
            holder.via.setText(item.getVia());
            holder.createdAt.setText(item.getCreatedAt());

            if (item.getStatus().equalsIgnoreCase("CREDITED")) {
                holder.status.setText(getString(R.string.credited_by));
            } else if (item.getStatus().equalsIgnoreCase("DEBITED")) {
                holder.status.setText(getString(R.string.debited_from));
            } else {
                holder.status.setText(item.getStatus());
            }

        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

}
