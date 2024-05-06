plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.singhealth.enhance"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.singhealth.enhance"
        minSdk = 30
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }
    packagingOptions{
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/license.txt")
        exclude("META-INF/NOTICE")
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/notice.txt")
        exclude("META-INF/ASL2.0")
        exclude("META-INF/*.kotlin_module")
        exclude("META-INF/INDEX.LIST")
        exclude("META-INF/io.netty.versions.properties")

    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

}

dependencies {

    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
//    aws
//    amplify aws
    implementation ("com.amplifyframework:aws-api:2.14.6")
    implementation ("com.amplifyframework:aws-auth-cognito:2.14.6")
    implementation ("com.amplifyframework:aws-predictions:2.14.6")
    implementation("software.amazon.awssdk:textract:2.17.62")
    implementation("androidx.media3:media3-common:1.2.0")
    coreLibraryDesugaring ("com.android.tools:desugar_jdk_libs:2.0.4")

    // NOTE: **** PLEASE Avoid updating 'core-ktx' and 'appcompat' dependency, unless necessary
    implementation("androidx.core:core-ktx:1.9.0")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Material 3 dependencies
    implementation("com.google.android.material:material:1.10.0")

    // TODO: Where required by IDE, add dependencies for layout/view
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Unit test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.5.0"))

    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics-ktx")

    // TODO: Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries
    // Dependency for the Firebase Authentication library
    implementation("com.google.firebase:firebase-auth-ktx")

    // Dependency for the Firestore Database library
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Dependency for the Firebase Storage
    implementation("com.google.firebase:firebase-storage-ktx")

    // Dependencies for Firebase ML Vision
    implementation("com.google.firebase:firebase-ml-vision:24.1.0")
    implementation("com.google.android.gms:play-services-vision:20.1.3")
    implementation("com.google.android.gms:play-services-vision-common:19.1.3")

    // TODO: Add the dependencies for image cropper
    // Dependency for the CanHub image cropper
    implementation("com.vanniktech:android-image-cropper:4.5.0")

    // Dependency for the Glide image loading framework
    implementation("com.github.bumptech.glide:glide:4.16.0")


    // TODO: Add the dependencies for encrypting data
    // Dependency for SQLCipher
    implementation("net.zetetic:sqlcipher-android:4.5.5@aar")
    implementation("androidx.security:security-crypto:1.0.0")

    // Dependency for secure shared preference
    implementation("androidx.security:security-crypto:1.1.0-alpha03")

    // Dependency for password hashing
    implementation(group = "at.favre.lib", name = "bcrypt", version = "0.9.0")

    // WebView Dependencies
    implementation("androidx.webkit:webkit:1.8.0")
}