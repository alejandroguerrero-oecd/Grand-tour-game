# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class com.grandtour.**$$serializer { *; }
-keepclassmembers class com.grandtour.** {
    *** Companion;
}
-keepclasseswithmembers class com.grandtour.** {
    kotlinx.serialization.KSerializer serializer(...);
}
