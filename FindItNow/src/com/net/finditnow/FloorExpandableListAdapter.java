package com.net.finditnow;
/***
 * FloorExpandableListAdapter by Chanel Huang
 * Version 1.0
 * 
 * This is a customer adapter for the building a scrollabel list
 * to display the floor details.
 * 
 */
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;
import android.text.Html;
import android.view.LayoutInflater;
import android.util.Log;


public class FloorExpandableListAdapter extends BaseExpandableListAdapter {
	
	//data for populating the list
	private String[] floorNames;	//names of the floor
	private String info;			//information associated
	private Context context;			
	private int iconId;				//id of the category's icon
	
	/**
	 * Creates a new FloorExpandableListAdapter with each variable initialized
	 * 
	 * @param context - the context in which the list will be displayed
	 * @param floorName - the list of floor names
	 * @param info - the additional information associated
	 * @param iconId - the resource id of the icon of the category
	 */
	public FloorExpandableListAdapter(Context context,String[] floorName, String info,
			int iconId) {
		super();
		this.context = context;
		this.floorNames = floorName;
		this.info = info;
		this.iconId = iconId;
	}
	
	/**
	 * Gets the data associated with the given child within the given group.
	 * 
	 * @param groupPosition - the position of the parent view
	 * @param childPosition - the position of the child view
	 * @return the data of the child 
	 */
	public Object getChild(int groupPosition, int childPosition) {
		return info;
	}

	/**
	 * Gets a View that displays the data for the given child within the given group.
	 * 
	 * @param groupPosition - the position of the group that contains the child
	 * @param childPosition - the position of the child (for which the View is returned) within the group
	 * @param isLastChild - Whether the child is the last child within the group
	 * @param convertView - the old view to reuse, if possible
	 * @param parent - the parent that this view will eventually be attached to
	 * 
	 * @return the View corresponding to the child at the specified position 
	 */
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
		//the layout/view which is defined by a layout XML
		View relative = LayoutInflater.from(context).inflate(R.layout.flrlist_child, parent,false);

		//This is the text for any additional information associated with this
		// particular object
		TextView text = (TextView) relative.findViewById(R.id.floorDetailText);
		String specialInfo = "";
		if (info != null)
			specialInfo = info.replace("\n", "<br />");

		//sets it to the text field
		text.setText(Html.fromHtml(specialInfo));
		
		//This is the button for reporting something to be missing
		Button button = (Button) relative.findViewById(R.id.flrDetailButton);
		button.setOnClickListener( new View.OnClickListener()
    	{
    		public void onClick(View v)
    		{
    			//pops a Dialog to confirm the user's intent
    			AlertDialog.Builder builder = new AlertDialog.Builder(context);
    			builder.setMessage("This is not actually connect to update and don't have"
    						+ " the object id yet, but!!\n"
    						+ "Are you sure that this is not here?");
    			builder.setCancelable(false);
    			//confirms the action and perform the update accordingly 
    			builder.setPositiveButton("Yes! I am sure.", new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
   		                //sents info to update.php here
    		                dialog.dismiss();
    		           }
    		       });
    			//cancels the action if the user didn't mean to do it
    			builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
    		                dialog.cancel();
    		           }
    		       });

    			AlertDialog dailog = builder.create();
    			dailog.show();
    		}
    	});
		
		return relative;
	}

	/**
	 * Gets the number of children in a specified group.
	 * 
	 * @param groupPosition - the position of the group that contains the child
	 * @return the children count in the specified group 
	 */
	public int getChildrenCount(int groupPosition) {
		//every group (parent) only has 1 child, which is the information display.
		return 1;
	}

	/**
	 * Gets the data associated with the given group.
	 * 
	 * @param groupPosition - the position of the group that contains the child
	 * @return the data child for the specified group 
	 */
	public Object getGroup(int groupPosition) {
		return floorNames[groupPosition];
	}
	/**
	 * Gets the number of groups.
	 * 
	 * @return the number of groups
	 */
	public int getGroupCount() {
		return floorNames.length;
	}

	/**
	 * Gets a View that displays the given group.
	 * @param groupPosition - the position of the group for which the View is returned
	 * @param isExpanded - whether the group is expanded or collapsed
	 * @param convertView - the old view to reuse, if possible. 
	 * @param parent - the parent that this view will eventually be attached to
	 * 
	 * @return the View corresponding to the group at the specified position 
	 */
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		
		//the layout/view which is defined by a layout XML
		View relative = LayoutInflater.from(context).inflate(R.layout.flrlist_item, parent,false);
				
		//Text for displaying the floor name
		TextView text = (TextView) relative.findViewById(R.id.flrName);
		text.setText(floorNames[groupPosition]);
		
		//the icon associated with the category
		ImageView img = (ImageView) relative.findViewById(R.id.flrIcon);
		img.setImageResource(iconId);
		
		return relative;
	}

	//The following methods are suppose to be override, but is not
	// of importance here, so they contain no meaningful results
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}

	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}
}