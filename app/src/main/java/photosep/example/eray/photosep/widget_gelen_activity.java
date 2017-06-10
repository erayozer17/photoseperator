package photosep.example.eray.photosep;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class widget_gelen_activity extends AppCompatActivity {

    public AutoCompleteTextView autoCompleteTextView;
    public Button button;
    private static final int CAMERA_CAPTURE = 20;
    public File photoFile;
    public ArrayAdapter adapter;

    public String girilenDosyaAdi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_widget_gelen_activity);

        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.editText5);
        button = (Button) findViewById(R.id.button5);

        if (ContextCompat.checkSelfPermission(widget_gelen_activity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                ||
                ContextCompat.checkSelfPermission(widget_gelen_activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(widget_gelen_activity.this,getResources().getString(R.string.izinleri_verdiginden_emin_ol),Toast.LENGTH_LONG).show();
            button.setEnabled(false);

        } else {
            adapter = new ArrayAdapter(widget_gelen_activity.this,android.R.layout.simple_list_item_1,new MainActivity().dosyalarDondur());
            autoCompleteTextView.setAdapter(adapter);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                girilenDosyaAdi = autoCompleteTextView.getText().toString();
                if (girilenDosyaAdi.equals(""))
                    girilenDosyaAdi =  new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                autoCompleteTextView.setText("");
                startCapture(girilenDosyaAdi);
            }
        });
    }

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

    public File CreateImageFile(String isim) throws IOException
    {
        File file = null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".jpg";
        File defaultFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+isim);
        boolean confirm = true;
        if (!defaultFile.exists()){
            confirm = defaultFile.mkdir();
        }

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
            Toast t = Toast.makeText(widget_gelen_activity.this,"Klasör oluşturulamadı. Gereken izni verdiğinizden emin olun.",Toast.LENGTH_LONG);
            t.show();

        }
        return file;
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {

        switch(requestCode)
        {
            case CAMERA_CAPTURE:
                if(resultCode == RESULT_OK)
                {
                    Toast t = Toast.makeText(widget_gelen_activity.this,girilenDosyaAdi+" - klasörüne kaydedildi",Toast.LENGTH_SHORT);
                    t.show();
                    new MainActivity(this, photoFile);
                    startCapture(girilenDosyaAdi);
                }
                break;
        }
    }
}
