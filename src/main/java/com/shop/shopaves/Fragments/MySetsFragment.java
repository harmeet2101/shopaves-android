package com.shop.shopaves.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shop.shopaves.Activity.CommentsActivity;
import com.shop.shopaves.Activity.GroupDetailsActivity;
import com.shop.shopaves.Activity.MySetsActivity;
import com.shop.shopaves.Activity.ProfileActivity;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.Util.PicassoCache;
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by amsyt005 on 13/1/17.
 */
public class MySetsFragment extends Fragment implements Response.ErrorListener, Response.Listener<JSONObject> {

    private RecyclerView recyclerView;
    private ArrayList<JSONITEM> lists = new ArrayList<>();
    private ProgressDialog pd;
    private CollectionSetAdapter adapter;
    private AppStore aps;
    private int selectedPosition = 0;
    private EditText search;

    public MySetsFragment(EditText search) {
        this.search = search;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.collection_sets, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.setsrecycleview);
        aps = new AppStore(getActivity());
        makePublicCollectionSetsRequest("", "");
        if(C.SEARCH_TYPE.equals(Constants.SETS)) {
            search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        if (!TextUtils.isEmpty(search.getText().toString()))
                            makePublicCollectionSetsRequest("", search.getText().toString());
                        //   makePublicCollectionRequest("", search.getText().toString());
                        C.hideSoftKeyboard(getActivity());
                        Log.i("enterPressed", "Enter pressed");
                        //  hideSoftKeyboard();
                        return true;
                    }
                    return false;
                }
            });
        }
        return view;
    }

    private void setCollectionSetAdapter() {
        adapter = new CollectionSetAdapter(lists);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void makePublicCollectionSetsRequest(String skip, String key) {
        pd = C.getProgressDialog(getActivity());
        Map<String, String> map = new HashMap<>();
        if (!TextUtils.isEmpty(aps.getData(Constants.USER_ID)))
            map.put("userId", aps.getData(Constants.USER_ID));
        map.put("myGroups", "false");
        map.put("key", C.getEncodedString(key));
        map.put("skip", skip);
        Net.makeRequest(C.APP_URL + ApiName.GET_GROUPS_API, map, this, this);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(getActivity(),VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Log.e("getGoupsResponse", jsonObject.toString());
        Model model = new Model(jsonObject.toString());
        if (model.getStatus().equals(Constants.SUCCESS_CODE)) {
            lists.clear();
            Model dataArray[] = model.getDataArray();
            for (Model data : dataArray) {
                lists.add(new JSONITEM(data));
            }
            setCollectionSetAdapter();
        }
    }

    private class CollectionSetAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private List<JSONITEM> source;

        public CollectionSetAdapter(List<JSONITEM> source) {
            this.source = source;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_collection_sets, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final JSONITEM items = source.get(position);
            final Model model = items.model;
            holder.collectionName.setText(model.getName());
            holder.userName.setText(model.getUserName());
            holder.textView3.setText(C.parseDate(C.getDateInMillis(model.getTimeStampSmall())));
            holder.commentCount.setText("" + model.getCommentCount());
            JSONArray collectionImageArray = model.getJsonImageArray();
            String modelUserId = "" + model.getProductUserId();
            String userId = aps.getData(Constants.USER_ID);
            Log.e("model id and userid", modelUserId + "userId" + userId);
            if (modelUserId.equals(userId)) {
                holder.addTo.setVisibility(View.VISIBLE);
                holder.addTo.setImageResource(R.drawable.delete);
            } else
                holder.addTo.setVisibility(View.INVISIBLE);

            if (collectionImageArray != null) {
                for (int i = 0; i < collectionImageArray.length(); i++) {
                    try {
                        PicassoCache.getPicassoInstance(getActivity()).load(C.getImageUrl(collectionImageArray.get(i).toString())).into(i == 0 ? holder.setOneImage : i == 1 ? holder.setTwoImage : i == 2 ? holder.setThreeImage : holder.setFourImage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            PicassoCache.getPicassoInstance(getActivity()).load(C.getImageUrl(model.getUserImage())).placeholder(R.drawable.female).into(holder.userImg);
            if (model.getLikesStatus() == 1) {
                holder.likeIcon.setImageResource(R.drawable.like);
                holder.likeCount.setText("" + model.getLikes());
                holder.likeIcon.setTag("1");
            } else {
                holder.likeIcon.setImageResource(R.drawable.unlike);
                holder.likeCount.setText("" + model.getLikes());
                holder.likeIcon.setTag("2");
            }

            holder.setGroupView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivityForResult(new Intent(getActivity(), GroupDetailsActivity.class).putExtra(Constants.GROUP_ID, "" + model.getId()), 100);
                }
            });
            holder.addcomment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(aps.getData(Constants.USER_ID))) {
                        //    startActivity(new Intent(getActivity(), CommentsActivity.class).putExtra(Constants.GROUP_ID, "" + model.getId()));
                        startActivityForResult(new Intent(getActivity(), CommentsActivity.class).putExtra(Constants.GROUP_ID, "" + model.getId()), 200);
                    } else
                        C.setLoginMessage(getActivity());
                }
            });

            holder.likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pd = C.getProgressDialog(getActivity());
                    Map<String, String> map = new HashMap<>();
                    map.put("userId", aps.getData(Constants.USER_ID));
                    map.put("gid", "" + model.getId());
                    map.put("status", holder.likeIcon.getTag().equals("1") ? "2" : "1");
                    Net.makeRequestParams(C.APP_URL + ApiName.LIKE_GROUP_API, map, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            pd.dismiss();
                            Model model = new Model(jsonObject);
                            if (model.getStatus().equals(Constants.SUCCESS_CODE)) {
                                Log.e("LikeGroupStatus", jsonObject.toString());
                                if (model.getLikeStatus() == 1) {
                                    holder.likeIcon.setImageResource(R.drawable.like);
                                    holder.likeIcon.setTag("1");
                                } else {
                                    holder.likeIcon.setImageResource(R.drawable.unlike);
                                    holder.likeIcon.setTag("2");
                                }
                                holder.likeCount.setText("" + model.getLikes());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            pd.dismiss();
                            if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                                VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                                volleyError = error;
                            }
                            Toast.makeText(getActivity(), VolleyErrors.setError(volleyError), Toast.LENGTH_LONG).show();
                        }
                    });

                   /* firstLoad = false;
                    if(!TextUtils.isEmpty(aps.getData(Constants.USER_ID))) {
                        if (holder.likeIcon.getTag().equals("1")) {
                            makeLikeRequest("" + model.getId(), "2");
                            adapterPositionView = position;
                        } else {
                            makeLikeRequest("" + model.getId(), "1");
                            adapterPositionView = position;
                        }
                    } else{
                        C.setLoginMessage(getActivity());
                    }*/
                }

            });

            holder.userProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!("" + model.getUserId()).equals(aps.getData(Constants.USER_ID)))
                        startActivity(new Intent(getActivity(), ProfileActivity.class).putExtra(Constants.USER_ID, "" + model.getProductUserId()));
                }
            });

            holder.addTo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedPosition = position;
                    makeDeleteGroupRequest("" + model.getId());
                }
            });
        }

        @Override
        public int getItemCount() {
            return source.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }


    }


    private class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView setOneImage, setTwoImage, setThreeImage, setFourImage, addTo;
        TextView collectionName, userName, textView3, likeCount, commentCount;
        LinearLayout addcomment, likes, setGroupView;
        ImageView likeIcon;
        CircleImageView userImg;
        RelativeLayout userProfile;

        public MyViewHolder(View itemView) {
            super(itemView);
            setOneImage = (ImageView) itemView.findViewById(R.id.setoneimage);
            setTwoImage = (ImageView) itemView.findViewById(R.id.settwoimage);
            setThreeImage = (ImageView) itemView.findViewById(R.id.setthreeimage);
            setFourImage = (ImageView) itemView.findViewById(R.id.setfourimage);
            addTo = (ImageView) itemView.findViewById(R.id.addto);
            userImg = (CircleImageView) itemView.findViewById(R.id.usr);
            collectionName = (TextView) itemView.findViewById(R.id.usr_tag);
            userName = (TextView) itemView.findViewById(R.id.user_nme);
            textView3 = (TextView) itemView.findViewById(R.id.tag_tm);
            likeCount = (TextView) itemView.findViewById(R.id.likes);
            commentCount = (TextView) itemView.findViewById(R.id.commnts);
            addcomment = (LinearLayout) itemView.findViewById(R.id.comment);
            likeIcon = (ImageView) itemView.findViewById(R.id.likeicon);
            likes = (LinearLayout) itemView.findViewById(R.id.like);
            setGroupView = (LinearLayout) itemView.findViewById(R.id.setview);
            userProfile = (RelativeLayout) itemView.findViewById(R.id.usrprofile);
            //addTo.setVisibility(View.GONE);
            itemView.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bitmap productBitmap = ((BitmapDrawable) setOneImage.getDrawable()).getBitmap();
                    if (productBitmap != null) {
                        C.shareContentExp(getActivity(), collectionName.getText().toString(), C.getImageUri(getActivity(), productBitmap));
                    }
                }
            });
        }
    }

    private void makeDeleteGroupRequest(String gid) {
        pd = C.getProgressDialog(getActivity());
        Map<String, String> map = new HashMap<>();
        map.put("userId", aps.getData(Constants.USER_ID));
        map.put("gid", gid);
        Net.makeRequestParams(C.APP_URL + ApiName.DELETE_GROUP_API, map, deleteGroupResponse, this);
    }

    Response.Listener<JSONObject> deleteGroupResponse = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Model model = new Model(jsonObject.toString());
            if (model.getStatus().equals(Constants.SUCCESS_CODE)) {
                lists.remove(selectedPosition);
                adapter.notifyDataSetChanged();
            }
        }
    };

    private class JSONITEM {
        private Model model;

        public JSONITEM(Model model) {
            this.model = model;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == 200) {
            makePublicCollectionSetsRequest("", "");
        }
    }
}
