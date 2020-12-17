package org.tensorflow.lite.examples.detection;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;



public class FirebaseDatabaseControl {

    private static Query mQuery;
    private static FirebaseDatabase firebaseDatabase;
    private static DatabaseReference databaseReference;
    private static AdapterRoutine adapterRoutine;
    private static AdapterObject adapterObject;
    private static ArrayList<RoutineObject> mAdapterItems;
    private static ArrayList<ObjectDetected> mAdapterItemsObj;
    private static ArrayList<String> mAdapterKeys;

    public static DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public static void setUpDataBase() {
        if (firebaseDatabase == null) {
            firebaseDatabase = FirebaseDatabase.getInstance();
            //firebaseDatabase.setPersistenceEnabled(true);
            databaseReference = firebaseDatabase.getReference();
        }
    }

    public static Query getQuery(String userId) {
        mQuery = databaseReference.child("Users").child("Customers").child(userId).child("Rutinas");
        return mQuery;
    }

    public static Query getQueryobj(String userId) {
        mQuery = databaseReference.child("Users").child("Customers").child(userId).child("objetos");
        return mQuery;
    }

    public static AdapterRoutine Adapter(String userId) {
        if (adapterRoutine == null) {
            adapterRoutine = new AdapterRoutine(getQuery(userId), RoutineObject.class, mAdapterItems, mAdapterKeys);
            return adapterRoutine;
        } else {
            return adapterRoutine;
        }
    }

    public static AdapterObject Adapter2(String userId) {
        if (adapterObject == null) {
            adapterObject = new AdapterObject(getQueryobj(userId), ObjectDetected.class, mAdapterItemsObj, mAdapterKeys);
            return adapterObject;
        } else {
            adapterObject = new AdapterObject(getQueryobj(userId), ObjectDetected.class, mAdapterItemsObj, mAdapterKeys);
            return adapterObject;
        }
    }



}
