package com.android.facecase;

import com.android.facecase.detail.BaseActivity;
import com.android.facecase.detail.FaceActivity;
import com.android.facecase.helper.FileHelper;

import java.util.Optional;

/**
 * @author：TianLong
 * @date：2019/12/1 18:50
 */
public class Constant {
    public static String FACE_MODEL_PATH = FileHelper.getInstance().getFaceModelFolderPath();
    public static String FACE_IMAGE_PATH = FileHelper.getInstance().getFaceImageFolderPath();
    //请填写申请的APP_KEY
    public static String APP_KEY = "face-dl-test";

    public static int IMAGE_WIDTH = 480;
    public static int IMAGE_HEIGHT = 640;
}
