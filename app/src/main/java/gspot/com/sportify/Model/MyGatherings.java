package gspot.com.sportify.Model;


import gspot.com.sportify.Controller.LoginActivity;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by Aaron on 5/8/2016.
 */
public class MyGatherings {

    String mListOwner;
    //List<String> mGatherings;
    //String mGatherings;


    public MyGatherings(){
    }

    public MyGatherings(String mListOwner){
        this.mListOwner = mListOwner;
        //this.mGatherings = new ArrayList<String>();
        //this.mGatherings = mGatherings;

        //mGatherings.add("TEST");
        //mGatherings.add("test2");
    }

    public String getmListOwner()
    {
        return mListOwner;
    }

    /*public List getMGathering() {
        return mGatherings;
    }*/

    /*public void addGathering(String sportUID)
    {
        mGatherings.add(sportUID);
    }*/


}
