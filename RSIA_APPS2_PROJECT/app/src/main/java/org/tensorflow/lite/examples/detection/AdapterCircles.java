package org.tensorflow.lite.examples.detection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class AdapterCircles extends ArrayAdapter<Circles> {

    private Context mContext;
    private List<Circles> moviesList;

    public AdapterCircles(Context context, ArrayList<Circles> list) {
        super(context, 0, list);
        mContext = context;
        moviesList = list;
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.item_circulos, parent, false);

        Circles currentMovie = moviesList.get(position);

        ImageView imgVwProfilePic = listItem.findViewById(R.id.imgVwProfileCircle);
        Glide.with(getContext()).load(currentMovie.getPicUrl()).into(imgVwProfilePic);

        TextView name = listItem.findViewById(R.id.txtVwNombre);
        name.setText(currentMovie.getName());

        TextView ape = listItem.findViewById(R.id.txtVwApellido);
        ape.setText(currentMovie.getLastname());


        return listItem;
    }

}