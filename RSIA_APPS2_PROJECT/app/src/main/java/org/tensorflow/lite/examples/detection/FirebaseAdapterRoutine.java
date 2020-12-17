package org.tensorflow.lite.examples.detection;

import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Created by Matteo on 24/08/2015.
 * <p/>
 * This class is a generic way of backing an Android RecyclerView with a Firebase location.
 * It handles all of the child events at the given Firebase location.
 * It marshals received data into the given class type.
 * Extend this class and provide an implementation of the abstract methods, which will notify when
 * the adapter list changes.
 * <p/>
 * This class also simplifies the management of configuration change (e.g.: device rotation)
 * allowing the restore of the list.
 *
 * @param <T> The class type to use as a model for the data contained in the children of the
 *            given Firebase location
 */
public abstract class FirebaseAdapterRoutine<ViewHolder extends RecyclerView.ViewHolder, T>
        extends RecyclerView.Adapter<ViewHolder> {

    private Query mQuery;
    private Class<RoutineObject> mItemClass;
    private ArrayList<RoutineObject> mItems;
    private ArrayList<String> mKeys;

    /**
     * @param query     The Firebase location to watch for data changes.
     *                  Can also be a slice of a location, using some combination of
     *                  <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>.
     * @param itemClass The class of the items.
     */
    public FirebaseAdapterRoutine(Query query, Class<RoutineObject> itemClass) {
        this(query, itemClass, null, null);
    }

    /**
     * @param query     The Firebase location to watch for data changes.
     *                  Can also be a slice of a location, using some combination of
     *                  <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>.
     * @param itemClass The class of the items.
     * @param items     List of items that will load the adapter before starting the listener.
     *                  Generally null or empty, but this can be useful when dealing with a
     *                  configuration change (e.g.: reloading the adapter after a device rotation).
     *                  Be careful: keys must be coherent with this list.
     * @param keys      List of keys of items that will load the adapter before starting the listener.
     *                  Generally null or empty, but this can be useful when dealing with a
     *                  configuration change (e.g.: reloading the adapter after a device rotation).
     *                  Be careful: items must be coherent with this list.
     */
    public FirebaseAdapterRoutine(Query query, Class<RoutineObject> itemClass,
                                  @Nullable ArrayList<RoutineObject> items,
                                  @Nullable ArrayList<String> keys) {
        this.mQuery = query;
        if (items != null && keys != null) {
            this.mItems = items;
            this.mKeys = keys;
            Log.d("KEY", keys + items.toString());
        } else {
            mItems = new ArrayList<RoutineObject>();
            mKeys = new ArrayList<String>();

        }
        this.mItemClass = itemClass;
        query.addChildEventListener(mListener);
    }

    private ChildEventListener mListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            String key = dataSnapshot.getKey();
            if (!mKeys.contains(key)) {
                RoutineObject item = dataSnapshot.getValue(FirebaseAdapterRoutine.this.mItemClass);
                //item.setReferenciaDB(key);
                int insertedPosition;
                if (previousChildName == null) {
                    mItems.add(0, item);
                    mKeys.add(0, key);
                    insertedPosition = 0;
                } else {
                    int previousIndex = mKeys.indexOf(previousChildName);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == mItems.size()) {
                        mItems.add(item);
                        Collections.sort(mItems, new Comparator<RoutineObject>() {
                            @Override
                            public int compare(RoutineObject o1, RoutineObject o2) {
                                return o1.getmHourIni().compareTo(o2.getmHourIni());
                            }
                        });
                        mKeys.add(mItems.indexOf(item), key);
                    } else {
                        mItems.add(nextIndex, item);
                        Collections.sort(mItems, new Comparator<RoutineObject>() {
                            @Override
                            public int compare(RoutineObject o1, RoutineObject o2) {
                                return o1.getmHourIni().compareTo(o2.getmHourIni());
                            }
                        });
                        mKeys.add(mItems.indexOf(item), key);
                    }
                    insertedPosition = nextIndex;
                }
                int position = mItems.indexOf(item);
                notifyItemInserted(position);
                itemAdded(item, key, position);
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String key = dataSnapshot.getKey();

            if (mKeys.contains(key)) {
                int index = mKeys.indexOf(key);
                RoutineObject oldItem = mItems.get(index);
                RoutineObject newItem = dataSnapshot.getValue(FirebaseAdapterRoutine.this.mItemClass);

                mItems.set(index, newItem);

                notifyItemChanged(index);
                itemChanged(oldItem, newItem, key, index);
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String key = dataSnapshot.getKey();

            if (mKeys.contains(key)) {
                int index = mKeys.indexOf(key);
                RoutineObject item = mItems.get(index);

                mKeys.remove(index);
                mItems.remove(index);

                notifyItemRemoved(index);
                itemRemoved(item, key, index);
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            String key = dataSnapshot.getKey();

            int index = mKeys.indexOf(key);
            RoutineObject item = dataSnapshot.getValue(FirebaseAdapterRoutine.this.mItemClass);
            mItems.remove(index);
            mKeys.remove(index);
            int newPosition;
            if (previousChildName == null) {
                mItems.add(0, item);
                mKeys.add(0, key);
                newPosition = 0;
            } else {
                int previousIndex = mKeys.indexOf(previousChildName);
                int nextIndex = previousIndex + 1;
                if (nextIndex == mItems.size()) {
                    mItems.add(item);
                    mKeys.add(key);
                } else {
                    mItems.add(nextIndex, item);
                    mKeys.add(nextIndex, key);
                }
                newPosition = nextIndex;
            }
            notifyItemMoved(index, newPosition);
            itemMoved(item, key, index, newPosition);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }


    };

    protected FirebaseAdapterRoutine() {
    }

    @Override
    public abstract ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(ViewHolder holder, final int position);

    @Override
    public int getItemCount() {
        return (mItems != null) ? mItems.size() : 0;
    }

    /**
     * Clean the adapter.
     * ALWAYS call this method before destroying the adapter to remove the listener.
     */
    public void destroy() {
        mQuery.removeEventListener(mListener);
    }

    /**
     * Returns the list of items of the adapter: can be useful when dealing with a configuration
     * change (e.g.: a device rotation).
     * Just save this list before destroying the adapter and pass it to the new adapter (in the
     * constructor).
     *
     * @return the list of items of the adapter
     */
    public ArrayList<RoutineObject> getItems() {
        return mItems;
    }

    /**
     * Returns the list of keys of the items of the adapter: can be useful when dealing with a
     * configuration change (e.g.: a device rotation).
     * Just save this list before destroying the adapter and pass it to the new adapter (in the
     * constructor).
     *
     * @return the list of keys of the items of the adapter
     */
    public ArrayList<String> getKeys() {
        return mKeys;
    }

    /**
     * Returns the item in the specified position
     *
     * @param position Position of the item in the adapter
     * @return the item
     */
    public RoutineObject getItem(int position) {
        return mItems.get(position);
    }

    /**
     * Returns the position of the item in the adapter
     *
     * @param item Item to be searched
     * @return the position in the adapter if found, -1 otherwise
     */
    public int getPositionForItem(RoutineObject item) {
        return mItems != null && mItems.size() > 0 ? mItems.indexOf(item) : -1;
    }

    /**
     * Check if the searched item is in the adapter
     *
     * @param item Item to be searched
     * @return true if the item is in the adapter, false otherwise
     */
    public boolean contains(RoutineObject item) {
        return mItems != null && mItems.contains(item);
    }

    /**
     * ABSTRACT METHODS THAT MUST BE IMPLEMENTED BY THE EXTENDING ADAPTER.
     */

    /**
     * Called after an item has been added to the adapter
     *
     * @param item     Added item
     * @param key      Key of the added item
     * @param position Position of the added item in the adapter
     */
    protected abstract void itemAdded(RoutineObject item, String key, int position);

    /**
     * Called after an item changed
     *  @param oldItem  Old version of the changed item
     * @param newItem  Current version of the changed item
     * @param key      Key of the changed item
     * @param position Position of the changed item in the adapter
     */
    protected abstract void itemChanged(RoutineObject oldItem, RoutineObject newItem, String key, int position);

    /**
     * Called after an item has been removed from the adapter
     *  @param item     Removed item
     * @param key      Key of the removed item
     * @param position Position of the removed item in the adapter
     */
    protected abstract void itemRemoved(RoutineObject item, String key, int position);

    /**
     * Called after an item changed position
     *
     * @param item        Moved item
     * @param key         Key of the moved item
     * @param oldPosition Old position of the changed item in the adapter
     * @param newPosition New position of the changed item in the adapter
     */
    protected abstract void itemMoved(RoutineObject item, String key, int oldPosition, int newPosition);

}
