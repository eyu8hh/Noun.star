# Noun Star — مشروع Android (WebView، يعمل دون إنترنت)

التطبيق يعرض index.html من داخل الحزمة، والبيانات في IndexedDB على الهاتف (تبقى بعد إغلاق التطبيق وإعادة التشغيل).
الحد الأدنى: Android 10. النسخ الاحتياطية تُحفظ في: التنزيلات/NounStar.

## الطريقة 1 — بدون تثبيت أي شيء (GitHub Actions)
1. أنشئ مستودعًا على GitHub وارفع محتويات هذا المجلد كما هي.
2. افتح تبويب Actions ثم Build APK (يعمل تلقائيًا، أو Run workflow).
3. بعد دقائق نزّل NounStar-apk من أسفل صفحة التشغيل، وفيه app-debug.apk. ثبّته على الهاتف (اسمح بالتثبيت من مصدر غير معروف).

## الطريقة 2 — Android Studio
1. شغّل مرة واحدة (يحتاج Node.js وإنترنت): `bash prepare.sh` — يضع Lucide وخط Cairo داخل التطبيق.
2. افتح المجلد في Android Studio وانتظر المزامنة، ثم Build > Build APK(s).
3. الملف في app/build/outputs/apk/debug/app-debug.apk

## الطريقة 3 — سطر الأوامر
`bash prepare.sh && gradle assembleDebug` (JDK 17 وAndroid SDK وGradle 8.9)

ملاحظة: النسخة debug موقّعة بمفتاح التطوير وتصلح للتثبيت المباشر. للنشر في المتجر استخدم Build > Generate Signed Bundle/APK.
تنبيه: حذف التطبيق يمسح بياناته، لذا خذ نسخة احتياطية من المزيد > نسخة احتياطية قبل ذلك.
