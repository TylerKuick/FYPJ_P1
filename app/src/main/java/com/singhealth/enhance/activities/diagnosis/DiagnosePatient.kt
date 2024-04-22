package com.singhealth.enhance.activities.diagnosis

import android.annotation.SuppressLint
import aws.smithy.kotlin.runtime.util.length
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Determine patient's BP Stage based on the given Systolic and Diastolic values. Set recentDate to null if not necessary
// recentDate param is just for confirmation that the data is most recent (for profile updates)
fun diagnosePatient(recentSys: Long, recentDia: Long, recentDate: String?): String {
    // Log recent date, sys and dia data in Logcat
    if (recentDate != null) {
        println("Date: $recentDate, Most Recent Sys: $recentSys, Most Recent Dia: $recentDia")
    }
    else {
        println("Most Recent Sys: $recentSys, Most Recent Dia: $recentDia")
    }
    // Determine patient BP Stage
    var BPStage : String = if (recentSys < 90 || recentDia < 60) {
        "Low BP"
    }
    else if (recentSys <= 120 && recentDia <= 80) {
        "Normal BP"
    }
    else if (recentSys >= 160 || recentDia >= 100) {
        "Stage 2 Hypertension"
    }
    else if (recentSys >= 140 || recentDia >= 90) {
        "Stage 1 Hypertension"
    }
    else if (recentSys > 120 || recentDia > 80) {
        "High Normal BP"
    }
    else {
        "N/A"
    }

    return BPStage
}

// Used to sort all patient records stored in db
// documents param passed into function after get() function from Firestore db
fun sortPatientVisits(documents: QuerySnapshot) : List<Diag> {
    // Add all BP readings into array
    val arr = ArrayList<Diag>()
    val inputDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
    // Adding all documents with Sys and Dia data into an array as a "Diag" Object
    for (document in documents) {
        val dateTimeString = document.get("date") as? String
        val dateTime = LocalDateTime.parse(dateTimeString, inputDateFormatter)
        val sysBP = document.get("averageSysBP") as? Long
        val diaBP = document.get("averageDiaBP") as? Long
        arr.add(
            Diag(
                dateTime.toString(),
                sysBP,
                diaBP
            )
        )
    }
    // Sort array by date in descending order
    val sortedArr = arr.sortedByDescending { it.date }

    return sortedArr
}

fun showControlStatus(documents: QuerySnapshot, patientAge: Int, date : String?): String {
    // P1 2024
    // Control Status: How well the patient can control their BP (maintain BP under a limit),
    // <140/90 for >18 yrs and <150/90 for >60 yrs. Determined by taking the average of last 6
    // records (incl. most recent BP recording), if the average is under the limit, the patient
    // exhibits good BP Control, else they have bad BP Control
    var controlStat: String = "N/A"
    var totalSys: Long = 0
    var totalDia: Long = 0

    // Returns an array of objects containing the Sys/Dia BP values and date
    var sortedVisits = sortPatientVisits(documents)

    // Check if a date is specified
    if (date != null) {
        // newSortedList contains all visits before and including the specified date
        val (newSortedList, pass) = sortedVisits.partition{ it.date!! <= date }
        sortedVisits = newSortedList
    }


    // Fixed len represents number of visits to refer to when determining control status
    var len = 6
    // When number of visits is less than 6, make len the size of list
    if (sortedVisits.size <= len) {
        len = sortedVisits.size - 1
    }

    // Sum all of the Sys and Dia BP Values from last 6 records (incl. scan)
    for (i in 0..len) {
        var entry = sortedVisits[i]
        println(entry.date)
        val sysData = entry.avgSysBP
        val diaData = entry.avgDiaBP
        if (sysData != null && diaData != null) {
            totalSys += sysData
            totalDia += diaData
        }
    }

    // Average Sys BP throughout all visits
    val avgSys = totalSys / len
    // Average Dia BP throughout all visits
    val avgDia = totalDia / len
    // Different Sys and Dia limits for different age groups
    if (patientAge >= 60) {
        val sysLimit = 150
        val diaLimit = 90
        if (avgSys >= sysLimit || avgDia >= diaLimit) { // If either Sys or Dia BP exceed limit, patient has poor bp control
            controlStat = "Poor BP Control. Patient's average blood pressure over the last ${len} visits is above ${sysLimit}/${diaLimit} mmHg"
            return controlStat
        } else {
            controlStat = "Good BP Control. Patient's average blood pressure over the last ${len} visits is below 140/90 mmHg"
            return controlStat
        }
    } else if (patientAge >= 18) {
        val sysLimit = 140
        val diaLimit = 90
        if (avgSys >= sysLimit || avgDia >= diaLimit) { // If either Sys or Dia BP exceed limit, patient has poor bp control
            controlStat = "Poor BP Control. Patient's average blood pressure over the last ${len} visits is above ${sysLimit}/${diaLimit} mmHg"
            return controlStat
        } else {
            controlStat = "Good BP Control. Patient's average blood pressure over the last ${len} visits is below 140/90 mmHg"
            return controlStat
        }
    }
    return controlStat
}

@SuppressLint("SetTextI18n")
fun showRecommendation(bpStage: String) : ArrayList<String>{
    // P1 2024 Version
    // Provide categories of recommendation based on the patient's current BP Stage
    var dietText : String = "No Recommendations"
    var lifestyleText : String = "No Recommendations"
    var medicalText : String = "No Recommendations"
    var ouputList = ArrayList <String>()
    when (bpStage) {

        "Normal BP" -> { dietText = "Continue maintaining healthy lifestyle."
                       lifestyleText = "Continue maintaining healthy lifestyle."
                       medicalText = "Continue maintaining healthy lifestyle." }

        "High Normal BP" ->{
            dietText= "- Lower sodium intake (< 3.6g / day)\n\n"
            lifestyleText = "- Increase physical activity (2.5 - 5 hours / week)\n" +
                "- Maintain healthy weight (BMI < 22.9)\n" +
                "- Sufficient sleep (>7 hours / night)\n"}

        "Stage 1 Hypertension" -> {
            dietText = "- Healthy diet\n" +
                "- Lower sodium intake (< 2g / day)\n" +
                "- Limit caffeine\n\n"
            lifestyleText = "- Manage stress\n" +
                "- Increase physical activity (2.5 - 5 hours / week)\n" +
                "- Maintain healthy weight (BMI < 22.9)\n" +
                "- Stop smoking and/or drinking\n" +
                "- Sufficient sleep (>7 hours / night)\n\n"
             medicalText = "- Checkup regularly\n"
        }

        "Stage 2 Hypertension" -> {
            dietText = "- Healthy diet\n" +
                "- Lower sodium intake (< 1.5g / day)\n" +
                "- Limit caffeine\n\n"
            lifestyleText = "- Manage stress\n" +
                "- Increase physical activity (2.5 - 5 hours / week)\n" +
                "- Maintain healthy weight (BMI < 22.9)\n" +
                "- Stop smoking and/or drinking\n" +
                "- Sufficient sleep (>7 hours / night)\n\n"
            medicalText="- Take prescribed medications\n" +
                "- Check up regularly\n"
        }
    }
    ouputList.add(dietText)
    ouputList.add(lifestyleText)
    ouputList.add(medicalText)

    return ouputList
}