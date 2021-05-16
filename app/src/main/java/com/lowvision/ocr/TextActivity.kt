package com.lowvision.ocr

import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_text.*
import java.util.*

class TextActivity : AppCompatActivity() , TextToSpeech.OnInitListener{

    private var tts: TextToSpeech? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text)
        tts = TextToSpeech(this, this)
        seekbar()
        spinnerBox()
        // Get The Text
        val resultText = intent.getStringExtra("photo_result")
        resultTextView.text = resultText
        readButton.setOnClickListener{
            Log.e("result",resultText!!)
                speakOut(resultText!!)
        }



        // Back Button On Action Bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        bottomSheet.clipToOutline=true
        BottomSheetBehavior.from(bottomSheet).apply {
            peekHeight=100
            this.state= BottomSheetBehavior.STATE_COLLAPSED

            val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    // Do something for new state

                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    Log.e("Slide",slideOffset.toString())
                    when (slideOffset) {
                        0f -> {
                            bottomSheet.setBackgroundColor(Color.WHITE)
                        }
                        in .1f..0.2f -> {
                            bottomSheet.setBackgroundColor(Color.parseColor("#F0FBEF"))
                        }
                        in .2f..0.3f -> {
                            bottomSheet.setBackgroundColor(Color.parseColor("#E0F7DE"))
                        }
                        in .3f..0.4f -> {
                            bottomSheet.setBackgroundColor(Color.parseColor("#D1F3CE"))
                        }
                        in .4f..0.5f -> {
                            bottomSheet.setBackgroundColor(Color.parseColor("#C1EFBD"))
                        }
                        in .5f..0.6f -> {
                            bottomSheet.setBackgroundColor(Color.parseColor("#B2EBAD"))
                        }
                        in .6f..0.7f -> {
                            bottomSheet.setBackgroundColor(Color.parseColor("#A3E79C"))
                        }
                        in .7f..0.8f -> {
                            bottomSheet.setBackgroundColor(Color.parseColor("#95E38C"))
                        }
                        in .8f..0.9f -> {
                            bottomSheet.setBackgroundColor(Color.parseColor("#86DF7C"))
                        }
                        in .9f..1f -> {
                            bottomSheet.setBackgroundColor(Color.parseColor("#76DB6B"))
                        }

                    }
                }
            }
           this.addBottomSheetCallback(bottomSheetCallback)

        }

    }
    private fun speakOut(text:String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "1")
    }

    override fun onInit(p0: Int) {
        if (p0 == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result = tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language specified is not supported!")
            } else {
                readButton!!.isEnabled = true
            }

        } else {
            Log.e("TTS", "Initilization Failed!")
        }
    }

    public override fun onDestroy() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }
    private fun seekbar() {
        seekBar.progress = 3
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                when (progress) {
                    0 -> resultTextView.textSize = 20F
                    1 -> resultTextView.textSize = 30F
                    2 -> resultTextView.textSize = 40F
                    3 -> resultTextView.textSize = 50F
                    4 -> resultTextView.textSize = 60F
                    5 -> resultTextView.textSize = 70F
                    6 -> resultTextView.textSize = 80F
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }
    private fun spinnerBox() {
        ArrayAdapter.createFromResource(
            this,
            R.array.colors_string,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        resultTextView.setBackgroundColor(Color.WHITE)
                        resultTextView.setTextColor(Color.BLACK)
                    }
                    1 -> {
                        resultTextView.setBackgroundColor(Color.WHITE)
                        resultTextView.setTextColor(Color.parseColor("#C02F1D"))
                    }
                    2 -> {
                        resultTextView.setBackgroundColor(Color.WHITE)
                        resultTextView.setTextColor(Color.parseColor("#107869"))
                    }
                    3 -> {
                        resultTextView.setBackgroundColor(Color.BLACK)
                        resultTextView.setTextColor(Color.WHITE)
                    }
                    4 -> {
                        resultTextView.setBackgroundColor(Color.BLACK)
                        resultTextView.setTextColor(Color.YELLOW)
                    }
                    5 -> {
                        resultTextView.setBackgroundColor(Color.parseColor("#C02F1D"))
                        resultTextView.setTextColor(Color.WHITE)
                    }
                    6 -> {
                        resultTextView.setBackgroundColor(Color.parseColor("#107869"))
                        resultTextView.setTextColor(Color.WHITE)
                    }
                    7 -> {
                        resultTextView.setBackgroundColor(Color.parseColor("#43ABC9"))
                        resultTextView.setTextColor(Color.WHITE)
                    }
                    8 -> {
                        resultTextView.setBackgroundColor(Color.parseColor("#093145"))
                        resultTextView.setTextColor(Color.WHITE)
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
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