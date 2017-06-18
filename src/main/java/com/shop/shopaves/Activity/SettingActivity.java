package com.shop.shopaves.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.Interface.FragmentCallBack;
import com.shop.shopaves.R;

@SuppressLint("ValidFragment")
public class SettingActivity extends android.support.v4.app.Fragment{

    private SettingAdapter settingAdapter;
    private FragmentCallBack fragmentCallBack;
    private ListView settingList;
    private String[] SETTINGS_ITEMS;


    @SuppressLint("ValidFragment")
    public SettingActivity(FragmentCallBack fragmentCallBack) {
        this.fragmentCallBack = fragmentCallBack;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_setting, container, false);
        settingAdapter = new SettingAdapter();
        settingList = (ListView)v.findViewById(R.id.settinglist);
        v.setAnimation(inFromLeftAnimation());
       SETTINGS_ITEMS = new String[]{"Payment",getResources().getString(R.string.transaction_details),getResources().getString(R.string.language),getResources().getString(R.string.shipping_address),getResources().getString(R.string.region),getResources().getString(R.string.preferences),getResources().getString(R.string.sale_alert),getResources().getString(R.string.notifications),getResources().getString(R.string.contacts),getResources().getString(R.string.order_history),getResources().getString(R.string.term_of_use_privacy_policy),getResources().getString(R.string.faq),getResources().getString(R.string.contact_us),getResources().getString(R.string.manage_social)};
        for(int i = 0; i<SETTINGS_ITEMS.length; i++){
            settingAdapter.add(new Item(SETTINGS_ITEMS[i], Constants.SETTINGS_ICONS[i]));
        }
        settingList.setAdapter(settingAdapter);
       // Constants.SETTINGS_ITEMS = new String[]{getActivity().getResources().getString(R.string.payment),getActivity().getResources().getString(R.string.transaction_details),getActivity().getResources().getString(R.string.language),getActivity().getResources().getString(R.string.shipping_address),getActivity().getResources().getString(R.string.region),getActivity().getResources().getString(R.string.preferences),getActivity().getResources().getString(R.string.sale_alert),getActivity().getResources().getString(R.string.notifications),getActivity().getResources().getString(R.string.contacts),getActivity().getResources().getString(R.string.order_history),getActivity().getResources().getString(R.string.term_of_use_privacy_policy),getActivity().getResources().getString(R.string.faq),getActivity().getResources().getString(R.string.contact_us),getActivity().getResources().getString(R.string.manage_social)};
        return v;
    }
    private Animation inFromLeftAnimation() {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(500);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }

    private class SettingAdapter extends ArrayAdapter<Item>  {

        MyHolder holder;

        public SettingAdapter() {
            super(getActivity(), R.layout.listgroup);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            holder = null;
            if (row == null) {
                LayoutInflater inflater = (getActivity()).getLayoutInflater();
                row = inflater.inflate(R.layout.listgroup, parent, false);
                holder = new MyHolder();
                holder.categoryName = (TextView) row.findViewById(R.id.lblListHeader);
                holder.icon = (ImageView) row.findViewById(R.id.listimg);
                row.setTag(holder);
            } else {
                holder = (MyHolder) row.getTag();
            }
            final Item item = getItem(position);
            holder.categoryName.setText(item.name);
            holder.icon.setImageResource(item.settingIcon);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(position == 0)
                        startActivity(new Intent(getContext(),PaymentLocalActivity.class));
                    else if(position == 1)
                        startActivity(new Intent(getContext(),TransactionDetailsActivity.class));
                    else if (position == 2)
                        startActivity(new Intent(getContext(), LanguageActivity.class));
                    else if (position == 3)
                        startActivity(new Intent(getContext(), ChangeShippingAddressActivity.class));
                    else if (position == 4)
                        startActivity(new Intent(getContext(), RegionActivity.class));
                    else if (position == 5)
                        startActivity(new Intent(getContext(), SignupStepActivity.class).putExtra(Constants.TYPE, "preference"));
                    else if (position == 6)
                        startActivity(new Intent(getContext(), SalesAlertActivity.class));
                    else if (position == 7)
                        startActivity(new Intent(getContext(), NotificationActivity.class));
                    else if (position == 8)
                        startActivity(new Intent(getContext(), ContactsActivity.class));
                    else if (position == 9)
                        startActivity(new Intent(getContext(), OrderHistory.class));
                    else if (position == 10)
                        startActivity(new Intent(getContext(), Terms_PolicyActivity.class));
                    else if (position == 11)
                        startActivity(new Intent(getContext(), RewardFAQ.class));
                    else if (position == 12)
                        startActivity(new Intent(getContext(), ContactUsActivity.class));
                    else if (position == 13)
                        startActivity(new Intent(getContext(), ManageSocialActivity.class));
                    else if (position == 14)
                        startActivity(new Intent(getContext(), ManageSocialActivity.class));



                }
            });
            // PicassoCache.getPicassoInstance(getActivity()).load(C.ASSET_URL+item.logo).into(holder.brandLogo);
            return row;
        }

    }


    class  MyHolder{
        public TextView categoryName;
        public ImageView icon;
    }


    public class Item{
        public String name;
        public int settingIcon;


        public Item(String name,int settingIcon) {
            this.name = name;
            this.settingIcon = settingIcon;
        }
    }
}
