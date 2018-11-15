package com.crazy_iter.checkarround.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
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
import com.crazy_iter.checkarround.AddSpotActivity;
import com.crazy_iter.checkarround.Data.JobItem;
import com.crazy_iter.checkarround.Dialogs.DialogCall;
import com.crazy_iter.checkarround.Dialogs.DialogSpots;
import com.crazy_iter.checkarround.R;
import com.crazy_iter.checkarround.StartActivity;
import com.crazy_iter.checkarround.StaticsAndAPIs.API_URLs;
import com.crazy_iter.checkarround.StaticsAndAPIs.StaticCodes;
import com.crazy_iter.checkarround.StaticsAndAPIs.StaticString;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by CrazyITer on 4/5/2018.
 */

public class SpotsAdapter extends RecyclerView.Adapter<SpotsAdapter.ViewHolderSpot>
        implements StaticString, StaticCodes, API_URLs {

    private Context context;
    private ArrayList<JobItem> spotItemArrayList;
    private boolean isForMe;
    private RequestQueue requestQueue;
    private int retryNum = 0;

    public SpotsAdapter(Context context, ArrayList<JobItem> allJobItemArrayList) {
        this.context = context;
        this.spotItemArrayList = allJobItemArrayList;
        this.isForMe = false;
        this.requestQueue = Volley.newRequestQueue(context);

    }

    public SpotsAdapter(Context context, ArrayList<JobItem> allJobItemArrayList, boolean isForMe) {
        this.context = context;
        this.spotItemArrayList = allJobItemArrayList;
        this.isForMe = isForMe;
        this.requestQueue = Volley.newRequestQueue(context);
    }


    @Override
    public ViewHolderSpot onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_all_jobs, parent, false);
        return new ViewHolderSpot(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolderSpot holder, final int position) {
        holder.positionTVAll.setText(spotItemArrayList.get(position).getPosition());
        holder.hoursTVAll.setText(spotItemArrayList.get(position).getTime());
        holder.daysTVAll.setText(spotItemArrayList.get(position).getDays());
        holder.companyTVAll.setText(spotItemArrayList.get(position).getCompany() + ", " + spotItemArrayList.get(position).getAddress());
        holder.addressTVAll.setText(spotItemArrayList.get(position).getAddress());
        holder.salaryTVAll.setText(spotItemArrayList.get(position).getSalary());

        if (spotItemArrayList.get(position).isStared()) {
            holder.isStaredIVAll.setImageDrawable(context.getDrawable(R.drawable.ic_star));
        } else {
            holder.isStaredIVAll.setImageDrawable(context.getDrawable(R.drawable.ic_star_border));
        }

        if (isForMe) {
            if (spotItemArrayList.get(position).isDone()) {
                holder.isStaredIVAll.setImageDrawable(context.getDrawable(R.drawable.ic_check_circle));
            } else {
                holder.isStaredIVAll.setImageDrawable(context.getDrawable(R.drawable.ic_done));
            }
        }

        if (isForMe) {
            holder.callSMSLL.setVisibility(View.GONE);
            holder.editDeleteLL.setVisibility(View.VISIBLE);
        } else {
            holder.callSMSLL.setVisibility(View.VISIBLE);
            holder.editDeleteLL.setVisibility(View.GONE);
        }

        holder.isStaredIVAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isForMe) {
                    if (spotItemArrayList.get(position).isDone()) {
                        holder.isStaredIVAll.setImageDrawable(context.getDrawable(R.drawable.ic_done));
                        spotItemArrayList.get(position).setDone(false);
                    } else {
                        holder.isStaredIVAll.setImageDrawable(context.getDrawable(R.drawable.ic_check_circle));
                        spotItemArrayList.get(position).setDone(true);
                    }
                    toggleDone(spotItemArrayList.get(position).getJobId());
                } else {
                    if (StartActivity.user == null) {
                        Toast.makeText(context, R.string.sign_in_please, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (spotItemArrayList.get(position).isStared()) {
                        holder.isStaredIVAll.setImageDrawable(context.getDrawable(R.drawable.ic_star_border));
                        spotItemArrayList.get(position).setStared(false);
                    } else {
                        holder.isStaredIVAll.setImageDrawable(context.getDrawable(R.drawable.ic_star));
                        spotItemArrayList.get(position).setStared(true);
                    }
                    toggleStaredSpot(spotItemArrayList.get(position).getJobId());
                }
            }
        });
        try {
            Picasso.with(context)
                    .load(spotItemArrayList.get(position).getPhone())
                    .placeholder(R.drawable.googleg_standard_color_18)
                    .into(holder.iconPositionIVAll);
        } catch (Exception ignored) {}
        holder.iconPositionIVAll.setVisibility(View.GONE);

        holder.moreDetailsLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.moreDetailsAll.getVisibility() == View.VISIBLE) {
                    holder.moreDetailsAll.setVisibility(View.GONE);
                    holder.moreDetailsIcon.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_drop_up));
                    holder.moreDetailsTV.setText(R.string.more_details);
                } else {
                    holder.moreDetailsAll.setVisibility(View.VISIBLE);
                    holder.moreDetailsIcon.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_drop_down));
                    holder.moreDetailsTV.setText(R.string.less_details);
                }
            }
        });
        holder.descTVAll.setText((context.getString(R.string.gender_show) + spotItemArrayList.get(position).getGender().toUpperCase() + "\n" + spotItemArrayList.get(position).getDescription()).trim());

        holder.sendMessageLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spotItemArrayList.get(position).getPhone().length() == 0) {
                    Toast.makeText(context, R.string.phone_not_availible, Toast.LENGTH_SHORT).show();
                } else {
                    sendMessage(spotItemArrayList.get(position).getPhone());
                }
            }
        });

        holder.makeCallLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spotItemArrayList.get(position).getPhone().length() == 0) {
                    Toast.makeText(context, R.string.phone_not_availible, Toast.LENGTH_SHORT).show();
                } else {
                    new DialogCall(context, spotItemArrayList.get(position).getPhone()).show();
                }
            }
        });

        holder.editLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editSpot(spotItemArrayList.get(position));
            }
        });

        holder.deleteLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(context);
                deleteDialog.setMessage(R.string.sure_to_delete_spot)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteSpot(spotItemArrayList.get(position).getJobId());
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .create()
                        .show();
            }
        });

    }

    private void editSpot(JobItem spot) {
        AddSpotActivity.jobItem = spot;
        Intent intent = new Intent(context, AddSpotActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("isEdit", true);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    private void deleteSpot(final String id) {
        DialogSpots.textView.setText("load");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, DELETE_SPOT + id,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        retryNum = 0;
                        DialogSpots.textView.setText("done");
                        requestQueue.cancelAll(S_DELETE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                requestQueue.cancelAll(S_DELETE);
                if (retryNum < RETRY_NUM) {
                    retryNum++;
                    deleteSpot(id);
                } else {
                    Toast.makeText(context, R.string.try_again, Toast.LENGTH_SHORT).show();
                    DialogSpots.textView.setText("load");
                    retryNum = 0;
                }
            }
        });
        jsonObjectRequest.setTag(S_DELETE);
        requestQueue.add(jsonObjectRequest);
    }

    private void sendMessage(String num) {
        Uri uri = Uri.parse("smsto:"+num);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        context.startActivity(Intent.createChooser(it, context.getString(R.string.send_by)));
    }

    @Override
    public int getItemCount() {
        return spotItemArrayList.size();
    }

    private void toggleStaredSpot(final String id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(S_SPOT_ID, id);
            jsonObject.put(S_UserID, StartActivity.user.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.POST, TOGGLE_STARED_SPOT, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        retryNum = 0;
                        Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                        requestQueue.cancelAll(S_TOGGLE_STAR);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                requestQueue.cancelAll(S_TOGGLE_STAR);
                if (retryNum < RETRY_NUM) {
                    retryNum++;
                    toggleStaredSpot(id);
                } else {
                    Toast.makeText(context, R.string.try_again, Toast.LENGTH_SHORT).show();
                    retryNum = 0;
                }
            }
        });
        jsonObjectRequest.setTag(S_TOGGLE_STAR);
        requestQueue.add(jsonObjectRequest);
    }

    private void toggleDone(final String id) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, DONE_SPOT + id,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        retryNum = 0;
                        requestQueue.cancelAll(S_TOGGLE_DONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                requestQueue.cancelAll(S_TOGGLE_DONE);
                if (retryNum < RETRY_NUM) {
                    retryNum++;
                    toggleDone(id);
                } else {
                    Toast.makeText(context, R.string.try_again, Toast.LENGTH_SHORT).show();
                    retryNum = 0;
                }
            }
        });
        jsonObjectRequest.setTag(S_TOGGLE_DONE);
        requestQueue.add(jsonObjectRequest);
    }

    class ViewHolderSpot extends RecyclerView.ViewHolder {
        LinearLayout moreDetailsLL, moreDetailsAll, makeCallLL, sendMessageLL, editLL, deleteLL, callSMSLL, editDeleteLL;
        CardView spotItemCard;
        TextView positionTVAll, hoursTVAll, daysTVAll, companyTVAll, addressTVAll, salaryTVAll, descTVAll, moreDetailsTV;
        ImageView isStaredIVAll, iconPositionIVAll, moreDetailsIcon;

        ViewHolderSpot(View itemView) {
            super(itemView);
            editLL = itemView.findViewById(R.id.editSpotLL);
            deleteLL = itemView.findViewById(R.id.deleteSpotLL);
            callSMSLL = itemView.findViewById(R.id.callSMSLL);
            editDeleteLL = itemView.findViewById(R.id.editDeleteLL);
            spotItemCard = itemView.findViewById(R.id.spotItemCard);
            positionTVAll = itemView.findViewById(R.id.positionTVAll);
            hoursTVAll = itemView.findViewById(R.id.hoursTVAll);
            daysTVAll = itemView.findViewById(R.id.daysTVAll);
            companyTVAll = itemView.findViewById(R.id.companyTVAll);
            addressTVAll = itemView.findViewById(R.id.addressTVAll);
            salaryTVAll = itemView.findViewById(R.id.salaryTVAll);
            isStaredIVAll = itemView.findViewById(R.id.isStaredIVAll);
            iconPositionIVAll = itemView.findViewById(R.id.iconPositionIVAll);
            descTVAll = itemView.findViewById(R.id.descTVAll);
            moreDetailsTV = itemView.findViewById(R.id.moreDetailsTV);
            moreDetailsIcon = itemView.findViewById(R.id.dropIconMoreDetails);
            moreDetailsLL = itemView.findViewById(R.id.moreDetailsLL);
            moreDetailsAll = itemView.findViewById(R.id.moreDetailsAll);
            sendMessageLL = itemView.findViewById(R.id.sendMessageLL);
            makeCallLL = itemView.findViewById(R.id.makeCallLL);
        }
    }

}

