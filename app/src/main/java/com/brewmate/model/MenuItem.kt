package com.brewmate.model

data class MenuItem(
    val id: String,
    val name: String,
    val basePrice: Double,
    val rating: Double,
    val category: String,
    val createCoffee: () -> Coffee
)

object MenuData {
    val items = listOf(
        MenuItem("espresso", "Espresso", 2.50, 4.6, "Hot") { Espresso() },
        MenuItem("hot_chocolate", "Hot Chocolate", 3.00, 4.4, "Hot") { HotChocolate() },
        MenuItem("ice_coffee", "Iced Coffee", 3.25, 4.5, "Cold") { IceCoffee() },
        MenuItem("caramel_frap", "Caramel Frappuccino", 4.50, 4.7, "Cold") { CamelFrappuccino() },
        MenuItem("mixed_black", "Mixed Black Coffee", 2.75, 4.3, "Hot") { MixedBlackCoffee() }
    )
}