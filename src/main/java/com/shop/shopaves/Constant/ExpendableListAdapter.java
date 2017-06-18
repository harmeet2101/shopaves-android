package com.shop.shopaves.Constant;

import java.util.HashMap;
import java.util.List;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shop.shopaves.R;
import com.squareup.picasso.Picasso;


public class ExpendableListAdapter extends BaseExpandableListAdapter{
	private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
    String imgs[];
//	int booking_imgs[]={R.drawable.resevationricon,R.drawable.favicon,R.drawable.historyicon};
	//int listings[]={R.drawable.adlisticon,R.drawable.menulisticon};
//	int more[]={R.drawable.menurateicon,R.drawable.menushareicon,/*R.drawable.menusupporticon,*/R.drawable.menuhelpicon,R.drawable.menutermicon,R.drawable.menuabouticon,R.drawable.menulogouticon};


    public ExpendableListAdapter(Context context, List<String> listDataHeader,
            HashMap<String, List<String>> listChildData,String [] MENU_ICON) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this.imgs = MENU_ICON;
    }



    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }
 
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
 
    public View getChildView(int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
 
        final String childText = (String) getChild(groupPosition, childPosition);
 
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }
        

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);
 
        ImageView img = (ImageView)convertView.findViewById(R.id.childimg);
        txtListChild.setText(childText);
        
       /* if(groupPosition == 1)
        	img.setImageResource(this.booking_imgs[childPosition]);
        if(groupPosition == 2)
         	img.setImageResource(this.booking_imgs[childPosition]);*/
       // if(groupPosition == 5)
           //	img.setImageResource(this.more[childPosition]);
        
       
        return convertView;
    }

 
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }
 
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.listgroup, null);
        }
   
		TextView lblListHeader = (TextView) convertView
				.findViewById(R.id.lblListHeader);
		ImageView img = (ImageView) convertView.findViewById(R.id.listimg);
		ImageView arrow_img = (ImageView) convertView.findViewById(R.id.arrow);

        if(groupPosition<imgs.length)
		//img.setImageResource(this.imgs[groupPosition]);
         //Picasso.with(_context).load(C.ASSET_URL).error(R.mipmap.nophoto).placeholder(R.mipmap.nophoto).into(profile_picture);
         Picasso.with(_context).load(C.ASSET_URL+imgs[groupPosition]).into(img);

		lblListHeader.setText(headerTitle);
		//arrow_img.setImageResource(R.drawable.rightarrow);
		
		//if(isExpanded)
		//	arrow_img.setImageResource(R.drawable.downarrow);
		
		
		
		/*if((groupPosition == 1) || (groupPosition == 2) || (groupPosition == 5))
			arrow_img.setVisibility(View.VISIBLE);
		else
			arrow_img.setVisibility(View.GONE);*/
		
		
		return convertView;
    }
 
    public boolean hasStableIds() {
        return false;
    }
 
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

   
}
