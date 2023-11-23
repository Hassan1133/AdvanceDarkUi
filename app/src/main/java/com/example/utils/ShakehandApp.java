package com.example.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.onesignal.OneSignal;
import com.example.R;

import java.util.Date;

public class ShakehandApp extends Application implements Application.ActivityLifecycleCallbacks, LifecycleObserver{
        private static final String ONESIGNAL_APP_ID = "2710f0ac-a924-419b-8a6c-99a35fd11433";
        private AppOpenAdManager appOpenAdManager;
        private Activity currentActivity;
        private OnShowAdCompleteListener adCompleteListener;

        //  private static ShakehandApp sInstance;

        @Override
        public void onCreate() {
            super.onCreate();
            //sInstance = this;
// OneSignal Initialization
            OneSignal.initWithContext(this);
            OneSignal.setAppId(ONESIGNAL_APP_ID);
            this.registerActivityLifecycleCallbacks(this);
            MobileAds.initialize(
                    this,
                    new OnInitializationCompleteListener() {
                        @Override
                        public void onInitializationComplete(InitializationStatus initializationStatus) {
                        }
                    });
            ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
            appOpenAdManager = new AppOpenAdManager();

        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        protected void onMoveToForeground() {
            // Show the ad (if available) when the app moves to foreground.
            appOpenAdManager.showAdIfAvailable(currentActivity);
        }


        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {

            if (!appOpenAdManager.isShowingAd) {
                currentActivity = activity;
            }
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
        }

        public void showAdIfAvailable(
                @NonNull Activity activity,
                @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
            // We wrap the showAdIfAvailable to enforce that other classes only interact with MyApplication
            // class.
            adCompleteListener = onShowAdCompleteListener;
            appOpenAdManager.showAdIfAvailable(activity, onShowAdCompleteListener);
        }

        public interface OnShowAdCompleteListener {
            void onShowAdComplete();
        }


        private class AppOpenAdManager {

            private static final String LOG_TAG = "AppOpenAdManager";

            private AppOpenAd appOpenAd = null;
            private boolean isLoadingAd = false;
            private boolean isShowingAd = false;


            private long loadTime = 0;

            public AppOpenAdManager() {
            }

            private void loadAd(Context context) {
                // Do not load ad if there is an unused ad or one is already loading.
                if (isLoadingAd || isAdAvailable()) {
                    return;
                }

                isLoadingAd = true;
                AdRequest request = new AdRequest.Builder().build();
                AppOpenAd.load(
                        context,
                        getString(R.string.admob_open_ad),
                        request,
                        AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                        new AppOpenAd.AppOpenAdLoadCallback() {
                            @Override
                            public void onAdLoaded(AppOpenAd ad) {
                                appOpenAd = ad;
                                isLoadingAd = false;
                                loadTime = (new Date()).getTime();

                                Log.d(LOG_TAG, "onAdLoaded.");
                            }

                            @Override
                            public void onAdFailedToLoad(LoadAdError loadAdError) {
                                isLoadingAd = false;
                                Log.d(LOG_TAG, "onAdFailedToLoad: " + loadAdError.getMessage());
                            }
                        });
            }

            /**
             * Check if ad was loaded more than n hours ago.
             */
            private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
                long dateDifference = (new Date()).getTime() - loadTime;
                long numMilliSecondsPerHour = 3600000;
                return (dateDifference < (numMilliSecondsPerHour * numHours));
            }

            /**
             * Check if ad exists and can be shown.
             */
            private boolean isAdAvailable() {
                return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
            }


            private void showAdIfAvailable(@NonNull final Activity activity) {
                Log.d("TAG", "showAdIfAvailable:Name " + activity.getClass().getName());
                Log.d("TAG", "showAdIfAvailable:CanonicalName " + activity.getClass().getCanonicalName());
                Log.d("TAG", "showAdIfAvailable:SimpleName " + activity.getClass().getSimpleName());

                if (activity.getClass().getSimpleName().equals("SplashActivity"))
                    return;

                showAdIfAvailable(
                        activity,
                        new OnShowAdCompleteListener() {
                            @Override
                            public void onShowAdComplete() {
                                // Empty because the user will go back to the activity that shows the ad.
                                Log.d(LOG_TAG, "on Ad Complete.");
                                if (adCompleteListener != null)
                                    adCompleteListener.onShowAdComplete();
                            }
                        });
            }

            private void showAdIfAvailable(
                    @NonNull final Activity activity,
                    @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {

                if (activity.getClass().getSimpleName().equals("SplashActivity"))
                    return;

                // If the app open ad is already showing, do not show the ad again.
                if (isShowingAd) {
                    Log.d(LOG_TAG, "The app open ad is already showing.");
                    return;
                }

                // If the app open ad is not available yet, invoke the callback then load the ad.
                if (!isAdAvailable()) {
                    Log.d(LOG_TAG, "The app open ad is not ready yet.");
                    onShowAdCompleteListener.onShowAdComplete();
                    loadAd(activity);
                    return;
                }

                Log.d(LOG_TAG, "Will show ad.");

                appOpenAd.setFullScreenContentCallback(
                        new FullScreenContentCallback() {
                            /** Called when full screen content is dismissed. */
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Set the reference to null so isAdAvailable() returns false.
                                appOpenAd = null;
                                isShowingAd = false;
                                Log.d(LOG_TAG, "Dismiss ad");
                                onShowAdCompleteListener.onShowAdComplete();
                                loadAd(activity);
                            }

                            /** Called when fullscreen content failed to show. */
                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                appOpenAd = null;
                                isShowingAd = false;

                                onShowAdCompleteListener.onShowAdComplete();
                                loadAd(activity);
                            }

                            /** Called when fullscreen content is shown. */
                            @Override
                            public void onAdShowedFullScreenContent() {
                                Log.d(LOG_TAG, "onAdShowedFullScreenContent.");
                            }
                        });

                isShowingAd = true;
                appOpenAd.show(activity);
            }
        }
    }

