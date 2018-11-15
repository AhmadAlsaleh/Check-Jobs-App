package com.crazy_iter.checkarround.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.crazy_iter.checkarround.Data.Places;
import com.crazy_iter.checkarround.Dialogs.DialogAddNewPlace;
import com.crazy_iter.checkarround.Dialogs.DialogSelectPlace;
import com.crazy_iter.checkarround.R;
import com.crazy_iter.checkarround.StaticsAndAPIs.API_URLs;
import com.crazy_iter.checkarround.StaticsAndAPIs.StaticCodes;
import com.crazy_iter.checkarround.StaticsAndAPIs.StaticString;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by CrazyITer on 5/11/2018.
 */

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> implements StaticCodes, StaticString, API_URLs {

    private ArrayList<Places> placesArrayList;
    private Context context;
    private RequestQueue requestQueue;
    private int retryNum = 0;
    private boolean toSelect;

    public PlacesAdapter(ArrayList<Places> placesArrayList, Context context) {
        this.placesArrayList = placesArrayList;
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
        this.toSelect = false;
    }

    public PlacesAdapter(ArrayList<Places> placesArrayList, Context context, boolean toSelect) {
        this.placesArrayList = placesArrayList;
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
        this.toSelect = toSelect;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_place, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.placeTV.setText(placesArrayList.get(position).getTitle());
        if (toSelect) {
            holder.placeEditIV.setVisibility(View.GONE);
            holder.placeDeleteIV.setVisibility(View.GONE);
        }
        holder.placeDeleteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.sure_to_delete_place);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deletePlace(placesArrayList.get(position).getId());
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                        .create()
                        .show();
            }
        });
        holder.placeEditIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DialogAddNewPlace(context, placesArrayList.get(position)).show();
            }
        });
        holder.placeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toSelect) {
                    DialogSelectPlace.selectedPlace = placesArrayList.get(position);
                    DialogSelectPlace.textView.setText("done");
                } else {
                    new DialogAddNewPlace(context, placesArrayList.get(position)).show();
                }
            }
        });

    }

    private void deletePlace(final String id) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, DELETE_PLACE + id,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        requestQueue.cancelAll(S_DELETE);
                        Toast.makeText(context, R.string.deleted, Toast.LENGTH_SHORT).show();
                        ((Activity)context).recreate();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                requestQueue.cancelAll(S_DELETE);
                if (retryNum < RETRY_NUM) {
                    retryNum++;
                    deletePlace(id);
                } else {
                    Toast.makeText(context, R.string.try_again, Toast.LENGTH_SHORT).show();
                }

            }
        });
        jsonObjectRequest.setTag(S_DELETE);
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public int getItemCount() {
        return this.placesArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout placeLL;
        private TextView placeTV;
        private ImageView placeEditIV, placeDeleteIV;

        public ViewHolder(View itemView) {
            super(itemView);
            placeLL = itemView.findViewById(R.id.placeLL);
            placeTV = itemView.findViewById(R.id.placeTV);
            placeDeleteIV = itemView.findViewById(R.id.placeDeleteIV);
            placeEditIV = itemView.findViewById(R.id.placeEditIV);
        }
    }
}
