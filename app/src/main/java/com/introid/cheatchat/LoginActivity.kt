package com.introid.cheatchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_SignIn.setOnClickListener {
             signIn()
        }
    }

    private fun signIn() {
        val email: String = et_email_login.text.toString()
        val password : String = et_password_login.text.toString()

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                if (!it.isSuccessful) return@addOnCompleteListener
                Toast.makeText(this,"Sign In SuccessFul ${it.result!!.user!!.uid}",Toast.LENGTH_SHORT).show()
                val intent= Intent(this,MessegesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
            .addOnFailureListener{
                Toast.makeText(this,"Failed to Sign In  ${it.message}",Toast.LENGTH_SHORT).show()
            }
    }
}
