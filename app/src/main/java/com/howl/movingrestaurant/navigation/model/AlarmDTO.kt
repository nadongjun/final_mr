package com.howl.movingrestaurant.navigation.model

data class AlarmDTO(
    var destinationUid : String? = null,
    var userId : String? = null,
    var uid : String? = null,

    //0 : like alarm
    //1 : comment alarm
    //2 : follow alarmd
    //3 : unfollow
    //4 : Send message
    //5 : Get message
    var kind : Int? = null,
    var message : String? = null,
    var timestamp : Long? = null
)