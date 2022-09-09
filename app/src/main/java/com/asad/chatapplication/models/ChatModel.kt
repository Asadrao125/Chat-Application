package com.asad.chatapplication.models

class ChatModel(
    var message: String = "",
    var time: String = "",
    var messageId: String = "",
    var senderId: String = "",
    var recieverId: String = "",
    var imageUrl: String = "",
    var fileUrl: String = "",
    // Message Status
    // 1 for Simple Send
    // 2 for send and delivered
    // 3 for blue tick
    var messageStatus: Int = 0
)