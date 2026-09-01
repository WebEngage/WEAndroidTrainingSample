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
     const val ATTR_RELATIONSHIP_VALUE_TIER = "policyNumber"

     // WebEngage event names (use exactly as written)
     const val EVENT_CARD_PROMOTION_VISITED = "card_promotion_visited"
     const val EVENT_CARD_APPLICATION_STARTED = "card_application_started"
     const val EVENT_CARD_APPLICATION_COMPLETED = "card_application_completed"

     // Allowed relationship tier values
     val TIER_OPTIONS = listOf("tier_1", "tier_2", "tier_3", "tier_4")

     // Fragment tags for the card app shell
     const val cardsTAG = "CARDS"
     const val accountTAG = "ACCOUNT"

     // Inline personalization placement ids (must match the dashboard campaign targetView)
     const val PLACEMENT_PROMOTION = "visa_promotion"
     const val PLACEMENT_ABANDONMENT = "visa_cart_abandonment"
}
