package com.asad.chatapplication.activitiesimport android.annotation.SuppressLintimport android.app.Activityimport android.content.Contextimport android.content.Intentimport android.graphics.Pointimport android.graphics.drawable.BitmapDrawableimport android.media.MediaPlayerimport android.media.MediaPlayer.OnCompletionListenerimport android.net.Uriimport android.os.Bundleimport android.text.Editableimport android.text.TextWatcherimport android.util.Logimport android.view.Gravityimport android.view.LayoutInflaterimport android.view.Viewimport android.widget.*import androidx.annotation.NonNullimport androidx.appcompat.app.AppCompatActivityimport androidx.recyclerview.widget.LinearLayoutManagerimport androidx.recyclerview.widget.RecyclerViewimport com.asad.chatapplication.Rimport com.asad.chatapplication.adapters.ChatAdapterimport com.asad.chatapplication.models.ChatModelimport com.asad.chatapplication.utils.AudioRecorderimport com.asad.chatapplication.utils.Dialog_CustomProgressimport com.asad.chatapplication.utils.RecyclerItemClickListenerimport com.asad.chatapplication.utils.StaticFunctionsimport com.asad.chatapplication.utils.StaticFunctions.Companion.ShowToastimport com.google.android.gms.tasks.OnCompleteListenerimport com.google.android.gms.tasks.OnFailureListenerimport com.google.android.gms.tasks.OnSuccessListenerimport com.google.firebase.auth.FirebaseAuthimport com.google.firebase.database.*import com.google.firebase.storage.FirebaseStorageimport com.google.firebase.storage.UploadTaskimport com.loopj.android.http.AsyncHttpClientimport com.loopj.android.http.AsyncHttpResponseHandlerimport com.squareup.picasso.Picassoimport cz.msebera.android.httpclient.Headerimport cz.msebera.android.httpclient.entity.StringEntityimport org.json.JSONExceptionimport org.json.JSONObjectimport java.io.Fileimport java.io.UnsupportedEncodingExceptionimport java.text.SimpleDateFormatimport java.util.*import kotlin.collections.ArrayListclass Chat : AppCompatActivity() {    var senderId: String = ""    var recieverId: String = ""    var recieverName: String = ""    var recieverProfilePic: String = ""    var fcmToken: String = ""    var senderName: String = ""    var senderProfilePic: String = ""    var etMessage: EditText? = null    var imgSend: ImageView? = null    var imgStartRecording: ImageView? = null    var imgStopRecording: ImageView? = null    var imgAttachment: ImageView? = null    var tvUsername: TextView? = null    var profilePic: ImageView? = null    var imgBack: ImageView? = null    var tvOnline: TextView? = null    var nameLayout: LinearLayout? = null    var mChat: ArrayList<ChatModel>? = ArrayList()    var chatRecyclerview: RecyclerView? = null    var chatAdapter: ChatAdapter? = null    var Image_Request_Code = 7    var FILE_Request_Code = 8    var customProgressDialog: Dialog_CustomProgress? = null    var seenListener: ValueEventListener? = null    var chatRefrence: DatabaseReference? = null    var audioRecorder: AudioRecorder? = null    var mediaPlayer: MediaPlayer = MediaPlayer()    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        setContentView(R.layout.chat)        getSupportActionBar()?.hide()        senderId = intent.getStringExtra("senderId")!!        recieverId = intent.getStringExtra("recieverId")!!        recieverName = intent.getStringExtra("recieverName")!!        fcmToken = intent.getStringExtra("token")!!        senderName = intent.getStringExtra("senderName")!!        recieverProfilePic = intent.getStringExtra("recieverPic")!!        senderProfilePic = intent.getStringExtra("senderPic")!!        customProgressDialog = Dialog_CustomProgress(this)        nameLayout = findViewById(R.id.nameLayout)        etMessage = findViewById(R.id.etMessage)        imgSend = findViewById(R.id.imgSend)        imgStartRecording = findViewById(R.id.imgStartRecording)        imgStopRecording = findViewById(R.id.imgStopRecording)        imgAttachment = findViewById(R.id.imgAttachment)        chatRecyclerview = findViewById(R.id.chatRecyclerview)        tvUsername = findViewById(R.id.tvUsername)        profilePic = findViewById(R.id.profilePic)        imgBack = findViewById(R.id.imgBack)        tvOnline = findViewById(R.id.tvOnline)        val linearLayoutManager = LinearLayoutManager(this)        linearLayoutManager.stackFromEnd = true        chatRecyclerview?.layoutManager = linearLayoutManager        chatRecyclerview?.setHasFixedSize(true)        tvUsername?.setText(recieverName)        Picasso.get().load(recieverProfilePic).placeholder(R.drawable.ic_user).into(profilePic)        imgBack?.setOnClickListener(View.OnClickListener {            onBackPressed()        })        nameLayout?.setOnClickListener {            val intent = Intent(this, ViewProfile::class.java)            intent.putExtra("id", recieverId)            startActivity(intent)        }        chatRecyclerview?.addOnItemTouchListener(            RecyclerItemClickListener(                applicationContext,                chatRecyclerview!!, object : RecyclerItemClickListener.OnItemClickListener {                    override fun onItemClick(view: View?, position: Int) {                        val chatModel: ChatModel = mChat!!.get(position)                        if (!chatModel.voiceMessage.isEmpty()) {                            chatAdapter!!.selectedPosition = position                            chatAdapter!!.notifyDataSetChanged()                            if (mediaPlayer != null) {                                if (mediaPlayer.isPlaying()) {                                    mediaPlayer.stop()                                    mediaPlayer.reset()                                }                            }                            if (mediaPlayer.isPlaying()) {                                mediaPlayer.stop()                                mediaPlayer.reset()                            } else {                                mediaPlayer = MediaPlayer()                                mediaPlayer.setDataSource(chatModel.voiceMessage)                                mediaPlayer.prepare()                                mediaPlayer.start()                            }                            mediaPlayer.setOnCompletionListener(OnCompletionListener { mediaPlayer ->                                mediaPlayer.stop()                                chatAdapter!!.selectedPosition = -1                                chatAdapter!!.notifyDataSetChanged()                            })                        }                    }                    override fun onItemLongClick(view: View?, position: Int) {                        val location = IntArray(2)                        view!!.getLocationOnScreen(location)                        val p = Point()                        p.x = location[0]                        p.y = location[1]                        if (FirebaseAuth.getInstance().currentUser!!.uid.equals(mChat!!.get(position).recieverId)) {                            showStatusPopup(                                this@Chat,                                p,                                mChat!!.get(position).messageId,                                mChat!!.get(position).messageReaction                            )                        }                    }                })        )        imgSend?.setOnClickListener(View.OnClickListener {            val time: String =                SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(Date())            val message: String = etMessage?.text.toString().trim()            if (!message.isEmpty()) {                sendMessage(                    senderId, recieverId, message, time, "",                    "", 1, ""                )            }        })        imgStartRecording?.setOnClickListener {            audioRecorder = AudioRecorder("Service/audio_" + System.currentTimeMillis())            audioRecorder!!.start()            imgStartRecording!!.visibility = View.GONE            imgStopRecording!!.visibility = View.VISIBLE        }        imgStopRecording?.setOnClickListener {            audioRecorder!!.stop()            imgStartRecording!!.visibility = View.VISIBLE            imgStopRecording!!.visibility = View.GONE            Log.d("audio_path", "onCreate: " + audioRecorder!!.path)            uploadVoiceMessageToFirebase(Uri.fromFile(File(audioRecorder!!.path)))        }        etMessage!!.addTextChangedListener(object : TextWatcher {            override fun afterTextChanged(s: Editable) {}            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {            }            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {                if (s.length > 0) {                    imgStartRecording!!.visibility = View.GONE                    imgStopRecording!!.visibility = View.GONE                    imgSend!!.visibility = View.VISIBLE                } else {                    imgStartRecording!!.visibility = View.VISIBLE                    imgStopRecording!!.visibility = View.GONE                    imgSend!!.visibility = View.GONE                }            }        })        imgAttachment?.setOnClickListener(View.OnClickListener {            val popupMenu = PopupMenu(this@Chat, imgAttachment)            popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu())            popupMenu.setOnMenuItemClickListener { menuItem ->                if (menuItem.itemId == R.id.imageMenu) {                    val intent = Intent()                    intent.type = "image/*"                    intent.action = Intent.ACTION_GET_CONTENT                    startActivityForResult(intent, Image_Request_Code)                } else if (menuItem.itemId == R.id.fileMenu) {                    val intent = Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT)                    startActivityForResult(                        Intent.createChooser(intent, "Select a file"),                        FILE_Request_Code                    )                }                true            }            popupMenu.show()        })        readMessages(senderId, recieverId)        seenMessage()    }    @SuppressLint("NewApi")    private fun showStatusPopup(        context: Activity,        p: Point,        messageId: String,        messageReaction: String    ) {        val viewGroup = context.findViewById<View>(R.id.llSortChangePopup)        val layoutInflater =            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater        val layout: View = layoutInflater.inflate(R.layout.popup_menu_layout, null)        val changeStatusPopUp = PopupWindow(context)        changeStatusPopUp.setContentView(layout)        changeStatusPopUp.setWidth(LinearLayout.LayoutParams.MATCH_PARENT)        changeStatusPopUp.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT)        changeStatusPopUp.setFocusable(true)        val OFFSET_X = 30        val OFFSET_Y = -200        changeStatusPopUp.setBackgroundDrawable(BitmapDrawable())        changeStatusPopUp.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y)        val imglike: ImageView = layout.findViewById(R.id.imgLike)        val imgHeart: ImageView = layout.findViewById(R.id.imgHeart)        val imgSmile: ImageView = layout.findViewById(R.id.imgSmile)        val imgShock: ImageView = layout.findViewById(R.id.imgShock)        val imgSad: ImageView = layout.findViewById(R.id.imgSad)        val imgAngry: ImageView = layout.findViewById(R.id.imgAngry)        val refrence: DatabaseReference = FirebaseDatabase.getInstance()            .getReference().child("Chats").child(messageId).child("messageReaction")        if (messageReaction.equals("LIKE")) {            imglike.setBackgroundResource(R.drawable.reaction_selected_circle)        } else if (messageReaction.equals("HEART")) {            imgHeart.setBackgroundResource(R.drawable.reaction_selected_circle)        } else if (messageReaction.equals("SMILE")) {            imgSmile.setBackgroundResource(R.drawable.reaction_selected_circle)        } else if (messageReaction.equals("SHOCK")) {            imgShock.setBackgroundResource(R.drawable.reaction_selected_circle)        } else if (messageReaction.equals("SAD")) {            imgSad.setBackgroundResource(R.drawable.reaction_selected_circle)        } else if (messageReaction.equals("ANGRY")) {            imgAngry.setBackgroundResource(R.drawable.reaction_selected_circle)        }        imglike.setOnClickListener {            if (messageReaction.equals("LIKE")) {                refrence.setValue("No")            } else {                refrence.setValue("LIKE")            }            changeStatusPopUp.dismiss()        }        imgHeart.setOnClickListener {            if (messageReaction.equals("HEART")) {                refrence.setValue("No")            } else {                refrence.setValue("HEART")            }            changeStatusPopUp.dismiss()        }        imgSmile.setOnClickListener {            if (messageReaction.equals("SMILE")) {                refrence.setValue("No")            } else {                refrence.setValue("SMILE")            }            changeStatusPopUp.dismiss()        }        imgShock.setOnClickListener {            if (messageReaction.equals("SHOCK")) {                refrence.setValue("No")            } else {                refrence.setValue("SHOCK")            }            changeStatusPopUp.dismiss()        }        imgSad.setOnClickListener {            if (messageReaction.equals("SAD")) {                refrence.setValue("No")            } else {                refrence.setValue("SAD")            }            changeStatusPopUp.dismiss()        }        imgAngry.setOnClickListener {            if (messageReaction.equals("ANGRY")) {                refrence.setValue("No")            } else {                refrence.setValue("ANGRY")            }            changeStatusPopUp.dismiss()        }    }    private fun apiCallForNotification(        message: String,        messageType: Int,        imageUrl: String,        fileUrl: String,        senderId: String,        recieverId: String    ) {        val jsonObject = JSONObject()        val notificationObj = JSONObject()        val dataObj = JSONObject()        try {            jsonObject.put("to", fcmToken)            dataObj.put("senderId", senderId)            dataObj.put("recieverId", recieverId)            dataObj.put("senderPic", senderProfilePic)            dataObj.put("recieverPic", recieverProfilePic)            dataObj.put("messageType", messageType)            dataObj.put("senderName", senderName)            dataObj.put("recieverName", recieverName)            dataObj.put("token", fcmToken)            if (messageType == 1) {                dataObj.put("body", message)            } else if (messageType == 2) {                dataObj.put("body", "Shared an attachment")                dataObj.put("imageUrl", imageUrl)            } else if (messageType == 3) {                if (fileUrl.contains("jpg") || fileUrl.contains("png")                    || fileUrl.contains("jpeg")                ) {                    dataObj.put("body", "Shared an attachment")                } else {                    dataObj.put("body", "Shared a file")                }                dataObj.put("fileUrl", fileUrl)            } else if (messageType == 4) {                dataObj.put("body", "Shared a voice")            }            jsonObject.put("notification", notificationObj)            jsonObject.put("data", dataObj)            Log.d("dataObj", "apiCallForNotification: " + dataObj)        } catch (e: JSONException) {            e.printStackTrace()        }        val client = AsyncHttpClient()        client.setTimeout(10000)        client.addHeader("Content-Type", "application/json");        client.addHeader(            "Authorization",            "key=AAAAm8_kL2g:APA91bEeBGFJooUZ1sG04Ti-kDEIe-kYWQ4iJEXWv2GWTSm8L7KWbgvN0dF5ya3g43I_U-oIkd9LeGb7Oj9IOXVXtAZBnssB3GgET4bkm6aXWEovofdD0jYLd6a633kP95lEOhzb0QEK"        )        try {            val entity = StringEntity(jsonObject.toString())            client.post(                applicationContext,                "https://fcm.googleapis.com/fcm/send",                entity,                "application/json",                object : AsyncHttpResponseHandler() {                    override fun onSuccess(                        statusCode: Int,                        headers: Array<Header?>?,                        responseBody: ByteArray?                    ) {                        val content = String(responseBody!!)                        Log.d("onSuccess", "Success: $content")                    }                    override fun onFailure(                        statusCode: Int,                        headers: Array<Header?>?,                        responseBody: ByteArray?,                        error: Throwable?                    ) {                        val content = String(responseBody!!)                        Log.d("onFailure", "Failure: $content")                    }                })        } catch (e: UnsupportedEncodingException) {            e.printStackTrace()        }    }    private fun checkUserStatus() {        val userRef: DatabaseReference =            FirebaseDatabase.getInstance().getReference().child("Statuses")        userRef.child(recieverId).addValueEventListener(object : ValueEventListener {            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {                val status: String = dataSnapshot.child("status").value.toString()                val lastSeen: String = dataSnapshot.child("lastSeen").value.toString()                if (!status.isEmpty()) {                    if (status.equals("online")) {                        tvOnline?.setText("Online")                    } else {                        tvOnline?.setText("Last Seen " + lastSeen)                    }                }            }            override fun onCancelled(@NonNull databaseError: DatabaseError) {}        })    }    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {        super.onActivityResult(requestCode, resultCode, data)        if (requestCode == Image_Request_Code            && resultCode == RESULT_OK            && data != null            && data.data != null        ) {            val file_uri = data.data            uploadImageToFirebase(file_uri!!)        } else if (requestCode == FILE_Request_Code            && resultCode == RESULT_OK            && data != null            && data.data != null        ) {            val file_uri = data.data            uploadFileToFirebase(file_uri!!)        }    }    private fun uploadFileToFirebase(fileUri: Uri) {        customProgressDialog?.show()        val fileName = UUID.randomUUID().toString() + "." + StaticFunctions.GetMimeType(            fileUri,            applicationContext        )        val refStorage = FirebaseStorage.getInstance().reference.child("chatFiles/$fileName")        refStorage.putFile(fileUri)            .addOnSuccessListener(                OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->                    taskSnapshot.storage.downloadUrl.addOnSuccessListener {                        customProgressDialog?.dismiss()                        val time: String =                            SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(Date())                        sendMessage(                            senderId, recieverId, "Shared a file", time, "",                            it.toString(), 3, ""                        )                    }                }).addOnFailureListener(OnFailureListener { e ->                customProgressDialog?.hide()                print(e.message)            })    }    private fun uploadVoiceMessageToFirebase(fileUri: Uri) {        customProgressDialog?.show()        val fileName = UUID.randomUUID().toString() + "." + StaticFunctions.GetMimeType(            fileUri, applicationContext        )        val refStorage = FirebaseStorage.getInstance().reference.child("chatVoices/$fileName")        refStorage.putFile(fileUri)            .addOnSuccessListener(                OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->                    taskSnapshot.storage.downloadUrl.addOnSuccessListener {                        customProgressDialog?.dismiss()                        val time: String =                            SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(Date())                        sendMessage(                            senderId, recieverId, "Shared a Voice", time, "",                            "", 4, it.toString()                        )                    }                }).addOnFailureListener(OnFailureListener { e ->                customProgressDialog?.hide()                print(e.message)            })    }    private fun uploadImageToFirebase(fileUri: Uri) {        customProgressDialog?.show()        val fileName = UUID.randomUUID().toString() + ".jpg"        val refStorage = FirebaseStorage.getInstance().reference.child("chatPictures/$fileName")        refStorage.putFile(fileUri)            .addOnSuccessListener(                OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->                    taskSnapshot.storage.downloadUrl.addOnSuccessListener {                        customProgressDialog?.dismiss()                        val time: String =                            SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(Date())                        sendMessage(                            senderId, recieverId, "Shared an attachment",                            time, it.toString(), "", 2, ""                        )                    }                }).addOnFailureListener(OnFailureListener { e ->                customProgressDialog?.hide()                print(e.message)            })    }    private fun sendMessage(        senderId: String,        recieverId: String,        message: String,        time: String,        imageUrl: String,        fileUrl: String,        messageType: Int,        voiceMessage: String    ) {        customProgressDialog?.show()        apiCallForNotification(message, messageType, imageUrl, fileUrl, senderId, recieverId)        etMessage?.setText("")        val sendMessageRef: DatabaseReference =            FirebaseDatabase.getInstance().getReference().child("Chats")        val pushId = sendMessageRef.push().key        val chatModel = ChatModel(            message, time, pushId!!, senderId, recieverId,            imageUrl, fileUrl, "Delivered", "No", voiceMessage, messageType.toString()        )        sendMessageRef.child(pushId).setValue(chatModel).addOnCompleteListener(OnCompleteListener {            customProgressDialog?.dismiss()            if (!it.isSuccessful) {                ShowToast(applicationContext, it.exception?.localizedMessage!!)            }        })    }    private fun readMessages(senderId: String, recieverId: String) {        customProgressDialog?.show()        val readMessageRef: DatabaseReference =            FirebaseDatabase.getInstance().getReference().child("Chats")        readMessageRef.addValueEventListener(object : ValueEventListener {            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {                customProgressDialog?.dismiss()                mChat?.clear()                for (snapshot in dataSnapshot.children) {                    val chat: ChatModel = snapshot.getValue(ChatModel::class.java)!!                    if (chat.recieverId.equals(senderId) && chat.senderId.equals(recieverId) ||                        chat.recieverId.equals(recieverId) && chat.senderId.equals(senderId)                    ) {                        mChat?.add(chat)                    }                }                checkUserStatus()                chatAdapter = ChatAdapter(this@Chat, mChat!!, recieverName, recieverProfilePic)                chatRecyclerview!!.adapter = chatAdapter            }            override fun onCancelled(@NonNull databaseError: DatabaseError) {            }        })    }    private fun seenMessage() {        chatRefrence = FirebaseDatabase.getInstance().getReference().child("Chats")        val fuser = FirebaseAuth.getInstance().currentUser        seenListener = chatRefrence!!.addValueEventListener(object : ValueEventListener {            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {                for (snapshot in dataSnapshot.children) {                    val chatModel = snapshot.getValue(ChatModel::class.java)                    if (chatModel?.recieverId.equals(fuser?.uid)) {                        val hashMap: HashMap<String, Any> = HashMap()                        hashMap["messageStatus"] = "Seen"                        snapshot.ref.updateChildren(hashMap)                    }                }            }            override fun onCancelled(@NonNull databaseError: DatabaseError) {}        })    }    override fun onPause() {        super.onPause()        chatRefrence?.removeEventListener(seenListener!!)    }}