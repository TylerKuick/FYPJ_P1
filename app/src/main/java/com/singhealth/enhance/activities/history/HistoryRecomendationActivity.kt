package com.singhealth.enhance.activities.history

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.singhealth.enhance.R
import com.singhealth.enhance.activities.DashboardActivity
import com.singhealth.enhance.activities.MainActivity
import com.singhealth.enhance.activities.diagnosis.diagnosePatient
import com.singhealth.enhance.activities.ocr.ScanActivity
import com.singhealth.enhance.activities.patient.ProfileActivity
import com.singhealth.enhance.databinding.ActivityRecommendationBinding
import com.singhealth.enhance.security.AESEncryption
import com.singhealth.enhance.security.SecureSharedPreferences
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class HistoryRecomendationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecommendationBinding

    private var patientID: String? = null

    private var avgSysBP: Long = 0
    private var avgDiaBP: Long = 0
    private var patientAge: Int = 0

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecommendationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@HistoryRecomendationActivity, HistoryActivity::class.java))
                finish()
            }
        })

        val patientSharedPreferences =
            SecureSharedPreferences.getSharedPreferences(applicationContext)
        if (patientSharedPreferences.getString("patientID", null).isNullOrEmpty()) {
            val mainIntent = Intent(this, MainActivity::class.java)
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_LONG).show()
            startActivity(mainIntent)
            finish()
        } else {
            patientID = patientSharedPreferences.getString("patientID", null)
        }
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    false
                }

                R.id.item_scan -> {
                    startActivity(Intent(this, ScanActivity::class.java))
                    finish()
                    false
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
        val avgBPBundle = intent.extras
        avgSysBP = avgBPBundle!!.getInt("avgSysBP").toLong()
        avgDiaBP = avgBPBundle!!.getInt("avgDiaBP").toLong()
        println(avgSysBP)
        println(avgDiaBP)

        // Display average BP
        binding.avgHomeSysBPTV.text = avgSysBP.toString()
        binding.avgHomeDiaBPTV.text = avgDiaBP.toString()



        // Calculate patient's age
        val docRef = db.collection("patients").document(patientID.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    val birthDate = LocalDate.parse(
                        AESEncryption().decrypt(
                            document.getString("dateOfBirth").toString()
                        ), formatter
                    )
                    val currentDate = LocalDate.now()
                    val period = Period.between(birthDate, currentDate)
                    patientAge = period.years

                    showControlStatus()
                    showRecommendation()
                }
            }
            .addOnFailureListener { e ->
                MaterialAlertDialogBuilder(this)
                    .setIcon(R.drawable.ic_error)
                    .setTitle("Firestore Database connection error")
                    .setMessage("The app is currently experiencing difficulties establishing a connection with the Firestore Database.\n\nIf this issue persists, please reach out to your IT helpdesk and provide them with the following error code for further assistance:\n\n$e")
                    .setPositiveButton(resources.getString(R.string.ok_dialog)) { dialog, _ -> dialog.dismiss() }
                    .show()
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                startActivity(Intent(this, HistoryActivity::class.java))
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showControlStatus() {
        if (patientAge >= 60) {
            if (avgSysBP >= 150 && avgDiaBP >= 90) {
                binding.controlStatusTV.text = "This patient has sub-optimum BP control."
            } else if (avgSysBP >= 120 && avgDiaBP >= 80) {
                binding.controlStatusTV.text = "This patient has normal BP control."
            } else if (avgSysBP >= 90 && avgDiaBP >= 60) {
                binding.controlStatusTV.text = "This patient has ideal BP control."
            } else {
                binding.controlStatusTV.text = "This patient has low BP control."
            }
        } else if (patientAge >= 18) {
            if (avgSysBP >= 140 && avgDiaBP >= 90) {
                binding.controlStatusTV.text = "This patient has sub-optimum BP control."
            } else if (avgSysBP >= 120 && avgDiaBP >= 80) {
                binding.controlStatusTV.text = "This patient has normal BP control."
            } else if (avgSysBP >= 90 && avgDiaBP >= 60) {
                binding.controlStatusTV.text = "This patient has ideal BP control."
            } else {
                binding.controlStatusTV.text = "This patient has low BP control."
            }
        } else {
            binding.controlStatusTV.text =
                "Unable to provide control status for this patient. Manual intervention is required."
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showRecommendation() {
        /* P4B 2023 Version
        if (patientAge >= 60) {
            if (avgSysBP >= 150 && avgDiaBP >= 90) {
                binding.recommendationTV.text =
                    "Re-enforce medication adherence. Consider to up-titrate anti-hypertensive medications and advice on lifestyle modifications to improve BP control."
            } else if (avgSysBP >= 120 && avgDiaBP >= 80) {
                binding.recommendationTV.text =
                    "Encourage patient to keep up the effort in maintaining a healthy BP level."
            } else if (avgSysBP >= 90 && avgDiaBP >= 60) {
                binding.recommendationTV.text =
                    "Prescribe medication to increase BP to a normal level."
            } else {
                binding.recommendationTV.text = "Medical intervention may be required."
            }
        } else if (patientAge >= 18) {
            if (avgSysBP >= 140 && avgDiaBP >= 90) {
                binding.recommendationTV.text =
                    "Re-enforce medication adherence. Consider to up-titrate anti-hypertensive medications and advice on lifestyle modifications to improve BP control."
            } else if (avgSysBP >= 120 && avgDiaBP >= 80) {
                binding.recommendationTV.text =
                    "Encourage patient to keep up the effort in maintaining a healthy BP level."
            } else if (avgSysBP >= 90 && avgDiaBP >= 60) {
                binding.recommendationTV.text =
                    "Prescribe medication to increase BP to a normal level."
            } else {
                binding.recommendationTV.text = "Medical intervention may be required."
            }
        } else {
            binding.recommendationTV.text =
                "Unable to provide recommendation for this patient. Manual intervention is required."
        }
        */

        // P1 2024 Version
        // Provide categories of recommendation based on the patient's current BP Stage

        // Display BP Stage
        var refBPStage = diagnosePatient(avgSysBP, avgDiaBP, null)
        println(refBPStage)
        when (refBPStage) {
            "Normal BP" -> binding.recommendationTV.text =
                "Continue maintaining healthy lifestyle"

            "High Normal BP" -> binding.recommendationTV.text = "- Lower sodium intake\n" +
                    "- Maintain healthy weight\n" +
                    "- Sufficient sleep\n" +
                    "- Increase physical activity\n"

            "Stage 1 Hypertension" -> binding.recommendationTV.text = "- Lower sodium intake\n" +
                    "- Manage stress\n" +
                    "- Maintain healthy weight\n" +
                    "- Healthy diet\n- Limit caffeine\n" +
                    "- Stop smoking and/or drinking\n" +
                    "- Sufficient sleep\n" +
                    "- Increase physical activity\n" +
                    "- Checkup regularly\n"

            "Stage 2 Hypertension" -> binding.recommendationTV.text = "- Lower sodium intake\n" +
                    "- Manage stress\n" +
                    "- Maintain healthy weight\n" +
                    "- Healthy diet\n" +
                    "- Limit caffeine\n" +
                    "- Stop smoking and/or drinking\n" +
                    "- Sufficient sleep\n" +
                    "- Increase physical activity\n" +
                    "- Take medications\n" +
                    "- Check up regularly\n" +
                    "\n"
        }
    }
}