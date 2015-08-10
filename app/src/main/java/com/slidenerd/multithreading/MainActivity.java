package com.slidenerd.multithreading;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private EditText editText;
    private ListView listView;
    private String[] listOfImages;
    ProgressBar progressBar;
    LinearLayout loadingSection = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.downLoadURL);
        listView = (ListView) findViewById(R.id.urlList);
        listView.setOnItemClickListener(this);
        listOfImages = getResources().getStringArray(R.array.imageUrls);
        progressBar = (ProgressBar) findViewById(R.id.downloadProgress);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void downLoadImage(View view) {
        String url = editText.getText().toString();



        //not in video added to keep main picture directory clean
        makeCatsDir();

         Thread myThread = new Thread(new DownloadImagesThread(url));
         myThread.start();
    }

    private void makeCatsDir() {
        String homeDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        File catDirectory = new File(homeDirectory + "/catPics");

        if (!catDirectory.exists()) catDirectory.mkdir();
    }


    public boolean downLoadImageUsingThreads(String url) {
          /*
        1 create the url object that represents the url
        2 open connection using that url
        3 read data using input stream into a byte array
        4 open a file outputstream to save data on sd card
        5 write data to fileoutputstream
        6 close connection
         */
        boolean successfull = false;
        URL downloadURL = null;
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        File file = null;

        try {
            downloadURL = new URL(url);
            connection = (HttpURLConnection) downloadURL.openConnection();
            inputStream = connection.getInputStream();

            String homeDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
            file = new File(homeDirectory + "/catPics" + "/" + Uri.parse(url).getLastPathSegment());
            fileOutputStream = new FileOutputStream(file);

            int read = -1;
            //read 1024 bytes per time
            byte[] buffer = new byte[1024];

            // -1 means that there is no valid value
            while ((read = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, read);

            }
            successfull = true;

        } catch (MalformedURLException e) {
            L.m("" + e);
        } catch (IOException e) {
            L.m("" + e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    L.m("" + e);
                }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    L.m("" + e);
                }
            }
        }
        return successfull;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        editText.setText(listOfImages[position]);

    }


    private class DownloadImagesThread implements Runnable {

        private String url;
        public DownloadImagesThread(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            downLoadImageUsingThreads(url);
        }
    }


}
