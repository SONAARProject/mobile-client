package pt.fcul.lasige.sonaar.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;

import pt.fcul.lasige.sonaar.data.Constants;

public class ImageUtils {
    public static byte[] getImageToAPI(int bitMapCutoutX, int bitMapCutoutY, int bitMapCutoutWidth, int bitMapCutoutHeight){
        File screenshot = getLastScreenshot();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(screenshot.getAbsolutePath(), options);

        if(bitMapCutoutX != -1 && bitMapCutoutY != -1 &&
                bitMapCutoutWidth != -1 && bitMapCutoutHeight != -1) {
            bitmap = Bitmap.createBitmap(bitmap, bitMapCutoutX, bitMapCutoutY, bitMapCutoutWidth, bitMapCutoutHeight);
        }

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);

        return bos.toByteArray();
    }

    public static File getLastScreenshot(){
        File directory = new File(Constants.EXTERNAL_STORAGE_PATH + "/Pictures/Screenshots");
        if(!directory.exists()) {
            directory = new File(Constants.EXTERNAL_STORAGE_PATH + "/DCIM/Screenshots");
        }
        File[] files = directory.listFiles();
        long lastModifiedTime = Long.MIN_VALUE;
        File screenshot = null;

        if (files != null) {
            for (File file : files) {
                if (file.lastModified() > lastModifiedTime) {
                    screenshot = file;
                    lastModifiedTime = file.lastModified();
                }
            }
        }

        return screenshot;
    }
}
