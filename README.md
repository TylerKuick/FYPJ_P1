FYPJ P1

Front-End is hosted on Firebase with the given URL: https://enhance-bdc3f.web.app

Project Console: https://console.firebase.google.com/project/enhance-bdc3f/overview

Main Changes made to ENHANCe in P1: 
1. Updated Control Status and Recommendation functions 
2. Added diagnosePatient function to display different BP Stages [Added to ProfileActivity, RecommendationActivity, HistoryActivity]
3. Redesign "Outcome and Recommendation" XML Layout (Added dividers for each category of recommendations, added images to indicate the severity of the patient's blood pressure)
4. Reconfigure Analytics Dashboard to use WebView (Embed Looker Reports to display visualisations instead of creating them from scratch)

**Note:** Dashboard function will not display in Android Studio Emulator. To test it, connect an android device to Android Studio via USB or Wifi and run the app. 

New Services used in P1: 
1. BigQuery (Used to stream Firestore data to other services [Looker Studio, Healthcare API])
2. Looker Studio for Visualisations
3. Firebase Hosting for Web App. Found here: https://github.com/TylerKuick/FYPJ_P1_Web_App

**Note:** Currently, web app is configured for static pages only and only supports redirects to other pages. For more functions or to serve dynamic content, visit https://firebase.google.com/docs/hosting/functions

**Looker Studio Report:**
* Looker Report is not filtered by default. All scans from every patient is presented in the visualisations. Hence, to filter the report, patient document id is included within the URL when the user taps on the dashboard icon in the ENHANCe app.
* On the front-end of the web app, the URL and its parameters (patient_doc_id) is extracted. The parameters are then included at the end of the Looker Studio Embed link to filter the report to only show values with the corresponding patient document id.
* To do the above, a "Custom BigQuery Query" is used as the data source. For the specific query statement, refer to the "Firebase Hosting for Web App" Github link above. 

Potential Improvements for FYPJ P2: 
1. Refine Control Status: Current function takes the 6 most recent visits/scans to determine the patient's control status. Can be configured to use a 3-months timeframe to determine control status (Take the average BP from the last 3 months and compare it to the "Poor Control Status Threshold" - 140/90 for >18 yrs old, or 150/90 for >60 yrs old
2. Analytics Dashboard: Modify or add new visualisations that could be useful for doctor's reference (Current visualisations included: Average Sys and Dia BP values, Chart of the Average Sys and Dia BP values over time)
3. Complete FHIR Integration: With interoperability being an issue in the medical field, we thought that ENHANCe could operate with the FHIR Standard in mind. FHIR Standard - Normalises the data structure of medical data foe easier communication with other medical facilities. 
