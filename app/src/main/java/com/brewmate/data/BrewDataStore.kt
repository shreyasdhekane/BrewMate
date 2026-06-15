package com.brewmate.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.brewmate.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import com.brewmate.model.*


private val Context.dataStore by preferencesDataStore(name = "brewmate_prefs")

class BrewDataStore(private val context: Context) {

    private object Keys {
        val CART = stringPreferencesKey("cart_items")
        val FAVOURITES = stringPreferencesKey("favourite_ids")
        val ORDER_COUNT = intPreferencesKey("todays_order_count")
        val ORDER_HISTORY = stringPreferencesKey("order_history")

        val REGISTERED_BARISTAS = stringPreferencesKey("registered_baristas")

        val CURRENT_BARISTA_ID = stringPreferencesKey("current_barista_id")
    }

    //Cart

    val cartFlow: Flow<List<CartItem>> = context.dataStore.data.map { prefs ->
        parseCartItems(prefs[Keys.CART] ?: "[]")
    }

    suspend fun saveCart(cartItems: List<CartItem>) {
        context.dataStore.edit { prefs ->
            prefs[Keys.CART] = serializeCartItems(cartItems)
        }
    }

    private fun serializeCartItems(items: List<CartItem>): String {
        val array = JSONArray()
        items.forEach { item ->
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("menuItemId", item.menuItemId)
            obj.put("milkType", item.milkType.name)
            val addOnsArray = JSONArray()
            item.addOns.forEach { addOnsArray.put(it.name) }
            obj.put("addOns", addOnsArray)
            obj.put("quantity", item.quantity)
            array.put(obj)
        }
        return array.toString()
    }

    private fun parseCartItems(json: String): List<CartItem> {
        val array = JSONArray(json)
        val result = mutableListOf<CartItem>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val menuItemId = obj.getString("menuItemId")
            val menuItem = MenuData.items.find { it.id == menuItemId } ?: continue

            val milkType = MilkType.valueOf(obj.getString("milkType"))
            val addOnsArray = obj.getJSONArray("addOns")
            val addOns = mutableSetOf<AddOnType>()
            for (j in 0 until addOnsArray.length()) {
                addOns.add(AddOnType.valueOf(addOnsArray.getString(j)))
            }
            val quantity = obj.getInt("quantity")
            val coffee = buildCoffee(menuItem, milkType, addOns)

            result.add(
                CartItem(
                    id = obj.getString("id"),
                    coffee = coffee,
                    menuItemId = menuItemId,
                    milkType = milkType,
                    addOns = addOns,
                    quantity = quantity
                )
            )
        }
        return result
    }

    // Favourites

    val favouritesFlow: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        val array = JSONArray(prefs[Keys.FAVOURITES] ?: "[]")
        (0 until array.length()).map { array.getString(it) }.toSet()
    }

    suspend fun saveFavourites(ids: Set<String>) {
        context.dataStore.edit { prefs ->
            val array = JSONArray()
            ids.forEach { array.put(it) }
            prefs[Keys.FAVOURITES] = array.toString()
        }
    }

    // Today's Order Count

    val orderCountFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[Keys.ORDER_COUNT] ?: 0
    }

    suspend fun saveOrderCount(count: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ORDER_COUNT] = count
        }
    }

    // Order History

    val orderHistoryFlow: Flow<List<OrderHistoryEntry>> = context.dataStore.data.map { prefs ->
        parseOrderHistory(prefs[Keys.ORDER_HISTORY] ?: "[]")
    }

    suspend fun saveOrderHistory(history: List<OrderHistoryEntry>) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ORDER_HISTORY] = serializeOrderHistory(history)
        }
    }

    private fun serializeOrderHistory(history: List<OrderHistoryEntry>): String {
        val array = JSONArray()
        history.forEach { entry ->
            val obj = JSONObject()
            obj.put("orderId", entry.orderId)
            obj.put("timestamp", entry.timestamp)
            obj.put("subtotal", entry.subtotal)
            obj.put("tax", entry.tax)
            obj.put("total", entry.total)

            val itemsArray = JSONArray()
            entry.items.forEach { item ->
                val itemObj = JSONObject()
                itemObj.put("menuItemId", item.menuItemId)
                itemObj.put("description", item.description)
                itemObj.put("unitPrice", item.unitPrice)
                itemObj.put("quantity", item.quantity)
                itemsArray.put(itemObj)
            }
            obj.put("items", itemsArray)
            array.put(obj)
        }
        return array.toString()
    }

    private fun parseOrderHistory(json: String): List<OrderHistoryEntry> {
        val array = JSONArray(json)
        val result = mutableListOf<OrderHistoryEntry>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val itemsArray = obj.getJSONArray("items")
            val items = mutableListOf<OrderHistoryItem>()
            for (j in 0 until itemsArray.length()) {
                val itemObj = itemsArray.getJSONObject(j)
                items.add(
                    OrderHistoryItem(
                        menuItemId = itemObj.getString("menuItemId"),
                        description = itemObj.getString("description"),
                        unitPrice = itemObj.getDouble("unitPrice"),
                        quantity = itemObj.getInt("quantity")
                    )
                )
            }
            result.add(
                OrderHistoryEntry(
                    orderId = obj.getString("orderId"),
                    timestamp = obj.getLong("timestamp"),
                    items = items,
                    subtotal = obj.getDouble("subtotal"),
                    tax = obj.getDouble("tax"),
                    total = obj.getDouble("total")
                )
            )
        }
        return result.sortedByDescending { it.timestamp }
    }
    // Auth

    val registeredBaristasFlow: Flow<List<BaristaProfile>> = context.dataStore.data.map { prefs ->
        parseBaristas(prefs[Keys.REGISTERED_BARISTAS] ?: "[]")
    }

    suspend fun saveRegisteredBaristas(baristas: List<BaristaProfile>) {
        context.dataStore.edit { prefs ->
            prefs[Keys.REGISTERED_BARISTAS] = serializeBaristas(baristas)
        }
    }

    val currentBaristaIdFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[Keys.CURRENT_BARISTA_ID]?.takeIf { it.isNotEmpty() }
    }

    suspend fun saveCurrentBaristaId(id: String?) {
        context.dataStore.edit { prefs ->
            prefs[Keys.CURRENT_BARISTA_ID] = id ?: ""
        }
    }

    private fun serializeBaristas(list: List<BaristaProfile>): String {
        val array = JSONArray()
        list.forEach {
            val obj = JSONObject()
            obj.put("id", it.id)
            obj.put("name", it.name)
            obj.put("role", it.role.name)
            obj.put("shiftInfo", it.shiftInfo)
            array.put(obj)
        }
        return array.toString()
    }

    private fun parseBaristas(json: String): List<BaristaProfile> {
        val array = JSONArray(json)
        val result = mutableListOf<BaristaProfile>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            result.add(
                BaristaProfile(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    role = BaristaRole.valueOf(obj.optString("role", "BARISTA")),
                    shiftInfo = obj.optString("shiftInfo", "Morning Shift • 7:00 AM – 3:00 PM")
                )
            )
        }
        return result
    }
}