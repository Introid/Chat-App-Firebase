package com.introid.cheatchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.introid.cheatchat.models.ChatMessage
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {
    val adapter = GroupAdapter<GroupieViewHolder>()
    val toUser : User?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        rv_chat_log.adapter= adapter

        val toUser= intent.getParcelableExtra<User>("user")

        supportActionBar?.title= toUser.username


        listenForMessages()
        btn_send_message.setOnClickListener {
            performSendMessage()
        }

    }

    private fun listenForMessages() {
        val ref = FirebaseDatabase.getInstance().getReference("/messages")
        ref.addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage= p0.getValue(ChatMessage::class.java)
                if (chatMessage != null){
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid){
                        val currentUser = MessegesActivity.currentUser?:return
                        adapter.add(ChatFromItem(chatMessage.text,currentUser))
                    }else {

                        adapter.add(ChatToItem(chatMessage.text,toUser!!))
                    }
                }

            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })
    }

    private fun performSendMessage() {

        val fromId= FirebaseAuth.getInstance().uid
        val user= intent.getParcelableExtra<User>("user")
        val toId= user.uid
        if (fromId == null)return

        val text = et_new_message.text.toString()
        val reference= FirebaseDatabase.getInstance().getReference("/messages").push()
        val chatMessage = ChatMessage(reference.key!!,text,fromId,toId,System.currentTimeMillis()/1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {

            }
    }

    private fun dummyData() {
        val adapter = GroupAdapter<GroupieViewHolder>()

        rv_chat_log.adapter= adapter

    }
}
class ChatFromItem(val text : String ,val user: User) : Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.tv_received_message.text= text
        val uri= user.profilePictureUrl
        val targetImageView= viewHolder.itemView.user_from_profile_image
        Picasso.get().load(uri).into(targetImageView)
    }

    }

class ChatToItem(val text : String ,val user: User) : Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.tv_send_message.text=text
        val uri= user.profilePictureUrl
        val targetImageView= viewHolder.itemView.user_to_profile_image
        Picasso.get().load(uri).into(targetImageView)
    }

}
