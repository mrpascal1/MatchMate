package com.shahidshaadi.matchmate.data.remote

data class User(
    val name: Name?,
    val dob: Dob?,
    val picture: Picture?,
    val gender: String?,
    val email: String?,
    val phone: String?,
    val login: Login?,
    val location: Location?
)

data class Name(
    val first: String?,
    val last: String?
)

data class Dob(
    val age: Int?
)

data class Picture(
    val large: String?
)

data class Login(
    val uuid: String?
)

data class Location(
    val city: String?,
    val state: String?,
    val country: String?
)