package com.example.shiyansanbofangqi;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private List<Music> lists;
    private Adapter adapter;
    private ListView listView;
    private MediaPlayer mediaPlayer;
    private SeekBar seek;
    private Timer timer = new Timer();
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seek = findViewById(R.id.seek);
        listView = findViewById(R.id.listView);

        seek.setProgress(0);
        mediaPlayer = new MediaPlayer();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b == true){
                    mediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        lists = new MusicUtils().getMusic(this);
        Log.i(TAG, "" + lists);
        adapter = new MyAdapter(lists, this);
        listView.setAdapter((ListAdapter) adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                index = i;
                seek.setProgress(0);
                mediaPlayer.reset();
                try {
                    seek.setMax(Integer.parseInt(lists.get(i).getDuration()));
                    mediaPlayer.setDataSource(lists.get(i).getData());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                seek.setProgress(mediaPlayer.getCurrentPosition());
            }
        },0,1000);
    }

    public void click (View view) {

        switch (view.getId()){
            case R.id.button_start:
                mediaPlayer.start();
                break;

            case R.id.button_pause:
                mediaPlayer.pause();
                break;

            case R.id.button_last:
                index--;
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(lists.get(index).getData());

                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.button_next:
                index++;
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(lists.get(index).getData());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.add:
                add();
                break;

            case R.id.delete:

                break;

            case R.id.sunxu:

                break;

            case R.id.suiji:

                break;
        }

    }


    private boolean add() {
        final String type = "audio/*";
        final String[] mimeTypes = null;

        boolean result = pick(this, this, type, mimeTypes, true, 1);

        return result;
    }

    public static final boolean pick(Activity context, MainActivity fragment,
                                     String type, String[] mimeTypes,
                                     boolean documentOnly, int requestCode) {

        boolean result = false;

        if (context == null && fragment == null) {
            return false;
        }

        ArrayList<String> list = new ArrayList<>();
        list.add(Intent.ACTION_OPEN_DOCUMENT);
        list.add(Intent.ACTION_GET_CONTENT);

        if (!documentOnly) {
            list.add(0, Intent.ACTION_PICK);
        }

        for (String action : list) {
            if (action.equalsIgnoreCase(Intent.ACTION_PICK)) {
                if (mimeTypes != null && mimeTypes.length > 1) {
                    continue;
                }
            }

            Intent intent = new Intent();

            intent.setAction(action);
            intent.setType(type);

            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, false);

            if (mimeTypes != null && mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }

            if (action.equalsIgnoreCase(Intent.ACTION_OPEN_DOCUMENT)
                    || action.equalsIgnoreCase(Intent.ACTION_GET_CONTENT)) {
                intent.addCategory(Intent.CATEGORY_OPENABLE);
            }

            try {

                if (fragment != null) {
                    fragment.startActivityForResult(intent, requestCode);
                } else {
                    context.startActivityForResult(intent, requestCode);
                }

                result = true;

                break;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}