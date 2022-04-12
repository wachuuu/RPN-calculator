package com.example.rpncalc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import java.util.*
import kotlin.collections.ArrayList
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun checkIfWhole(number: Double): String {
        if( ceil(number) == floor(number)) {
            return number.toInt().toString()
        }
        return number.toString()
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
            tv.text = checkIfWhole(values.pop())
        }
        tv = findViewById(rowIDs[0])
        tv.text = checkIfWhole(row0Value)
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
        if (ceil(row.text.toString().toDouble()) != ceil(row.text.toString().toDouble())) return
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
        row.text = checkIfWhole(-row.text.toString().toDouble())
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
        val row1: TextView = findViewById(rowIDs[1])
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
