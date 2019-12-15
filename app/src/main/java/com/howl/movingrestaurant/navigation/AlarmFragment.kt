package com.howl.movingrestaurant.navigation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.howl.movingrestaurant.R
import com.howl.movingrestaurant.navigation.model.AlarmDTO
import com.howl.movingrestaurant.navigation.model.userDTO
import kotlinx.android.synthetic.main.fragment_alarm.view.*
import kotlinx.android.synthetic.main.item_comment.view.*
import kotlinx.android.synthetic.main.item_comment.view.commentviewitem_textview_profile
import kotlinx.android.synthetic.main.item_userfollowlist.view.*
import kotlinx.android.synthetic.main.item_userlist.view.*
 import kotlinx.android.synthetic.main.item_userlist.view.profile_button

class AlarmFragment : Fragment() {
    var searchResult = userDTO()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_alarm, container, false)
        view.alarmfragment_recyclerview.adapter = AlarmRecyclerviewAdapter()
        view.alarmfragment_recyclerview.layoutManager = LinearLayoutManager(activity)
        view.search_button.setOnClickListener {
            FirebaseFirestore.getInstance()
                .collection("userinfo")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                    for(snapshot in querySnapshot!!.documents!!){
                        if(snapshot.toObject(userDTO::class.java)!!.name.toString() == view.search.text.toString()) {

                            searchResult = snapshot.toObject(userDTO::class.java)!!
                            view.search_result.text = "이름: " + searchResult.name.toString() + " 지역: "+ searchResult.local.toString() + " 나이: " + searchResult.age.toString() + " 업종: " + searchResult.job.toString()


                        }
                    }
                }
        }
        view.user_recyclerview.adapter = userecyclerviewAdapter()
        view.user_recyclerview.layoutManager = LinearLayoutManager(activity)

        view.unfollow_recyclerview.adapter = unfollowecyclerviewAdapter()
        view.unfollow_recyclerview.layoutManager = LinearLayoutManager(activity)
        return view
    }
    ////////////////////////////////////////////////////////////////////////////////////
    inner class AlarmRecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var alarmDTOList: ArrayList<AlarmDTO> = arrayListOf()
        var stringList: ArrayList<String> = arrayListOf()

        init {
            val uid = FirebaseAuth.getInstance().currentUser?.uid

            FirebaseFirestore.getInstance().collection("alarms").whereEqualTo("destinationUid", uid)
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    alarmDTOList.clear()
                    if (querySnapshot == null) return@addSnapshotListener

                    for (snapshot in querySnapshot.documents) {
                        if(snapshot.toObject(AlarmDTO::class.java)!!.kind == 2 && snapshot.toObject(AlarmDTO::class.java)!!.uid != FirebaseAuth.getInstance().currentUser?.uid && !stringList.contains(snapshot.toObject(AlarmDTO::class.java)!!.userId.toString())) {
                            stringList.add(snapshot.toObject(AlarmDTO::class.java)!!.userId.toString())
                            alarmDTOList.add(snapshot.toObject(AlarmDTO::class.java)!!)
                        }
                    }
                    notifyDataSetChanged()
                }
                /*FirebaseFirestore.getInstance().collection("alarms").whereEqualTo("uid", uid)
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (querySnapshot == null) return@addSnapshotListener

                    for (snapshot in querySnapshot.documents) {
                        if(snapshot.toObject(AlarmDTO::class.java)!!.kind == 2 && snapshot.toObject(AlarmDTO::class.java)!!.uid != FirebaseAuth.getInstance().currentUser?.uid && !stringList.contains(snapshot.toObject(AlarmDTO::class.java)!!.userId.toString())) {
                            alarmDTOList.add(snapshot.toObject(AlarmDTO::class.java)!!)
                        }
                    }
                    notifyDataSetChanged()
                }*/

        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(p0.context).inflate(R.layout.item_comment, p0, false)

            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return alarmDTOList.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var view = p0.itemView


            view.commentviewitem_textview_profile.visibility = View.INVISIBLE


            //if(alarmDTOList[p1].kind == 2) {
                val str_0 = alarmDTOList[p1].userId
                view.commentviewitem_textview_profile.visibility = View.VISIBLE

                view.commentviewitem_textview_profile.text = str_0

                Log.e("test", str_0)
            //}


            view.commentviewitem_textview_comment.visibility = View.INVISIBLE
        }

    }
    ////////////////////////////////////////////////////////////////////////////////////
    inner class userecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var userDTOList: ArrayList<userDTO> = arrayListOf()

        init {

            FirebaseFirestore.getInstance().collection("userinfo")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    userDTOList.clear()
                    if (querySnapshot == null) return@addSnapshotListener

                    for (snapshot in querySnapshot.documents) {
                        if(snapshot.toObject(userDTO::class.java)!!.uid != FirebaseAuth.getInstance().currentUser?.uid) {
                            userDTOList.add(snapshot.toObject(userDTO::class.java)!!)
                        }
                    }
                    notifyDataSetChanged()
                }
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(p0.context).inflate(R.layout.item_userlist, p0, false)

            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return userDTOList.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var view = p0.itemView

            val str_0 = userDTOList[p1].name
            view.commentviewitem_textview_profile.text = str_0

            view.profile_button.setOnClickListener {
                //유저목록
                var fragment = UserFragment()
                var bundle = Bundle()
                bundle.putString("destinationUid", userDTOList[p1].uid)
                bundle.putString("userId", userDTOList[p1].userID)
                fragment.arguments = bundle
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.main_content, fragment)?.commit()
            }
        }

        //view.commentviewitem_textview_comment.visibility = View.INVISIBLE
    }
    ////////////////////////////////////////////////////////////////////////////////////
    inner class unfollowecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
         var alarmDTOList: ArrayList<AlarmDTO> = arrayListOf()
        var stringList: ArrayList<String> = arrayListOf()

        init {

            val uid = FirebaseAuth.getInstance().currentUser?.uid

            FirebaseFirestore.getInstance().collection("alarms").whereEqualTo("destinationUid", uid)
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    alarmDTOList.clear()
                    if (querySnapshot == null) return@addSnapshotListener

                    for (snapshot in querySnapshot.documents) {
                        if(snapshot.toObject(AlarmDTO::class.java)!!.kind == 3 && snapshot.toObject(AlarmDTO::class.java)!!.uid != FirebaseAuth.getInstance().currentUser?.uid && !stringList.contains(snapshot.toObject(AlarmDTO::class.java)!!.userId.toString())) {
                            stringList.add(snapshot.toObject(AlarmDTO::class.java)!!.userId.toString())
                            alarmDTOList.add(snapshot.toObject(AlarmDTO::class.java)!!)
                        }
                    }
                    notifyDataSetChanged()
                }

        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(p0.context).inflate(R.layout.item_comment, p0, false)

            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return alarmDTOList.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var view = p0.itemView


            view.commentviewitem_textview_profile.visibility = View.INVISIBLE


            //if(alarmDTOList[p1].kind == 3) {
            val str_0 = alarmDTOList[p1].userId
            view.commentviewitem_textview_profile.visibility = View.VISIBLE

            view.commentviewitem_textview_profile.text = str_0

            Log.e("test", str_0)
            //}


            view.commentviewitem_textview_comment.visibility = View.INVISIBLE
        }

        //view.commentviewitem_textview_comment.visibility = View.INVISIBLE
    }

}


