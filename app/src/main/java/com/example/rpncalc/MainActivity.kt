package com.example.rpncalc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun numberClicked(v: View) {
        val b: Button = v as Button
        val row: TextView = findViewById(R.id.row0)
        if (row.text == getString(R.string.zero)) {
            row.text = b.text.toString()
        } else {
            row.text = row.text.toString().plus(b.text.toString())
        }
    }

    fun clear(v: View) {
        val row: TextView = findViewById(R.id.row0)
        row.text = getString(R.string.zero)
    }
}