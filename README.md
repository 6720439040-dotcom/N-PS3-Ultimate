# N-PS3 Ultimate — Android Launcher (v0.2.0)

โปรเจกต์ Android Studio (Kotlin) สำหรับ Samsung Galaxy S24 Ultra / Android ARM64

## สิ่งที่ทำได้ในรุ่นนี้
- ตรวจจับแอป aPS3e (`aenu.aps3e`) และ ARMSX3 (`com.armsx3.play`, with legacy fallback `com.armsx3`)
- เลือก Emulator หลัก
- เพิ่มไฟล์เกมผ่าน Android Storage Access Framework
- เพิ่มโฟลเดอร์เกมผ่าน SAF
- เก็บ URI ที่ผู้ใช้เลือกไว้ใน SharedPreferences
- เปิดแอป Emulator ที่ติดตั้ง
- ทดลองส่งไฟล์ที่เลือกไป ARMSX3 ด้วย `ACTION_VIEW` และ read permission grant
- สำหรับ aPS3e จะเปิดแอปแล้วให้ผู้ใช้เลือกเกมจากคลังใน Emulator เนื่องจากยังไม่มี external game-launch intent ที่ยืนยันได้จากเอกสารสาธารณะ

## ข้อจำกัดที่ต้องทราบ
นี่คือ **Launcher** ไม่ใช่ PS3 emulator core. เกมจะรันโดย aPS3e หรือ ARMSX3 ที่ติดตั้งแยกเท่านั้น
การส่ง `ACTION_VIEW` ไป ARMSX3 อาจขึ้นกับเวอร์ชันและชนิดไฟล์ที่ติดตั้ง และต้องทดสอบบนเครื่องจริง
การเลือกโฟลเดอร์ด้วย SAF ไม่ได้แปลว่า Emulator ปลายทางจะได้รับสิทธิ์อ่าน URI นั้นโดยอัตโนมัติในทุกเวอร์ชัน
ยังไม่มี API สาธารณะที่ยืนยันได้สำหรับสั่งปรับค่า FPS/CPU/SPU/อุณหภูมิใน Emulator จาก Launcher นี้
ไม่มีเกม, firmware, BIOS หรือ proprietary files รวมอยู่ในโปรเจกต์

## Package IDs ที่ใช้
- aPS3e: `aenu.aps3e`
- ARMSX3: `com.armsx3`
- ARMSX3 launcher activity (known integration reference): `com.armsx2.MainActivity`

## Build ด้วย Android Studio
1. ติดตั้ง Android Studio รุ่นปัจจุบัน
2. เปิดโฟลเดอร์ `N-PS3-Ultimate-Android`
3. ให้ Android Studio ติดตั้ง Android SDK Platform 35 และ JDK 17 ตามที่โปรเจกต์ร้องขอ
4. Sync Gradle
5. เลือก Build > Build Bundle(s) / APK(s) > Build APK(s)
6. APK debug จะอยู่ที่ `app/build/outputs/apk/debug/app-debug.apk`

หรือใช้ terminal จากโฟลเดอร์โปรเจกต์:
```bash
gradle assembleDebug
```
หากมี Gradle wrapper ใน Android Studio ให้ใช้ `./gradlew assembleDebug` (Windows: `gradlew.bat assembleDebug`).

## ทดสอบบน S24 Ultra
1. ติดตั้ง aPS3e และ ARMSX3 จากแหล่งทางการ/Google Play
2. เปิดแต่ละ Emulator หนึ่งครั้งเพื่อให้สร้างโฟลเดอร์และตั้งค่าเริ่มต้น
3. ติดตั้ง PS3 firmware ที่ได้มาอย่างถูกต้องใน Emulator แต่ละตัว
4. เพิ่มเกมที่คุณมีสิทธิ์ใช้งานใน Emulator
5. เปิด N-PS3 Ultimate, เลือก Core และเพิ่ม ISO/PKG หรือโฟลเดอร์เกม
6. ทดสอบ ARMSX3 file handoff; หากไม่รองรับ ให้เปิด Emulator แล้วเลือกเกมในตัวแอป
7. ตรวจสอบ logcat หาก Android ปฏิเสธ Intent หรือ URI grant

## Licensing
Launcher code ในโปรเจกต์นี้เป็นตัวอย่างใหม่ ไม่มีการคัดลอก source code ของ aPS3e หรือ ARMSX3
aPS3e และ ARMSX3 เป็นโครงการแยกที่มีเงื่อนไขไลเซนส์ของตนเอง
หากนำโค้ด GPL ของ Emulator มารวม แจกจ่าย หรือแก้ไข ต้องปฏิบัติตาม GPL-2.0 และข้อกำหนดของไฟล์แต่ละส่วน
