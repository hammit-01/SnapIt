package com.example.snapit.Datastore

data class UserDataClass(
    val uid: String,
    val name: String,
    var nick_name: String,
    val b_day: String,
    var comment: String,
    val imgNum: Int,
    val videoNum: Int,
    var profile: String? = null
)