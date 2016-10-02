package gspot.com.sportify.Model;

import android.util.Log;

import com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import gspot.com.sportify.utils.App;

/**
 * Created by yunfanyang on 5/8/16.
 */
public class SportTypes extends Observable {

    public ArrayList<SportType> sportTypes = new ArrayList<>();
    public ArrayList<String> sports = new ArrayList<>();

    public void readSportTypes() {

        App.dbref.child("Sports").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot child: dataSnapshot.getChildren()) {

                    SportType newPost = child.getValue(SportType.class);
                    newPost.addSID(child.getKey());
                    Log.i("Sport Types", newPost.getName());

                    sportTypes.add(newPost);
                }

                /*To notify the observers we have changed*/
                setChanged();
                notifyObservers();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
