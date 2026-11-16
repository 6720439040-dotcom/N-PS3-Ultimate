package com.mbuilt.nps3ultimate

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable

class MainActivity : Activity() {
    private val aps3ePackage = "aenu.aps3e"
    private val armsx3Package = "com.armsx3.play"
    private val armsx3LegacyPackage = "com.armsx3"
    private val prefs by lazy { getSharedPreferences("nps3_library", MODE_PRIVATE) }
    private var selectedCore = armsx3Package
    private var selectedUri: Uri? = null
    private lateinit var status: TextView
    private lateinit var libraryBox: LinearLayout
    private lateinit var coreSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedCore = prefs.getString("core", armsx3Package) ?: armsx3Package
        buildUi()
        refreshLibrary()
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    private fun rounded(color: Int, stroke: Int = 0): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(16).toFloat()
            if (stroke != 0) setStroke(dp(1), stroke)
        }

    private fun label(text: String, size: Float, color: Int, bold: Boolean = false): TextView =
        TextView(this).apply {
            this.text = text
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(null, Typeface.BOLD)
        }

    private fun buildUi() {
        val bg = Color.rgb(9, 13, 20)
        val panel = Color.rgb(17, 25, 37)
        val blue = Color.rgb(84, 181, 255)
        val white = Color.rgb(237, 245, 255)
        val muted = Color.rgb(145, 164, 186)

        val scroll = ScrollView(this).apply { setBackgroundColor(bg) }
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(22), dp(18), dp(30))
        }
        scroll.addView(page)
        setContentView(scroll)

        page.addView(label("N-PS3 ULTIMATE", 22f, white, true))
        page.addView(label("ANDROID LAUNCHER  •  GALAXY S24 ULTRA", 10f, blue, true).apply {
            setPadding(0, dp(4), 0, dp(18))
        })

        val hero = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(Color.rgb(20, 43, 67), Color.rgb(40, 80, 112))
            setPadding(dp(16), dp(17), dp(16), dp(17))
        }
        hero.addView(label("PS3 GAME HUB", 11f, Color.rgb(82, 224, 208), true))
        hero.addView(label("เลือกเกม แล้วเปิดเล่น\nผ่าน Emulator ที่ติดตั้งไว้", 22f, white, true).apply {
            setPadding(0, dp(8), 0, dp(6))
        })
        hero.addView(label("โหมด Balanced • ไม่แก้ไขไฟล์ Emulator", 12f, muted))
        page.addView(hero)

        page.addView(section("สถานะ Emulator", muted))
        val coreCard = card(panel)
        coreCard.addView(label("เลือก Emulator ที่จะใช้", 14f, white, true))
        coreSpinner = Spinner(this)
        val options = listOf("ARMSX3", "aPS3e")
        coreSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, options)
        coreSpinner.setSelection(if (selectedCore == aps3ePackage) 1 else 0)
        coreSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCore = if (position == 1) aps3ePackage else armsx3Package
                prefs.edit().putString("core", selectedCore).apply()
            }
        }
        coreCard.addView(coreSpinner)
        val installed = TextView(this).apply {
            textSize = 12f
            setTextColor(muted)
            text = "ARMSX3: ${if (isArmsX3Installed()) "ติดตั้งแล้ว" else "ยังไม่พบ"}   •   aPS3e: ${if (isInstalled(aps3ePackage)) "ติดตั้งแล้ว" else "ยังไม่พบ"}"
            setPadding(0, dp(8), 0, 0)
        }
        coreCard.addView(installed)
        page.addView(coreCard)

        page.addView(section("คลังเกมของฉัน", muted))
        val actions = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        actions.addView(actionButton("＋  เพิ่มไฟล์เกม (ISO / PKG)", blue) { chooseFile() })
        actions.addView(actionButton("▣  เพิ่มโฟลเดอร์เกม PS3", Color.rgb(40, 58, 78)) { chooseFolder() })
        actions.addView(actionButton("▶  เปิดเกมที่เลือก", Color.rgb(36, 157, 151)) { launchSelectedGame() })
        page.addView(actions)

        libraryBox = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        page.addView(section("รายการเกม", muted))
        page.addView(libraryBox)

        status = label("พร้อมใช้งาน • เลือก Emulator และเพิ่มเกมของคุณ", 12f, muted)
        status.setPadding(dp(2), dp(16), dp(2), 0)
        page.addView(status)

        val note = TextView(this).apply {
            text = "หมายเหตุ: N-PS3 เป็น Launcher ไม่ใช่ Emulator Core เกมจะถูกประมวลผลโดย aPS3e หรือ ARMSX3 ที่ติดตั้งแยก การเปิดไฟล์โดยตรงขึ้นกับ Intent ที่ Emulator รองรับ"
            textSize = 11f
            setTextColor(Color.rgb(243, 212, 166))
            setPadding(dp(12), dp(12), dp(12), dp(12))
            background = rounded(Color.rgb(43, 36, 24))
        }
        page.addView(note)
    }

    private fun section(title: String, color: Int) = label(title, 16f, Color.WHITE, true).apply {
        setPadding(dp(2), dp(22), 0, dp(10))
    }

    private fun card(color: Int) = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        background = rounded(color, Color.rgb(38, 56, 76))
        setPadding(dp(14), dp(14), dp(14), dp(14))
    }

    private fun actionButton(text: String, color: Int, action: () -> Unit) =
        Button(this).apply {
            this.text = text
            isAllCaps = false
            setTextColor(Color.WHITE)
            background = rounded(color)
            setOnClickListener { action() }
            val lp = LinearLayout.LayoutParams(-1, dp(52))
            lp.bottomMargin = dp(8)
            layoutParams = lp
        }

    private fun isInstalled(pkg: String): Boolean =
        try {
            packageManager.getPackageInfo(pkg, 0)
            true
        } catch (_: Exception) { false }

    private fun isArmsX3Installed(): Boolean = isInstalled(armsx3Package) || isInstalled(armsx3LegacyPackage)
    private fun installedArmsX3Package(): String? = when {
        isInstalled(armsx3Package) -> armsx3Package
        isInstalled(armsx3LegacyPackage) -> armsx3LegacyPackage
        else -> null
    }

    private fun chooseFile() {
        val i = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
                "application/octet-stream",
                "application/x-iso9660-image",
                "application/vnd.android.package-archive"
            ))
        }
        @Suppress("DEPRECATION")
        startActivityForResult(i, 101)
    }

    private fun chooseFolder() {
        val i = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        @Suppress("DEPRECATION")
        startActivityForResult(i, 102)
    }

    @Deprecated("Uses platform activity result for broad Android compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK || data?.data == null) return
        val uri = data.data!!
        try {
            val flags = data.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            contentResolver.takePersistableUriPermission(uri, flags)
        } catch (_: Exception) { }
        val kind = if (requestCode == 102) "FOLDER" else "FILE"
        val id = System.currentTimeMillis().toString()
        prefs.edit().putString("game_$id", "$kind|${uri}|${displayName(uri)}").apply()
        selectedUri = uri
        refreshLibrary()
        status.text = "เพิ่มแล้ว: ${displayName(uri)}"
    }

    private fun displayName(uri: Uri): String {
        var name = uri.lastPathSegment ?: "PS3 game"
        try {
            contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)?.use {
                if (it.moveToFirst()) name = it.getString(0) ?: name
            }
        } catch (_: Exception) { }
        return name
    }

    private fun refreshLibrary() {
        if (!::libraryBox.isInitialized) return
        libraryBox.removeAllViews()
        val entries = prefs.all.filterKeys { it.startsWith("game_") }.toSortedMap()
        if (entries.isEmpty()) {
            libraryBox.addView(label("ยังไม่มีเกม • กดเพิ่มไฟล์หรือโฟลเดอร์ด้านบน", 12f, Color.LTGRAY))
            return
        }
        entries.forEach { (key, value) ->
            val parts = (value as? String)?.split("|", limit = 3) ?: return@forEach
            if (parts.size < 3) return@forEach
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                background = rounded(Color.rgb(17, 25, 37), Color.rgb(38, 56, 76))
                setPadding(dp(12), dp(10), dp(12), dp(10))
                val title = label(parts[2], 14f, Color.WHITE, true)
                addView(title)
                addView(label("${parts[0]} • แตะเพื่อเลือก", 10f, Color.rgb(145, 164, 186)))
                setOnClickListener {
                    selectedUri = Uri.parse(parts[1])
                    status.text = "เลือกแล้ว: ${parts[2]}"
                }
                setOnLongClickListener {
                    prefs.edit().remove(key).apply()
                    refreshLibrary()
                    true
                }
            }
            val lp = LinearLayout.LayoutParams(-1, -2)
            lp.bottomMargin = dp(8)
            libraryBox.addView(row, lp)
        }
        libraryBox.addView(label("เคล็ดลับ: แตะรายการเพื่อเลือก • กดค้างเพื่อลบออกจาก Launcher", 10f, Color.GRAY))
    }

    private fun launchSelectedGame() {
        val uri = selectedUri
        val pkg = if (selectedCore == aps3ePackage) aps3ePackage else installedArmsX3Package()
        if (pkg == null || !isInstalled(pkg)) {
            status.text = "ไม่พบ Emulator ที่เลือก กรุณาติดตั้งก่อน"
            val market = if (selectedCore == aps3ePackage)
                "https://play.google.com/store/apps/details?id=aenu.aps3e"
            else "https://play.google.com/store/apps/details?id=com.armsx3.play"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(market)))
            return
        }

        if (uri != null && pkg != aps3ePackage) {
            // ARMSX3 accepts Android external launch intents. A selected document URI is
            // passed with a temporary read grant; support may depend on file type/version.
            val view = Intent(Intent.ACTION_VIEW).apply {
                setPackage(pkg)
                setDataAndType(uri, contentResolver.getType(uri) ?: "*/*")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            try {
                startActivity(view)
                status.text = "ส่งไฟล์ให้ ARMSX3 แล้ว"
                return
            } catch (_: ActivityNotFoundException) {
                status.text = "ARMSX3 ไม่รับไฟล์ชนิดนี้ในเวอร์ชันที่ติดตั้ง กำลังเปิดแอปแทน"
            } catch (e: Exception) {
                status.text = "เปิดไฟล์โดยตรงไม่สำเร็จ: ${e.message}"
            }
        } else if (uri != null && pkg == aps3ePackage) {
            status.text = "aPS3e: เปิดแอปก่อน แล้วเลือกเกมจากคลังภายใน (ยังไม่มี Intent เปิดเกมที่ยืนยันได้)"
        }

        val launch = packageManager.getLaunchIntentForPackage(pkg)
        if (launch != null) {
            try {
                startActivity(launch)
                if (uri == null) status.text = "เปิด Emulator แล้ว"
            } catch (e: Exception) {
                status.text = "เปิด Emulator ไม่สำเร็จ: ${e.message}"
            }
        } else {
            status.text = "ติดตั้ง Emulator แล้วแต่ไม่พบหน้าจอเปิดแอป"
        }
    }
}
