package gspot.com.sportify.Model;
import com.fasterxml.jackson.annotation.*;
import com.firebase.client.Firebase;

import gspot.com.sportify.utils.App;

/**
 * Created by yunfanyang on 5/1/16.
 */
public class SportType {

    @JsonIgnore
    private boolean isChecked;

    @JsonIgnore
    private String mSID;

    @JsonProperty("name")
    private String mName;

    @JsonProperty("description")
    private String mDescription;

    public void addSID(String id)
    {
        mSID = id;
    }

    public void addName(String name)
    {
        mName = name;
    }
    public void addDescription(String desc)
    {
        mDescription = desc;
    }

    /**
     * A function to create a new sport type.
     * For test only.
     * !This function should be removed from production code, and client side shouldn't
     *  have the permission to create a new sport. Also Sports node in the firebase should
     *  only have read permission.
     * @param name
     * @param desc
     * @return
     */
    public static SportType createASportType(String name, String desc)
    {
        SportType sportType = new SportType();
        sportType.mName = name;
        sportType.mDescription = desc;
        Firebase sports = App.dbref.child("Sports").push();
        sports.setValue(sportType);
        sportType.addSID(sports.getKey());
        return sportType;
    }

    public String getName() {
        return mName;
    }
}