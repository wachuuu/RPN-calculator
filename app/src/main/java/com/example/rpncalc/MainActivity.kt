package com.example.rpncalc

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.preference.PreferenceManager
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow

enum class InputState {
    APPEND,
    OVERRIDE,
    ADDNEW
}

class MainActivity : AppCompatActivity() {
    private var stack = ArrayDeque<Double>()
    private var inputState = InputState.APPEND
    private val rowIDs: IntArray = intArrayOf(R.id.row0, R.id.row1, R.id.row2, R.id.row3)
    private var roundRange = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.app_toolbar)
        setSupportActionBar(toolbar)

        setBorderPreferences()
        setRoundRangePreferences()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val settingsIntent = Intent(this, AppSettings::class.java)
        startActivity(settingsIntent)
        return super.onOptionsItemSelected(item)
    }

    private fun setBorderPreferences() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val highlightFirstRow = sharedPreferences.getBoolean("highlight", true)

        val colorID: Int = when (sharedPreferences.getString("color", "")) {
            "gray" -> R.color.gray
            "orange" -> R.color.orange
            "white" -> R.color.white
            else -> R.color.white
        }

        for (id in rowIDs) {
            val row: TextView = findViewById(id)
            row.backgroundTintList = ColorStateList.valueOf(resources.getColor(colorID))
        }

        if (highlightFirstRow) {
            val row: TextView = findViewById(rowIDs[0])
            row.backgroundTintList = when (colorID) {
                R.color.gray -> ColorStateList.valueOf(resources.getColor(R.color.orange))
                R.color.white -> ColorStateList.valueOf(resources.getColor(R.color.orange))
                R.color.orange -> ColorStateList.valueOf(resources.getColor(R.color.white))
                else -> ColorStateList.valueOf(resources.getColor(colorID))
            }
        }
    }

    private fun setRoundRangePreferences() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val roundRangePref = sharedPreferences.getString("round", "2")
        roundRange = roundRangePref?.toInt() ?: 2
    }

    private fun parseNumber(number: Double): String {
        if( ceil(number) == floor(number) || roundRange == 0) {
            return number.toInt().toString()
        }
        return String.format("%.${roundRange}f", number)
    }

    private fun padStackToSize(size: Int): ArrayDeque<Double> {
        val paddedStack = ArrayDeque<Double>()
        if (stack.size >= size) {
            var limit = size
            for (elem in stack) {
                if (limit <= 0) break
                paddedStack.push(elem)
                limit--
            }
        } else {
            for (elem in stack) {
                paddedStack.push(elem)
            }
            for (i in 0 until size-stack.size) {
                paddedStack.push(0.0)
            }
        }
        return paddedStack
    }

    private fun updateRowsView(row0Value: Double = 0.0, state: InputState = InputState.APPEND) {
        inputState = state
        val values = padStackToSize(3)
        var tv: TextView
        for (i in rowIDs.size-1 downTo 1) {
            tv = findViewById(rowIDs[i])
            tv.text = parseNumber(values.pop())
        }
        tv = findViewById(rowIDs[0])
        tv.text = parseNumber(row0Value)
    }

    fun numberClicked(v: View) {
        val b: Button = v as Button
        val row: TextView = findViewById(rowIDs[0])
        if (row.text == getString(R.string.zero) || inputState == InputState.OVERRIDE) {
            row.text = b.text.toString()
            inputState = InputState.APPEND
        }
        else if (inputState == InputState.ADDNEW)
        {
            stack.push(row.text.toString().toDouble())
            updateRowsView(row0Value = b.text.toString().toDouble(), state = InputState.APPEND)
        }
        else {
            row.text = row.text.toString().plus(b.text.toString())
        }
    }

    fun dotClicked(v: View) {
        val row: TextView = findViewById(rowIDs[0])
        if (row.text.toString().contains(".")) return
        if (ceil(row.text.toString().toDouble()) != floor(row.text.toString().toDouble())) return
        if (row.text == getString(R.string.zero) || inputState == InputState.OVERRIDE) {
            row.text = "0".plus(getString(R.string.dot))
        }
        else if (inputState == InputState.ADDNEW) {
            stack.push(row.text.toString().toDouble())
            updateRowsView(state = InputState.APPEND)
            row.text = "0".plus(getString(R.string.dot))
        }
        else {
            row.text = row.text.toString().plus(getString(R.string.dot))
        }
        inputState = InputState.APPEND
    }

    fun changeNumberSign(v: View) {
        val row: TextView = findViewById(rowIDs[0])
        row.text = parseNumber(-row.text.toString().toDouble())
    }

    fun clear(v: View) {
        val row: TextView = findViewById(rowIDs[0])
        row.text = getString(R.string.zero)
    }

    fun clearAll(v: View) {
        stack.clear()
        updateRowsView()
    }

    fun swap(v: View) {
        val row0: TextView = findViewById(rowIDs[0])
        if (stack.size > 0) {
            val elem1 = stack.pop()
            val elem2 = row0.text.toString().toDouble()
            stack.push(elem2)
            updateRowsView(row0Value = elem1, state = InputState.OVERRIDE)
        }
    }

    fun drop(v: View) {
        if (stack.size > 0) {
            val elem = stack.pop()
            updateRowsView(row0Value = elem, state = InputState.ADDNEW)
            return
        }
        updateRowsView()
    }

    fun enter(v: View) {
        val row: TextView = findViewById(rowIDs[0])
        val value = row.text.toString().toDouble()
        stack.push(value)
        updateRowsView(row0Value = value, state = InputState.OVERRIDE)
    }

    fun add(v: View) {
        if (stack.size > 0) {
            val row0: TextView = findViewById(rowIDs[0])
            val x = row0.text.toString().toDouble()
            val y = stack.pop()
            updateRowsView(row0Value = x+y, state = InputState.ADDNEW)
        }
    }

    fun subtract(v: View) {
        if (stack.size > 0) {
            val row0: TextView = findViewById(rowIDs[0])
            val x = row0.text.toString().toDouble()
            val y = stack.pop()
            updateRowsView(row0Value = y-x, state = InputState.ADDNEW)
        }
    }

    fun multiply(v: View) {
        if (stack.size > 0) {
            val row0: TextView = findViewById(rowIDs[0])
            val x = row0.text.toString().toDouble()
            val y = stack.pop()
            updateRowsView(row0Value = x*y, state = InputState.ADDNEW)
        }
    }

    fun divide(v: View) {
        if (stack.size > 0) {
            val row0: TextView = findViewById(rowIDs[0])
            val x = row0.text.toString().toDouble()
            val y = stack.pop()
            updateRowsView(row0Value = y/x, state = InputState.ADDNEW)
        }
    }

    fun sqrt(v: View) {
        val row0: TextView = findViewById(rowIDs[0])
        val x = row0.text.toString().toDouble()
        updateRowsView(row0Value = kotlin.math.sqrt(x), state = InputState.ADDNEW)
    }

    fun pow(v: View) {
        if (stack.size > 0) {
            val row0: TextView = findViewById(rowIDs[0])
            val x = row0.text.toString().toDouble()
            val y = stack.pop()
            updateRowsView(row0Value = y.pow(x), state = InputState.ADDNEW)
        }
    }
}
