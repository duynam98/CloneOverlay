package com.duynam.cloneoverlay.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.duynam.cloneoverlay.R;
import com.duynam.cloneoverlay.databinding.ActivityMainBinding;
import com.duynam.cloneoverlay.ui.activity.cropImage.CropImageActivity;
import com.duynam.cloneoverlay.ui.activity.main.GetImageFromDeviceAsynTask;
import com.duynam.cloneoverlay.ui.activity.main.ListImageDeviceAdapter;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GetImageFromDeviceAsynTask.OnLoadImage, ListImageDeviceAdapter.OnClickImageDevice {

    private ActivityMainBinding mainBinding;
    private ListImageDeviceAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private GetImageFromDeviceAsynTask deviceAsynTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initRecycleView();
        initAsyncTask();
        initPermission();
    }

    private void initAsyncTask() {
        deviceAsynTask = new GetImageFromDeviceAsynTask(getApplicationContext(), this);
    }

    private void initRecycleView() {
        adapter = new ListImageDeviceAdapter(this, this);
        gridLayoutManager = new GridLayoutManager(this, 3);
        mainBinding.rvListImage.setLayoutManager(gridLayoutManager);
        mainBinding.rvListImage.setAdapter(adapter);
    }

    private void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                deviceAsynTask.execute();
            } else if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            deviceAsynTask.execute();
        } else {
            initPermission();
        }
    }

    @Override
    public void onLoadFinish(List<String> data) {
        adapter.setData(data);
    }

    @Override
    public void onClickImage(String patch) {
        CropImage.activity(Uri.fromFile(new File(patch)))
                .start(this);
    }

    private void start(String a) {
        Intent intent = new Intent(this, CropImageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("data", a);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                start(resultUri.toString());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}