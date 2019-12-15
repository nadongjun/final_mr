package com.howl.movingrestaurant.navigation

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.FacebookSdk
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.howl.movingrestaurant.R
import com.howl.movingrestaurant.navigation.model.AlarmDTO
import com.howl.movingrestaurant.navigation.model.ContentDTO
import com.howl.movingrestaurant.navigation.util.FcmPush
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.item_comment.view.*

class CommentActivity : AppCompatActivity() {
    var contentUid : String? = null
    var contentTimestamp : String? = null
    var destinationUid : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        contentUid = intent.getStringExtra("contentUid")
        destinationUid = intent.getStringExtra("destinationUid")
        contentTimestamp = intent.getStringExtra("contentTimestamp")
        Toast.makeText(
            getApplicationContext(),
            contentTimestamp,
            Toast.LENGTH_LONG
        ).show()
        comment_recyclerview.adapter = CommentRecyclerviewAdapter()
        comment_recyclerview.layoutManager = LinearLayoutManager(this)
//버튼 눌렀을 경우 comment data 클래스에 데이터 넣고 firebase에 저장
        comment_btn_send?.setOnClickListener {
            var comment = ContentDTO.Comment()
            comment.userId = FirebaseAuth.getInstance().currentUser?.email
            comment.uid = FirebaseAuth.getInstance().currentUser?.uid
            comment.comment = comment_edit_message.text.toString()
            comment.timestamp = System.currentTimeMillis()
//firestore 글의 timestamp에 접근한 후 doc를 받아오고 글과 관련된 comment를 작성한 후 내용에 timestamp를 넣음
            FirebaseFirestore.getInstance().collection("images").document(contentTimestamp.toString()).collection("comments").document(comment.timestamp.toString()).set(comment)
            commentAlarm(destinationUid!!,comment_edit_message.text.toString())
            comment_edit_message.setText("")
        }
    }
    fun commentAlarm(destinationUid : String, message : String){
        var alarmDTO = AlarmDTO()
        alarmDTO.destinationUid = destinationUid
        alarmDTO.userId = FirebaseAuth.getInstance().currentUser?.email
        alarmDTO.kind = 1
        alarmDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
        alarmDTO.timestamp = System.currentTimeMillis()
        alarmDTO.message = message
        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

        var msg = FirebaseAuth.getInstance().currentUser?.email + " " + getString(R.string.alarm_comment) + " of " + message
        FcmPush.instance.sendMessage(destinationUid,"Howlstagram",msg)
    }
    inner class CommentRecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        var comments : ArrayList<ContentDTO.Comment> = arrayListOf()
        init {
            FirebaseFirestore.getInstance()
                .collection("images")
                .document(contentUid!!)
                .collection("comments")
                .orderBy("timestamp")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    comments.clear()
                    if(querySnapshot == null)return@addSnapshotListener

                    for(snapshot in querySnapshot.documents!!){
                        comments.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)
                    }
                    notifyDataSetChanged()
                }
        }
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(p0.context).inflate(R.layout.item_comment,p0,false)
            return CustomViewHolder(view)
        }

        private inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return comments.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var view = p0.itemView
            view.commentviewitem_textview_comment.text = comments[p1].comment
            view.commentviewitem_textview_profile.text = comments[p1].userId
            view.edit_comment.visibility = View.INVISIBLE
            view.delete_comment.visibility = View.INVISIBLE
            if(comments!![p1].uid == FirebaseAuth.getInstance().currentUser?.uid) {
                view.edit_comment.visibility = View.VISIBLE
                view.delete_comment.visibility = View.VISIBLE
                //수정 버튼
                view.edit_comment.setOnClickListener {
                    var comment = ContentDTO.Comment()
                    comment.userId = FirebaseAuth.getInstance().currentUser?.email
                    comment.uid = FirebaseAuth.getInstance().currentUser?.uid
                    comment.comment = comment_edit_message.text.toString()
                    comment.timestamp = System.currentTimeMillis()
                    //firestore 글의 timestamp에 접근한 후 doc를 받아오고 글과 관련된 comment를 작성한 후 내용에 timestamp를 넣음
                    FirebaseFirestore.getInstance().collection("images").document(contentTimestamp!!).collection("comments").document(comments!![p1].timestamp.toString()).set(comment)
                    commentAlarm(destinationUid!!,comment_edit_message.text.toString())
                    comment_edit_message.setText("")

                }

                //삭제 버튼
                view.delete_comment.setOnClickListener {
                    FirebaseFirestore.getInstance().collection("images").document(contentTimestamp!!).collection("comments").document(comments!![p1].timestamp.toString()).delete()

                }
            }
            FirebaseFirestore.getInstance()
                .collection("profileImages")
                .document(comments[p1].uid!!)
                .get()
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        var url = task.result!!["image"]
                        Glide.with(p0.itemView.context).load(url).apply(RequestOptions().circleCrop()).into(view.commentviewitem_imageview_profile)
                    }
                }
        }


    }
}
