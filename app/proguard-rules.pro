-basedirectory proguard-pro
-include proguard-banner.pro
-include proguard-baseadapter.pro
-include proguard-glide.pro
-include proguard-okgo.pro
-include proguard-permission.pro
-include proguard-photopicker.pro
-include proguard-statistics.pro
-include proguard-umeng.pro
-include proguard-umeng-push.pro
-include proguard-yinshi.pro

-keep class * extends com.raizlabs.android.dbflow.config.DatabaseHolder { *; }

-dontwarn javax.annotation.**

-keep public class * extends android.app.Service { *; }

-keep class * implements android.kindergartens.com.base.BaseEntity { *; }

#指定压缩级别
-optimizationpasses 5

#不跳过非公共的库的类成员
-dontskipnonpubliclibraryclassmembers

#混淆时采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#把混淆类中的方法名也混淆了
-useuniqueclassmembernames

#优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification

#将文件来源重命名为“SourceFile”字符串
-renamesourcefileattribute SourceFile
#保留行号
-keepattributes SourceFile,LineNumberTable

#保持所有实现 Serializable 接口的类成员
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#Fragment不需要在AndroidManifest.xml中注册，需要额外保护下
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment

# 保持测试相关的代码
-dontnote junit.framework.**
-dontnote junit.runner.**
-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**
# 保持java.lang包 要不跟日历控件冲突 打包会失败
-dontwarn java.lang.invoke.*
-dontnote android.net.http.*
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**