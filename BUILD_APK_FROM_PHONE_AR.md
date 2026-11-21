# بناء LINKO APK من الموبايل بدون Android Studio

1. أنشئ حسابًا مجانيًا على https://github.com إذا لم يكن لديك حساب.
2. من GitHub اضغط + ثم New repository. سمّه `LINKO-Android` واجعله Private ثم Create repository.
3. افتح المستودع، اختر Add file ثم Upload files.
4. فك ضغط `LINKO-Android-Starter.zip` أولًا. ارفع **محتويات مجلد LINKO** إلى جذر المستودع (وليس مجلد LINKO نفسه).
   يجب أن يظهر في الجذر: `app` و`build.gradle.kts` و`settings.gradle.kts` ومجلد `.github`.
5. اضغط Commit changes.
6. افتح تبويب Actions في المستودع. اختر `Build LINKO APK` ثم Run workflow.
7. انتظر حتى تظهر علامة نجاح خضراء. افتح عملية التشغيل الناجحة، وانزل إلى Artifacts.
8. نزّل `LINKO-debug-APK.zip`، وافتحه واستخرج `app-debug.apk`، ثم ثبّته على هاتف Android.

إذا فشل البناء، افتح سجل العملية وانسخ رسالة الخطأ الأخيرة وأرسلها لي.

ملاحظات:
- هذا ينتج Debug APK للتجربة وليس إصدارًا نهائيًا موقّعًا للنشر في Google Play.
- لا ترفع مفاتيح توقيع أو كلمات مرور إلى المستودع.
- نجاح البناء لا يعني أن كل ميزات الاتصال قد اختُبرت بين هاتفين؛ يلزم اختبار فعلي لاحقًا.
