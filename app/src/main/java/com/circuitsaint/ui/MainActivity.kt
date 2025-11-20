package com.circuitsaint.ui

import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.circuitsaint.R
import com.circuitsaint.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Configurar Navigation Component
        setupNavigation()
        applyLogoGradient()
    }
    
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        val navController = navHostFragment?.navController
        
        navController?.let {
            binding.bottomNavigationView.setupWithNavController(it)
        }
    }

    private fun applyLogoGradient() {
        val textView = binding.toolbar.findViewById<android.widget.TextView>(R.id.logoTitle)
        val text = textView.text.toString()
        if (text.isEmpty()) return
        val width = textView.paint.measureText(text)
        val shader = LinearGradient(
            0f,
            0f,
            width,
            0f,
            intArrayOf(
                android.graphics.Color.parseColor("#FF6B9D"),
                android.graphics.Color.parseColor("#FBBF24"),
                android.graphics.Color.parseColor("#8B5CF6")
            ),
            null,
            Shader.TileMode.CLAMP
        )
        textView.paint.shader = shader
        textView.invalidate()
    }
}

