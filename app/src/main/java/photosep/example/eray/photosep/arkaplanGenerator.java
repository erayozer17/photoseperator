package photosep.example.eray.photosep;

import java.util.Random;

public class arkaplanGenerator {
    public static int arkaplan(){
        int sonucArkaplan = 0;
        Random r = new Random();
        int sayi = r.nextInt(10);
        switch (sayi){
            case 0:
                sonucArkaplan = photosep.example.eray.photosep.R.drawable.bavaria;
                break;
            case 1:
                sonucArkaplan = photosep.example.eray.photosep.R.drawable.waterfall;
                break;
            case 2:
                sonucArkaplan = photosep.example.eray.photosep.R.drawable.more;
                break;
            case 3:
                sonucArkaplan = photosep.example.eray.photosep.R.drawable.mountains;
                break;
            case 4:
                sonucArkaplan = photosep.example.eray.photosep.R.drawable.pisa;
                break;
            case 5:
                sonucArkaplan = photosep.example.eray.photosep.R.drawable.sea;
                break;
            case 6:
                sonucArkaplan = photosep.example.eray.photosep.R.drawable.sunset;
                break;
            case 7:
                sonucArkaplan = photosep.example.eray.photosep.R.drawable.sunset2;
                break;
            case 8:
                sonucArkaplan = photosep.example.eray.photosep.R.drawable.wai;
                break;
            case 9:
                sonucArkaplan = photosep.example.eray.photosep.R.drawable.water;
                break;
        }
        return sonucArkaplan;
    }
}
