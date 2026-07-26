package com.example.model

enum class AppLanguage(val code: String, val displayName: String, val flag: String) {
    ENGLISH("EN", "English", "🇬🇧"),
    SINHALA("SI", "සිංහල", "🇱🇰"),
    TAMIL("TA", "தமிழ்", "🇱🇰")
}

object LanguageManager {

    private val translations = mapOf(
        "app_title" to mapOf(
            AppLanguage.ENGLISH to "SMARTZONE",
            AppLanguage.SINHALA to "ස්මාර්ට් සෝන්",
            AppLanguage.TAMIL to "ஸ்மார்ட் ஜோன்"
        ),
        "search_hint" to mapOf(
            AppLanguage.ENGLISH to "Search parts, brand, model...",
            AppLanguage.SINHALA to "උපාංග, සන්නාම, මාදිලි සොයන්න...",
            AppLanguage.TAMIL to "பாகங்கள், பிராண்ட், மாதிரியைத் தேடுங்கள்..."
        ),
        "home" to mapOf(
            AppLanguage.ENGLISH to "Home",
            AppLanguage.SINHALA to "මුල් පිටුව",
            AppLanguage.TAMIL to "முகப்பு"
        ),
        "shop" to mapOf(
            AppLanguage.ENGLISH to "Shop",
            AppLanguage.SINHALA to "සාප්පුව",
            AppLanguage.TAMIL to "கடை"
        ),
        "track_order" to mapOf(
            AppLanguage.ENGLISH to "Track Order",
            AppLanguage.SINHALA to "ඇණවුම ලුහුබඳින්න",
            AppLanguage.TAMIL to "ஆர்டரை கண்காணிக்கவும்"
        ),
        "wishlist" to mapOf(
            AppLanguage.ENGLISH to "Wishlist",
            AppLanguage.SINHALA to "කැමති ලැයිස්තුව",
            AppLanguage.TAMIL to "விருப்பப்பட்டியல்"
        ),
        "cart" to mapOf(
            AppLanguage.ENGLISH to "Cart",
            AppLanguage.SINHALA to "කරත්තය",
            AppLanguage.TAMIL to "வண்டி"
        ),
        "profile" to mapOf(
            AppLanguage.ENGLISH to "Profile",
            AppLanguage.SINHALA to "ගිණුම",
            AppLanguage.TAMIL to "சுயவிவரம்"
        ),
        "admin" to mapOf(
            AppLanguage.ENGLISH to "Admin",
            AppLanguage.SINHALA to "පරිපාලක",
            AppLanguage.TAMIL to "நிர்வாகி"
        ),
        "categories" to mapOf(
            AppLanguage.ENGLISH to "SHOP CATEGORIES",
            AppLanguage.SINHALA to "වර්ගීකරණයන්",
            AppLanguage.TAMIL to "பிரிவுகள்"
        ),
        "latest_products" to mapOf(
            AppLanguage.ENGLISH to "LATEST ROUTERS & TECH PRODUCTS",
            AppLanguage.SINHALA to "නවතම රවුටර් සහ තාක්ෂණික නිෂ්පාදන",
            AppLanguage.TAMIL to "சமீபத்திய ரவுட்டர்கள் & தொழில்நுட்ப பொருட்கள்"
        ),
        "add_to_cart" to mapOf(
            AppLanguage.ENGLISH to "Add to Cart",
            AppLanguage.SINHALA to "කරත්තයට එක් කරන්න",
            AppLanguage.TAMIL to "வண்டியில் சேர்க்கவும்"
        ),
        "buy_now" to mapOf(
            AppLanguage.ENGLISH to "Buy Now",
            AppLanguage.SINHALA to "දැන් මිලදී ගන්න",
            AppLanguage.TAMIL to "இப்போது வாங்கவும்"
        ),
        "payzy_info" to mapOf(
            AppLanguage.ENGLISH to "or up to 4 x installments with PayZy",
            AppLanguage.SINHALA to "හෝ PayZy හරහා වාරික 4 කින් ගෙවන්න",
            AppLanguage.TAMIL to "அல்லது PayZy உடன் 4 தவணைகளில் செலுத்தவும்"
        ),
        "checkout" to mapOf(
            AppLanguage.ENGLISH to "Checkout",
            AppLanguage.SINHALA to "ගෙවීම් කිරීමට යන්න",
            AppLanguage.TAMIL to "செக்அவுட்"
        ),
        "total" to mapOf(
            AppLanguage.ENGLISH to "Total Amount",
            AppLanguage.SINHALA to "මුළු එකතුව",
            AppLanguage.TAMIL to "மொத்த தொகை"
        ),
        "payment_method" to mapOf(
            AppLanguage.ENGLISH to "Payment Gateway",
            AppLanguage.SINHALA to "ගෙවීම් ක්‍රමය",
            AppLanguage.TAMIL to "பணம் செலுத்தும் முறை"
        ),
        "order_tracking_status" to mapOf(
            AppLanguage.ENGLISH to "Delivery & Order Tracking",
            AppLanguage.SINHALA to "ඇණවුම් ලුහුබැඳීමේ තත්ත්වය",
            AppLanguage.TAMIL to "டெலிவரி & ஆர்டர் கண்காணிப்பு"
        ),
        "write_review" to mapOf(
            AppLanguage.ENGLISH to "Write a Product Review",
            AppLanguage.SINHALA to "සමාලෝචනයක් එක් කරන්න",
            AppLanguage.TAMIL to "ஒரு மதிப்புரையை எழுதுங்கள்"
        ),
        "submit" to mapOf(
            AppLanguage.ENGLISH to "Submit",
            AppLanguage.SINHALA to "යොමු කරන්න",
            AppLanguage.TAMIL to "சமர்ப்பிக்கவும்"
        ),
        "login_title" to mapOf(
            AppLanguage.ENGLISH to "Sign In to SmartZone",
            AppLanguage.SINHALA to "SmartZone වෙත පිවිසෙන්න",
            AppLanguage.TAMIL to "SmartZone இல் உள்நுழையவும்"
        ),
        "google_signin" to mapOf(
            AppLanguage.ENGLISH to "Continue with Google Sign-In",
            AppLanguage.SINHALA to "Google හරහා පිවිසෙන්න",
            AppLanguage.TAMIL to "Google உடன் தொடரவும்"
        )
    )

    fun getString(key: String, language: AppLanguage): String {
        val map = translations[key]
        return map?.get(language) ?: map?.get(AppLanguage.ENGLISH) ?: key
    }
}
