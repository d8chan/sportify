package gspot.com.sportify.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;

import gspot.com.sportify.Model.Gathering;

/**
 * Created by amir on 5/6/16.
 * This class provides a hashmap of all
 * the sports that are offered by the app
 * it will have convert a list recieved by
 * the data base to all upper case, and sorted
 */
public class GatheringTypeProvider{

    public static HashMap<String, List<String>> getDataHashMap(String[] _sports) {

        HashMap<String, List<String>> gatheringsHashMap = new HashMap<>();
        char alphabet = 'A';

        /*Sort the sport types alphabetically*/
        Arrays.sort(_sports);

        /*Convert the array to a list*/
        List<String> sports = new ArrayList<>();

        /*populate the list*/
        for(String item: _sports)
            sports.add(item.toUpperCase());


        for(int i = 0; i < sports.size(); i++)
        {
            /*if the 1st char of the sport matches the
            * alpha letter we are on*/
            if(sports.get(i).charAt(0) == alphabet)
            {
                /*is the key in the map ye?*/
                if(!gatheringsHashMap.containsKey(alphabet + ""))
                    /*add the key to the map*/
                    gatheringsHashMap.put(alphabet + "", new ArrayList<String>());

                /*add the port to the list of the key*/
                gatheringsHashMap.get(alphabet + "").add(sports.get(i));
            }
            /*new alphabet*/
            else{
                ++alphabet;
                /*dont skip the sport*/
                --i;
            }
        }

        return gatheringsHashMap;
    }
}
