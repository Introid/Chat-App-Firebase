package com.introid.cheatchat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btn_register.setOnClickListener {
             performRegistration()

        }
        tv_signUp.setOnClickListener {
            val intent= Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        btn_choose_image.setOnClickListener {
            val intent= Intent(Intent.ACTION_PICK)
            intent.type= "image/*"
            startActivityForResult(intent,0)

        }
    }
    private var selectedPhotoUri : Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode ==0 && resultCode == Activity.RESULT_OK && data != null){
           selectedPhotoUri= data.data

            profile_image_circular.setImageURI(selectedPhotoUri)
            profile_image_circular.alpha= 0f
            Log.d("Main", "Image set successful")

//            val bitmap= MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
//            profile_image_circular.setImageBitmap(bitmap)
//
        }
    }

    private fun performRegistration() {
        val email =et_email.text.toString()
        val password= et_password.text.toString()

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,"Please Enter Email And Password!!",Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                if (!it.isSuccessful) return@addOnCompleteListener
                //else if
                Log.d("Main" , "Successful ${it.result!!.user!!.uid}")
                uploadUserPhoto()
            }
            .addOnFailureListener {
                Toast.makeText(this,"Failed to register!! ${it.message}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadUserPhoto() {
        if(selectedPhotoUri == null) return
        val filename= UUID.randomUUID().toString()
        val ref= FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("Main","Image Upload Successful ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener{
                    Log.d("Main","File location $it")
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener{
                Toast.makeText(this,"${it.message}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid= FirebaseAuth.getInstance().uid?:""
        val ref =FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid,et_username.text.toString(),profileImageUrl)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("Main", "User information Saved Successfully !!")
                val intent= Intent(this,MessegesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

    }
}
class User (val uid : String , val username : String, val profilePictureUrl : String ){
    constructor(): this("", "","")
}

