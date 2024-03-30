package com.singhealth.enhance.activities.diagnosis

import com.google.firebase.firestore.QuerySnapshot
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
    var BPStage : String = if (recentSys <= 120 && recentDia <= 80) {
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
    // Test
    // Sort array by date in descending order
    val sortedArr = arr.sortedByDescending { it.date }

    return sortedArr
}