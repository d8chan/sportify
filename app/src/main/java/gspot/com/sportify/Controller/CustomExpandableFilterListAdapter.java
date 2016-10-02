package gspot.com.sportify.Controller;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;

import gspot.com.sportify.R;

/**
 * Created by amir on 5/1/16.
 */
public class CustomExpandableFilterListAdapter extends BaseExpandableListAdapter {

    /*Define activity context*/
    private Context mContext;

    /*A-Z, list of sports within alphabet letters*/
    private HashMap<String, List<String>> mGatheringType;

    /*ArrayList that is what each key in
     *the above hash map points to*/
    private List<String> mGatheringList;

    /*Hashmap for keeping track of our checkbox check states*/
    private HashMap<Integer, boolean[]> mChildCheckSports;

    /* Our getChildView & getGroupView use the viewholder patter
	 * Here are the viewholders defined, the inner classes are
	 * at the bottom*/
    private ChildViewHolder childViewHolder;
    private GroupViewHolder groupViewHolder;

    /*if all the children are selected or not*/
    private boolean mIsAllSelected;

    public CustomExpandableFilterListAdapter(Context context,
                                             HashMap<String, List<String>> hashMap,
                                             List<String> list) {
        mGatheringType = hashMap;
        mContext = context;
        mGatheringList = list;


        /*Initialize our hashmap containing our check states here*/
        mChildCheckSports = new HashMap<>();

        /*Allocate appropriate memory beforehand so when the user
        * wants everything selected we will have a position for every
        * child allocated bc the 1st the activity starts mChildCheckStates
        * will only contain as many elements that fit on the screen
        * but not all the parent and child elements*/
        for(int i = 0; i < mGatheringList.size(); i++)
        {
            for(int j = 0; j < mGatheringList.get(i).length(); j++){
                mChildCheckSports.put(i, new boolean[getChildrenCount(i)]);
            }
        }
    } //end constructor

    public CustomExpandableFilterListAdapter(Context context,
                                             HashMap<String, List<String>> hashMap,
                                             List<String> list, HashMap<Integer,
                                       boolean[]> previouslyCheckedSports) {
        mGatheringType = hashMap;
        mContext = context;
        mGatheringList = list;
        mChildCheckSports = previouslyCheckedSports;
    } //end constructor

    public HashMap<Integer, boolean[]> getSelectedBooleanHashMap() { return mChildCheckSports; }

    /*
    * getter method for all the clients to use to determine which children were
    * selected within the expandable list view
    * */
    public List<String> getSelectedSports(){

        List<String> selectedSports = new ArrayList<>();

        for(int parent = 0; parent < mChildCheckSports.size(); parent++){
            for(int  child = 0; child < mChildCheckSports.get(parent).length; child++){
                if(mChildCheckSports.get(parent)[child]){
                    String sport = mGatheringType.get(mGatheringList.get(parent)).get(child);
                    selectedSports.add(sport);
                } //end if
            } //end inner for
        }//end outer for
        return selectedSports;
    }

    @Override
    public int getGroupCount() { return mGatheringType.size(); }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mGatheringType.get(mGatheringList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) { return mGatheringList.get(groupPosition); }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mGatheringType.get(mGatheringList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) { return groupPosition; }

    @Override
    public long getChildId(int groupPosition, int childPosition) { return childPosition; }

    @Override
    public boolean hasStableIds() { return false; }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        Log.i("", "groupView");

        String groupTitle = (String)getGroup(groupPosition);

        /*Initialize for the first time*/
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gathering_type_selection_parent, parent, false);

            // Initialize the GroupViewHolder defined at the bottom of this document
            groupViewHolder = new GroupViewHolder();

            groupViewHolder.mGroupText = (TextView) convertView.findViewById(R.id.gathering_type_parent);

            /*Set the tag for the next time this function is called*/
            convertView.setTag(groupViewHolder);
        }//end if

        else{
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        groupViewHolder.mGroupText.setText(groupTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        Log.i("getChildView", "getChildView");

        String childTitle = (String) getChild(groupPosition, childPosition);

        /*set a reference to the positions for the onCheckedChangedListener*/
        final int mGroupPosition = groupPosition;
        final int mChildPosition = childPosition;

        /*Initialize for the first time*/
        if (convertView == null) {

            LayoutInflater inflater =
                    (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gathering_type_selection, parent, false);

            /*Initialize the GroupViewHolder defined at the bottom of this document*/
            childViewHolder = new ChildViewHolder();

            childViewHolder.mChildCheckBox = (CheckBox) convertView.findViewById(R.id.gathering_type);

            /*Set the tag for the next time this function is called*/
            convertView.setTag(R.layout.gathering_type_selection, childViewHolder);
        }
        /*get the tag to the view*/
        else {
            childViewHolder = (ChildViewHolder) convertView.getTag(R.layout.gathering_type_selection);
        }

        /*set the text of the box*/
        childViewHolder.mChildCheckBox.setText(childTitle);

        /*
		 * You have to set the onCheckChangedListener to null
		 * before restoring check states because each call to
		 * "setChecked" is accompanied by a call to the
		 * onCheckChangedListener
		 */
        childViewHolder.mChildCheckBox.setOnCheckedChangeListener(null);

        /*
        * if the hashmap mChildCheckStates<Integer, Boolean[]> contains
        * the value of the parent view (group) of this child (aka, the key),
        * then retrive the boolean array getChecked[]
        */
        boolean getChecked [] = mChildCheckSports.get(groupPosition);

         /* Set the check state of this position's checkbox based on the
          * boolean value of getChecked[]*/
        childViewHolder.mChildCheckBox.setChecked(getChecked[childPosition]);


        /*listener for when a child is clicked on*/
        childViewHolder.mChildCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    boolean getChecked[] = mChildCheckSports.get(mGroupPosition);
                    getChecked[mChildPosition] = isChecked;
                    mChildCheckSports.put(mGroupPosition, getChecked);

                } else {

                    boolean getChecked[] = mChildCheckSports.get(mGroupPosition);
                    getChecked[mChildPosition] = isChecked;
                    mChildCheckSports.put(mGroupPosition, getChecked);
                }
            }
        });

        return convertView;
    }//end getChild view

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) { return false; }

    /* this utility function should be called when the user wants all the
    * children to be checked or unchecked
    * this function sets the mChildCheckStates all the selectAll
    * notifyDataSetChanged is called to have the effects be immediate*/
    public void setAllChildStates(boolean selectAll) {

        /* Set the value for future when the client wants to know
        * which items are selected*/
        mIsAllSelected = selectAll;

        for (int i = 0; i < mChildCheckSports.size(); i++) {
            for (int j = 0; j < mChildCheckSports.get(i).length; j++)
                mChildCheckSports.get(i)[j] = selectAll;
        }

        /*update the screen*/
        /* This function will look at what items are desplayed on the screen
         * at the moment of its call(looks at the row indexes) and calls the
         * getChildView and then the GroupView methods with each item's
         * position that is on the screen
         */
        notifyDataSetChanged();
    } //setAllChildStates

    /*This fucntion will expand all the children in the list*/
    public void expandAllChildren(ExpandableListView view)
    {
        final int SIZE = getGroupCount();
        /*expand each group*/
        for (int i = 0; i < SIZE; i++){
            view.expandGroup(i);
        }
    }

    /*This fucntion will collapse all the children in the list*/
    public void collapseAllChildren(ExpandableListView view)
    {
        final int SIZE = getGroupCount();
        /*collapse each group*/
        for (int i = 0; i < SIZE; i++){
            view.collapseGroup(i);
        }
    }

    /*ViewHolder classes*/
    public final class ChildViewHolder{
        CheckBox mChildCheckBox;
    }

    public final class GroupViewHolder {
        TextView mGroupText;
    }

}
