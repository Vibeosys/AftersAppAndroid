package com.aftersapp.helper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.aftersapp.fragments.ImagePickHelperFragment;
import com.aftersapp.fragments.ImageSourcePickDialogFragment;

/**
 * Created by akshay on 26-09-2016.
 */
public class ImagePickHelper {

    public void pickAnImage(Fragment fragment, int requestCode) {
        ImagePickHelperFragment imagePickHelperFragment = ImagePickHelperFragment.start(fragment, requestCode);
        showImageSourcePickerDialog(fragment.getChildFragmentManager(), imagePickHelperFragment);
    }

    public void pickAnImage(FragmentActivity activity, int requestCode) {
        ImagePickHelperFragment imagePickHelperFragment = ImagePickHelperFragment.start(activity, requestCode);
        showImageSourcePickerDialog(activity.getSupportFragmentManager(), imagePickHelperFragment);
    }

    private void showImageSourcePickerDialog(FragmentManager fm, ImagePickHelperFragment fragment) {
        ImageSourcePickDialogFragment.show(fm,
                new ImageSourcePickDialogFragment.LoggableActivityImageSourcePickedListener(fragment));
    }
}
