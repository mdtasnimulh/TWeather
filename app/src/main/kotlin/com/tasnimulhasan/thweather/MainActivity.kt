package com.tasnimulhasan.thweather

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tasnimulhasan.common.base.BaseActivity
import com.tasnimulhasan.thweather.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun viewBindingLayout() = ActivityMainBinding.inflate(layoutInflater)

    override fun initializeView(savedInstanceState: Bundle?) {  }
}