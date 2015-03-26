# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Jonathan\AppData\Local\Android\sdk1/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keepclassmembers public class * extends com.skocken.efficientadapter.lib.viewholder.AbsViewHolder {
    public <init>(...);
}

-keep class com.bluelinelabs.logansquare.** { *; }
-keep @com.bluelinelabs.logansquare.annotation.JsonObject class *
-keep class **$$JsonObjectMapper { *; }

-keep public class shared_presage.** { *; }
-keep public class com.google.android.youtube.player.** { *; }

-keepattributes *Annotation*
-keepattributes JavascriptInterface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keep public class io.presage.activities.PresageActivity

-keep public class * implements io.presage.utils.actvities.WebViewActivityHelper$WebViewInterface
-keepclassmembers class io.presage.utils.actvities.WebViewActivityHelper$WebViewInterface {
    *;
}

-keep public class * implements io.presage.utils.actvities.VideoActivityHelper$WebViewInterface
-keepclassmembers class io.presage.activities.VideoActivityHelper$WebViewInterface {
    *;
}

-keep public class * implements io.presage.utils.actvities.VideoActivityHelper$VideoJavaScriptInterface
-keepclassmembers class io.presage.utils.actvities.VideoActivityHelper$VideoJavaScriptInterface {
    *;
}

-keep public class io.presage.formats.Webviews$WebViewInterface
-keep public class * implements io.presage.formats.Webviews$WebViewInterface
-keepclassmembers class io.presage.formats.Webviews$WebViewInterface {
    *;
}
