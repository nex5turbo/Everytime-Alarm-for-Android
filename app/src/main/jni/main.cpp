//
// Created by nex5t on 2021-05-07.
//

#include <jni.h>
#include "com_example_myapplication2_OpenCvModule.h"

#include <opencv2/opencv.hpp>

extern "C" {
JNIEXPORT jintArray JNICALL Java_com_example_myapplication2_OpenCvModule_ConvertRGBtoGray
        (JNIEnv *env, jobject instance, jlong mat_addr_input, jlong mat_addr_result){

}

}