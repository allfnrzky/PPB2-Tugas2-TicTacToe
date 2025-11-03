package com.example.multiplayertic_tac_toe

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.multiplayertic_tac_toe.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityGameBinding
    private var gameModel: GameModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Game Tic Tac Toe"


        GameData.fetchGameModel()

        // Tombol game
        binding.btn0.setOnClickListener(this)
        binding.btn1.setOnClickListener(this)
        binding.btn2.setOnClickListener(this)
        binding.btn3.setOnClickListener(this)
        binding.btn4.setOnClickListener(this)
        binding.btn5.setOnClickListener(this)
        binding.btn6.setOnClickListener(this)
        binding.btn7.setOnClickListener(this)
        binding.btn8.setOnClickListener(this)

        // Tombol mulai game
        binding.startGameBtn.setOnClickListener {
            startGame()
        }

        // Observe perubahan game dari Firebase
        GameData.gameModel.observe(this) {
            gameModel = it
            setUI()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    fun setUI() {
        gameModel?.apply {
            binding.btn0.text = filledPos[0]
            binding.btn1.text = filledPos[1]
            binding.btn2.text = filledPos[2]
            binding.btn3.text = filledPos[3]
            binding.btn4.text = filledPos[4]
            binding.btn5.text = filledPos[5]
            binding.btn6.text = filledPos[6]
            binding.btn7.text = filledPos[7]
            binding.btn8.text = filledPos[8]

            when (gameStatus) {
                GameStatus.CREATED -> {
                    binding.startGameBtn.visibility = View.INVISIBLE
                    binding.gameStatusText.text = "ID Game: $gameId\nMenunggu pemain lain untuk bergabung..."
                }

                GameStatus.JOINED -> {
                    binding.startGameBtn.visibility = View.VISIBLE
                    binding.startGameBtn.text = "Mulai Game"
                    binding.gameStatusText.text = "Tekan tombol 'Mulai Game' untuk memulai"
                }

                GameStatus.INPROGRESS -> {
                    binding.startGameBtn.visibility = View.INVISIBLE
                    binding.gameStatusText.text = if (GameData.myID == currentPlayer)
                        "Giliran kamu!"
                    else
                        "Giliran pemain $currentPlayer"
                }

                GameStatus.FINISHED -> {
                    binding.startGameBtn.visibility = View.VISIBLE
                    binding.startGameBtn.text = "Mulai Ulang!"

                    binding.gameStatusText.text = when {
                        winner.isNotEmpty() -> if (GameData.myID == winner)
                            "Kamu menang!"
                        else
                            "Pemain $winner menang!"
                        else -> "Seri!!"
                    }
                }

                else -> {}
            }
        }
    }

    fun startGame() {
        gameModel?.apply {
            // Jika game sudah selesai → reset papan
            if (gameStatus == GameStatus.FINISHED) {
                filledPos = Array(9) { "" }.toMutableList()
                currentPlayer = "X"
                winner = ""
            }

            // Update status ke INPROGRESS
            updateGameData(
                GameModel(
                    gameId = gameId,
                    gameStatus = GameStatus.INPROGRESS,
                    filledPos = filledPos,
                    currentPlayer = currentPlayer,
                    winner = winner
                )
            )
        }
    }

    fun updateGameData(model: GameModel) {
        GameData.saveGameModel(model)
    }

    fun checkForWinner() {
        val winningPos = arrayOf(
            intArrayOf(0, 1, 2),
            intArrayOf(3, 4, 5),
            intArrayOf(6, 7, 8),
            intArrayOf(0, 3, 6),
            intArrayOf(1, 4, 7),
            intArrayOf(2, 5, 8),
            intArrayOf(0, 4, 8),
            intArrayOf(2, 4, 6),
        )

        gameModel?.apply {
            for (i in winningPos) {
                if (
                    filledPos[i[0]] == filledPos[i[1]] &&
                    filledPos[i[1]] == filledPos[i[2]] &&
                    filledPos[i[0]].isNotEmpty()
                ) {
                    gameStatus = GameStatus.FINISHED
                    winner = filledPos[i[0]]
                }
            }

            if (filledPos.none { it.isEmpty() } && gameStatus != GameStatus.FINISHED) {
                gameStatus = GameStatus.FINISHED
            }

            updateGameData(this)
        }
    }

    override fun onClick(v: View?) {
        gameModel?.apply {
            if (gameStatus != GameStatus.INPROGRESS) {
                Toast.makeText(applicationContext, "Game belum dimulai!", Toast.LENGTH_SHORT)
                    .show()
                return
            }

            if (gameId != "-1" && currentPlayer != GameData.myID) {
                Toast.makeText(applicationContext, "Bukan giliran kamu!", Toast.LENGTH_SHORT).show()
                return
            }

            val clickedPos = (v?.tag as String).toInt()
            if (filledPos[clickedPos].isEmpty()) {
                filledPos[clickedPos] = currentPlayer
                currentPlayer = if (currentPlayer == "X") "O" else "X"
                checkForWinner()
                updateGameData(this)
            }
        }
    }
}
