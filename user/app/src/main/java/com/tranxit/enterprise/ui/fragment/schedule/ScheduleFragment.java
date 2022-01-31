package com.tranxit.enterprise.ui.fragment.schedule;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tranxit.enterprise.user.R;
import com.tranxit.enterprise.base.BaseActivity;
import com.tranxit.enterprise.base.BaseFragment;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.HttpException;
import retrofit2.Response;

import static com.tranxit.enterprise.base.BaseActivity.RIDE_REQUEST;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends BaseFragment implements ScheduleIView {

    DatePickerDialog.OnDateSetListener dateSetListener;
    TimePickerDialog.OnTimeSetListener timeSetListener;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.time)
    TextView time;
    Unbinder unbinder;

    private SchedulePresenter<ScheduleFragment> presenter = new SchedulePresenter<>();

    public ScheduleFragment() {

        dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            Calendar myCalendar = Calendar.getInstance();
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            date.setText(BaseActivity.SIMPLE_DATE_FORMAT.format(myCalendar.getTime()));
        };

        timeSetListener = (timePicker, selectedHour, selectedMinute) -> time.setText(selectedHour + ":" + selectedMinute);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_schedule;
    }

    @Override
    public View initView(View view) {
        ButterKnife.bind(this, view);
        presenter.attachView(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick({R.id.date, R.id.time, R.id.schedule_request})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.date:
                datePicker(dateSetListener);
                break;
            case R.id.time:
                timePicker(timeSetListener);
                break;
            case R.id.schedule_request:
                sendRequest();
                break;
        }
    }

    private void sendRequest() {
        if (date.getText().toString().isEmpty() || time.getText().toString().isEmpty()) {
            Toast.makeText(activity(), R.string.please_select_date_time, Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> map = new HashMap<>(RIDE_REQUEST);
        map.put("schedule_date", date.getText().toString());
        map.put("schedule_time", time.getText().toString());
        map.put("service_required", "normal");
        showLoading();
        presenter.sendRequest(map);
    }

    @Override
    public void onSuccess(Object object) {
        hideLoading();
        Toast.makeText(activity(), "Schedule Ride Requested Successfully.", Toast.LENGTH_SHORT).show();
        RIDE_REQUEST.remove("d_address");
        RIDE_REQUEST.remove("d_latitude");
        RIDE_REQUEST.remove("d_longitude");

        Intent intent = new Intent("INTENT_FILTER");
        intent.putExtra("removeDAddress", true);
        activity().sendBroadcast(intent);
    }

    @Override
    public void onError(Throwable e) {
        hideLoading();
        if (e instanceof HttpException) {
            Response response = ((HttpException) e).response();

            try {
                JSONObject jObjError = new JSONObject(response.errorBody().string());
                if (jObjError.has("error")) {
                    if (response.code() >= 500) {
                        Toast.makeText(getContext(), Html.fromHtml(jObjError.optString("error") + "<br>" + response.code() + "--" + response.message()), Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getContext(), jObjError.optString("error"), Toast.LENGTH_SHORT).show();
                    }
                }else {
                    if (response.code() >= 500) {
                        Toast.makeText(getContext(), Html.fromHtml(jObjError.optString("error") + "<br>" + response.code() + "--" + response.message()), Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getContext(), jObjError.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception exp) {
                Log.e("Error", exp.getMessage());
            }
        }
    }
}
