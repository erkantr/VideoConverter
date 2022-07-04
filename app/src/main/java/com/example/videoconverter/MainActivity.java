package com.example.videoconverter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;

public class MainActivity extends AppCompatActivity {

    EditText url;
    String get_URL;
    String new_link;
    Button download;


    Button indir,indirmeListesi;
    EditText link_edit;
    String link;
    private long sira;
    private DownloadManager dm;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        url = findViewById(R.id.url);
        download = findViewById(R.id.download);
        this.setTitle("Video Dönüştürücü");
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.dark_red));

        Dexter.withContext(this).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.cancelPermissionRequest();
            }
        }).check();

        /*
        BroadcastReceiver receiver = new BroadcastReceiver() {//BroadCast Receiver kullanma sebebimiz downloadın bittiğini koda iletebilmek için
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {//Eğer indirme başarılı ise dosyayı açacağız
                    sira = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);//İndirilen dosyanın Downloads daki sırasını alıyoruz
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(sira); //Sıraya göre sorguluyorz
                    Cursor c = dm.query(query);//Sorgu sonucunu Cursor a iletiyoruz
                    if (c.moveToFirst()) {
                        int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);

                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) { //İndirme Başarılı ise

                            String dosyaAdi = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE)); //Dosya adını alıyoruz

                            Toast.makeText(getApplicationContext(), dosyaAdi+" Başarıyla indi", Toast.LENGTH_LONG).show();
                            //indirmeListesi.setEnabled(true);//İndirme Listesindeki butonu enable ediyoruz

                        }else{
                            Toast.makeText(getApplicationContext(), "Hatalı Link", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

        };
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));//BroadCast Reciverı DownloadManageri dinlemesi için başlatıyoruz

         */


        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadVideo();
            }
        });
    }
    public void downloadVideo(){
        get_URL = url.getText().toString();

        new YouTubeExtractor(this) {
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                if (ytFiles != null ) {
                    int itag = 398;
                    if (ytFiles.get(itag) != null){
                        String downloadUrl = ytFiles.get(itag).getUrl();
                    String title = "PoweredByErkan";

                    dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);//DownloadManager objesi oluşturuyoruz
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
                    //request.setTitle(title);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".mp4");
                    request.allowScanningByMediaScanner();
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);//İndireceğimiz linki veriyoruz
                    sira = dm.enqueue(request);
                } else {
                        Toast.makeText(MainActivity.this, "nall", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }.extract(get_URL, true, true);

        /*
        YouTubeUriExtractor youTubeUriExtractor = new YouTubeUriExtractor(MainActivity.this) {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onUrisAvailable(String videoId, String videoTitle, SparseArray<YtFile> ytFiles) {
                if (ytFiles!=null){
                    int tag = 22;
                    new_link = ytFiles.get(tag).getUrl();
                    String title = "PoweredByErkan";

                    String extension = MimeTypeMap.getFileExtensionFromUrl(new_link);

                    MimeTypeMap mime = MimeTypeMap.getSingleton();
                    String mimeType = mime.getMimeTypeFromExtension(extension);
                   // if (mimeType != null) {
                        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);//DownloadManager objesi oluşturuyoruz
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(new_link));
                        //request.setTitle(title);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".mp4");
                        request.allowScanningByMediaScanner();
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);//İndireceğimiz linki veriyoruz
                        sira = dm.enqueue(request);//indirmeye başlıyoruz.Geriye download sırasını döner

//                    }

                    /*
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(new_link));
                    request.setTitle(title);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".mp4");

                    @SuppressLint({"ServiceCast", "StaticFieldLeak"}) DownloadManager downloadManager = (DownloadManager) getApplicationContext().getSystemService(Context.DISPLAY_SERVICE);
                    DisplayManager displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);

                    request.allowScanningByMediaScanner();
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                    downloadManager.enqueue(request);

                     /
                }
            }
        };
        youTubeUriExtractor.execute(get_URL);
        */
    }
}