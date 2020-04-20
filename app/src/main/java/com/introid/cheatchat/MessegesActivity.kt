package com.introid.cheatchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.introid.cheatchat.models.ChatMessage
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_messeges.*
import kotlinx.android.synthetic.main.row_latest_messages.view.*

class MessegesActivity : AppCompatActivity() {
    companion object{
        var currentUser: User?= null
    }
    val adapter= GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messeges)

        rv_latest_messages.adapter= adapter
        rv_latest_messages.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        adapter.setOnItemClickListener { item, view ->
            val intent= Intent(this, ChatLogActivity::class.java)
            val row = item as LatestMessageRow
            intent.putExtra(NewMessagesActivity.USER_KEY,row.chatPartnerUser)
            startActivity(intent)
        }
        listenForLatestMessages()
        fetchCurrentUser()
        checkVerification()


    }
    val latestMessagesMap= HashMap<String,ChatMessage>()
    private fun refreshRecyclerViewMessages(){
        adapter.clear()
        latestMessagesMap.values.forEach{
            adapter.add(LatestMessageRow(it))
        }

    }

    private fun listenForLatestMessages() {
        val fromId= FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("latest-messages/$fromId")
        ref.addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }
            override fun onChildRemoved(p0: DataSnapshot) {

            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage= p0.getValue(ChatMessage::class.java)?:return
                latestMessagesMap[p0.key!!]= chatMessage
                refreshRecyclerViewMessages()
            }
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage= p0.getValue(ChatMessage::class.java)?:return
                latestMessagesMap[p0.key!!]= chatMessage
                refreshRecyclerViewMessages()

            }



        })
    }



    class LatestMessageRow(val chatMessage : ChatMessage): Item<GroupieViewHolder>(){
        var chatPartnerUser : User?= null
        override fun getLayout(): Int {
            return R.layout.row_latest_messages
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.item_latest_message_row.text= chatMessage.text

            val chatPartnerId: String
            if (chatMessage.fromId == FirebaseAuth.getInstance().uid){
                chatPartnerId= chatMessage.toId
            }else {
                chatPartnerId= chatMessage.fromId
            }
            val ref= FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
                ref.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        chatPartnerUser =p0.getValue(User::class.java)
                     viewHolder.itemView.item_username_row_latest_message.text=chatPartnerUser?.username
                     Picasso.get().load(chatPartnerUser?.profilePictureUrl).into(viewHolder.itemView.item_image_row_latest_message)
                    }

                })

        }

    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
            ref.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                   currentUser= p0.getValue(User::class.java)
                }

            })
    }

    private fun checkVerification() {
        val uid= FirebaseAuth.getInstance().uid
        if(uid == null){
            val intent = Intent(this,RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_new_messeges ->{
                val intent= Intent(this,NewMessagesActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out ->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this,RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
       menuInflater.inflate(R.menu.nav_menu_items,menu)
        return super.onCreateOptionsMenu(menu)
    }
}
