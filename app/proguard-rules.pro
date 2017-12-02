-basedirectory proguard-pro
-include proguard-umeng.pro
-include proguard-umeng-push.pro


# start ------BaseRecyclerViewAdapterHelper
-keep class com.chad.library.adapter.** {
   *;
}
# end ------BaseRecyclerViewAdapterHelper

# start ------glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# end ------glide

# start ------ali oss
-keep class com.alibaba.sdk.android.oss.** { *; }
-dontwarn okio.**
-dontwarn org.apache.commons.codec.binary.**
# start ------ali oss

# start ------萤石云
-dontwarn com.ezviz.player.**
-keep class com.ezviz.player.** { *;}

-dontwarn com.ezviz.statistics.**
-keep class com.ezviz.statistics.** { *;}

-dontwarn com.ezviz.stream.**
-keep class com.ezviz.stream.** { *;}

-dontwarn com.hik.**
-keep class com.hik.** { *;}

-dontwarn com.hikvision.**
-keep class com.hikvision.** { *;}

-dontwarn com.videogo.**
-keep class com.videogo.** { *;}

-dontwarn com.videogo.**
-keep class org.MediaPlayer.PlayM4.** { *;}

#Gson混淆配置
-keepattributes Annotation
-keep class sun.misc.Unsafe { *; }
-keep class com.idea.fifaalarmclock.entity.*
-keep class com.google.gson.stream.* { *; }

-dontwarn com.videogo.**
-keep class org.MediaPlayer.PlayM4.** { *;}
# start ------萤石云