package com.example.earthquake;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class detailsadapter extends ArrayAdapter {
    public detailsadapter(Activity context, ArrayList<details> items){
        super(context,0,items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

//        Log.v("main", "Details Adapter\n");

        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        details currentdetail = (details) getItem(position);

        TextView magnitude = (TextView) listItemView.findViewById(R.id.magnitude);
        // made the magnitude in format of 1 Decimal value
        String formattedmagntiude = formatMagnitude(currentdetail.getMagnitude());
        magnitude.setText(formattedmagntiude);

        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitude.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(currentdetail.getMagnitude());

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);

        String rawplace = currentdetail.getPlace();
        String placeoffset;
        String placelocation;

        TextView placeOffset = (TextView) listItemView.findViewById(R.id.placeoffset);
        TextView placeLocation = (TextView) listItemView.findViewById(R.id.placelocation);
        if (rawplace.indexOf(" of ") != -1){
            placeoffset = rawplace.substring(0, rawplace.indexOf(" of ") + 4);
            placelocation = rawplace.substring(rawplace.indexOf(" of ") + 4);
            placeOffset.setText(placeoffset);
            placeLocation.setText(placelocation);
        } else {
            placeOffset.setText("Near By");
            placeLocation.setText(rawplace);
        }
//        place.setText( currentdetail.getPlace());

        // creating new date object
        Date dateobject = new Date(currentdetail.getTime());

        TextView date = (TextView) listItemView.findViewById(R.id.date);
        // extracting date from given time in millisecond
        String formatteddate = formatDate(dateobject);
        date.setText(formatteddate);

        TextView time = (TextView) listItemView.findViewById(R.id.time);
        // extracting time from given time in millisecond
        String formattedtime = formatTime(dateobject);
        time.setText(formattedtime);


        return listItemView;
    }

    private String formatMagnitude(double magnitude) {
        DecimalFormat magnitudeFormat = new DecimalFormat("0.0");
        return magnitudeFormat.format(magnitude);
    }

    private String formatTime(Date dateobject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateobject);
    }

    private String formatDate(Date dateobject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM DD, YYYY");
        return dateFormat.format(dateobject);
    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }
}
