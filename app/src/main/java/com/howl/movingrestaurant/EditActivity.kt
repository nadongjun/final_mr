package com.howl.movingrestaurant

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.CallbackManager
import com.facebook.FacebookSdk
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.howl.movingrestaurant.navigation.model.userDTO
import kotlinx.android.synthetic.main.activity_edit.*

class EditActivity : AppCompatActivity() {
    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null
    var uid : String? = null
    var currentDTO = userDTO()

     override fun onCreate(savedInstanceState: Bundle?) {

         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_edit)
         /*intent.getStringExtra("favoriteCount")
         intent.getStringExtra("imageUrl")
         intent.getStringExtra("private")
         intent.getStringExtra("uid")
         intent.getStringExtra("userId")*/
         FirebaseFirestore.getInstance()
             .collection("userinfo")
             .addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                 for (snapshot in querySnapshot!!.documents!!) {
                     if (snapshot.toObject(userDTO::class.java)!!.uid == FirebaseAuth.getInstance().currentUser?.uid) {

                         currentDTO = snapshot.toObject(userDTO::class.java)!!
                         name_edit.hint = currentDTO.name.toString()
                         local_edit.hint = currentDTO.local.toString()
                         age_edit.hint = currentDTO.age.toString()

                         Toast.makeText(
                             getApplicationContext(),
                             "find " + currentDTO.name.toString(),
                             Toast.LENGTH_LONG
                         ).show()

                     }
                 }
             }


         exit_button.setOnClickListener {

             startActivity(Intent(this, MainActivity::class.java))
         }

         edit.setOnClickListener {

             editinfo()
             // startActivity(Intent(this, MainActivity::class.java))
             ///
            /*FirebaseFirestore.getInstance().collection("userinfo")
                 .document(FirebaseAuth.getInstance().currentUser?.uid.toString()).set(userDTO).addOnCompleteListener {
                 if (it.isSuccessful) {
                     Toast.makeText(
                         getApplicationContext(),
                         "생성 완료.",
                         Toast.LENGTH_LONG
                     ).show()
                     startActivity(Intent(this, LoginActivity::class.java))
                 } else {
                     Toast.makeText(
                         getApplicationContext(),
                         "생성 실패.",
                         Toast.LENGTH_LONG
                     ).show()
                 }
             }*/

         }
     }

    override fun onStart() {
        super.onStart()
        //moveMainPage(auth?.currentUser) //자동 로그인 기능
    }

    fun editinfo() {


        var userDTO = userDTO()

        userDTO.uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        userDTO.age = age_edit.text.toString()
        userDTO.local = local_edit.text.toString()
        userDTO.name = name_edit.text.toString()
        FirebaseFirestore.getInstance().collection("userinfo").document(userDTO.uid!!).set(userDTO).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(
                    getApplicationContext(),
                    "수정 완료.",
                    Toast.LENGTH_LONG
                ).show()
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                Toast.makeText(
                    getApplicationContext(),
                    "수정 실패.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}      // signinAndSignup()//이메일 클릭 리스너 + 회원가입 리스너, 회원 정보 입력화면 추가
/*auth?.createUserWithEmailAndPassword(email_edittext.text.toString(),password_edittext.text.toString())
?.addOnCompleteListener {
task ->
   if(task.isSuccessful){
       //Creating a user account
       moveMainPage(task.result?.user)
   }else if(task.exception?.message.isNullOrEmpty()){
       //Show the error message
       Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
   }else{
       //Login if you have account
       //signinEmail()
       Toast.makeText(getApplicationContext(),"이미 가입된 이메일 입니다.",Toast.LENGTH_LONG).show()
   }
}*/