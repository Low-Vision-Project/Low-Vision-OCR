package com.lowvision.ocr

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_text.*

class TextActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text)

        // Get The Text
        val resultText = intent.getStringExtra("photo_result")
        resultTextView.text = resultText

        // Back Button On Action Bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    // Override Function For Back Button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }


}