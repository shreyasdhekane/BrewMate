# BrewMate

> **Barista-side coffee ordering for Android — built with Kotlin & Jetpack Compose**

🌐 **[View Landing Page](https://shreyasdhekane.github.io/BrewMate)**

---

## Overview

BrewMate is a barista-side kiosk app that lets staff take customized coffee orders. Every milk swap, syrup pump, and extra shot is handled by a live **Decorator pattern** implementation — the order description and price update instantly as add-ons are selected.

---

## Screenshots

| Home                     | History                     | Customize                    | Cart                     | Receipt                        |
| ------------------------ | --------------------------- | ---------------------------- | ------------------------ | ------------------------------ |
| ![Home](images/home.png) | ![Menu](images/history.png) | ![Detail](images/detail.png) | ![Cart](images/cart.png) | ![Receipt](images/receipt.png) |

---

## Features

- 🎨 **Decorator pattern** — base coffees wrapped by milk, syrup, whip, and drizzle decorators; price and description update in real time
- 👤 **Barista auth** — 5-digit ID login (ID = username + password), fixed admin code (`00000`), and guest mode
- 🛒 **Full cart flow** — add/remove items, quantity controls, subtotal + 8% tax, place order
- 🧾 **Receipt system** — quick dialog on order placement + full itemized receipt screen with mock print button
- 📋 **Order history** — persisted log of all placed orders, expandable per-entry breakdown
- ❤️ **Favourites** — heart toggle on menu cards, dedicated Favourites tab
- 💾 **DataStore persistence** — cart, favourites, order history, and session survive app restarts
- ✨ **Polish** — screen slide/fade transitions, animated cart badge, spring-bounce chip selection, press-scale feedback on key buttons

---

## Tech Stack

| Layer         | Technology                      |
| ------------- | ------------------------------- |
| Language      | Kotlin                          |
| UI            | Jetpack Compose + Material3     |
| Architecture  | MVVM                            |
| State         | StateFlow + ViewModel           |
| Navigation    | Navigation Compose              |
| Persistence   | Jetpack DataStore (Preferences) |
| Serialization | `org.json` (bundled)            |
| Min SDK       | API 26 (Android 8.0)            |

---

## Design Pattern

The core ordering logic uses the **Decorator pattern** from `model/Coffee.kt`:

```kotlin
interface Coffee {
    fun getDescription(): String
    fun getCost(): Double
}

// Base coffees: Espresso, HotChocolate, IceCoffee, CamelFrappuccino, MixedBlackCoffee

// Decorators wrap any Coffee and add to its description + cost:
val order: Coffee = ExtraShotDecorator(
    VanillaSyrupDecorator(
        MilkDecorator(Espresso(), MilkType.OAT)
    )
)

order.getDescription() // "Espresso + Oat Milk + Vanilla Syrup + Extra Shot"
order.getCost()        // 2.50 + 0.65 + 0.60 + 1.00 = $4.75
```

---

## Package Structure

```
com.brewmate/
├── model/
│   ├── Coffee.kt          # Interface, base coffees, decorators, MilkType, AddOnType
│   ├── CartItem.kt        # Cart entry with full customization metadata
│   ├── MenuItem.kt        # Menu data + MenuData object
│   └── OrderHistory.kt    # OrderHistoryEntry + OrderHistoryItem
├── data/
│   └── BrewDataStore.kt   # DataStore persistence (cart, favourites, orders, auth)
├── viewmodel/
│   └── BrewViewModel.kt   # Single ViewModel — all app state via StateFlow
├── ui/
│   ├── screens/
│   │   ├── AuthScreen.kt
│   │   ├── HomeScreen.kt
│   │   ├── MenuScreen.kt
│   │   ├── ProductDetailScreen.kt
│   │   ├── CartScreen.kt
│   │   ├── FavouriteScreen.kt
│   │   ├── ProfileScreen.kt
│   │   ├── OrderHistoryScreen.kt
│   │   └── ReceiptScreen.kt
│   ├── navigation/
│   │   ├── Screen.kt
│   │   └── AppNavGraph.kt
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   └── util/
│       └── PressScale.kt  # Shared press-feedback Modifier
└── MainActivity.kt
```

---

## Auth Flow

| Mode    | How to access                                                                            |
| ------- | ---------------------------------------------------------------------------------------- |
| Barista | Enter your 5-digit ID — first time registers your name, subsequent logins go straight in |
| Admin   | Enter `00000`                                                                            |
| Guest   | Tap "Continue as Guest" — session is not persisted across restarts                       |

---

## Getting Started

1. Clone the repo
   ```bash
   git clone https://github.com/shreyasdhekane/BrewMate.git
   ```
2. Open in **Android Studio Hedgehog** or later
3. Sync Gradle — all dependencies resolve from `libs.versions.toml`
4. Run on an emulator (API 26+) or physical device

---

## Landing Page

The project showcase page is built with vanilla HTML, CSS, and JavaScript — no frameworks. It features a live interactive order builder that mirrors the Decorator pattern logic from `model/Coffee.kt`.

🌐 **[brewmate landing page](https://shreyasdhekane.github.io/BrewMate)**

Source: `/Landing Page/` in this repo.

---

## Author

**Shreyas Dhekane**
MSCS · Indiana University Luddy School · May 2027

[![LinkedIn](https://img.shields.io/badge/LinkedIn-shreyasdhekane-0A66C2?style=flat&logo=linkedin)](https://www.linkedin.com/in/shreyas-dhekane/)
[![GitHub](https://img.shields.io/badge/GitHub-shreyasdhekane-181717?style=flat&logo=github)](https://github.com/shreyasdhekane)

---

_Built as a portfolio project demonstrating the Decorator design pattern in a real Android application._
