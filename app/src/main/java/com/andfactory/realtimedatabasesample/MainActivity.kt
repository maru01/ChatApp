package com.andfactory.realtimedatabasesample

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.andfactory.realtimedatabasesample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        binding.loginButton.setOnClickListener {
            if (binding.userName.text.toString().isNotEmpty()) {
                ChatActivity.start(this, binding.userName.text.toString())
            }
        }
    }
}