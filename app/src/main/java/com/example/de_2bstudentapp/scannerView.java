package com.example.de_2bstudentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Size;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Controller.controller;
import ResponseModel.responseModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class scannerView extends AppCompatActivity {

    ListenableFuture cameraProviderFuture;
    ExecutorService cameraExecutor;
    PreviewView camPreview;
    MyImageAnalizer analyzer;
    boolean isProcessed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_scanner_view);

        camPreview = findViewById(R.id.camPreview);
        this.getWindow().setFlags(1024,1024);

        cameraExecutor = Executors.newSingleThreadExecutor();
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        analyzer = new MyImageAnalizer(getSupportFragmentManager());

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    if (ActivityCompat.checkSelfPermission(scannerView.this, Manifest.permission.CAMERA) != (PackageManager.PERMISSION_GRANTED)) {
                        ActivityCompat.requestPermissions(scannerView.this,new String[]{Manifest.permission.CAMERA},101);
                    } else {
                        ProcessCameraProvider processCameraProvider = (ProcessCameraProvider) cameraProviderFuture.get();
                        bindPreview(processCameraProvider);
                    }

                } catch (Exception e){
                    System.out.println(e);
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0) {
            ProcessCameraProvider processCameraProvider = null;
            try {
                processCameraProvider = (ProcessCameraProvider) cameraProviderFuture.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            bindPreview(processCameraProvider);
        }
    }

    private void bindPreview(ProcessCameraProvider processCameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(camPreview.getSurfaceProvider());
        ImageCapture imageCapture = new ImageCapture.Builder().build();
        processCameraProvider.unbindAll();
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1280,720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        imageAnalysis.setAnalyzer(cameraExecutor, analyzer);
        processCameraProvider.bindToLifecycle(this,cameraSelector,preview,imageCapture,imageAnalysis);
    }

    public class MyImageAnalizer implements ImageAnalysis.Analyzer {
        private FragmentManager fragmentManager;
        public MyImageAnalizer(FragmentManager fragmentManager) {
            this.fragmentManager = fragmentManager;
        }

        @Override
        public void analyze(@NonNull ImageProxy image) {
            scanQRCode(image);
        }

        private void scanQRCode(ImageProxy image) {
            @SuppressLint("UnsafeOptInUsageError") Image image1 = image.getImage();
            assert image1 != null;
            InputImage inputImage = InputImage.fromMediaImage(image1,image.getImageInfo().getRotationDegrees());
            BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_AZTEC)
                    .build();

            BarcodeScanner scanner = BarcodeScanning.getClient();
            Task<List<Barcode>> result = scanner.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                        @Override
                        public void onSuccess(List<Barcode> barcodes) {
                            if (!isProcessed) {
                                readQRCodeData(barcodes);
                            }
                            // Task completed successfully
                            // ...
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Task failed with an exception
                            // ...
                        }
                    }).addOnCompleteListener(new OnCompleteListener<List<Barcode>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<Barcode>> task) {
                            image.close();
                        }
                    });

        }
    }

    private void readQRCodeData(List<Barcode> barcodes) {

        String txt = "";
        for (Barcode barcode: barcodes) {

            Rect bounds = barcode.getBoundingBox();
            Point[] corners = barcode.getCornerPoints();

            String rawValue = barcode.getRawValue();

            int valueType = barcode.getValueType();

            // See API reference for complete list of supported types
            switch (valueType) {
                case Barcode.TYPE_TEXT:
                    txt = barcode.getDisplayValue();
//                        System.out.println( "++++++++++++++++++++++++++++++++    " + txt);
//                        onBackPressed();
                    break;
            }
        }



        if (txt.startsWith("TOC") || txt.startsWith("MI") || txt.startsWith("WP")  || txt.startsWith("AJ")
                || txt.startsWith("CPDP") || txt.startsWith("IOT")) {
//            secondActivity.lectureDetails = txt;
//            System.out.println( "++++++++++++++++++++++++++++++++    " + txt);

//            Intent intent = new Intent(scannerView.this,scanActivity.class);
//            intent.putExtra("subject_name",txt);
//            startActivity(intent);
//            finish();
            isProcessed = true;
            String subject_name = txt.toLowerCase();
            addAttendance(subject_name);

        }
        else if (!txt.equals("")){
            Toast.makeText(this, "You scanned wrong QR code", Toast.LENGTH_SHORT).show();
        }
    }

    private void addAttendance(String subject_name) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String temp = sp.getString("Enrollment_Number","0");
        Call<responseModel> call = controller.getInstance().getAPI().addAttendance(subject_name,temp);
        call.enqueue(new Callback<responseModel>() {
            @Override
            public void onResponse(Call<responseModel> call, Response<responseModel> response) {
                responseModel obj = response.body();
                String output = obj.getAttendanceMsg();
                if (output.equals("added")){
                    Toast.makeText(scannerView.this, "Attendance Recorded", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(scannerView.this,scanActivity.class));
                }

                if (output.equals("exist")){
                    Toast.makeText(scannerView.this, "Attendance already Recorded", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(scannerView.this,scanActivity.class));
                }
            }

            @Override
            public void onFailure(Call<responseModel> call, Throwable t) {
                Log.d("try2",t.getMessage());
            }
        });
    }
}