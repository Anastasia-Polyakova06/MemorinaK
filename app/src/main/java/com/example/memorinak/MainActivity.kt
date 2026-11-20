package com.example.memorinak

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.view.Gravity
import android.graphics.Color
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var firstCard: ImageView? = null
    private var isChecking = false
    private var matchedPairs = 0

    private lateinit var mainLayout: LinearLayout

    private val cardImages = mutableListOf(
        R.drawable.card1, R.drawable.card1,
        R.drawable.card2, R.drawable.card2,
        R.drawable.card3, R.drawable.card3,
        R.drawable.card4, R.drawable.card4,
        R.drawable.card5, R.drawable.card5,
        R.drawable.card6, R.drawable.card6,
        R.drawable.card7, R.drawable.card7,
        R.drawable.card8, R.drawable.card8
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startGame()
    }

    private fun startGame() {
        firstCard = null
        isChecking = false
        matchedPairs = 0

        cardImages.shuffle()

        mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val rows = Array(4) {
            LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1f
                )
            }
        }

        val colorListener = View.OnClickListener { view ->

            if (isChecking || !view.isClickable) {
                return@OnClickListener
            }

            val card = view as ImageView

            card.setImageResource(card.tag as Int)
            card.isClickable = false

            if (firstCard == null) {
                firstCard = card
            } else {
                val first = firstCard!!

                if (first.tag == card.tag) {
                    first.visibility = View.INVISIBLE
                    card.visibility = View.INVISIBLE

                    matchedPairs++
                    firstCard = null

                    if (matchedPairs == cardImages.size / 2) {
                        showVictoryScreen()
                    }

                } else {
                    isChecking = true

                    GlobalScope.launch(Dispatchers.Main) {
                        delay(1000)

                        first.setImageResource(R.drawable.card_back)
                        card.setImageResource(R.drawable.card_back)

                        first.isClickable = true
                        card.isClickable = true

                        firstCard = null
                        isChecking = false
                    }
                }
            }
        }

        for (i in 0 until 16) {
            val card = ImageView(this).apply {
                setImageResource(R.drawable.card_back)
                tag = cardImages[i]
                scaleType = ImageView.ScaleType.FIT_CENTER

                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1f
                )

                setPadding(1, 1, 1, 1)
                setOnClickListener(colorListener)
            }

            rows[i / 4].addView(card)
        }

        for (row in rows) {
            mainLayout.addView(row)
        }

        setContentView(mainLayout)
    }

    private fun showVictoryScreen() {
        val victoryLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
            setBackgroundColor(Color.WHITE)
        }

        val title = TextView(this).apply {
            text = "Поздравляем!"
            textSize = 32f
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
        }

        val message = TextView(this).apply {
            text = "Вы победили!\nВсе пары найдены!"
            textSize = 22f
            setTextColor(Color.DKGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 40)
        }

        val restartButton = Button(this).apply {
            text = "Начать заново"
            textSize = 18f
            setOnClickListener {
                startGame()
            }
        }

        victoryLayout.addView(title)
        victoryLayout.addView(message)
        victoryLayout.addView(restartButton)

        setContentView(victoryLayout)
    }
}