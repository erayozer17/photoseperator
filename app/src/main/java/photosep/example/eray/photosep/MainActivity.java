package photosep.example.eray.photosep;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements MediaScannerConnection.MediaScannerConnectionClient {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    private static final int CAMERA_CAPTURE = 20;
    private static final int REQUEST_WRITE_STORAGE = 112;
    public ImageView infoImage;
    public Button btnclickpicture;
    public Activity activity;
    public AutoCompleteTextView autoCompleteTextView;
    public String isim;
    public File photoFile;
    private MediaScannerConnection mMs;
    public ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = MainActivity.this;
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.editText);

        storagePermisson();

        ////////Arkaplana random fotograf atama
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.activity_main);
        linearLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(),arkaplanGenerator.arkaplan()));
        //////////////

        infoImage = (ImageView) findViewById(R.id.imageView4);

        ///info tuşuna alertdialog oluştur
        infoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
                dialog.setMessage(getResources().getString(R.string.bilgi));
                dialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.tamam), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        ///////////////////////

        /////////klavye açılıp başka bir yere tıklandığında klavyeyi kapatır
        autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    hideKeyboard(view);
                }
            }
        });
        //////////

        //////////klasör oluştur tuşu
        btnclickpicture = (Button) findViewById(R.id.button2);
        btnclickpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isim = autoCompleteTextView.getText().toString();
                if (isim.equals(""))
                    isim = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                autoCompleteTextView.setText("");
                cameraPermission();
            }
        });
        ///////////////////////////////

    }

    ///////////////Burada auto complete için hafızadan tüm dosyaları çekip autocomplete atıyorsun
    public String[] dosyalarDondur(){
        String [] dosyalar;
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        String[] names = file.list();
        dosyalar = new String[names.length];
        int sayac = 0;
        for (String name : names){
            dosyalar[sayac] = name;
            sayac++;
        }
        return dosyalar;
    }
    ///////////////////////////////////////////////////

    //////////Yukarıda çektigin dosyalarla oluşturdugun dosyalar arrayini burada kullanıp array adapter içine yazdırıyorsun ve adapteri autocomplete atıyorsun
    public void adapterSetEt(){
        adapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,dosyalarDondur());
        autoCompleteTextView.setAdapter(adapter);
    }

    ////////////kamera izni
    public void cameraPermission(){
        //daha önceden izin verilmiş mi verilmemiş mi kontrol ediyorsun.verilmemişse bu if içine giriyor.Verilmişse else
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);

        } else {

            startCapture(isim);

        }
    }
    ////////////////////////////////////////////

    ////dosya erişim izni
    public void storagePermisson(){
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);

        } else {

            adapterSetEt();

        }
    }

    ///////edittext dışında bir yere tıklanınca klavyeyi kapatan metod
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    ///////////

    ////////////izin sonuçları neticesinde dönen callback metodu.izin verilmesi ya da reddedilmesi durumunda yapılacaklar.
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // izin isteği olumluysa if, değilse else girer iki izin için de
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startCapture(isim);

                } else {

                    Toast.makeText(MainActivity.this,getResources().getString(R.string.kamera_izni_gerekli),Toast.LENGTH_LONG).show();

                }
                return;
            }
            case REQUEST_WRITE_STORAGE: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    adapterSetEt();

                } else {

                    Toast.makeText(MainActivity.this,getResources().getString(R.string.depolama_izni_gerekli),Toast.LENGTH_LONG).show();
                    btnclickpicture.setEnabled(false);

                }
                return;
            }
        }
    }
    //////////////////////////////////////

    ////Fotograf cekimine başlayan metod
    public void startCapture(String isim) {

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {

            photoFile = null;
            try {
                photoFile = CreateImageFile(isim);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(photoFile != null)
            {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(cameraIntent, CAMERA_CAPTURE);
                // TODO: 5.2.2017 eger startActivityForResult metodunu ileride versiyon 24 ve yukarısı için error verirse ve güncellemek istersen yapman gerekenler
                // http://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed
            }
        }
    }
    //////////////////////////////////////////

    /////////Fotografın kaydedileceği klasörü oluşturan metod.
    public File CreateImageFile(String isim) throws IOException
    {
        File file = null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".jpg";
        File defaultFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+isim);
        ///klasör burada oluşturuluyor
        boolean confirm = true;
        if (!defaultFile.exists()){
            confirm = defaultFile.mkdir();
        }
        ////////////////////////
        if (confirm){
            file = new File(defaultFile,imageFileName);
            if(file.exists()){
                int i = 2;
                while (file.exists()){
                    file = new File(defaultFile, timeStamp + "(" + i + ")" + ".jpg");
                    i++;
                }
            }
        } else {
            Toast t = Toast.makeText(MainActivity.this,getResources().getString(R.string.klasor_olusturulamadı),Toast.LENGTH_LONG);
            t.show();

        }
        return file;
    }
    //////////////////////////////////////////////////////

    /////////////////Cekilen fotografın durumuna göre ne yapılacagını belirten metod
    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {

        switch(requestCode)
        {
            case CAMERA_CAPTURE:
                if(resultCode == RESULT_OK)
                {
                    Toast t = Toast.makeText(MainActivity.this,isim+getResources().getString(R.string.kaydedildi),Toast.LENGTH_SHORT);
                    t.show();
                    new MainActivity(this, photoFile);
                    startCapture(isim);
                }
                break;
        }
    }
    /////////////////////////

    /////Bu yapıcı media scanner connector yapısını kullanmak için oluşturuldu
    public MainActivity(Context context, File f) {
        photoFile = f;
        mMs = new MediaScannerConnection(context, this);
        mMs.connect();
    }
    ///////////////////////////////

    public MainActivity(){

    }

    ///////media scanner connector kullanımında bağlanıp,dosyanın varlığını algılayan metod
    @Override
    public void onMediaScannerConnected() {
        mMs.scanFile(photoFile.getAbsolutePath(), null);
    }
    ///////////

    ///////madia scanner connectioni kapatan metod
    @Override
    public void onScanCompleted(String s, Uri uri) {
        mMs.disconnect();
    }
    }
    //////////////

