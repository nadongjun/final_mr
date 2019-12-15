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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
 import com.howl.movingrestaurant.navigation.model.userDTO
import kotlinx.android.synthetic.main.fragment_grid.view.*
 import kotlinx.android.synthetic.main.fragment_grid.*

class SignUpActivity : AppCompatActivity() {
    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null
    var currentUserUid : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_grid)
        //auth = FirebaseAuth.getInstance()
        auth = FirebaseAuth.getInstance()
        password_edittext.hint = "비밀번호는 문자와 숫자로 이루어집니다."
        email_sign_button.setOnClickListener {


            signup()
        }
    }
    override fun onStart() {
        super.onStart()
        //moveMainPage(auth?.currentUser) //자동 로그인 기능
    }
    fun signup() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
            email_edittext.text.toString(),
            password_edittext.text.toString()
        )?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //Creating a user account
                auth?.signInWithEmailAndPassword(
                    email_edittext.text.toString(),
                    password_edittext.text.toString()
                )
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            //Login
                        } else {
                            //Show the error message
                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                        }
                    }
            } else if (task.exception?.message.isNullOrEmpty()) {
                //Show the error message
                Toast.makeText(this, task.exception?.message + "isNullOrEmpty", Toast.LENGTH_LONG).show()
            } else {
                //Login if you have account
                //signinEmail()
                Toast.makeText(
                    getApplicationContext(),
                    "이미 가입된 이메일 입니다.",
                    Toast.LENGTH_LONG
                ).show()

                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    email_edittext.text.toString(),
                    password_edittext.text.toString()
                )
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            //Login
                        } else {
                            //Show the error message
                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
            }

            var userDTO = userDTO()
            userDTO.uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
            userDTO.userID = FirebaseAuth.getInstance().currentUser?.email.toString()
            userDTO.age = age_edittext.text.toString()
            userDTO.local = local_edittext.text.toString()
            userDTO.name = name_edittext.text.toString()
            userDTO.job = job_edittext.text.toString()
            FirebaseFirestore.getInstance().collection("userinfo").document(userDTO.uid!!).set(userDTO).addOnCompleteListener {
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
                }
        }

    /*
     var tsDocFollowing = firestore?.collection("users")?.document(currentUserUid!!)!!.set(followDTO)
    firestore?.runTransaction { transaction ->
        var followDTO =
            transaction.get(tsDocFollowing!!).toObject(FollowDTO::class.java)
        if (followDTO == null) {
            followDTO = FollowDTO()
            followDTO.uid = currentUserUid!!
            followDTO.age = age_edittext.toString()
            followDTO.local = local_edittext.toString()
            followDTO.name = name_edittext.toString()
            transaction.set(tsDocFollowing, followDTO!!)
            return@runTransaction
        } else {

            followDTO.uid = currentUserUid!!
            followDTO.age = age_edittext.toString()
            followDTO.local = local_edittext.toString()
            followDTO.name = name_edittext.toString()
            transaction.set(tsDocFollowing, followDTO!!)
            return@runTransaction
        }



        transaction.set(tsDocFollowing, followDTO)
        return@runTransaction
    }
    */

        //startActivity(Intent(this, LoginActivity::class.java))


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