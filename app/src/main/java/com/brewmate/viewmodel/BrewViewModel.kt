package com.brewmate.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.brewmate.data.BrewDataStore
import com.brewmate.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BrewViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = BrewDataStore(application.applicationContext)

    //Cart State
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    val cartItemCount: StateFlow<Int> = _cartItems
        .map { items -> items.sumOf { it.quantity } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val cartSubtotal: StateFlow<Double> = _cartItems
        .map { items -> items.sumOf { it.totalPrice } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val cartTax: StateFlow<Double> = cartSubtotal
        .map { it * 0.08 }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val cartTotal: StateFlow<Double> = combine(cartSubtotal, cartTax) { sub, tax -> sub + tax }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    //Today's Orders

    private val _todaysOrderCount = MutableStateFlow(0)
    val todaysOrderCount: StateFlow<Int> = _todaysOrderCount.asStateFlow()

    //Order History

    private val _orderHistory = MutableStateFlow<List<OrderHistoryEntry>>(emptyList())
    val orderHistory: StateFlow<List<OrderHistoryEntry>> = _orderHistory.asStateFlow()

    //Favourites

    private val _favouriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favouriteIds: StateFlow<Set<String>> = _favouriteIds.asStateFlow()

    fun toggleFavourite(menuItemId: String) {
        _favouriteIds.update { current ->
            if (menuItemId in current) current - menuItemId else current + menuItemId
        }
        viewModelScope.launch { dataStore.saveFavourites(_favouriteIds.value) }
    }

    //Product Customization

    private val _selectedMilkType = MutableStateFlow(MilkType.WHOLE)
    val selectedMilkType: StateFlow<MilkType> = _selectedMilkType.asStateFlow()

    private val _selectedAddOns = MutableStateFlow<Set<AddOnType>>(emptySet())
    val selectedAddOns: StateFlow<Set<AddOnType>> = _selectedAddOns.asStateFlow()

    private val _selectedQuantity = MutableStateFlow(1)
    val selectedQuantity: StateFlow<Int> = _selectedQuantity.asStateFlow()

    fun selectMilkType(milkType: MilkType) {
        _selectedMilkType.value = milkType
    }

    fun toggleAddOn(addOn: AddOnType) {
        _selectedAddOns.update { current ->
            if (addOn in current) current - addOn else current + addOn
        }
    }

    fun setQuantity(quantity: Int) {
        _selectedQuantity.value = quantity.coerceAtLeast(1)
    }

    fun incrementQuantity() = setQuantity(_selectedQuantity.value + 1)
    fun decrementQuantity() = setQuantity(_selectedQuantity.value - 1)

    fun resetCustomization() {
        _selectedMilkType.value = MilkType.WHOLE
        _selectedAddOns.value = emptySet()
        _selectedQuantity.value = 1
    }

    fun buildCustomizedCoffee(menuItem: MenuItem): Coffee {
        return buildCoffee(menuItem, _selectedMilkType.value, _selectedAddOns.value)
    }

    //Cart Actions

    fun addCurrentSelectionToCart(menuItem: MenuItem) {
        val coffee = buildCustomizedCoffee(menuItem)
        val cartItem = CartItem(
            coffee = coffee,
            menuItemId = menuItem.id,
            milkType = _selectedMilkType.value,
            addOns = _selectedAddOns.value,
            quantity = _selectedQuantity.value
        )
        _cartItems.update { it + cartItem }
        resetCustomization()
        persistCart()
    }

    fun updateCartItemQuantity(cartItemId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(cartItemId)
            return
        }
        _cartItems.update { items ->
            items.map { if (it.id == cartItemId) it.copy(quantity = newQuantity) else it }
        }
        persistCart()
    }

    fun removeFromCart(cartItemId: String) {
        _cartItems.update { items -> items.filter { it.id != cartItemId } }
        persistCart()
    }

    fun placeOrder() {
        val items = _cartItems.value
        val itemsOrdered = items.sumOf { it.quantity }
        if (itemsOrdered <= 0) return

        val historyItems = items.map {
            OrderHistoryItem(
                menuItemId = it.menuItemId,
                description = it.description,
                unitPrice = it.unitPrice,
                quantity = it.quantity
            )
        }
        val entry = OrderHistoryEntry(
            timestamp = System.currentTimeMillis(),
            items = historyItems,
            subtotal = cartSubtotal.value,
            tax = cartTax.value,
            total = cartTotal.value
        )
        _lastPlacedOrder.value = entry
        _orderHistory.update { listOf(entry) + it }
        _todaysOrderCount.update { it + itemsOrdered }
        _cartItems.value = emptyList()

        viewModelScope.launch {
            dataStore.saveOrderHistory(_orderHistory.value)
            dataStore.saveOrderCount(_todaysOrderCount.value)
            dataStore.saveCart(_cartItems.value)
        }
    }

    private fun persistCart() {
        viewModelScope.launch { dataStore.saveCart(_cartItems.value) }
    }


    init {
        viewModelScope.launch {
            _cartItems.value = dataStore.cartFlow.first()
            _favouriteIds.value = dataStore.favouritesFlow.first()
            _todaysOrderCount.value = dataStore.orderCountFlow.first()
            _orderHistory.value = dataStore.orderHistoryFlow.first()
            _registeredBaristas.value = dataStore.registeredBaristasFlow.first()

            val savedId = dataStore.currentBaristaIdFlow.first()
            _currentBarista.value = when {
                savedId == null -> null
                savedId == ADMIN_ID -> BaristaProfile(
                    id = ADMIN_ID, name = "Admin", role = BaristaRole.ADMIN, shiftInfo = "Admin Access"
                )
                else -> _registeredBaristas.value.find { it.id == savedId }
            }

            _isLoading.value = false
        }
    }

    private val _currentBarista = MutableStateFlow<BaristaProfile?>(null)
    val currentBarista: StateFlow<BaristaProfile?> = _currentBarista.asStateFlow()

    private val _registeredBaristas = MutableStateFlow<List<BaristaProfile>>(emptyList())

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    sealed class SignInResult {
        object Success : SignInResult()
        object NotFound : SignInResult()
        object InvalidFormat : SignInResult()
    }

    fun signIn(id: String): SignInResult {
        if (!id.matches(Regex("^\\d{5}$"))) {
            return SignInResult.InvalidFormat
        }
        if (id == ADMIN_ID) {
            val admin = BaristaProfile(
                id = ADMIN_ID,
                name = "Admin",
                role = BaristaRole.ADMIN,
                shiftInfo = "Admin Access"
            )
            _currentBarista.value = admin
            persistCurrentBarista(admin.id)
            return SignInResult.Success
        }
        val found = _registeredBaristas.value.find { it.id == id }
        return if (found != null) {
            _currentBarista.value = found
            persistCurrentBarista(found.id)
            SignInResult.Success
        } else {
            SignInResult.NotFound
        }
    }

    sealed class RegisterResult {
        object Success : RegisterResult()
        object IdTaken : RegisterResult()
        object InvalidFormat : RegisterResult()
        object EmptyName : RegisterResult()
    }

    fun register(id: String, name: String): RegisterResult {
        if (!id.matches(Regex("^\\d{5}$")) || id == ADMIN_ID) {
            return RegisterResult.InvalidFormat
        }
        if (name.isBlank()) {
            return RegisterResult.EmptyName
        }
        if (_registeredBaristas.value.any { it.id == id }) {
            return RegisterResult.IdTaken
        }
        val profile = BaristaProfile(id = id, name = name.trim())
        _registeredBaristas.update { it + profile }
        _currentBarista.value = profile
        viewModelScope.launch {
            dataStore.saveRegisteredBaristas(_registeredBaristas.value)
        }
        persistCurrentBarista(profile.id)
        return RegisterResult.Success
    }

    fun continueAsGuest() {
        _currentBarista.value = BaristaProfile(
            id = GUEST_ID,
            name = "Guest",
            role = BaristaRole.GUEST,
            shiftInfo = "Guest Access"
        )

    }

    fun logOut() {
        _currentBarista.value = null
        persistCurrentBarista(null)
    }

    private fun persistCurrentBarista(id: String?) {
        viewModelScope.launch { dataStore.saveCurrentBaristaId(id) }
    }
    private val _lastPlacedOrder = MutableStateFlow<OrderHistoryEntry?>(null)
    val lastPlacedOrder: StateFlow<OrderHistoryEntry?> = _lastPlacedOrder.asStateFlow()

    fun clearLastPlacedOrder() {
        _lastPlacedOrder.value = null
    }
}