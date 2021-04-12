package pt.fcul.lasige.sonaar.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import pt.fcul.lasige.sonaar.data.Constants;

public class ImageUtils {
    public static byte[] getImageToAPI(Context ctx, int bitMapCutoutX, int bitMapCutoutY, int bitMapCutoutWidth, int bitMapCutoutHeight){
        File screenshot = getLastScreenshot(ctx);
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

    @Deprecated
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

    public static File getLastScreenshot(Context ctx){

        String pathOfImage;
        long dateModified, lastModifiedTime = Long.MIN_VALUE;
        File screenshot = null;

        ContentResolver contentResolver = ctx.getContentResolver();
        Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.MediaColumns.DATA, MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED};

        Cursor cursor = contentResolver.query(uri, projection, null,
                null, null);

        int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (cursor.moveToNext()) {
            pathOfImage = cursor.getString(columnIndexData);
            dateModified = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)));
            if(pathOfImage.toLowerCase().contains("screenshot_")) {
                if (dateModified > lastModifiedTime) {
                    screenshot = new File(pathOfImage);
                    lastModifiedTime = dateModified;
                }
            }
        }
        cursor.close();

        return screenshot;
    }

    public static ArrayList<Uri> getAllScreenShots(Context ctx){
        Uri uri;
        ArrayList<Uri> listOfImages = new ArrayList<>();
        Cursor cursor;
        ContentResolver contentResolver = ctx.getContentResolver();
        int columnIndexData;
        String pathOfImage;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.MediaColumns.DATA, MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DISPLAY_NAME};

        cursor = contentResolver.query(uri, projection, null,
                null, null);
        Uri deleteUri;
        columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (cursor.moveToNext()) {
            pathOfImage = cursor.getString(columnIndexData);
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            if(pathOfImage.toLowerCase().contains("screenshot_"))
                listOfImages.add(deleteUri);
        }
        cursor.close();

        return listOfImages;
    }
}
