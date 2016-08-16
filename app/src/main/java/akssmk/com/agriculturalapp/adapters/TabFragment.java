package akssmk.com.agriculturalapp.adapters;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import akssmk.com.agriculturalapp.R;
import akssmk.com.agriculturalapp.application.MyApplication;
import akssmk.com.agriculturalapp.application.MySingleton;
import akssmk.com.agriculturalapp.modals.ItemBazaar;

public class TabFragment extends Fragment {

    private String date_string;
    RecyclerView recyclerView;
    BazaarAdapter adapter;
    String state,district,day;
    TextView state_view,district_view,day_view;
    FloatingActionButton btn;
    ProgressBar p;
    String day_string;
    private ArrayList<ItemBazaar> items;

    private String URL_FINAL = "http://kharita.freevar.com/agriculture_market.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        date_string = getArguments().getString("date");
        state = getArguments().getString("state");
        district = getArguments().getString("district");

        Log.v("dateRecieved", date_string);
        View view = inflater.inflate(R.layout.singletab, container, false);

        items = new ArrayList<>();
        state_view=(TextView)view.findViewById(R.id.state);
        district_view=(TextView)view.findViewById(R.id.district);
        day_view=(TextView)view.findViewById(R.id.arrival_date);
        p = (ProgressBar)view.findViewById(R.id.progress);
        p.setVisibility(View.VISIBLE);

        recyclerView=(RecyclerView)view.findViewById(R.id.crop_recycler);

        //if(i!=null){
            /*state=i.getStringExtra(SurveyActivity.STATE);
            district=i.getStringExtra(SurveyActivity.DISTRICT);
            day=i.getStringExtra(SurveyActivity.CROP);*/

           // state="Rajasthan";
            //district="Kota";
            day=day_string;

            day_view.setText(date_string);  //

            state_view.setText(state);
            district_view.setText(district);

            sendRequest(URL_FINAL,state,district,date_string);
       // }




        return view;
    }

    private void sendRequest(String url, final String State, final String District, final String Day)
    {
        StringRequest s = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.v("RESPONSE#", response);

                    JSONObject result = new JSONObject(response);
                    JSONArray a=result.getJSONArray("result");
                    for(int i=0;i<a.length();i++){
                        String  market,commodity,variety,min,max,avg;

                        JSONObject w=a.getJSONObject(i);
                        ItemBazaar item = new ItemBazaar();
                        market = w.getString("Market");
                        commodity = w.getString("Commodity");
                        variety = w.getString("Variety");
                        min = w.getString("Min");
                        max = w.getString("Max");
                        avg = w.getString("Modal");

                        item.setMarket(market);
                        item.setCommodity(commodity);
                        item.setMin(min);
                        item.setMax(max);
                        item.setAvg(avg);
                        item.setVariety(variety);

                        items.add(item);

                    }
                    adapter=new BazaarAdapter(getActivity(),items);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(adapter);
                    p.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("State",State);
                map.put("District",District);
                map.put("Date",Day);
                return map;
            }
        };
        MySingleton.getInstance(MyApplication.getAppContext()).addToRequestQueue(s);


    }

}
