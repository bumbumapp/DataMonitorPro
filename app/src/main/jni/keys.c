//
// Created by drnoob on 04/05/22.
//

#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_drnoob_datamonitor_ui_fragments_NetworkDiagnosticsFragment_getApiKey(JNIEnv *env,
                                                                              jobject instance) {
    // TODO: implement getApiKey()
    return (*env)->  NewStringUTF(env, "f7a45ced624d3a70-1df5b7cd427370f7-b91ee21d6cb22d7b"); // F-Droid // Replace with Base64 encoded Api key from https://ipinfo.io/
}