package com.brewmate.model

//Interface
interface Coffee {
    fun getDescription(): String
    fun getCost(): Double
}
// Components
class Espresso: Coffee {
    override fun getDescription(): String = "Espresso"
    override fun getCost(): Double = 2.50
}
class HotChocolate : Coffee {
    override fun getDescription(): String = "Hot Chocolate"
    override fun getCost(): Double = 3.00
}

class IceCoffee : Coffee {
    override fun getDescription(): String = "Iced Coffee"
    override fun getCost(): Double = 3.25
}

class CamelFrappuccino : Coffee {
    override fun getDescription(): String = "Caramel Frappuccino"
    override fun getCost(): Double = 4.50
}

class MixedBlackCoffee : Coffee {
    override fun getDescription(): String = "Mixed Black Coffee"
    override fun getCost(): Double = 2.75
}

//Decorator Base Class
abstract class CoffeeDecorator(protected open val decoratedCoffee: Coffee) : Coffee{
    override fun getDescription(): String = decoratedCoffee.getDescription()
    override fun getCost(): Double = decoratedCoffee.getCost()
}
enum class MilkType(val label: String, val extraCost: Double) {
    WHOLE("Whole Milk", 0.0),
    TWO_PERCENT("2% Milk", 0.0),
    ONE_PERCENT("1% Milk", 0.0),
    SKIM("Skim Milk", 0.0),
    OAT("Oat Milk", 0.65),
    ALMOND("Almond Milk", 0.65),
    SOY("Soy Milk", 0.65)
}

class MilkDecorator(coffee: Coffee, private val milkType: MilkType) : CoffeeDecorator(coffee) {
    override fun getDescription(): String = "${decoratedCoffee.getDescription()} + ${milkType.label}"
    override fun getCost(): Double = decoratedCoffee.getCost() + milkType.extraCost
}

class VanillaSyrupDecorator(coffee: Coffee) : CoffeeDecorator(coffee) {
    override fun getDescription(): String = "${decoratedCoffee.getDescription()} + Vanilla Syrup"
    override fun getCost(): Double = decoratedCoffee.getCost() + 0.60
}

class ExtraShotDecorator(coffee: Coffee) : CoffeeDecorator(coffee) {
    override fun getDescription(): String = "${decoratedCoffee.getDescription()} + Extra Shot"
    override fun getCost(): Double = decoratedCoffee.getCost() + 1.00
}

class WhipCreamDecorator(coffee: Coffee) : CoffeeDecorator(coffee) {
    override fun getDescription(): String = "${decoratedCoffee.getDescription()} + Whip Cream"
    override fun getCost(): Double = decoratedCoffee.getCost() + 0.75
}

class CaramelDrizzleDecorator(coffee: Coffee) : CoffeeDecorator(coffee) {
    override fun getDescription(): String = "${decoratedCoffee.getDescription()} + Caramel Drizzle"
    override fun getCost(): Double = decoratedCoffee.getCost() + 0.70
}

//Add-ons Type(Multi)
enum class AddOnType(val label: String){
    VANILLA_SYRUP("Vanilla Syrup"),
    EXTRA_SHOT("Extra Shot"),
    WHIP_CREAM("Whip Cream"),
    CARAMEL_DRIZZLE("Caramel Drizzle")
}

fun applyAddOn(coffee: Coffee, type: AddOnType): Coffee = when (type) {
    AddOnType.VANILLA_SYRUP -> VanillaSyrupDecorator(coffee)
    AddOnType.EXTRA_SHOT -> ExtraShotDecorator(coffee)
    AddOnType.WHIP_CREAM -> WhipCreamDecorator(coffee)
    AddOnType.CARAMEL_DRIZZLE -> CaramelDrizzleDecorator(coffee)
}

//shared coffee builder
fun buildCoffee(menuItem: MenuItem, milkType: MilkType, addOns: Set<AddOnType>): Coffee {
    var coffee: Coffee = menuItem.createCoffee()
    coffee = MilkDecorator(coffee, milkType)
    for (addOn in addOns) {
        coffee = applyAddOn(coffee, addOn)
    }
    return coffee
}