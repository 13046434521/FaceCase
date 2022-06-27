package com.android.facecase.helper;

import android.content.Context;
import android.util.Log;

import com.imi.camera.camera.ImiCamera;
import com.imi.sdk.face.Constant;
import com.imi.sdk.face.FaceQuality;
import com.imi.sdk.face.LivenessMode;
import com.imi.sdk.face.OnSessionInitializeListener;
import com.imi.sdk.face.Quality;
import com.imi.sdk.face.QualityMode;
import com.imi.sdk.face.QualityThreshold;
import com.imi.sdk.face.Session;
import com.imi.sdk.face.SessionConfig;
import com.imi.sdk.faceid.dl.internal.QualityOption;

import static com.android.facecase.Constant.APP_KEY;
import static com.android.facecase.Constant.FACE_MODEL_PATH;

/**
 * @author：TianLong
 * @date：2019/12/1 14:51
 * 人脸SessionHelper类 简单的封装
 */
public class SessionHelper {
    private Session mFaceSession;

    private SessionHelper() {
    }

    public static SessionHelper getInstance() {
        return SessionHelperHolder.SESSION_HELPER;
    }

    public void initSession(Context context, OnSessionInitializeListener listener, SessionConfig sessionConfig) {
        mFaceSession = new Session(context);
        mFaceSession.configure(sessionConfig);

        mFaceSession.initializeAsync(listener);
    }

    public SessionConfig getSessionConfig() {
        SessionConfig sessionConfig = new SessionConfig();
        sessionConfig.camera = ImiCamera.getInstance();
        // 授权信息
        sessionConfig.appKey = APP_KEY;
        sessionConfig.livenessMode = LivenessMode.FAR;

        sessionConfig.isCpuAuthorization = false;
        sessionConfig.isIRExposure = false;
        Constant.UUID="IMIH-A200CPSV7F5T56047";
        // 设置相应文件路径
        sessionConfig.modelPath = FACE_MODEL_PATH;
        QualityOption mQualityOption = new QualityOption();
        mQualityOption.setValidRoll(20);
        mQualityOption.setValidPitch(20);
        mQualityOption.setValidYaw(20);
        mQualityOption.setEyeCloseFactor(0.58f);
        mQualityOption.setMouthOpenFactor(0.5f);
        mQualityOption.setValidSizeRate(0.6f);
        mQualityOption.setBlur(0.5f);
        mQualityOption.setColorBrightLight(0.9f);
        mQualityOption.setColorDarkLight(0.1f);
        mQualityOption.setEyeDistance(60);
        sessionConfig.mQualityOption = mQualityOption;
        sessionConfig.mQualityMode = QualityMode.SINGLE_RESULT;
        sessionConfig.mQualityThreshold = new QualityThreshold.QualityThresholdBuilder().setBlur(0.5f)
                .setPitchUp(-20).setPitchDown(20).setYawLeft(-20).setYawRight(20)
                .setYawLeft(-20).setYawRight(20).setEyeDistance(20)
                .setMouthOpen(0.5f).setEyeClose(0.5f).setFaceMask(0.5f).setDarkGlasses(0.3f).setNormalGlasses(0.3f)
                .setForeheadCover(1).setLeftEyeCover(0.5f).setRightEyeCover(0.5f).setNoseCover(0.5f).setMouthCover(0.5f)
                .setChinCover(0.5f).setLeftFaceCover(0.5f).setRightFaceCover(0.5f).setColorBrightLight(0.9f).setColorDarkLight(0.1f).build();
        Log.d("测试",sessionConfig.mQualityThreshold.toString());
        return sessionConfig;
    }

    public Session getFaceSession() {
        return mFaceSession;
    }


    private static class SessionHelperHolder {
        private static final SessionHelper SESSION_HELPER = new SessionHelper();
    }
}
