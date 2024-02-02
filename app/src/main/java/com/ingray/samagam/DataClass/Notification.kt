package com.ingray.samagam.DataClass

data class Notification(
    var title: String="",
    var message: String="",
    var imageUrl: String="",
    var clubUrl: String="",
    var time: String="",
    var token: String="",
    var timeDifference: Long=0,
    var pdfLink:String="",
    var description:String=""
)