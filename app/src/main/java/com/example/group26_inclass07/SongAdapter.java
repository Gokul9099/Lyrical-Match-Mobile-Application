package com.example.group26_inclass07;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;


public class SongAdapter extends ArrayAdapter<Song> {
    public SongAdapter(Context context, int resource, List<Song> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Song song = getItem(position);
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.song_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.tv_track_name = (TextView) convertView.findViewById(R.id.tv_track_name);
            viewHolder.tv_artist_name = (TextView) convertView.findViewById(R.id.tv_artist_name);
            viewHolder.tv_album_name = (TextView) convertView.findViewById(R.id.tv_album_name);
            viewHolder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_track_name.setText("Track: "+song.track_name);
        viewHolder.tv_artist_name.setText("Artist: "+song.artist_name);
        viewHolder.tv_album_name.setText("Album: "+song.album_name);
        viewHolder.tv_date.setText("Date: "+song.updated_time);
        return  convertView;
    }

    public SongAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Song> objects) {
        super(context, resource, objects);
    }
    private static class ViewHolder{
        TextView tv_track_name;
        TextView tv_artist_name;
        TextView tv_album_name;
        TextView tv_date;
    }
}