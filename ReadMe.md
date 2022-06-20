# FaceCase

### 2022-6-19
1. AndroidX引入Android包的时候，在gradle.properties中加入android.enableJetifier=true 自动把android库转为AndroidX
2. 如使用config.gradle 统一配置文件。需要在build.gradle 中加入 apply from:"config.gradle"
3. 如何查看JniCrash：aarch64-linux-android-addr2line -e 库位置 错误地址  
   aarch64-linux-android-addr2line -e libimage_utils.so 000000000003fbb4
4. opencv:转换函数 Mat bgrMat; Mat rgbMat; cvtColor(bgrMat,rgbMat,CV_BGR2RGB);
5. 推送tag：git push origin 1.0.0
6. 导入第三方库：settings.gradle中：
```groovy
   include ':androidUtils' 
   project(':androidUtils').projectDir= file('F:\\HJIMI\\AndroidProjectDemo\\ImageUtils\\androidutils')
```
7.  除去多余的so库:build.gradle
```groovy
    packagingOptions {
        pickFirst "lib/armeabi-v7a/libopencv_java3.so"
        pickFirst "lib/arm64-v8a/libopencv_java3.so"
    }
```
### 2022-6-20