package com.example.a.packthat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v4.util.LruCache;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * My custom expandable list adapter to handle event lists and their items.
 * All functions are required to extend from baseExpandableListAdapter
 */
public class ExpandableListEventAdapter extends BaseExpandableListAdapter {
    private Activity context;
    private ArrayList<EventList> listDataHeader;
    private HashMap<EventList, List<EventListItem>> listDataChild;

    public ExpandableListEventAdapter(Activity context, ArrayList<EventList> listEventHeaders
            , HashMap<EventList, List<EventListItem>> listChildData){
        this.context = context;
        this.listDataHeader = listEventHeaders;
        this.listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition){
        return listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition){
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final EventListItem currListItem = (EventListItem) getChild(groupPosition, childPosition);
        final String childText = currListItem.eliName;
        LayoutInflater inflater = context.getLayoutInflater();
        Button button;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.expandable_list_event_item, null);
            button = (Button) convertView.findViewById(R.id.button_complete_item);
            convertView.setTag(button);
        }else{
            button = (Button) convertView.getTag();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    RequestQueue requestQueue = Volley.newRequestQueue(context);
                    JSONObject params = new JSONObject();
                    params.put("eliId", currListItem.eliId);
                    params.put("completedBy", User.ProfileImg);
                    String url = "http://webdev.cs.uwosh.edu/students/thomaa04/PackThatLiveServer/completeEventListItem.php";
                    JsonObjectRequest jsObjRequest = new JsonObjectRequest
                            (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        int affected = response.getInt("affected");
                                        if(affected > 0){
                                            currListItem.eliCompletedBy = User.ProfileImg;
                                            notifyDataSetChanged();
                                        }
                                    } catch (Exception ex) {
                                        System.out.println(ex.toString());
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.i("ELEA", Arrays.toString(error.getStackTrace()));
                                    System.out.println("Error");
                                }
                            });
                    requestQueue.add(jsObjRequest);
                } catch (Exception e) {
                    Log.i("ExpandableListAdapter", Arrays.toString(e.getStackTrace()));
                    Toast.makeText(context, "Error creating new event.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        NetworkImageView completedByImage = (NetworkImageView) convertView.findViewById(R.id.imageView_completedBy);
        RequestQueue requestQueue;
        ImageLoader imageLoader;
        requestQueue = Volley.newRequestQueue(context);
        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache(){
            private final LruCache<String, Bitmap> cache = new LruCache<>(10);
            public void putBitmap(String url, Bitmap bitmap){ cache.put(url, bitmap); }
            public Bitmap getBitmap(String url) {return cache.get(url); }
        });

        completedByImage.setImageUrl("http://webdev.cs.uwosh.edu/students/thomaa04/PackThatLiveServer/uploads/" + currListItem.eliCompletedBy, imageLoader);
        TextView txtListChild = (TextView) convertView.findViewById(R.id.textView_event_list_body);
        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listDataChild.get(listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final EventList currEventList = (EventList) getGroup(groupPosition);
        String headerTitle = currEventList.eventListName;
        Button button;
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandable_list_event, null);
            button = (Button) convertView.findViewById(R.id.button_add_event_list_item);
            convertView.setTag(button);
        }else{
            button = (Button) convertView.getTag();
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = context.getLayoutInflater();
                View addEventListLayout = inflater.inflate(R.layout.dialog_add_list, null);
                final EditText addEventListItemEdit = (EditText) addEventListLayout.findViewById(R.id.editText_add_event_List);
                final Button addEventListItem = (Button) addEventListLayout.findViewById(R.id.button_add_event_list);
                TextView listLabel = (TextView) addEventListLayout.findViewById(R.id.textView_add_event_list);
                listLabel.setText("Item Name");

                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Add Item");
                alert.setView(addEventListLayout);
                alert.setCancelable(true);

                final AlertDialog dialog = alert.create();
                addEventListItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String listItemName = addEventListItemEdit.getText().toString();
                        if(!listItemName.equals("")) {
                            try {
                                RequestQueue requestQueue = Volley.newRequestQueue(context);
                                JSONObject params = new JSONObject();
                                params.put("eventListId", currEventList.eventListId);
                                params.put("listItemName", listItemName);
                                String url = "http://webdev.cs.uwosh.edu/students/thomaa04/PackThatLiveServer/addEventListItem.php";
                                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                                        (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    int tempListId = response.getInt("id");
                                                    String tempEventListItemName = response.getString("name");
                                                    EventListItem newEvent = new EventListItem(tempEventListItemName, tempListId, "0.jpg");
                                                    currEventList.eventListItems.add(newEvent);
                                                    notifyDataSetChanged();
                                                    dialog.dismiss();
                                                } catch (Exception ex) {
                                                    System.out.println(ex.toString());
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Log.i("HomeFragment", Arrays.toString(error.getStackTrace()));
                                                System.out.println("Error");
                                            }
                                        });
                                requestQueue.add(jsObjRequest);
                            } catch (Exception e) {
                                Log.i("HomeFragment", Arrays.toString(e.getStackTrace()));
                                Toast.makeText(context, "Error creating new event.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                dialog.show();
            }
        });
        TextView item  = (TextView) convertView.findViewById(R.id.textView_event_list_header);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(headerTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
