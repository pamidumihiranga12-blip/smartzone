package com.example.data

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseManager {
    private const val TAG = "FirebaseManager"

    const val API_KEY = "AIzaSyDaKFHWjMFfabGw0l1NILs_kb8hF5FCRhU"
    const val AUTH_DOMAIN = "srmobile-6091e.firebaseapp.com"
    const val PROJECT_ID = "srmobile-6091e"
    const val STORAGE_BUCKET = "srmobile-6091e.firebasestorage.app"
    const val MESSAGING_SENDER_ID = "977403967748"
    const val APP_ID = "1:977403967748:web:a90a347b40126ec50f3851"
    const val MEASUREMENT_ID = "G-WLRJ826P2L"

    private var initialized = false

    fun initialize(context: Context) {
        if (initialized) return
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApiKey(API_KEY)
                    .setApplicationId(APP_ID)
                    .setProjectId(PROJECT_ID)
                    .setGcmSenderId(MESSAGING_SENDER_ID)
                    .setStorageBucket(STORAGE_BUCKET)
                    .build()

                FirebaseApp.initializeApp(context, options)
                Log.d(TAG, "Firebase initialized with project ID: $PROJECT_ID")
            } else {
                Log.d(TAG, "FirebaseApp already initialized.")
            }
            initialized = true
        } catch (e: Throwable) {
            Log.e(TAG, "Error initializing Firebase", e)
        }
    }

    fun getFirestore(): FirebaseFirestore? {
        return try {
            FirebaseFirestore.getInstance()
        } catch (e: Throwable) {
            Log.e(TAG, "Error getting Firestore instance", e)
            null
        }
    }

    fun syncProduct(id: String, title: String, price: Double, category: String, description: String, imageUrl: String) {
        try {
            val map = hashMapOf(
                "id" to id,
                "title" to title,
                "price" to price,
                "category" to category,
                "description" to description,
                "imageUrl" to imageUrl,
                "updatedAt" to System.currentTimeMillis()
            )
            getFirestore()?.collection("products")?.document(id)?.set(map)
                ?.addOnSuccessListener { Log.d(TAG, "Product $id saved to Firestore") }
        } catch (e: Exception) {
            Log.e(TAG, "Failed syncing product to Firestore", e)
        }
    }

    fun syncSiteConfig(
        storeName: String,
        storeSubtitle: String,
        phone: String,
        address: String,
        bankName: String,
        bankAccNo: String,
        bankAccName: String,
        merchantId: String,
        secretKey: String
    ) {
        try {
            val map = hashMapOf(
                "storeName" to storeName,
                "storeSubtitle" to storeSubtitle,
                "phone" to phone,
                "address" to address,
                "bankName" to bankName,
                "bankAccNo" to bankAccNo,
                "bankAccName" to bankAccName,
                "payzyMerchantId" to merchantId,
                "payzySecretKey" to secretKey,
                "updatedAt" to System.currentTimeMillis()
            )
            getFirestore()?.collection("site_config")?.document("main")?.set(map)
                ?.addOnSuccessListener { Log.d(TAG, "Site config saved to Firestore") }
        } catch (e: Exception) {
            Log.e(TAG, "Failed syncing site config to Firestore", e)
        }
    }

    fun syncOrder(orderId: String, totalAmount: Double, paymentMethod: String, address: String, status: String) {
        try {
            val map = hashMapOf(
                "orderId" to orderId,
                "totalAmount" to totalAmount,
                "paymentMethod" to paymentMethod,
                "address" to address,
                "status" to status,
                "createdAt" to System.currentTimeMillis()
            )
            getFirestore()?.collection("orders")?.document(orderId)?.set(map)
                ?.addOnSuccessListener { Log.d(TAG, "Order $orderId saved to Firestore") }
        } catch (e: Exception) {
            Log.e(TAG, "Failed syncing order to Firestore", e)
        }
    }
}
