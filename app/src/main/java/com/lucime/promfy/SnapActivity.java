package com.lucime.promfy;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImage.OnPictureSavedListener;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import com.lucime.promfy.GPUImageFilterTools;
import com.lucime.promfy.GPUImageFilterTools.FilterAdjuster;
import com.lucime.promfy.GPUImageFilterTools.OnGpuImageFilterChosenListener;
import com.lucime.promfy.R;
import com.lucime.promfy.utils.CameraHelper;
import com.lucime.promfy.utils.CameraHelper.CameraInfo2;

import com.rey.material.widget.Slider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SnapActivity extends AppCompatActivity implements OnSeekBarChangeListener, OnClickListener {
    private GPUImage mGPUImage;
    private CameraHelper mCameraHelper;
    private CameraLoader mCamera;
    private GPUImageFilter mFilter;
    private FilterAdjuster mFilterAdjuster;
    private String[] whiteBalanceList = {
            Camera.Parameters.WHITE_BALANCE_CLOUDY_DAYLIGHT,
            Camera.Parameters.WHITE_BALANCE_DAYLIGHT,
            Camera.Parameters.WHITE_BALANCE_FLUORESCENT,
            Camera.Parameters.WHITE_BALANCE_INCANDESCENT,
            Camera.Parameters.WHITE_BALANCE_SHADE,
            Camera.Parameters.WHITE_BALANCE_TWILIGHT };
    private String[] sceneModeList = {
            Camera.Parameters.SCENE_MODE_BEACH,
            Camera.Parameters.SCENE_MODE_ACTION,
            Camera.Parameters.SCENE_MODE_CANDLELIGHT,
            Camera.Parameters.SCENE_MODE_FIREWORKS,
            Camera.Parameters.SCENE_MODE_LANDSCAPE,
            Camera.Parameters.SCENE_MODE_NIGHT,
            Camera.Parameters.SCENE_MODE_PARTY,
            Camera.Parameters.SCENE_MODE_PORTRAIT,
            Camera.Parameters.SCENE_MODE_THEATRE,
            Camera. Parameters.SCENE_MODE_STEADYPHOTO,
            Camera.Parameters.SCENE_MODE_SUNSET,
            Camera.Parameters.SCENE_MODE_SPORTS,
            Camera.Parameters.SCENE_MODE_HDR,
            Camera.Parameters.SCENE_MODE_SNOW };
    private ImageView cameraFlashControlView, cameraSceneModeView, cameraWhitebalanceView;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap);
        ((SeekBar) findViewById(R.id.seekBar)).setOnSeekBarChangeListener(this);
        findViewById(R.id.button_choose_filter).setOnClickListener(this);
        findViewById(R.id.button_capture).setOnClickListener(this);
        findViewById(R.id.img_flash_control).setOnClickListener(this);
        findViewById(R.id.img_scenemode_control).setOnClickListener(this);
        findViewById(R.id.img_white_balance).setOnClickListener(this);

        LucimeCamUser.setFlashmode(Camera.Parameters.FLASH_MODE_OFF);
        //LucimeCamUser.setWhitebalance();

        mGPUImage = new GPUImage(this);
        mGPUImage.setGLSurfaceView((GLSurfaceView) findViewById(R.id.surfaceView));

        mCameraHelper = new CameraHelper(this);
        mCamera = new CameraLoader();

        View cameraSwitchView = findViewById(R.id.img_switch_camera);
        cameraSwitchView.setOnClickListener(this);
        if (!mCameraHelper.hasFrontCamera() || !mCameraHelper.hasBackCamera()) {
            cameraSwitchView.setVisibility(View.GONE);
        }

        cameraFlashControlView = (ImageView) findViewById(R.id.img_flash_control);
        cameraSceneModeView = (ImageView) findViewById(R.id.img_scenemode_control);
        cameraWhitebalanceView = (ImageView) findViewById(R.id.img_white_balance);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCamera.onResume();
    }

    @Override
    protected void onPause() {
        mCamera.onPause();
        super.onPause();
    }

    @Override
    public void onClick(final View v) {
        Camera.Parameters params = mCamera.mCameraInstance.getParameters();
        switch (v.getId()) {
            case R.id.button_choose_filter:
                GPUImageFilterTools.showDialog(this, new OnGpuImageFilterChosenListener() {

                    @Override
                    public void onGpuImageFilterChosenListener(final GPUImageFilter filter) {
                        switchFilterTo(filter);
                    }
                });
                ((SeekBar) findViewById(R.id.seekBar)).setVisibility(View.VISIBLE);
                break;

            case R.id.button_capture:
                LucimeCamUser.setTakeSwitchFlag(2);
                if (mCamera.mCameraInstance.getParameters().getFocusMode().equals(
                        Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    takePicture();
                } else {
                    mCamera.mCameraInstance.autoFocus(new Camera.AutoFocusCallback() {

                        @Override
                        public void onAutoFocus(final boolean success, final Camera camera) {
                            takePicture();
                        }
                    });
                }
                break;

            case R.id.img_switch_camera:
                mCamera.switchCamera();
                LucimeCamUser.setTakeSwitchFlag(1);
                takePicture();
                break;

            case R.id.img_flash_control:
                if(LucimeCamUser.getFlashmode() == Camera.Parameters.FLASH_MODE_OFF){
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    LucimeCamUser.setFlashmode(Camera.Parameters.FLASH_MODE_ON);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        cameraFlashControlView.setImageDrawable(getResources().getDrawable(R.drawable.flash, getApplicationContext().getTheme()));
                    } else {
                        cameraFlashControlView.setImageDrawable(getResources().getDrawable(R.drawable.flash));
                    }
                }
                else{
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    LucimeCamUser.setFlashmode(Camera.Parameters.FLASH_MODE_OFF);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        cameraFlashControlView.setImageDrawable(getResources().getDrawable(R.drawable.flash_off, getApplicationContext().getTheme()));
                    } else {
                        cameraFlashControlView.setImageDrawable(getResources().getDrawable(R.drawable.flash_off));
                    }
                }
                mCamera.mCameraInstance.setParameters(params);
                break;

            case R.id.img_scenemode_control:
                int randomIndex1 = getRandomInteger(sceneModeList.length, 0);
                params.setSceneMode(sceneModeList[randomIndex1]);
                mCamera.mCameraInstance.setParameters(params);
                Toast.makeText(SnapActivity.this, "Scene Mode set to "+sceneModeList[randomIndex1], Toast.LENGTH_LONG).show();
                break;

            case R.id.img_white_balance:
                int randomIndex2 = getRandomInteger(whiteBalanceList.length, 0);
                params.setWhiteBalance(whiteBalanceList[randomIndex2]);
                mCamera.mCameraInstance.setParameters(params);
                Toast.makeText(SnapActivity.this, "White Balance set to "+whiteBalanceList[randomIndex2], Toast.LENGTH_LONG).show();
                break;
        }
    }

    public static int getRandomInteger(int maximum, int minimum){
        return ((int) (Math.random()*(maximum - minimum))) + minimum;
    }

    private void takePicture() {
        // TODO get a size that is about the size of the screen
        Camera.Parameters params = mCamera.mCameraInstance.getParameters();
        params.setRotation(90);
        mCamera.mCameraInstance.setParameters(params);
        for (Camera.Size size : params.getSupportedPictureSizes()) {
            Log.i("ASDF", "Supported: " + size.width + "x" + size.height);
        }
        mCamera.mCameraInstance.takePicture(null, null,
                new Camera.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] data, final Camera camera) {

                        final File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                        if (pictureFile == null) {
                            Log.d("ASDF",
                                    "Error creating media file, check storage permissions");
                            return;
                        }

                        try {
                            FileOutputStream fos = new FileOutputStream(pictureFile);
                            fos.write(data);
                            fos.close();
                        } catch (FileNotFoundException e) {
                            Log.d("ASDF", "File not found: " + e.getMessage());
                        } catch (IOException e) {
                            Log.d("ASDF", "Error accessing file: " + e.getMessage());
                        }

                        data = null;
                        Bitmap bitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
                        // mGPUImage.setImage(bitmap);
                        final GLSurfaceView view = (GLSurfaceView) findViewById(R.id.surfaceView);
                        view.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                        mGPUImage.saveToPictures(bitmap, "GPUImage",
                                System.currentTimeMillis() + ".jpg",
                                new OnPictureSavedListener() {

                                    @Override
                                    public void onPictureSaved(final Uri
                                                                       uri) {
                                        pictureFile.delete();
                                        camera.startPreview();
                                        view.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
                                        if(LucimeCamUser.getTakeSwitchFlag() == 2){
                                            LucimeCamUser.setOutputImage(uri);
                                            Intent intentone = new Intent(SnapActivity.this, BrandActivity.class);
                                            startActivity(intentone);
                                        }
                                    }
                                });
                    }
                });
    }

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private static File getOutputMediaFile(final int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private void switchFilterTo(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            mFilter = filter;
            mGPUImage.setFilter(mFilter);
            mFilterAdjuster = new FilterAdjuster(mFilter);
        }
    }

    @Override
    public void onProgressChanged(final SeekBar seekBar, final int progress,
                                  final boolean fromUser) {
        if (mFilterAdjuster != null) {
            mFilterAdjuster.adjust(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(final SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar) {
    }

    private class CameraLoader {

        private int mCurrentCameraId = 0;
        private Camera mCameraInstance;
        private List<Camera.Size> mSupportedPreviewSizes;
        private Camera.Size sizeReqd;

        public void onResume() {
            setUpCamera(mCurrentCameraId);
        }

        public void onPause() {
            releaseCamera();
        }

        public void switchCamera() {
            releaseCamera();
            mCurrentCameraId = (mCurrentCameraId + 1) % mCameraHelper.getNumberOfCameras();
            setUpCamera(mCurrentCameraId);
        }

        private void setUpCamera(final int id) {
            mCameraInstance = getCameraInstance(id);
            Parameters parameters = mCameraInstance.getParameters();
            this.mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
            Camera.Size previewSize = (Camera.Size) this.mSupportedPreviewSizes.get(0);
            // TODO adjust by getting supportedPreviewSizes and then choosing
            // the best one for screen size (best fill screen)

            parameters.setPreviewSize(previewSize.width, previewSize.height);
            parameters.setPictureSize(previewSize.width, previewSize.height);
            parameters.setJpegQuality(100);

            if (parameters.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            mCameraInstance.setParameters(parameters);

            int orientation = mCameraHelper.getCameraDisplayOrientation(
                    SnapActivity.this, mCurrentCameraId);
            CameraInfo2 cameraInfo = new CameraInfo2();
            mCameraHelper.getCameraInfo(mCurrentCameraId, cameraInfo);
            boolean flipHorizontal = cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT;
            mGPUImage.setUpCamera(mCameraInstance, orientation, flipHorizontal, false);
        }

        /** A safe way to get an instance of the Camera object. */
        private Camera getCameraInstance(final int id) {
            Camera c = null;
            try {
                c = mCameraHelper.openCamera(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return c;
        }

        private void releaseCamera() {
            mCameraInstance.setPreviewCallback(null);
            mCameraInstance.release();
            mCameraInstance = null;
        }
    }
}
