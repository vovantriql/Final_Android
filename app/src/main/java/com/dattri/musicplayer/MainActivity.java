package com.hunghd.musicplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<SongInfo> _songs = new ArrayList<SongInfo>();;
    RecyclerView recyclerView;
    SeekBar seekBar;
    SongAdapter songAdapter;
    MediaPlayer mediaPlayer;
    private Handler myHandler = new Handler();
    String urlGetData="http://10.70.164.23/androidwebservices/getdata.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        songAdapter = new SongAdapter(this,_songs);
        recyclerView.setAdapter(songAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        songAdapter.setOnItemClickListener(new SongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final Button b, View view, final SongInfo obj, int position) {
                if(b.getText().equals("Stop")){
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    b.setText("Play");
                }else {

                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mediaPlayer = new MediaPlayer();
                                mediaPlayer.setDataSource(obj.getUrl());
                                mediaPlayer.prepareAsync();
                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        mp.start();
                                        seekBar.setProgress(0);
                                        seekBar.setMax(mediaPlayer.getDuration());
                                        Log.d("Prog", "run: " + mediaPlayer.getDuration());
                                    }
                                });
                                b.setText("Stop");



                            }catch (Exception e){}
                        }

                    };
                    myHandler.postDelayed(runnable,100);

                }
            }
        });
        //checkUserPermission();
        loadSongs(urlGetData);

        Thread t = new runThread();
        t.start();
    }

    public class runThread extends Thread {


        @Override
            public void run() {
                while (true) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                    Log.d("Runwa", "run: " + 1);
                    if (mediaPlayer != null) {
                        seekBar.post(new Runnable() {
                            @Override
                            public void run() {
                                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                            }
                        });

                        Log.d("Runwa", "run: " + mediaPlayer.getCurrentPosition());
                    }
                }
            }

    }

        private void checkUserPermission(){
        if(Build.VERSION.SDK_INT>=23){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},123);
                return;
            }
        }
        loadSongs(urlGetData);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 123:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    loadSongs(urlGetData);
                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    checkUserPermission();
                }
                break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }

    }

    private void loadSongs(String url){
//        String name = "lalala";
//        String artist = "unknown";
//        String url = "http://10.70.164.23/androidwebservices/Music/BangKhuang.mp3";
//        _songs.add(new SongInfo(name,artist,url));
//        songAdapter = new SongAdapter(MainActivity.this,_songs);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i =0;i<response.length();i++){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        _songs.add(new SongInfo(
                                object.getString("Baihat"),
                                object.getString("Casi"),
                                object.getString("Url")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                songAdapter.notifyDataSetChanged();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"lỗi",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }



}


