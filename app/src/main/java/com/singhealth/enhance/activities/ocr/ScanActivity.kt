package com.singhealth.enhance.activities.ocr
import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import aws.sdk.kotlin.services.textract.model.BlockType
import aws.sdk.kotlin.services.textract.model.DetectDocumentTextRequest
import aws.sdk.kotlin.services.textract.model.Document
import com.amplifyframework.core.Amplify
import com.amplifyframework.predictions.aws.AWSPredictionsPlugin
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.singhealth.enhance.activities.DashboardActivity
import com.singhealth.enhance.R
import com.singhealth.enhance.activities.MainActivity
import com.singhealth.enhance.activities.history.HistoryActivity
import com.singhealth.enhance.activities.patient.ProfileActivity
import com.singhealth.enhance.activities.patient.RegistrationActivity
import com.singhealth.enhance.activities.settings.SettingsActivity
import com.singhealth.enhance.databinding.ActivityScanBinding
import com.singhealth.enhance.security.SecureSharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException

class ScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanBinding

    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    private lateinit var cameraPermissions: Array<String>
    private lateinit var storagePermissions: Array<String>

    private lateinit var progressDialog: ProgressDialog

    private lateinit var outputUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Navigation drawer
        actionBarDrawerToggle = ActionBarDrawerToggle(this, binding.drawerLayout, 0, 0)
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        actionBarDrawerToggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }

                R.id.item_patient_registration -> {
                    startActivity(Intent(this, RegistrationActivity::class.java))
                    finish()
                    true
                }

                R.id.item_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    finish()
                    true
                }

                else -> {
                    false
                }
            }
        }

        // Navigation bar
        binding.bottomNavigationView.selectedItemId = R.id.item_scan

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    false
                }

                R.id.item_scan -> {
                    true
                }

                R.id.item_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    finish()
                    false
                }
                R.id.item_dashboard -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                    false
                }

                else -> {
                    false
                }
            }
        }

        val patientSharedPreferences = SecureSharedPreferences.getSharedPreferences(applicationContext)
        if (patientSharedPreferences.getString("patientID", null).isNullOrEmpty()) {
            val mainIntent = Intent(this, MainActivity::class.java)
            Toast.makeText(
                this,
                "Patient information could not be found in current session. Please try again.",
                Toast.LENGTH_LONG
            ).show()
            startActivity(mainIntent)
            finish()
        }

        cameraPermissions =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        progressDialog = ProgressDialog(this)
        progressDialog.setCanceledOnTouchOutside(false)

        binding.selectSourceBtn.setOnClickListener {
            onClickRequestPermission()
        }


    }

    // Below codes are for the camera and photo crop functionality
    private fun startCameraWithoutUri(includeCamera: Boolean, includeGallery: Boolean) {
        customCropImage.launch(
            CropImageContractOptions(
                uri = null,
                cropImageOptions = CropImageOptions(
                    imageSourceIncludeCamera = includeCamera,
                    imageSourceIncludeGallery = includeGallery,
                ),
            ),
        )
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    private val customCropImage = registerForActivityResult(CropImageContract()) {
        if (it !is CropImage.CancelledResult) {
            handleCropImageResult(it.uriContent.toString())
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


    private fun handleCropImageResult(uri: String) {
        outputUri = Uri.parse(uri.replace("file:", "")).also { parsedUri ->
            binding.cropIV.setImageUriAsync(parsedUri)
        }

        outputUri?.let { nonNullUri ->
            // Convert Uri to Bitmap
            val imageBitmap = getBitmapFromUri(nonNullUri)
            if (imageBitmap != null) {
                // Pass the Bitmap to detectText function
                detectText(imageBitmap)
            } else {
                Toast.makeText(this, "ERROR: Unable to convert Uri to Bitmap.", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "ERROR: No image to process.", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                MaterialAlertDialogBuilder(this)
                    .setIcon(R.drawable.ic_error)
                    .setTitle("Enable app permissions")
                    .setMessage("To use the OCR functionality, you need to allow access to your camera, gallery and external storage.")
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        }

    private fun onClickRequestPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) -> {
                startCameraWithoutUri(includeCamera = true, includeGallery = true)
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    // Below codes are for the OCR functions


//    AMAZON OCR
    private fun detectText(image: Bitmap) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val plugin = Amplify.Predictions.getPlugin("awsPredictionsPlugin") as AWSPredictionsPlugin
            val client = plugin.escapeHatch.textractClient
            val outputStream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            val imageBytes = outputStream.toByteArray()

            val myDoc = Document {
                bytes = imageBytes
            }

            val detectDocumentTextRequest = DetectDocumentTextRequest {
                document = myDoc
            }
            var fullText = ""
            val response = client.detectDocumentText(detectDocumentTextRequest)
            // Handle the response as needed
            response.blocks?.forEach { block ->
                if (block.blockType == BlockType.Word) { // Check if the block is a line
                    fullText += block.text + "\n"
                }
            }
            println("fullText: " + fullText)
            processChart2(fullText)
        } catch (e: Exception) {
           println("eror")
            println(e)
            }
        }

        // Handle the response as needed
//        Amplify.Predictions.identify(
//            TextFormatType.TABLE, image,
//            { result ->
//                val identifyResult = result as IdentifyDocumentTextResult
//                println("fulltext " + identifyResult.fullText)
//                processChart2(identifyResult.fullText)
//            },
//            { println("Identify failed" + it) }
//        )

    }



    private fun processChart2(text: String) {
            // Split the text into words
            // Clean data
            val words = text.split("\\s+".toRegex())
                .filter { it != "*" && it != "7" && it != "07" && it != "8" }
                .map {
                    when (it) {
                        "Sis", "Eis", "Su" -> "84"
                        "14" -> "121"
                        "10" -> "70"
                        "16" -> "116"
                        "1/6" -> "116"
                        else -> it.replace(Regex("[^\\d]"), "")
                    }
                }
                .filter { it.matches(Regex("\\d+")) && it.toInt() <= 210 }
                .toMutableList()

            print("Words: " + words)
            // Correct the list by adding a default systolic value "109" when needed
            var i = 0
            while (i < words.size - 1) {
                if (words[i].toInt() < 100 && words[i + 1].toInt() < 100) {
                    words.add(i, "109") // Insert default systolic value before the first diastolic
                    i++ // Skip the next value as we've just added a new systolic
                }
                i++
            }

        // Ensure even number of readings for pairing
            if (words.size % 2 != 0) {
                words.add("0") // Add a placeholder for missing diastolic
            }

            println("words after filtering + 0: $words")
            var sysBPList = mutableListOf<String>()
            var diaBPList = mutableListOf<String>()

        // Split the corrected readings into systolic and diastolic lists
            for (i in words.indices step 2) {
                sysBPList.add(words[i])
                diaBPList.add(words[i + 1])
            }

            val bundle = Bundle()
            bundle.putStringArrayList("sysBPList", ArrayList(sysBPList))
            bundle.putStringArrayList("diaBPList", ArrayList(diaBPList))
            if (intent.extras != null && intent.extras!!.containsKey("homeSysBPTarget")) {
                bundle.putString("homeSysBPTarget", intent.extras!!.getString("homeSysBPTarget"))
            }
            if (intent.extras != null && intent.extras!!.containsKey("homeDiaBPTarget")) {
                bundle.putString("homeDiaBPTarget", intent.extras!!.getString("homeDiaBPTarget"))
            }
            if (intent.extras != null && intent.extras!!.containsKey("clinicSysBPTarget")) {
                bundle.putString(
                    "clinicSysBPTarget",
                    intent.extras!!.getString("clinicSysBPTarget")
                )
            }
            if (intent.extras != null && intent.extras!!.containsKey("clinicDiaBPTarget")) {
                bundle.putString(
                    "clinicDiaBPTarget",
                    intent.extras!!.getString("clinicDiaBPTarget")
                )
            }

            if (intent.hasExtra("sysBPListHistory")) {
                bundle.putStringArrayList(
                    "sysBPListHistory",
                    intent.getStringArrayListExtra("sysBPListHistory")
                )
            }
            if (intent.hasExtra("diaBPListHistory")) {
                bundle.putStringArrayList(
                    "diaBPListHistory",
                    intent.getStringArrayListExtra("diaBPListHistory")
                )
            }

            val verifyScanIntent = Intent(this, VerifyScanActivity::class.java)
            verifyScanIntent.putExtras(bundle)

            progressDialog.dismiss()

            startActivity(verifyScanIntent)
            finish()

        }
    }
