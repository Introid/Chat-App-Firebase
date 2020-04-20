package com.introid.cheatchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_messages.*
import kotlinx.android.synthetic.main.item_new_user.view.*

class NewMessagesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_messages)

        supportActionBar?.title = "Select User"

//        val adapter= GroupAdapter< GroupieViewHolder>()
//
//
//        rv_newMessage.adapter= adapter
        fetchUser()
    }
    companion object {
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUser() {
       val ref= FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
            val adapter= GroupAdapter<GroupieViewHolder>()
            p0.children.forEach {
                val user = it.getValue(User::class.java)
                if (user != null) {
                    adapter.add(UserItem(user))
                }
            }
                adapter.setOnItemClickListener { item, view ->
                    val userItem= item as UserItem
                    val intent = Intent(view.context,ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY,userItem.user)
                    startActivity(intent)
                    finish()
                }

                rv_newMessage.adapter= adapter
            }

        })
    }
}
class UserItem (val user :User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.item_tv_new_user.text = user.username
        Picasso.get().load(user.profilePictureUrl).into(viewHolder.itemView.item_image_new_user)

    }
    override fun getLayout(): Int {
       return R.layout.item_new_user
    }

}
