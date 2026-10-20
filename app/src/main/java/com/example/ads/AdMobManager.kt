package com.example.ads

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

/**
 * Manages Google AdMob Rewarded Video Ads with aggressive offline caching
 * and automatic network-reconnection reloading.
 *
 * AdMob Policy Compliance:
 * - Clear user opt-in before showing rewarded ads
 * - Explicit reward declaration (e.g., +1 Hint or +3 Lives Revive)
 * - Safe preloading and error handling with seamless offline fallback
 * - Automatic reconnection listener to refill cache as soon as internet is back
 */
class AdMobManager(private val context: Context) {

    companion object {
        private const val TAG = "AdMobManager"
        // AdMob Rewarded Ad Unit ID provided by user (official Google Test Ad Unit)
        const val REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    }

    private var rewardedAd: RewardedAd? = null
    private var isLoading = false
    var isInitialized = false
        private set

    init {
        try {
            MobileAds.initialize(context) { initializationStatus ->
                isInitialized = true
                Log.d(TAG, "AdMob MobileAds initialized successfully: $initializationStatus")
                loadRewardedAd()
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to initialize AdMob: ${e.message}")
        }

        // Register Network Callback to automatically reload AdMob ads as soon as internet reconnects
        setupNetworkReconnectListener()
    }

    private fun setupNetworkReconnectListener() {
        try {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            connectivityManager.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    Log.d(TAG, "Internet connection re-established. Preloading fresh AdMob Rewarded Ad...")
                    loadRewardedAd()
                }

                override fun onLost(network: Network) {
                    Log.d(TAG, "Internet connection lost. In-app offline ad system will serve as fallback.")
                }
            })
        } catch (e: Throwable) {
            Log.w(TAG, "Failed to register network callback: ${e.message}")
        }
    }

    /**
     * Checks if active internet is currently available on the device.
     */
    fun isNetworkAvailable(): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
            val network = cm.activeNetwork ?: return false
            val capabilities = cm.getNetworkCapabilities(network) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (e: Throwable) {
            false
        }
    }

    /**
     * Preloads a rewarded ad unit so it is instantly available when requested.
     */
    fun loadRewardedAd(onLoaded: (() -> Unit)? = null) {
        if (rewardedAd != null || isLoading) return
        if (!isNetworkAvailable()) {
            Log.d(TAG, "Device is offline. Skipping AdMob online fetch; offline ad fallback is ready.")
            return
        }

        isLoading = true

        try {
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(
                context,
                REWARDED_AD_UNIT_ID,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        rewardedAd = ad
                        isLoading = false
                        Log.d(TAG, "RewardedAd successfully loaded and cached in memory.")
                        onLoaded?.invoke()
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        rewardedAd = null
                        isLoading = false
                        Log.w(TAG, "RewardedAd failed to load: ${loadAdError.message} (Code: ${loadAdError.code})")
                    }
                }
            )
        } catch (e: Throwable) {
            isLoading = false
            Log.e(TAG, "Exception loading RewardedAd: ${e.message}")
        }
    }

    /**
     * Checks if a rewarded video ad is cached and ready to display.
     */
    val isAdReady: Boolean
        get() = rewardedAd != null

    /**
     * Displays the rewarded ad to the user with standard callbacks.
     */
    fun showRewardedAd(
        activity: Activity,
        onRewardEarned: (amount: Int, type: String) -> Unit,
        onAdDismissed: () -> Unit,
        onAdFailedToShow: (errorReason: String) -> Unit
    ) {
        val ad = rewardedAd
        if (ad == null) {
            Log.w(TAG, "RewardedAd was not ready when show was requested.")
            loadRewardedAd()
            onAdFailedToShow("Ad not ready yet or offline")
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "RewardedAd full screen opened.")
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "RewardedAd dismissed.")
                rewardedAd = null
                // Immediately preload the next rewarded ad for future needs
                loadRewardedAd()
                onAdDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(TAG, "RewardedAd failed to show: ${adError.message}")
                rewardedAd = null
                loadRewardedAd()
                onAdFailedToShow(adError.message)
            }
        }

        try {
            ad.show(activity) { rewardItem ->
                Log.d(TAG, "Reward granted: ${rewardItem.amount} ${rewardItem.type}")
                onRewardEarned(rewardItem.amount, rewardItem.type)
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Exception showing RewardedAd: ${e.message}")
            rewardedAd = null
            loadRewardedAd()
            onAdFailedToShow(e.message ?: "Unknown error showing ad")
        }
    }
}
