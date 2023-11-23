plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.example"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.2.0")
    implementation("com.google.firebase:firebase-firestore:24.9.1")
    implementation("com.google.firebase:firebase-database:20.2.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.google.firebase:firebase-dynamic-links:21.1.0")
    implementation("com.google.firebase:firebase-analytics:21.3.0")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.firebase:firebase-storage:20.2.0")
    implementation("com.google.firebase:firebase-crashlytics:18.3.5")
    implementation("com.google.android.gms:play-services-ads-lite:22.3.0")

    // sdp
    implementation("com.intuit.sdp:sdp-android:1.1.0")

    // ssp
    implementation("com.intuit.ssp:ssp-android:1.1.0")

    // circular ImageView
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Java language implementation of navigation component
    val navVersion by extra { "2.7.4" }
    implementation("androidx.navigation:navigation-fragment:$navVersion")
    implementation("androidx.navigation:navigation-ui:$navVersion")


    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    /*admob open app ad*/
    val lifecycle_version by extra { "2.0.0" }
    implementation("androidx.lifecycle:lifecycle-extensions:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-runtime:$lifecycle_version")
    annotationProcessor("androidx.lifecycle:lifecycle-compiler:$lifecycle_version")
    // lottie fiels
    val lottieVersion by extra { "5.2.0" }
    implementation("com.airbnb.android:lottie:$lottieVersion")
    // sdp
    // page indication
    implementation("com.tbuonomo:dotsindicator:4.3")
    // round image
    implementation("com.makeramen:roundedimageview:2.3.0")
    // circular image
    implementation("de.hdodenhof:circleimageview:3.1.0")
    //Image loader
    implementation("com.github.bumptech.glide:glide:4.13.2")
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.2")
    // progress
    implementation("com.github.colakcode:clkProgress:v1.0")
    // pinview
    implementation("com.github.GoodieBag:Pinview:v1.4")
    //one signal
    implementation("com.onesignal:OneSignal:[4.0.0, 4.99.99]")
    // charts
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // zoom image
    implementation("com.otaliastudios:zoomlayout:1.9.0")
    // youtube player
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.0.0")
    //map location
    implementation("com.google.android.gms:play-services-location:21.0.1")

}