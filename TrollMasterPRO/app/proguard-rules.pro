# TrollMaster PRO ProGuard rules

# Keep OkHttp
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Keep data models
-keep class com.trollmaster.pro.data.model.** { *; }

# Keep JSON
-keep class org.json.** { *; }

# General Android
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions
