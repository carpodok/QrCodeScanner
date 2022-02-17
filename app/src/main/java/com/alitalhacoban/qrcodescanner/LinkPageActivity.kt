package com.alitalhacoban.qrcodescanner

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import io.github.ponnamkarthik.richlinkpreview.RichLinkViewTelegram
import io.github.ponnamkarthik.richlinkpreview.ViewListener
import java.lang.Exception
import java.lang.StringBuilder

class LinkPageActivity : AppCompatActivity() {

    lateinit var goWebsiteBtn: Button
    lateinit var goBackToScannerBtn: Button
    lateinit var linkTextView: TextView
    lateinit var copyIcon: ImageView

    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link_page)

        val intent = intent

        val link = intent.getStringExtra("link")?.let { checkAndGetUrl(it) }

        val richLinkViewTelegram: RichLinkViewTelegram = findViewById(R.id.richLinkViewTelegram)


        richLinkViewTelegram.setLink(link, object : ViewListener {
            override fun onSuccess(status: Boolean) {
            }

            override fun onError(e: Exception) {
            }
        })

        linkTextView = findViewById(R.id.linkTextView)
        goWebsiteBtn = findViewById(R.id.goToWebsiteBtn)
        goBackToScannerBtn = findViewById(R.id.goBackScanner)
        copyIcon = findViewById(R.id.copyIcon)
       // progressBar = findViewById(R.id.progressBar)

        linkTextView.text = link

        copyIcon.setOnClickListener {
            if (link != null) {
                copyText(link)
            }
        }

        goWebsiteBtn.setOnClickListener {
            if (link != null) {
                intentToBrowser(link)
            }
        }

        goBackToScannerBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun checkAndGetUrl(url: String): String {
        var url = url
        if (url[4] != 's') {
            url = StringBuilder(url).insert(4, "s").toString()
        }
        return url
    }


    private fun intentToBrowser(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    private fun copyText(text: String) {
        val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("copy_text", text)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(applicationContext, "Copied", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        val intent = Intent(this@LinkPageActivity, ScanPageActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }


}
