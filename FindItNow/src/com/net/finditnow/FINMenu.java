/**
 * This class displays the menu of buttons
 * each corresponding each category.  Simple options will launch the Map;
 * options with sub-categories will launch CategoryList.
 * 
 * Initial database retrieving routines are also executed before
 * the button grid is drawn.  An Internet connection is also checked.
 * 
 * This is the class that is first shown when FIN is
 * launched, after the splash screen.
 */
package com.net.finditnow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;

public class FINMenu extends Activity {
	
	private static HashMap<GeoPoint, Building> buildingsMap;
	private static HashMap<String, Integer> iconsMap;
	private static ArrayList<String> categories;
	private static ArrayList<String> buildings;
	
	/**
     * Check for a connection, generate our categories and buildings list
     * from the database, and set up the grid layout of buttons.
     */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		
		// Generate our list of categories from the database
		JSONArray listOfCategories = Get.requestFromDB(null, null, null);
		categories = getCategoriesList(listOfCategories);
		Collections.sort(categories);
		
        // Store a map from categories to icons so that other modules can use it
        iconsMap = createIconsList(categories, getApplicationContext());
		
		// Generate list of buildings from the database
		JSONArray listOfBuildings = Get.requestFromDB("", null, null);
		buildingsMap = JsonParser.parseBuildingJson(listOfBuildings.toString());
		buildings = createBuildingList(buildingsMap);
		Collections.sort(buildings);
		
		// Populate the grid with category buttons.
		GridView buttonGrid = (GridView) findViewById(R.id.gridview);
        buttonGrid.setAdapter(new ButtonAdapter(this));
	}
	
	/**
	 * This class populates the grid view.
	 * It is a list of image buttons
	 */
	public class ButtonAdapter extends BaseAdapter {
    	private Context mContext;
    	
    	/**
    	 * Constructor for ButtonAdapter
    	 * @param c The context of the client class.
    	 */
    	public ButtonAdapter(Context c) {
    		setmContext(c);
    	}
    	
    	/**
    	 * The number of items in the list
    	 */
    	public int getCount() {
    		return categories.size();
    	}

    	/**
    	 * Stub method; it doesn't matter what we return.
    	 * @param position
    	 * @return null (default)
    	 */
    	public Object getItem(int position) {
    		return null;
    	}
    	
    	/**
    	 * Stub method; it doesn't matter what we return.
    	 * @param position
    	 * @return 0 (default)
    	 */
    	public long getItemId(int position) {
    		return 0;
    	}

    	/**
    	 * Returns the class's own context
    	 * @return Returns the class's private context
    	 */
		public Context getmContext() {
			return mContext;
		}
    	
    	/**
    	 * Sets up the view shown in each grid cell:
    	 * The image button and the text displayed on top
    	 * @param position The index of the button
    	 * @param convertView The view to be returned
    	 * @param parent The parent ViewGroup that will house the view.
    	 * @return The generated view for this position.
    	 */
    	public View getView(int position, View convertView, ViewGroup parent) {
    		View myView;
			
    		// If not created yet, initialize it.
    		if (convertView == null) {	
    			LayoutInflater li = getLayoutInflater();
    			myView = li.inflate(R.layout.grid_item, null);
    		} else {
    			myView = convertView;
    		}
    		
    		// Add image button
			ImageButton ib = (ImageButton) myView.findViewById(R.id.grid_item_button);
			
			final String category = categories.get(position);
			ib.setImageResource(getBigIcon(category));
			
			if (category.equals("buildings") || category.equals("school_supplies")) {
				// Jump to CategoryList
				ib.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						Intent myIntent = new Intent(v.getContext(), CategoryList.class);
		                myIntent.putExtra("category", category);
		                startActivity(myIntent);
					}
    			});
			} else {
				// Otherwise, jump to map
    			ib.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						Intent myIntent = new Intent(v.getContext(), FINMap.class);
		                myIntent.putExtra("category", category);
		                startActivity(myIntent);
					}
    			});
			}
			
			// Add text above button.
			TextView tv = (TextView) myView.findViewById(R.id.grid_item_text);
	    	tv.setText(FINUtil.capFirstChar(category));
    		
    		return myView;
    	}

		/**
    	 * Sets the passed context to be our own context
    	 * @param mContext
    	 */
		public void setmContext(Context mContext) {
			this.mContext = mContext;
		}
    }
	
	/**
     * Returns an ArrayList of unique buildings from a HashMap of GeoPoints and buildings.
     */
	public static ArrayList<String> createBuildingList(HashMap<GeoPoint, Building> map) {
		ArrayList<String> list = new ArrayList<String>();
		for (GeoPoint point : map.keySet()) {
			list.add(map.get(point).getName());
		}
		return list;
	}
    
	/**
     * Returns a map from categories to icons (icons must be the same lower-case string as the category)
     * Populates the HashMap with both small icons and bigger icons.
     * Key for small icons: "<category>"
     * Key for big icons:  "<category>-big"
	 */
    public static HashMap<String, Integer> createIconsList(ArrayList<String> categories, Context c) {
    	HashMap<String, Integer> iconsMap = new HashMap<String, Integer>();

    	// Loop over each category and map it to the icon file associated with it
    	for (String str : categories) {
			iconsMap.put(str, c.getResources().getIdentifier("drawable/"+str, null, c.getPackageName()));
			iconsMap.put(str + "-big", c.getResources().getIdentifier("drawable/"+str+"_big", null, c.getPackageName()));
		}
    	
		return iconsMap;
    }
    
    /**
     * Returns a large-sized icon associated with the category
     * These icons are used for the menu's grid buttons.
     * 
     * @param category The top-level category
     * @return If no icon is found, return the default Android icon.
     * Otherwise, return the appropriate category icon.
     */
    public static Integer getBigIcon(String category) {
    	int bigIcon = iconsMap.get(category + "-big");
    	if (bigIcon == 0) {
    		return R.drawable.android;
    	} else {
    		return bigIcon;
    	}
    }
	
	/**
     * Returns the building associated with the GeoPoint
     */
    public static Building getBuilding(GeoPoint point) {
    	return buildingsMap.get(point);
    }
	
	/**
     * Returns an ArrayList of buildings
     */
	public static ArrayList<String> getBuildingsList() {
		return buildings;
	}
	

	/**
     * Returns an ArrayList of top-level categories
     */
	public static ArrayList<String> getCategoriesList() {
		return categories;
	}
	
    /**
     * Returns an ArrayList of categories, duplicated from the given JSONArray list.
     * The list is almost exactly the same, with the exclusion of regions, floors,
     * and exception of "school_supplies" (which is added as "supplies").
     * 
     * @param listOfCategories List of categories, in JSONArray form.
     */
	public static ArrayList<String> getCategoriesList(JSONArray listOfCategories) {
		ArrayList<String> category_list = new ArrayList<String>();
		for (int i = 0; i < listOfCategories.length(); i++) {
		    try {
		    	String category = listOfCategories.getString(i);
		    	if (!category.equals("regions") && !category.equals("floors")) {
		    		category_list.add(category);
		    	}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return category_list;
	}

	/**
     * Returns the GeoPoint associated with the building.
     * Icon overlays are positioned on these GeoPoints.
     * 
     * @param buildingName The full name of the building.
     * @return GeoPoint representing the 'center' of the building.
     */
	public static GeoPoint getGeoPointFromBuilding(String buildingName) {
		for (GeoPoint point : buildingsMap.keySet()) {
			if (getBuilding(point).getName().equals(buildingName)) {
				return point;
			}
		}
		return null;
	}

	/**
     * Returns a miniature-sized icon associated with the category
     * These icons are used for the map overlays and dialog windows.
     * 
     * @param category The top-level category
     * @return If no icon is found, return the default Android icon.
	 * otherwise, return the appropriate category icon.
     */
    public static Integer getIcon(String category) {
    	int icon = iconsMap.get(category);
    	if (icon == 0) {
    		return R.drawable.android;
    	} else {
    		return icon;
    	}
    }
    
    /**
     * Creates the Android options menu
     */
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }
    
    /**
     * Expand and define the Android options menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        	case R.id.add_new_button:
        		startActivity(new Intent(this, FINAddNew.class));
        		return true;
	        case R.id.help_button:
	        	startActivity(new Intent(this, FINHelp.class));
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
    
    /**
     * Prepares the options menu before being displayed.
     * Removes redundant Category option, and centering location
     * option (special for the Map only).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.findItem(R.id.categories_button).setVisible(false);
    	menu.findItem(R.id.my_location_button).setVisible(false);
    	return true;
    }
}