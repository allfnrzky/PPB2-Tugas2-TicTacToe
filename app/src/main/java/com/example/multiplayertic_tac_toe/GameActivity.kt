package com.example.multiplayertic_tac_toe

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.multiplayertic_tac_toe.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityGameBinding
    private var filledPos = Array(9) { "" }
    private var currentPlayer = "X"
    private var gameActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set listener untuk setiap tombol papan
        binding.btn0.setOnClickListener(this)
        binding.btn1.setOnClickListener(this)
        binding.btn2.setOnClickListener(this)
        binding.btn3.setOnClickListener(this)
        binding.btn4.setOnClickListener(this)
        binding.btn5.setOnClickListener(this)
        binding.btn6.setOnClickListener(this)
        binding.btn7.setOnClickListener(this)
        binding.btn8.setOnClickListener(this)

        // Tombol mulai ulang
        binding.startGameBtn.setOnClickListener {
            startGame()
        }

        // Awal game
        setGameStatus("Klik 'Start Game' untuk memulai")
    }

    private fun startGame() {
        filledPos = Array(9) { "" }
        currentPlayer = "X"
        gameActive = true

        // Kosongkan papan
        binding.btn0.text = ""
        binding.btn1.text = ""
        binding.btn2.text = ""
        binding.btn3.text = ""
        binding.btn4.text = ""
        binding.btn5.text = ""
        binding.btn6.text = ""
        binding.btn7.text = ""
        binding.btn8.text = ""

        setGameStatus("Giliran $currentPlayer")
    }

    override fun onClick(v: View?) {
        if (!gameActive) {
            Toast.makeText(this, "Game belum dimulai", Toast.LENGTH_SHORT).show()
            return
        }

        val clickedBtn = v as? android.widget.Button ?: return
        val tag = clickedBtn.tag?.toString()?.toIntOrNull() ?: return

        if (filledPos[tag].isNotEmpty()) {
            Toast.makeText(this, "Kotak sudah terisi!", Toast.LENGTH_SHORT).show()
            return
        }

        filledPos[tag] = currentPlayer
        clickedBtn.text = currentPlayer

        if (checkWinner()) {
            setGameStatus("Pemain $currentPlayer menang!")
            gameActive = false
            return
        }

        if (filledPos.none { it.isEmpty() }) {
            setGameStatus("Seri!")
            gameActive = false
            return
        }

        // Ganti pemain
        currentPlayer = if (currentPlayer == "X") "O" else "X"
        setGameStatus("Giliran $currentPlayer")
    }

    private fun checkWinner(): Boolean {
        val winPositions = arrayOf(
            intArrayOf(0, 1, 2),
            intArrayOf(3, 4, 5),
            intArrayOf(6, 7, 8),
            intArrayOf(0, 3, 6),
            intArrayOf(1, 4, 7),
            intArrayOf(2, 5, 8),
            intArrayOf(0, 4, 8),
            intArrayOf(2, 4, 6)
        )

        for (combo in winPositions) {
            val a = combo[0]
            val b = combo[1]
            val c = combo[2]

            if (filledPos[a] == filledPos[b] &&
                filledPos[b] == filledPos[c] &&
                filledPos[a].isNotEmpty()
            ) {
                return true
            }
        }
        return false
    }

    private fun setGameStatus(status: String) {
        binding.gameStatusText.text = status
    }
}
