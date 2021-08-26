package com.jawaid.liobrowser.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.zxing.Result;
import com.jawaid.liobrowser.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.CAMERA;

public class BarcodeActivity extends Activity implements ZXingScannerView.ResultHandler,EasyPermissions.PermissionCallbacks {
    private static final int CAMERA_PERMISSION_CODE = 102;
    private ZXingScannerView scannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_barcode);

        scannerView=findViewById(R.id.scannerView);
        scannerView.setAspectTolerance(0.5f);

        getPermissions();

    }

    @AfterPermissionGranted(123)
    private void getPermissions() {
        String[] perms = {CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            onStart();
        } else {
            EasyPermissions.requestPermissions(this, "We need permissions to make app usefull for you",
                    123, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

        onStart();
    }
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        scannerView.startCamera();          // Start camera on resume
    }


    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {

        String massage="Text: "+rawResult.getText()+" \n"+
                "Code Format: "+rawResult.getBarcodeFormat().toString()+" \n"+
                "Metadata: "+rawResult.getResultMetadata().toString()+" \n"+
                "Result Points: "+rawResult.getResultPoints().toString()+" \n"+
                "Name: "+rawResult.getClass().getName()+" \n";



        new AlertDialog.Builder(BarcodeActivity.this)
                .setTitle("Results")
                .setMessage(massage)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("Search On Internet", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent=new Intent(BarcodeActivity.this,WebViewActivity.class);
                        intent.putExtra("urllink",rawResult.getText());
                        startActivity(intent);
                        finish();

                    }})
                .setNegativeButton(android.R.string.no, null).show();

        // If you would like to resume scanning, call this method below:
        //scannerView.resumeCameraPreview(this);
    }


}