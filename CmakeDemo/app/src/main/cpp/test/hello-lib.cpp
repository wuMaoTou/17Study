#include <jni.h>
#include <string>
#include <android/log.h>

extern "C" JNIEXPORT jstring

#define LOG "JNILOG"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG,__VA_ARGS__)

JNICALL
Java_com_maotou_cmakedemo_MainActivity_stringFromJNIWhitHelloLib(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++ with hello-lib";

    int len = 5;
    LOGI("我是log %d", len);

    return env->NewStringUTF(hello.c_str());
}
