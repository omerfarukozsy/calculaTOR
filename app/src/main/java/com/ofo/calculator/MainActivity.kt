package com.ofo.calculator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ofo.calculator.databinding.ActivityMainBinding
import com.ofo.calculator.ui.calculator.CalculatorFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, CalculatorFragment.newInstance())
                .commit()
        }
    }
}