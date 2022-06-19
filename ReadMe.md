# FaceCase

### 2022-6-19
1.  AndroidX引入Android包的时候，在gradle.properties中加入android.enableJetifier=true 自动把android库转为AndroidX
2.  如使用config.gradle 统一配置文件。需要在build.gradle 中加入 apply from:"config.gradle"
3.  如何查看JniCrash：aarch64-linux-android-addr2line -e 库位置 错误地址  
        aarch64-linux-android-addr2line -e libimage_utils.so  000000000003fbb4 
4.  opencv:转换函数 Mat bgrMat; Mat rgbMat;  cvtColor(bgrMat,rgbMat,CV_BGR2RGB);