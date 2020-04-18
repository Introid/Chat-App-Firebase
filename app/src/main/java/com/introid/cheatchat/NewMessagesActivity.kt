package com.introid.cheatchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class NewMessagesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_messages)

        supportActionBar?.title = "Select User"
    }
}
