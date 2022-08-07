package com.practicalconnectlab.app.ui.podcast.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import com.practicalconnectlab.app.R
import com.practicalconnectlab.app.base.BaseViewModelFragment
import com.practicalconnectlab.app.ui.podcast.AudioRecorderActivity
import com.practicalconnectlab.app.ui.podcast.models.PodCastModel
import com.practicalconnectlab.app.ui.podcast.viewmodels.PodCastVM
import com.tatwadeep.phonicplayer.handler.PlayerListObserver
import kotlinx.android.synthetic.main.fragment_podcast.*


class PodCastFragment : BaseViewModelFragment<PodCastVM>(),
    PickiTCallbacks {

    private var pickIT: PickiT? = null
    private var audioButtonType = ""

    override fun buildViewModel(): PodCastVM {
        return ViewModelProvider(this)[PodCastVM::class.java]
    }

    override fun getContentResource(): Int = R.layout.fragment_podcast

    override fun initViews() {
        super.initViews()
        pickIT = PickiT(activity, this, activity)

        setRecyclerViewAdapters()

        initClickListeners()
    }

    private fun setRecyclerViewAdapters() {
        rcvPodCasts.adapter = viewModel.adapter
        PlayerListObserver.instance?.registerLifecycle(lifecycle)
    }

    private fun initClickListeners() {
        lytRecordAudio.setOnClickListener {
            audioButtonType = "Record Audio"
            permissionsBuilder(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).build().send {
                if (it.allGranted()) {
                    recordAudio()
                }
            }
        }
        lytImportAudio.setOnClickListener {
            audioButtonType = "Import File"
            permissionsBuilder(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).build().send {
                if (it.allGranted()) {
                    getTheAudioFromFolders()
                }
            }
        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.let {
                    val uri: Uri? = it.data
                    pickIT?.getPath(uri, Build.VERSION.SDK_INT)
                }
            }
        }

    private fun getTheAudioFromFolders() {
        val intentUpload = Intent()
        intentUpload.type = "audio/*"
        intentUpload.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(intentUpload)
    }

    private var resultLauncherForAudio =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.let {
                    val filePath: String = it.getStringExtra("file_path").toString()
                    val localFileName: String = it.getStringExtra("file_name").toString()
                    addPodCast(filePath, localFileName)
                }
            }
        }

    private fun recordAudio() {
        val audioIntent = Intent(activity, AudioRecorderActivity::class.java)
        resultLauncherForAudio.launch(audioIntent)
    }

    override fun PickiTonUriReturned() {

    }

    override fun PickiTonStartListener() {

    }

    override fun PickiTonProgressUpdate(progress: Int) {

    }

    override fun PickiTonCompleteListener(
        path: String?,
        wasDriveFile: Boolean,
        wasUnknownProvider: Boolean,
        wasSuccessful: Boolean,
        Reason: String?
    ) {
        Log.e("path ", path.toString())
        path?.let {
            addPodCast(it)
        }
    }

    private fun addPodCast(it: String, localFileName: String = "") {
        imgMicPlaceHolder.visibility = View.GONE
        rcvPodCasts.visibility = View.VISIBLE
        val uri = Uri.parse(it)
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(context, uri)
        val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        var time = ""
        durationStr?.let { duration ->
            time = formatMilliSecond(duration.toLong())
        }

        var fileName: String = it.substring(it.lastIndexOf("/") + 1)
        if (localFileName.isNotEmpty()) {
            val fileNameExt = fileName.split(".")[1]
            val finalName = "$localFileName.$fileNameExt"
            fileName = finalName
        }
        viewModel.adapter.addItem(
            PodCastModel(
                podCastTitle = fileName,
                podCastTotalTime = time,
                podCastPath = it,
                podCastSaveTime = System.currentTimeMillis()
            )
        )
    }

    override fun PickiTonMultipleCompleteListener(
        paths: ArrayList<String>?,
        wasSuccessful: Boolean,
        Reason: String?
    ) {

    }

    private fun formatMilliSecond(milliseconds: Long): String {
        var finalTimerString = ""
        val secondsString: String

        // Convert total duration into time
        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()

        // Add hours if there
        if (hours > 0) {
            finalTimerString = "$hours:"
        }

        // Prepending 0 to seconds if it is one digit
        secondsString = if (seconds < 10) {
            "0$seconds"
        } else {
            "" + seconds
        }
        finalTimerString = "$finalTimerString$minutes:$secondsString"
        return finalTimerString
    }
}