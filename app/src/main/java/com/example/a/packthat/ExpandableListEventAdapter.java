package com.example.a.packthat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ani Thomas on 12/6/2016.
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
        EventListItem tempListItem = (EventListItem) getChild(groupPosition, childPosition);
        final String childText = tempListItem.eliName;
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.expandable_list_event_item, null);
        }

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
        EventList temp = (EventList) getGroup(groupPosition);
        String headerTitle = temp.eventListName;
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandable_list_event, null);
        }
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
