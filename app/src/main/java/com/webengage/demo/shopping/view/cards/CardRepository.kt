package com.webengage.demo.shopping.view.cards

import android.content.Context
import android.util.Log
import com.webengage.demo.shopping.Constants
import org.json.JSONObject

/**
 * Loads the bundled cards.json asset and parses it with org.json
 * (matching the existing manual-parsing convention in this codebase — no Gson).
 */
object CardRepository {

    private var cache: List<Card>? = null

    fun getCards(context: Context): List<Card> {
        cache?.let { return it }
        val cards = mutableListOf<Card>()
        try {
            val json = context.assets.open("cards.json")
                .bufferedReader()
                .use { it.readText() }
            val array = JSONObject(json).getJSONArray("cards")
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val featureList = mutableListOf<String>()
                obj.optJSONArray("features")?.let { arr ->
                    for (j in 0 until arr.length()) featureList.add(arr.getString(j))
                }
                cards.add(
                    Card(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        tier = if (obj.isNull("tier")) null else obj.optString("tier"),
                        limit = obj.getString("limit"),
                        annualFee = obj.optString("annualFee"),
                        description = obj.getString("description"),
                        imageRes = obj.getString("imageRes"),
                        features = featureList
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(Constants.TAG, "Error parsing cards.json: ${e.message}")
        }
        cache = cards
        return cards
    }

    fun findById(context: Context, id: String?): Card? {
        if (id == null) return null
        return getCards(context).firstOrNull { it.id == id }
    }
}
