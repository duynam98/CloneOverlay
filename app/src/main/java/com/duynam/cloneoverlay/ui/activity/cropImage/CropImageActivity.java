package com.duynam.cloneoverlay.ui.activity.cropImage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.duynam.cloneoverlay.R;
import com.duynam.cloneoverlay.databinding.ActivityCropImageBinding;
import com.duynam.cloneoverlay.ui.cropview.HorizontalProgressWheelView;

import java.io.IOException;

public class CropImageActivity extends AppCompatActivity {

    private String patch;
    private ActivityCropImageBinding cropImageBinding;
    private Matrix matrix;
    private int w, h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cropImageBinding = DataBindingUtil.setContentView(this, R.layout.activity_crop_image);
        initImageContainer();
        rotate();
    }

    private void initImageContainer() {
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            patch = bundle.getString("data");
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(patch));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Glide.with(this).load(Uri.parse(patch)).into(cropImageBinding.layoutCrop);
            cropImageBinding.layoutCrop.setImageBitmap(bitmap);
        }
    }

    public void setImageCenterAfterCrop(int w, int h) {
        matrix = cropImageBinding.layoutCrop.getMatrix();
        Drawable d = cropImageBinding.layoutCrop.getDrawable();
        RectF imageRectF = new RectF(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        RectF viewRectF = new RectF(0, 0, w, h);
        matrix.setRectToRect(imageRectF, viewRectF, Matrix.ScaleToFit.CENTER);
        cropImageBinding.layoutCrop.setImageMatrix(matrix);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        w = cropImageBinding.layoutCrop.getWidth();
        h = cropImageBinding.layoutCrop.getHeight();
        setImageCenterAfterCrop(w, h);
    }

    private void rotate(){
        cropImageBinding.viewBottom.setScrollingListener(new HorizontalProgressWheelView.ScrollingListener() {
            @Override
            public void onScrollStart() {

            }

            @Override
            public void onScroll(float delta, float totalDistance) {
                matrix.postRotate(delta/42, w/2f, h/2f);
                cropImageBinding.layoutCrop.setImageMatrix(matrix);
            }

            @Override
            public void onScrollEnd() {

            }
        });
    }

}