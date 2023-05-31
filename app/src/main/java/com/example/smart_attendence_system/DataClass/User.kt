package com.example.smart_attendence_system.DataClass

import javax.security.auth.Subject

data class User(
    val class_name:String? = null,
    val section:String?=null,
    val subject:String?=null,
    var classid:String?=null

)
