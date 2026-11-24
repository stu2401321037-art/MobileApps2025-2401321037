package com.example.numistracker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhotoCaptureHelper {
    private static final String STATE_PENDING = "numis_photo_pending";
    private static final String STATE_NEW_PHOTOS = "numis_new_photos";
    private final AppCompatActivity activity;
    private final ImageView preview;
    private final ActivityResultLauncher<Uri> cameraLauncher;
    private final ArrayList<String> newPhotoPaths = new ArrayList<>();
    private String pendingPhotoPath;

    public PhotoCaptureHelper(AppCompatActivity activity, ImageView preview) {
        this.activity = activity;
        this.preview = preview;
        cameraLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.TakePicture(), success -> {
                    if (pendingPhotoPath == null) return;
                    String path = pendingPhotoPath;
                    pendingPhotoPath = null;
                    File photo = new File(path);
                    if (Boolean.TRUE.equals(success) && photo.exists() && photo.length() > 0) {
                        newPhotoPaths.add(path);
                        showPreview(path);
                        Toast.makeText(activity, "Photo added", Toast.LENGTH_SHORT).show();
                    } else {
                        photo.delete();
                        Toast.makeText(activity, "Photo not captured", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void openCamera() {
        File folder = new File(activity.getFilesDir(), "photos");
        if (!folder.exists() && !folder.mkdirs()) {
            Toast.makeText(activity, "Cannot create photo folder", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            File file = File.createTempFile("numis_", ".jpg", folder);
            Uri uri = FileProvider.getUriForFile(activity,
                    activity.getPackageName() + ".fileprovider", file);
            pendingPhotoPath = file.getAbsolutePath();
            cameraLauncher.launch(uri);
        } catch (IOException | IllegalArgumentException e) {
            Toast.makeText(activity, "Cannot open camera", Toast.LENGTH_LONG).show();
        }
    }

    public List<String> getNewPhotoPaths() {
        return new ArrayList<>(newPhotoPaths);
    }

    public void showPreview(String path) {
        File file = new File(path);
        if (!file.exists()) return;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int scale = 1;
        while (options.outWidth / scale > 800 || options.outHeight / scale > 800) scale *= 2;
        options.inJustDecodeBounds = false;
        options.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        if (bitmap != null) {
            preview.setVisibility(ImageView.VISIBLE);
            preview.setImageBitmap(bitmap);
            preview.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    public void restoreState(Bundle state) {
        if (state == null) return;
        pendingPhotoPath = state.getString(STATE_PENDING);
        ArrayList<String> previous = state.getStringArrayList(STATE_NEW_PHOTOS);
        if (previous != null) {
            newPhotoPaths.clear();
            newPhotoPaths.addAll(previous);
            if (!previous.isEmpty()) showPreview(previous.get(previous.size() - 1));
        }
    }

    public void saveState(Bundle state) {
        state.putString(STATE_PENDING, pendingPhotoPath);
        state.putStringArrayList(STATE_NEW_PHOTOS, new ArrayList<>(newPhotoPaths));
    }
}