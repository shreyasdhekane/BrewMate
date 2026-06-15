package com.brewmate.model

enum class BaristaRole {
    BARISTA, ADMIN, GUEST
}

data class BaristaProfile(
    val id: String,
    val name: String,
    val role: BaristaRole = BaristaRole.BARISTA,
    val shiftInfo: String = "Morning Shift • 7:00 AM – 3:00 PM"
)

const val ADMIN_ID = "00000"
const val GUEST_ID = "GUEST"