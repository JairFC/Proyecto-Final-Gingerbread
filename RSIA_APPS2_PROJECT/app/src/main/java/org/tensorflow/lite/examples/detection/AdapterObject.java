package org.tensorflow.lite.examples.detection;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.Query;

import org.tensorflow.lite.examples.detection.FirebaseAdapterObject;
import org.tensorflow.lite.examples.detection.FirebaseAdapterRoutine;
import org.tensorflow.lite.examples.detection.ObjectDetected;
import org.tensorflow.lite.examples.detection.R;


import java.util.ArrayList;


/**
 * Created by Matteo on 24/08/2015.
 */
public class AdapterObject extends FirebaseAdapterObject<AdapterObject.ViewHolder, AdapterObject> {

    /**
     * @param query     The Firebase location to watch for data changes.
     *                  Can also be a slice of a location, using some combination of
     *                  <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>.
     * @param itemClass The class of the items.
     */
    public AdapterObject(Query query, Class<ObjectDetected> itemClass) {
        super(query, itemClass);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        public TextView tvFideli;
        public CardView cardView;

        public ViewHolder(View view) {
            super(view);
            view.setClickable(true);

            tvTitle = view.findViewById(R.id.tvObjectTitle);
            tvFideli = view.findViewById(R.id.tvFidelidad);
            cardView = view.findViewById(R.id.cardViewObject);

        }
    }

    public AdapterObject(Query query, Class<ObjectDetected> itemClass, @Nullable ArrayList<ObjectDetected> items,
                         @Nullable ArrayList<String> keys) {
        super(query, itemClass, items, keys);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_object, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        ObjectDetected EmergencyObject = getItem(position);

        holder.tvTitle.setText(EmergencyObject.getName());
        holder.tvFideli.setText(EmergencyObject.getFidelidad());

    }

    @Override
    protected void itemAdded(ObjectDetected item, String key, int position) {
    }

    @Override
    protected void itemChanged(ObjectDetected oldItem, ObjectDetected newItem, String key, int position) {
    }

    @Override
    protected void itemRemoved(ObjectDetected item, String key, int position) {
    }

    @Override
    protected void itemMoved(ObjectDetected item, String key, int oldPosition, int newPosition) {
    }
}