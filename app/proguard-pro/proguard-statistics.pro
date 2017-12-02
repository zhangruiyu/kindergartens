-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keep public class [您的应用包名].R$*{
public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}