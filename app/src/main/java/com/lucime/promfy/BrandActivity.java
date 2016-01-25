package com.lucime.promfy;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.GPUImageView.OnPictureSavedListener;
import com.lucime.promfy.GPUImageFilterTools;
import com.lucime.promfy.GPUImageFilterTools.FilterAdjuster;
import com.lucime.promfy.GPUImageFilterTools.OnGpuImageFilterChosenListener;
import com.lucime.promfy.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import java.io.OutputStream;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class BrandActivity extends AppCompatActivity implements OnSeekBarChangeListener,
        OnClickListener, OnPictureSavedListener  {

    private static final int REQUEST_PICK_IMAGE = 1;
    private GPUImageFilter mFilter;
    private FilterAdjuster mFilterAdjuster;
    private GPUImageView mGPUImageView;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand);

        ((SeekBar) findViewById(R.id.seekBar)).setOnSeekBarChangeListener(this);
        findViewById(R.id.button_choose_filter).setOnClickListener(this);
        findViewById(R.id.button_save).setOnClickListener(this);
        findViewById(R.id.button_campaigns).setOnClickListener(this);
        findViewById(R.id.shareImage).setOnClickListener(this);
        findViewById(R.id.backCamera).setOnClickListener(this);

        mGPUImageView = (GPUImageView) findViewById(R.id.gpuimage);
        mGPUImageView.setImage(LucimeCamUser.getOutputImage());


       /* findViewById(R.id.backCamera).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.button_choose_filter:
                GPUImageFilterTools.showDialog(this, new OnGpuImageFilterChosenListener() {

                    @Override
                    public void onGpuImageFilterChosenListener(final GPUImageFilter filter) {
                        switchFilterTo(filter);
                        mGPUImageView.requestRender();
                        ((SeekBar) findViewById(R.id.seekBar)).setVisibility(View.VISIBLE);
                    }

                });
                break;
            case R.id.button_save:
                saveImage();
                Toast.makeText(BrandActivity.this, "Image saved", Toast.LENGTH_LONG).show();
                break;
            case R.id.button_campaigns:
                Toast.makeText(BrandActivity.this, "Woops! No Campaigns around!", Toast.LENGTH_LONG).show();
                break;
            case R.id.shareImage:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                Uri uri = LucimeCamUser.getOutputImage();
                shareIntent.setType("*/*");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "#LucimeAppTesting #SampleImage");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(shareIntent, "Share image using"));
                break;
            case R.id.backCamera:
                finish();
                break;

            default:
                break;
        }

    }

    @Override
    public void onPictureSaved(final Uri uri) {
        Toast.makeText(this, "Saved: " + uri.toString(), Toast.LENGTH_SHORT).show();
    }

    private void saveImage() {
        String fileName = System.currentTimeMillis() + ".jpg";
        mGPUImageView.saveToPictures("GPUImage", fileName, this);
//        mGPUImageView.saveToPictures("GPUImage", fileName, 1600, 1600, this);
    }

    private void switchFilterTo(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            mFilter = filter;
            mGPUImageView.setFilter(mFilter);
            mFilterAdjuster = new FilterAdjuster(mFilter);

            findViewById(R.id.seekBar).setVisibility(
                    mFilterAdjuster.canAdjust() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
        if (mFilterAdjuster != null) {
            mFilterAdjuster.adjust(progress);
        }
        mGPUImageView.requestRender();
    }

    @Override
    public void onStartTrackingTouch(final SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar) {
    }

    private void handleImage(final Uri selectedImage) {
        mGPUImageView.setImage(selectedImage);
    }
}
