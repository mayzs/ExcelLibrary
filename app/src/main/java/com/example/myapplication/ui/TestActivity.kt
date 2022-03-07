package com.example.myapplication.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityTestBinding

class TestActivity : AppCompatActivity() {
    lateinit var databinding:ActivityTestBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      databinding = DataBindingUtil.setContentView(this, R.layout.activity_test)
        databinding.test.setOnClickListener {
            startActivity(Intent(this,TestActivity::class.java))
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.i("tag","====1233===")
    }
}