package com.example.rpncalc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private var stack = ArrayDeque<Int>()
    private val rowIDs: IntArray = intArrayOf(R.id.row0, R.id.row1, R.id.row2, R.id.row3)

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

    fun clearAll(v: View) {
        val rows = getRows()
        for (row in rows) {
            row.text = getString(R.string.zero)
        }

        stack.clear()
    }

    fun enter(v: View) {
        val row: TextView = findViewById(R.id.row0)
        val value = row.text.toString().toInt()
        stack.push(value)
        updateRowsView()
    }

    private fun getRows(): ArrayList<TextView> {
        val rows = ArrayList<TextView>()
        for (id in rowIDs) {
            rows.add(findViewById(id))
        }
        return rows
    }

    private fun padStackToSize(size: Int): ArrayDeque<Int> {
        val paddedStack = ArrayDeque<Int>()
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
                paddedStack.push(0)
            }
        }
        return paddedStack
    }

    private fun updateRowsView() {
        val values = padStackToSize(3)
        var tv: TextView
        for (i in rowIDs.size-1 downTo 1) {
            tv = findViewById(rowIDs[i])
            tv.text = values.pop().toString()
        }
        tv = findViewById(rowIDs[0])
        tv.text = getString(R.string.zero)
    }
}
