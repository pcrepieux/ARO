/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_att_aro_libimobiledevice_ScreencaptureImpl */

#ifndef _Included_com_att_aro_libimobiledevice_ScreencaptureImpl
#define _Included_com_att_aro_libimobiledevice_ScreencaptureImpl
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_att_aro_libimobiledevice_ScreencaptureImpl
 * Method:    startService
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_att_aro_libimobiledevice_ScreencaptureImpl_startService
  (JNIEnv *, jobject);

/*
 * Class:     com_att_aro_libimobiledevice_ScreencaptureImpl
 * Method:    captureScreen
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_att_aro_libimobiledevice_ScreencaptureImpl_captureScreen
  (JNIEnv *, jobject);

/*
 * Class:     com_att_aro_libimobiledevice_ScreencaptureImpl
 * Method:    stopService
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_att_aro_libimobiledevice_ScreencaptureImpl_stopService
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif