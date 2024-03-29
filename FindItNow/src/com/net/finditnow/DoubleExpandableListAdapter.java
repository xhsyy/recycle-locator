package com.net.finditnow;
/***
 * DoubleExpandableListAdapter by Chanel Huang
 * Version 1.0
 * 
 * This is a customer adapter for the building a scrollabel expandable list 
 * of expandalbe to display the floor details of multiple category.
 * 
 * TODO: all the comments in this class need to be updated
 * 		modifies so it scroll to the current position when clicked onExpand.
 * 
 */
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DoubleExpandableListAdapter extends BaseExpandableListAdapter {

	//data for populating the list
	private Context context;			
	private Building building;				
	private String[] categories;
	private HashMap<String,CategoryItem> dataMap;

	public static int HEIGHT = 45;
	public static boolean set = false;
	/**
	 * Creates a new FloorExpandableListAdapter with each variable initialized
	 * 
	 * @param context - the context in which the list will be displayed
	 * @param floorName - the list of floor names
	 * @param info - the additional information associated
	 * @param iconId - the resource id of the icon of the category
	 * @param category - the category currently displaying
	 */
	public DoubleExpandableListAdapter(Context context,Building building,  String[] categories,HashMap<String,CategoryItem> dataMap) {
		super();
		this.context = context;
		this.building = building;
		this.categories = categories;
		this.dataMap = dataMap;
	}

	/**
	 * Gets the data associated with the given child within the given group.
	 * 
	 * @param groupPosition - the position of the parent view
	 * @param childPosition - the position of the child view
	 * @return the data of the child 
	 */
	public Object getChild(int groupPosition, int childPosition) {
		return dataMap.get(categories[groupPosition]).getInfo().get(childPosition);
	}
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}
	/**
	 * Gets the number of children in a specified group.
	 * 
	 * @param groupPosition - the position of the group that contains the child
	 * @return the children count in the specified group 
	 */
	public int getChildrenCount(int groupPosition) {
		return categoryOf(building.getFloorNames()[groupPosition]).length;
	}

	private String[] categoryOf(String floor)
	{
		ArrayList<String> temp = new ArrayList<String>();
		for (String cate: dataMap.keySet()){
			if (dataMap.get(cate).getFloor_names().contains(floor))
				temp.add(cate);
		}
		String[] result = new String[temp.size()];
		result = temp.toArray(result);
		return result;
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
		View relative = LayoutInflater.from(context).inflate(R.layout.multi_category_list, parent,false);

		//This is the text for any additional information associated with this
		// particular object
		ExpandableListView lv = (ExpandableListView) relative.findViewById(R.id.cateList);

		String[] parentText = { building.getFloorNames()[groupPosition]};
		String category = categoryOf(parentText[0])[childPosition];
		String dbCategory = FINHome.isItem(category) ? FINHome.getCategoryFromItem(category) : category;
		String item = FINHome.isItem(category)? dbCategory : "";

		String parentMode = "categoryView";
		relative.getLayoutParams().height= HEIGHT;

		lv.setOnGroupExpandListener(new DoubleOnExpandListener(relative,parent));
		lv.setOnGroupCollapseListener(new DoubleOnCollapseListener(relative));
		lv.setAdapter(new FloorExpandableListAdapter( context, dataMap.get(category),
				category,  dbCategory, item,  parentText,  parentMode) );

		return relative;
	}
	/**
	 * Gets the data associated with the given group.
	 * 
	 * @param groupPosition - the position of the group that contains the child
	 * @return the data child for the specified group 
	 */
	public Object getGroup(int groupPosition) {
		return building.getFloorNames()[groupPosition];
	}

	/**
	 * Gets the number of groups.
	 * 
	 * @return the number of groups
	 */
	public int getGroupCount() {
		return building.getFloorNames().length;
	}

	//The following methods are suppose to be override, but is not
	// of importance here, so they contain no meaningful results
	public long getGroupId(int groupPosition) {
		return 0;
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
		View  relative = LayoutInflater.from(context).inflate(R.layout.flrlist_item, parent,false);

		//Text for displaying the floor name
		TextView text = (TextView) relative.findViewById(R.id.flrName);
		text.setText(building.getFloorNames()[groupPosition]);

		//the icon comes later :)
		LinearLayout layout = ((LinearLayout) relative.findViewById(R.id.iconView));
		String[] icons = categoryOf(building.getFloorNames()[groupPosition]);
		for (int i = 0; i < icons.length; i++){
			ImageView imageView = new ImageView(context);
			imageView.setLayoutParams(new LinearLayout.LayoutParams(40, 40));
			imageView.setPadding(0,12,0,0);

			String cat = FINHome.isItem(icons[i])? FINHome.getCategoryFromItem(icons[i]) : icons[i];
			imageView.setImageResource(FINHome.getIcon(cat));

			layout.addView(imageView);
		}

		return relative;
	}

	public boolean hasStableIds() {
		return false;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	private class DoubleOnExpandListener implements ExpandableListView.OnGroupExpandListener{
		private View currentView ;
		private ViewGroup parent;
		public DoubleOnExpandListener(View view,ViewGroup parent){
			currentView = view;
			this.parent = parent;
		}
		public void onGroupExpand(int groupPosition) {

			ExpandableListView lv = (ExpandableListView) currentView.findViewById(R.id.cateList);


			View  relative = LayoutInflater.from(context).inflate(R.layout.flrlist_child, parent,false);

			float density = relative.getResources().getDisplayMetrics().density;
			String detialText = (String)lv.getExpandableListAdapter().getChild(groupPosition, 0);

			String[] lines = detialText.split("<br />");
			int detialHeight = 0;
			for (String line: lines){
				detialHeight += (Math.round(15* density + 0.5f)) * (1+ (line.length() / 35));
			}

			int paddings = relative.findViewById(R.id.layout_rootflr).getPaddingBottom()+relative.findViewById(R.id.layout_rootflr).getPaddingTop();

			TextView butt = (TextView) relative.findViewById(R.id.flrChildLayout).findViewById(R.id.flrDetailButton);
			int buttHeight = butt.getPaddingBottom()+butt.getPaddingTop();
			buttHeight += Math.round((20+3+3) * density + 0.5f);

			currentView.getLayoutParams().height = HEIGHT+(paddings+buttHeight+detialHeight);

			lv.setSelectedGroup(groupPosition);		
		}

	}
	private class DoubleOnCollapseListener implements ExpandableListView.OnGroupCollapseListener{
		private View currentView ;
		public DoubleOnCollapseListener(View view){
			currentView = view;
		}
		public void onGroupCollapse(int groupPosition) {
			currentView.getLayoutParams().height = HEIGHT;
		}

	}
}
