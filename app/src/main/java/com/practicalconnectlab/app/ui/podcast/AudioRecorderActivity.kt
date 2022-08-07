package com.practicalconnectlab.app.ui.podcast

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.*
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
import com.practicalconnectlab.app.R
import com.practicalconnectlab.app.base.BaseActivity
import com.practicalconnectlab.app.utils.CustomDialog
import kotlinx.android.synthetic.main.activity_audio_recorder.*
import java.io.File
import java.io.IOException

class AudioRecorderActivity : BaseActivity() {

    private var mRecorder: MediaRecorder? = null
    private var mPlayer: MediaPlayer? = null
    private var filePath: String? = null
    private var lastProgress = 0
    private var isPlaying = false
    var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_recorder)
        prepareRecording()
        startRecording()

        initClickListeners()
    }

    private fun initClickListeners() {
        imgClose.setOnClickListener {
            finish()
        }
        lytSave.setOnClickListener {
            dialog = CustomDialog(this)
            dialog?.setContentView(R.layout.dialog_save_audio)
            val btnSave = dialog?.findViewById(R.id.btnSave) as Button
            val edtPodCastName = dialog?.findViewById(R.id.edtPodCastName) as EditText

            btnSave.setOnClickListener {
                if (edtPodCastName.text.trim().isNotBlank()) {
                    val intent = Intent()
                    intent.putExtra("file_path", filePath)
                    intent.putExtra("file_name", edtPodCastName.text.trim().toString())
                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    Toast.makeText(this, "Podcast name is not empty!", Toast.LENGTH_LONG).show()
                }
            }
            (dialog as CustomDialog).showDialog()
        }
        lytUndo.setOnClickListener {
            prepareRecording()
            startRecording()
        }
        lytBtnRecord.setOnClickListener {
            prepareRecording()
            startRecording()
        }
        lytBtnStop.setOnClickListener {
            prepareStop()
            stopRecording()
        }
        imgViewPlay.setOnClickListener {
            if (!isPlaying && filePath != null) {
                isPlaying = true
                startPlaying()
            } else {
                isPlaying = false
                stopPlaying()
            }
        }
    }

    private fun prepareStop() {
        TransitionManager.beginDelayedTransition(llRecorder)
        lytRecord.visibility = View.VISIBLE
        viewCircleRed.visibility = View.GONE
        lytBtnStop.visibility = View.GONE
        llPlay.visibility = View.VISIBLE
    }

    private fun prepareRecording() {
        TransitionManager.beginDelayedTransition(llRecorder)
        lytRecord.visibility = View.GONE
        viewCircleRed.visibility = View.VISIBLE
        lytBtnStop.visibility = View.VISIBLE
        llPlay.visibility = View.GONE
    }

    private fun stopPlaying() {
        try {
            mPlayer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mPlayer = null
        //showing the play button
        imgViewPlay.setImageResource(R.drawable.ic_play_circle)
        chronometer.stop()
    }

    private fun startRecording() {
        tvAudioHeader.text = getString(R.string.lbl_recording)
        mRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(this)
        } else {
            MediaRecorder()
        }
        mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        val outputDir: File = cacheDir // context being the Activity pointer
        val outputFile = File.createTempFile("Audio_", ".mp3", outputDir)
        filePath = outputFile.path
        mRecorder?.setOutputFile(filePath)
        mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

        try {
            mRecorder?.prepare()
            mRecorder?.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        lastProgress = 0
        seekBar.progress = 0
        stopPlaying()
        // making the imageView a stop button starting the chronometer
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()
    }


    private fun stopRecording() {
        try {
            mRecorder?.stop()
            mRecorder?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mRecorder = null
        //starting the chronometer
        chronometer.stop()
        chronometer.base = SystemClock.elapsedRealtime()
        //showing the play button
        tvAudioHeader.text = getString(R.string.lbl_preview_audio)
    }


    private fun startPlaying() {
        mPlayer = MediaPlayer()
        try {
            mPlayer?.setDataSource(filePath)
            mPlayer?.prepare()
            mPlayer?.start()
        } catch (e: IOException) {
            Log.e("LOG_TAG", "prepare() failed")
        }

        //making the imageView pause button
        imgViewPlay.setImageResource(R.drawable.ic_pause_circle)

        seekBar.progress = lastProgress

        mPlayer?.seekTo(lastProgress)
        mPlayer?.let {
            seekBar.max = it.duration
        }
        seekBarUpdate()
        chronometer.start()

        mPlayer?.setOnCompletionListener {
            imgViewPlay.setImageResource(R.drawable.ic_play_circle)
            isPlaying = false
            chronometer.stop()
            chronometer.base = SystemClock.elapsedRealtime()
            mPlayer?.seekTo(0)
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (mPlayer != null && fromUser) {
                    mPlayer?.seekTo(progress)
                    mPlayer?.let {
                        chronometer.base = SystemClock.elapsedRealtime() - it.currentPosition
                    }
                    lastProgress = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun seekBarUpdate() {
        if (mPlayer != null) {
            val mCurrentPosition = mPlayer?.currentPosition
            mCurrentPosition?.let {
                seekBar.progress = it
                lastProgress = it
            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            seekBarUpdate()
        }, 100)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlaying()
        dialog?.let {
            it.dismiss()
        }
    }
}