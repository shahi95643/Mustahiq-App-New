# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# Keep Retrofit interfaces
-keep interface * { *; }

# Keep Retrofit annotations
-keepattributes *Annotation*

# Keep Retrofit method parameters
-keepclassmembers class * {
    @retrofit2.http.* <methods>;
}

# Keep data classes for Gson serialization/deserialization
-keepclassmembers class com.kpitb.mustahiq.update.models.** {
    <fields>;
}

# Keep Gson model classes (Replace 'com.yourpackage.model' with your actual package)
-keep class com.kpitb.mustahiq.update.models.** { *; }
-keepnames class com.kpitb.mustahiq.update.models.** { *; }

# Keep Retrofit response classes
-keep class retrofit2.** { *; }

# Keep OkHttp classes
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# Keep Gson converter
-keep class com.google.gson.** { *; }

# Keep generated adapters
-keep class *$GsonAdapter { *; }