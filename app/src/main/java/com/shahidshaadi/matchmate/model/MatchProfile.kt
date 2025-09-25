package com.shahidshaadi.matchmate.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shahidshaadi.matchmate.data.remote.User

@Entity(tableName = "matches")
data class MatchProfile(
    @PrimaryKey val id: String,
    val name: String?,
    val age: Int?,
    val gender: String?,
    val photoUrl: String?,
    val status: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val fullLocation: String? = null,
    val page: Int
)

fun User.toMatchProfile(page: Int): MatchProfile {
    val fullName = "${name?.first} ${name?.last}"
    val age = dob?.age
    val photo = picture?.large
    val email = this.email
    val phone = this.phone
    val fullLocation = "${location?.city}, ${location?.state}, ${location?.country}"
    return MatchProfile(
        id = login?.uuid ?: "-1",
        name = fullName,
        age = age,
        gender = gender,
        photoUrl = photo,
        email = email,
        phone = phone,
        fullLocation = fullLocation,
        page = page
    )
}