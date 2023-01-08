package com.arlanallacsta.submissionstoryapp.story

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.coroutineScope
import com.arlanallacsta.submissionstoryapp.MainActivity
import com.arlanallacsta.submissionstoryapp.R
import com.arlanallacsta.submissionstoryapp.databinding.ActivityStoryBinding
import com.arlanallacsta.submissionstoryapp.datastore.UserPreference
import com.arlanallacsta.submissionstoryapp.utils.Result
import com.arlanallacsta.submissionstoryapp.utils.createCustomTempFile
import com.arlanallacsta.submissionstoryapp.utils.reduceFileImage
import com.arlanallacsta.submissionstoryapp.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File

@AndroidEntryPoint
class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding
    private  var getFile: File? = null
    private var job: Job = Job()
    private val viewModel: StoryViewModel by viewModels()
    private lateinit var userPreference: UserPreference
    private lateinit var currentPhotoPath: String
    private var location: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_CODE_PERMISSIONS){
            if (!allPermissionGranted()){
                setMessage(this, "Permission not allowed")
                finish()
            }
        }
    }

    private fun allPermissionGranted() = REQ_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        userPreference = UserPreference(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        permissionGranted()

        binding.cameraButton.setOnClickListener {
            startTakePhoto()
        }
        binding.galleryButton.setOnClickListener{
            startGallery()
        }
        binding.uploadButton.setOnClickListener {
            if (getFile != null || !TextUtils.isEmpty(binding.etDescription.text.toString())){
                uploadStory(userPreference.token)
            }else{
                setMessage(this, "make sure all fields are filled")
            }
        }

        binding.smShareLocation.setOnCheckedChangeListener {_, isChecked ->
            if(isChecked) {
                getLastLocation()
            } else {
                location = null
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        Timber.d("$permissions")
        when {
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getLastLocation()
            }
            else -> {
                setMessage(this, resources.getString(R.string.permission_location_warning))
                binding.smShareLocation.isChecked = false
            }
        }
    }

    private fun permissionGranted() {
        if (!allPermissionGranted()){
            ActivityCompat.requestPermissions(this, REQ_PERMISSIONS, REQ_CODE_PERMISSIONS)
        }
    }

    companion object{
        private val REQ_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        private const val REQ_CODE_PERMISSIONS = 10
    }

    private fun setMessage(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()

    }

    private fun getLastLocation() {
        if(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if(it != null) {
                    location = it
                    Timber.tag("getLastLocation").d("${it.latitude}, ${it.longitude}")
                } else {
                    setMessage(this, resources.getString(R.string.location_message))
                    binding.smShareLocation.isChecked = false
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@StoryActivity,
                "com.arlanallacsta.submissionstoryapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile

            val result = BitmapFactory.decodeFile(getFile?.path)
            binding.previewImageView.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = uriToFile(selectedImg, this@StoryActivity)

            getFile = myFile

            binding.previewImageView.setImageURI(selectedImg)
        }
    }

    private fun uploadStory(authorization: String) {
        showLoading(true)
        val file = reduceFileImage(getFile as File)
        val description = binding.etDescription.text.toString().trim()
        val requestImageFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
        val imageMultipart = MultipartBody.Part.createFormData("photo", file.name, requestImageFile)
        var lat : String? = null
        var lon : String? = null

        if(location != null) {
            lat = location?.latitude.toString()
            lon = location?.longitude.toString()
        }

        lifecycle.coroutineScope.launchWhenResumed {
            if (job.isActive) job.cancel()
            job = launch {
                viewModel.uploadStory(authorization, description, lat, lon, imageMultipart).collect{result ->
                    when(result){
                        is Result.Success ->{
                            onSuccess()
                        }
                        is Result.Error ->{
                            onFailed()
                        }
                        is Result.Loading ->{
                            showLoading(true)
                        }
                    }
                }
            }
        }

    }

    private fun onFailed() {
        showLoading(false)
        setMessage(this@StoryActivity, "Failed story added")
    }

    private fun onSuccess() {
        showLoading(false)
        startActivity(Intent(this@StoryActivity, MainActivity::class.java))
        setMessage(this@StoryActivity, "Story Added successfully")
        finish()
    }

    private fun showLoading(b: Boolean) {
        if (b) {
            binding.pbStory.visibility = View.VISIBLE
        } else {
            binding.pbStory.visibility = View.GONE
        }
    }
}