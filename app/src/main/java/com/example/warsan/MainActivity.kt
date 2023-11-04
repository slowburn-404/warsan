package com.example.warsan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.WindowCompat
import com.example.warsan.children.ChildrenListFragment
import com.example.warsan.databinding.ActivityMainBinding
import com.example.warsan.guardian.RetrieveGuardianFragment
import com.example.warsan.models.ChildDetails

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(binding.root)

    }
}