package pt.fcul.lasige.sonaar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.AudioAttributes;
import android.media.Image;
import android.media.ImageReader;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import pt.fcul.lasige.sonaar.api.APIClient;

/**
 * Created by André Rodrigues on 15/06/2016.
 *
 * Create an overlay to hide the reccording
 */
public class ScreenShotCapture extends Activity {

    private static final String TAG = ScreenShotCapture.class.getName();
    private static final int REQUEST_CODE = 100;
    private static String STORE_DIRECTORY;
    private static int IMAGES_PRODUCED;
    private static String SCREENCAP_NAME = "hintmeup";
    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    private static MediaProjection sMediaProjection;
    private MediaProjectionManager mProjectionManager;
    private ImageReader mImageReader;
    private Handler mHandler;
    private VirtualDisplay mVirtualDisplay;
    private int mDensity;
    private int mWidth;
    private int mHeight;
    private int bitMapCutoutX;
    private int bitMapCutoutY;
    private int bitMapCutoutWidth;
    private int bitMapCutoutHeight;
    DisplayMetrics metrics;
    Bitmap bitmap;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @Override
        public void onImageAvailable(ImageReader reader) {

            Log.d(TAG, "IMAGE");

            Image image = null;

            try {
                image = mImageReader.acquireLatestImage();
                if (image == null)
                    return;

                Image.Plane[] planes = image.getPlanes();
                ByteBuffer buffer = planes[0].getBuffer();
                int width = image.getWidth();
                int height = image.getHeight();
                int pixelStride = planes[0].getPixelStride();
                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * width;

                // create bitmap
                bitmap = Bitmap.createBitmap(metrics, width, mHeight, Bitmap.Config.ARGB_8888);
                int offset = 0;

                for (int i = 0; i < height; ++i) {
                    int[] savedPixels = new int[width];
                    for (int j = 0; j < width; ++j) {
                        int pixel = 0;
                        pixel |= (buffer.get(offset) & 0xff) << 16;     // R
                        pixel |= (buffer.get(offset + 1) & 0xff) << 8;  // G
                        pixel |= (buffer.get(offset + 2) & 0xff);       // B
                        pixel |= (buffer.get(offset + 3) & 0xff) << 24; // A
                        //bitmap.setPixel(j, i, pixel);
                        savedPixels[j] = pixel;
                        offset += pixelStride;
                    }
                    bitmap.setPixels(savedPixels, 0, width, 0, i, width, 1);
                    offset += rowPadding;
                }

                if(bitMapCutoutX != -1 && bitMapCutoutY != -1 &&
                        bitMapCutoutWidth != -1 && bitMapCutoutHeight != -1) {
                    bitmap = Bitmap.createBitmap(bitmap, bitMapCutoutX, bitMapCutoutY, bitMapCutoutWidth, bitMapCutoutHeight);
                }
                //bitmap.copyPixelsFromBuffer(buffer);
                IMAGES_PRODUCED++;
                Log.e(TAG, "captured image: " + IMAGES_PRODUCED);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (image != null) {
                    image.close();
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private class MediaProjectionStopCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            Log.e("ScreenCapture", "stopping projection.");

            if (mVirtualDisplay != null) mVirtualDisplay.release();
            if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);
            sMediaProjection.unregisterCallback(MediaProjectionStopCallback.this);
            IMAGES_PRODUCED = 0;

            // write bitmap to a file
//            FileOutputStream fos = null;
//            try {
//                fos = new FileOutputStream(STORE_DIRECTORY + "myscreen.png");
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//                fos.flush();
//                fos.close();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            MediaPostCreationDetector.getInstance().callAPI(bos.toByteArray());

        }
    }


    /******************************************
     * Activity Lifecycle methods
     ************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Creating Screenshot");
        // call for the projection manager
        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent i = getIntent();
        bitMapCutoutX = i.getIntExtra("bitMapCutoutX", -1);
        bitMapCutoutY = i.getIntExtra("bitMapCutoutY", -1);
        bitMapCutoutHeight = i.getIntExtra("bitMapCutoutHeight", -1);
        bitMapCutoutWidth = i.getIntExtra("bitMapCutoutWidth", -1);

        // start capture handling thread
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mHandler = new Handler();
                Looper.loop();
            }
        }.start();
        startProjection();
    }

    @Override
    public void onDestroy() {
        // mp.stop();
        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            sMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);

            if (sMediaProjection != null) {
                File externalFilesDir = getExternalFilesDir(null);
                if (externalFilesDir != null) {
                    STORE_DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/screenshots/";
                    File storeDirectory = new File(STORE_DIRECTORY);
                    if (!storeDirectory.exists()) {
                        boolean success = storeDirectory.mkdirs();
                        if (!success) {
                            Log.e(TAG, "failed to create file storage directory.");
                            return;
                        }
                    }
                } else {
                    Log.e(TAG, "failed to create file storage directory, getExternalFilesDir is null.");
                    return;
                }

                // display metrics
                metrics = getResources().getDisplayMetrics();
                mDensity = metrics.densityDpi;
                // create virtual display depending on device width / height
                createVirtualDisplay();


                // register media projection stop callback
                sMediaProjection.registerCallback(new MediaProjectionStopCallback(), mHandler);

                //GO BACK
                MediaPostCreationDetector.getInstance().goBack();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stopProjection();
                    }
                }, 500);
            }
            //finish();

        }
    }

    /******************************************
     * UI Widget Callbacks
     *******************************/
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startProjection() {
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
    }

    private void stopProjection() {
        mHandler.post(new Runnable() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                if (sMediaProjection != null) {
                    sMediaProjection.stop();
                    Log.d(TAG, "STOPPED PROJECTION");

                }
            }
        });
    }


    /******************************************
     * Factoring Virtual Display creation
     ****************/
    @SuppressLint("WrongConstant")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void createVirtualDisplay() {
        // get width and height
        mWidth = metrics.widthPixels;
        mHeight = metrics.heightPixels;
        // start capture reader
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 1);
        mVirtualDisplay = sMediaProjection.createVirtualDisplay(SCREENCAP_NAME, mImageReader.getWidth(), mImageReader.getHeight(), mDensity, VIRTUAL_DISPLAY_FLAGS, mImageReader.getSurface(), null, mHandler);
        mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), mHandler);
    }
}