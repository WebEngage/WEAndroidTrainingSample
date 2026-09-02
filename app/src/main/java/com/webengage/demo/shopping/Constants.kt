package com.webengage.demo.shopping

object Constants {
     const val homeTAG = "HOME"
     const val userTAG = "USER"
     const val cartTAG = "CART"
     const val demoTAG = "DEMO"
     const val WEBENGAGE_ENGAGED = "webengage_engaged"
     const val CUID = "username"
     const val LICENSE_CODE = "license_code"
     const val TAG = "WEBENGAGE"
     const val FCM_REG_ID = "fcm_reg_id"
     const val SDK_MINIFIED = "sdk_minified"
     const val JSON_URL = "json_url"

     // Cross-sell session keys
     const val SESSION_USERNAME = "session_username"
     const val SESSION_TIER = "session_tier"

     // WebEngage custom user attribute
     const val ATTR_RELATIONSHIP_VALUE_TIER = "relationship_value_tier"

     // WebEngage event names (use exactly as written)
     const val EVENT_CARD_PROMOTION_VISITED = "card_promotion_visited"
     const val EVENT_CARD_APPLICATION_STARTED = "card_application_started"
     const val EVENT_CARD_APPLICATION_COMPLETED = "card_application_completed"

     // Allowed relationship tier values
     val TIER_OPTIONS = listOf("tier_1", "tier_2", "tier_3", "tier_4")

     // Human-readable tier labels for the Account pill (match the website's TIER_LABELS)
     val TIER_LABELS = mapOf(
         "tier_1" to "tier_1 — Premium",
         "tier_2" to "tier_2 — Gold",
         "tier_3" to "tier_3 — Standard",
         "tier_4" to "tier_4 — Basic (not eligible for offers)"
     )

     fun tierLabel(tier: String): String = TIER_LABELS[tier] ?: tier

     // Fragment tags for the card app shell
     const val cardsTAG = "CARDS"
     const val accountTAG = "ACCOUNT"

     // Single inline personalization placement id (must match the dashboard campaign
     // targetView). Both the promotion and abandonment campaigns render into this one
     // slot; the dashboard decides which shows.
     const val PLACEMENT_PROMOTION = "we_home_inline"
}
