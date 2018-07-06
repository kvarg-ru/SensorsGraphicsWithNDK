#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring

JNICALL
Java_android_academy_spb_sensorsgraphicswithndk_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jfloatArray

JNICALL
Java_android_academy_spb_sensorsgraphicswithndk_MainActivity_nativeAverageFloatArray(
        JNIEnv *env,
        jobject jobj,
        jfloatArray jfloatArray1,
        jfloatArray jfloatArray2,
        jfloatArray jfloatArray3) {

    jfloatArray result;
    result = env->NewFloatArray(3);

    jsize arrLen1 = env->GetArrayLength(jfloatArray1);
    jsize arrLen2 = env->GetArrayLength(jfloatArray2);
    jsize arrLen3 = env->GetArrayLength(jfloatArray3);

    jfloat tempResult[3] = {(jfloat)arrLen1, (jfloat)arrLen2, (jfloat)arrLen3};

    jfloat* arr1 = env->GetFloatArrayElements(jfloatArray1, 0);
    jfloat* arr2 = env->GetFloatArrayElements(jfloatArray2, 0);
    jfloat* arr3 = env->GetFloatArrayElements(jfloatArray3, 0);

    env->ReleaseFloatArrayElements(jfloatArray1, arr1, 0);
    env->ReleaseFloatArrayElements(jfloatArray2, arr2, 0);
    env->ReleaseFloatArrayElements(jfloatArray3, arr3, 0);

    env->SetFloatArrayRegion(result,0,3,tempResult);

    return result;

}

jfloat averageFloatArray(jfloat array[], jsize size) {
    jfloat average_result = 0;
    for (int i = 0; i < size; i++) {
        average_result += array[i];
    }
    return average_result/size;
}

extern "C"
JNIEXPORT jfloatArray

JNICALL
Java_android_academy_spb_sensorsgraphicswithndk_SensorsController_nativeAverageFloatArray(
        JNIEnv *env,
        jobject jobj,
        jfloatArray jfloatArray1,
        jfloatArray jfloatArray2,
        jfloatArray jfloatArray3,
        jsize arraysSize) {

    jfloatArray result;
    result = env->NewFloatArray(3);

    //jclass Exception = env->FindClass("java.lang.Exception");
    //env->ThrowNew(Exception, "Something shit in JNI Function");

    if (result == nullptr) {
        return result;
    }

    jfloat* arr1 = env->GetFloatArrayElements(jfloatArray1, 0);
    jfloat* arr2 = env->GetFloatArrayElements(jfloatArray2, 0);
    jfloat* arr3 = env->GetFloatArrayElements(jfloatArray3, 0);

    jfloat tempResult[3];
    tempResult[0] = averageFloatArray(arr1, arraysSize);
    tempResult[1] = averageFloatArray(arr2, arraysSize);
    tempResult[2] = averageFloatArray(arr3, arraysSize);

    env->ReleaseFloatArrayElements(jfloatArray1, arr1, 0);
    env->ReleaseFloatArrayElements(jfloatArray2, arr2, 0);
    env->ReleaseFloatArrayElements(jfloatArray3, arr3, 0);

    env->SetFloatArrayRegion(result,0,3,tempResult);

    return result;

}