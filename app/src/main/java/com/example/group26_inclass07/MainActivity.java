package com.example.group26_inclass07;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    ArrayList<Song> result = new ArrayList<>();
    int flag=0;
    EditText search_value;
    TextView limit_value;
    SeekBar limit;
    Button search;
    RadioGroup rgroup;
    ListView listview;
    int progressChangedValue = 5;
    ProgressDialog pd;
    ProgressBar progress;
    String API_KEY="dce1692392e70a6a2883acdaab411e6f",sort_by="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        search_value = (EditText)findViewById(R.id.et_search);
        limit_value = (TextView) findViewById(R.id.tv_limit_value);
        limit = (SeekBar) findViewById(R.id.sb_limit);
        search = (Button) findViewById(R.id.b_search);
        rgroup = (RadioGroup)findViewById(R.id.radioGroup);
        listview = (ListView)findViewById(R.id.listView);
        pd = new ProgressDialog(MainActivity.this);
        progress = (ProgressBar)findViewById(R.id.pb_progress);
        limit.setMax(20);
        limit.setProgress(0);
        limit_value.setText("5");
        limit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress+5;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                limit_value.setText(progressChangedValue+"");
            }
        });

        rgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rb_artist:
                        sort_by="s_artist_rating";
                        if (flag==1){
                            String url = "http://api.musixmatch.com/ws/1.1/track.search?" + "q=" + search_value.getText().toString() + "&page_size=" + progressChangedValue + "&"+sort_by+"=desc" + "&apikey="+API_KEY;
                            new GetSimpleAsync().execute(url);
                        }
                        break;
                    case R.id.rb_track:
                        sort_by="s_track_rating";
                        if (flag==1){
                            String urla = "http://api.musixmatch.com/ws/1.1/track.search?" + "q=" + search_value.getText().toString() + "&page_size=" + progressChangedValue + "&"+sort_by+"=desc" + "&apikey="+API_KEY;
                            new GetSimpleAsync().execute(urla);
                        }
                        break;
                    default:
                        Log.d("demo", "Radio Button Error");
                }
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnected()){
                    if(sort_by==""){
                        sort_by="s_track_rating";
                    }
                    Toast.makeText(MainActivity.this, sort_by, Toast.LENGTH_SHORT).show();
                    flag=1;
                    String urlr = "http://api.musixmatch.com/ws/1.1/track.search?" + "q=" + search_value.getText().toString() + "&page_size=" + progressChangedValue + "&"+sort_by+"=desc" + "&apikey="+API_KEY;
                    new GetSimpleAsync().execute(urlr);
                }else{
                    Toast.makeText(getApplicationContext(),"No Internet Connection!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    private class GetSimpleAsync extends AsyncTask<String,Void, ArrayList<Song>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pd.setMessage("Loading Songs...");
//            pd.show();
            listview.setAdapter(null);
            progress.setVisibility(View.VISIBLE);
            result.clear();

        }

        @Override
        protected ArrayList<Song> doInBackground(String... params) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");
                    JSONObject root = new JSONObject(json);
                    JSONObject message = root.getJSONObject("message");
                    JSONObject body = message.getJSONObject("body");
                    JSONArray track_list = body.getJSONArray("track_list");
                    for (int i=0;i<track_list.length();i++) {
                        JSONObject trackJson = track_list.getJSONObject(i).getJSONObject("track");
                        Song songdata = new Song();
                        if(trackJson.getString("track_name") != null ) {
                            songdata.track_name = trackJson.getString("track_name");
                        }else {
                            songdata.track_name = "";
                        }
                        if(trackJson.getString("album_name") != null ) {
                            songdata.album_name = trackJson.getString("album_name");
                        }else {
                            songdata.album_name = "";
                        }
                        if(trackJson.getString("artist_name") != null ) {
                            songdata.artist_name = trackJson.getString("artist_name");
                        }else {
                            songdata.artist_name = "";
                        }
                        if(trackJson.getString("updated_time") != null ) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                            try {
                                Date date = format.parse(trackJson.getString("updated_time"));
                                String required_format  = (String) DateFormat.format("MM-dd-yyyy", date);
                                songdata.updated_time = required_format;
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }else {
                            songdata.updated_time = "";
                        }
                        if(trackJson.getString("track_share_url") != null ) {
                            songdata.track_share_url = trackJson.getString("track_share_url");
                        }else {
                            songdata.track_share_url = "";
                        }
                        result.add(songdata);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {

                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<Song> songs) {
            super.onPostExecute(songs);
//            pd.dismiss();
            progress.setVisibility(View.INVISIBLE);

            SongAdapter songadapter = new SongAdapter(getBaseContext(),R.layout.song_item,result);
//            Log.d("demo",result+"");
            listview.setAdapter(songadapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Song selected = result.get(i);
                    String url = selected.track_share_url;
                    Intent a = new Intent(Intent.ACTION_VIEW);
                    a.setData(Uri.parse(url));
                    startActivity(a);

                }
            });

        }
    }
}
