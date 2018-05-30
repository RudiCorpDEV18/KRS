package info.rudicorpdev.krsfoto;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.hardware.camera2.CameraManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class KRSFoto extends AppCompatActivity {

    String root, fname;
    ImageView imgGallery, imgKamera, imgTentang, Tampil;
    Button btnKeluar;

    private static final int DATA_KAMERA = 11;
    private static final int PILIH_DATA = 1994;
    private static final int PERMISSION_REQUEST_CODE = 200;
    static final Integer WRITE_EXST = 0x2;
    static final Integer READ_EXST = 0x3;
    static final Integer INTERNET = 2017;


    String path_photo = "";
    Uri outPutfileUri;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_krsfoto);

        getSupportActionBar().hide();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermission()) {
                requestPermission();
            } else {
                Toast.makeText(this, "Selamat Datang ^_^", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Selamat Datang ^_^", Toast.LENGTH_LONG).show();
        }


        Tampil = (ImageView) findViewById(R.id.imgDepan);
        imgGallery = (ImageView) findViewById(R.id.imgGallery);
        imgKamera = (ImageView) findViewById(R.id.imgCamera);
        imgTentang = (ImageView) findViewById(R.id.imgTentang);
        btnKeluar = (Button) findViewById(R.id.btnkeluar);

        imgGallery.setImageDrawable(getState(R.drawable.gallery_t, R.drawable.gallery));
        imgKamera.setImageDrawable(getState(R.drawable.kamera_t, R.drawable.kamera));
        imgTentang.setImageDrawable(getState(R.drawable.tentang_t, R.drawable.tentang));

        imgKamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(Environment.getExternalStorageDirectory(), "KRSFoto.jpg");
                outPutfileUri = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri);
                startActivityForResult(intent, DATA_KAMERA);
            }
        });

        imgGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent Gallery = new Intent();
                    Gallery.setType("image/*");
                    Gallery.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(Gallery, "Pilih Gambar"), PILIH_DATA);
                }catch (Exception e){
                    Log.e(e.getClass().getName(), e.getMessage(), e);
                }
            }
        });

        imgTentang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(KRSFoto.this, Tentang.class));
            }
        });

        btnKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Keluar();
            }
        });
    }

    private boolean checkPermission() {
        int resultCamera = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int resultWrite = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        return resultCamera == PackageManager.PERMISSION_GRANTED && resultWrite == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean externalAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && externalAccepted) {

                    } else {
                        Toast.makeText(this, "Ijinkan dulu aplikasi ini untuk mengakses kamera dan gallery Anda", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Bitmap bm = null;

            // Camera
            if (requestCode == DATA_KAMERA) {
                String uri = outPutfileUri.toString();
                try {
                    bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), outPutfileUri);
                } catch (IOException e) { e.printStackTrace(); }


                // Gallery
            } else if (requestCode == PILIH_DATA) {
                try { bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                } catch (IOException e) { e.printStackTrace(); }
            }


            // Call method to pass image to Other Activity
            Bitmap scaledBitmap = scaleBitmapToMaxSize(800, bm);
            passBitmapToOtherActivity(scaledBitmap);
        }

    }

    public static Bitmap scaleBitmapToMaxSize(int maxSize, Bitmap bm) {
        int outWidth;
        int outHeight;
        int inWidth = bm.getWidth();
        int inHeight = bm.getHeight();
        if(inWidth > inHeight){
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, outWidth, outHeight, false);
        return resizedBitmap;
    }

    public String passBitmapToOtherActivity(Bitmap bitmap) {

        // Save Bitmap into the Device (to pass it to the other Activity)
        String fileName = "imagePassed";
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            fo.close();

            // Go to CreateMeme
            Intent i = new Intent(KRSFoto.this, Kompresi.class);
            startActivity(i);

        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }

    private Drawable getState(int img_w, int img_b) {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed}, getResources().getDrawable(img_b));
        states.addState(new int[]{android.R.attr.state_focused}, getResources().getDrawable(img_b));
        states.addState(new int[]{}, getResources().getDrawable(img_w));
        return states;
    }

    public void Keluar() {
        new AlertDialog.Builder(this)
                .setMessage("Apakah Anda ingin Keluar ?")
                .setCancelable(false)
                .setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Process.killProcess(Process.myPid());
                    }
                })
                .setNegativeButton("Tidak", null)
                .show();
    }
}
