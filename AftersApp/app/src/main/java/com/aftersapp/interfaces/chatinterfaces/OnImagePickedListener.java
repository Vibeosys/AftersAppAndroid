package com.aftersapp.interfaces.chatinterfaces;

import java.io.File;

/**
 * Created by akshay on 26-09-2016.
 */
public interface OnImagePickedListener {
    void onImagePicked(int requestCode, File file);

    void onImagePickError(int requestCode, Exception e);

    void onImagePickClosed(int requestCode);
}
