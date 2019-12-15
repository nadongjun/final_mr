package com.howl.movingrestaurant.navigation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.howl.movingrestaurant.R
import com.howl.movingrestaurant.navigation.model.AlarmDTO
import com.howl.movingrestaurant.navigation.model.ContentDTO
import com.howl.movingrestaurant.navigation.model.userDTO
import kotlinx.android.synthetic.main.fragment_grid.view.*
import kotlinx.android.synthetic.main.fragment_message.*
import kotlinx.android.synthetic.main.fragment_message.view.*
import kotlinx.android.synthetic.main.item_get_message.view.*
import kotlinx.android.synthetic.main.item_send_message.view.*


//문자 전송 fragment
class GridFragment : Fragment(){
    var auth : FirebaseAuth? = null
    var currentDTO = userDTO()//현재 유저 이름

    var firestore : FirebaseFirestore? = null
    var fragmentView : View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_message, container, false)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        FirebaseFirestore.getInstance()//현재 유저의 정보
            .collection("userinfo")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                for(snapshot in querySnapshot!!.documents!!){
                    if(snapshot.id == FirebaseAuth.getInstance().currentUser?.uid) {

                        currentDTO = snapshot.toObject(userDTO::class.java)!!
                    }
                }
            }
        //GridLayoutManager(activity, 3)

        view.get_recyclerview.adapter = GetFragmentRecyclerViewAdapter()
        view.get_recyclerview.layoutManager = LinearLayoutManager(activity)

        view.send_recyclerview.adapter = SendFragmentRecyclerViewAdapter()
        view.send_recyclerview.layoutManager = LinearLayoutManager(activity)

        view.send_button.setOnClickListener {

            //메세지 전송버튼 클릭시 목적지에 이름, 내용에 문자 내용이 전송됨 kind가 4일 경우 메세지, timestamp는 받을 경우 확인 버튼을 눌러 확인한 시간을 전송함.
            var alarmDTO = AlarmDTO()
            alarmDTO.destinationUid = dest_id.text.toString()
            alarmDTO.userId = auth?.currentUser?.email
            alarmDTO.uid = auth?.currentUser?.uid
            alarmDTO.kind = 4
           // alarmDTO.timestamp = null
            alarmDTO.message = dest_message.text.toString()

            FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)
        }


        return view
    }


    //받은 메세지
    inner class GetFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var alarmDTOList: ArrayList<AlarmDTO> = arrayListOf()
        var stringList: ArrayList<String> = arrayListOf()

        init {
            val uid = FirebaseAuth.getInstance().currentUser?.uid

            FirebaseFirestore.getInstance().collection("alarms")//.whereEqualTo("destinationUid", currentDTO.name.toString())
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    alarmDTOList.clear()
                    if (querySnapshot == null) return@addSnapshotListener

                    for (snapshot in querySnapshot.documents) {
                        if(snapshot.toObject(AlarmDTO::class.java)!!.kind == 4){ //&& currentDTO.name.toString() == snapshot.toObject(AlarmDTO::class.java)!!.destinationUid.toString()){ //&& snapshot.toObject(AlarmDTO::class.java)!!.uid != FirebaseAuth.getInstance().currentUser?.uid) {
                          //  stringList.add(snapshot.toObject(AlarmDTO::class.java)!!.userId.toString())
                            if(currentDTO.name.toString() == snapshot.toObject(AlarmDTO::class.java)!!.destinationUid.toString()) {
                                alarmDTOList.add(snapshot.toObject(AlarmDTO::class.java)!!)
                                Log.i(snapshot.toObject(AlarmDTO::class.java)!!.message, "")
                            }
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
            var view = LayoutInflater.from(p0.context).inflate(R.layout.item_get_message, p0, false)

            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return alarmDTOList.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var view = p0.itemView
            view.post_textview_comment.text =alarmDTOList[p1].message.toString()//.text = alarmDTOList[p1].message.toString()
            view.post_textview_profile.text = alarmDTOList[p1].userId.toString()

            view.get_message_button.setOnClickListener {
                //메세지 확인 버튼
                var alarmDTO = AlarmDTO()
                alarmDTO.destinationUid = alarmDTOList[p1].uid
                alarmDTO.userId = alarmDTOList[p1].userId
                alarmDTO.uid = alarmDTOList[p1].uid
                alarmDTO.kind = 5
                alarmDTO.timestamp = System.currentTimeMillis()
                alarmDTO.message = alarmDTOList[p1].message.toString()
                FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)
            }

         }

    }


    //보낸 메세지 확인, 보낸 메세지와 메세지의 수신여부, 수신 시간을 확인 할 수 있다.
    inner class SendFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var alarmDTOList: ArrayList<AlarmDTO> = arrayListOf()
        var stringList: ArrayList<String> = arrayListOf()

        init {
            val uid = FirebaseAuth.getInstance().currentUser?.uid

            FirebaseFirestore.getInstance().collection("alarms").whereEqualTo("destinationUid",auth?.currentUser?.uid)
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    alarmDTOList.clear()
                    if (querySnapshot == null) return@addSnapshotListener

                    for (snapshot in querySnapshot.documents) {
                        if(snapshot.toObject(AlarmDTO::class.java)!!.kind == 5 && !stringList.contains(snapshot.toObject(AlarmDTO::class.java)!!.userId.toString())) {
                           // stringList.add(snapshot.toObject(AlarmDTO::class.java)!!.userId.toString())
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
            var view = LayoutInflater.from(p0.context).inflate(R.layout.item_send_message, p0, false)

            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return alarmDTOList.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var view = p0.itemView
            view.get_textview_comment.text = alarmDTOList[p1].message.toString()
            view.get_textview_profile.text = alarmDTOList[p1].userId.toString()

            view.get_time.text = alarmDTOList[p1].timestamp.toString()

        }

    }
}
