package com.example.multiplayertic_tac_toe

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

object GameData {
    private var _gameModel: MutableLiveData<GameModel> = MutableLiveData()
    var gameModel: LiveData<GameModel> = _gameModel
    var myID = ""

    fun saveGameModel(model: GameModel) {
        _gameModel.postValue(model)

        if (model.gameId != "-1") {
            Firebase.firestore.collection("games")
                .document(model.gameId)
                .set(model)
                .addOnSuccessListener {
                    Log.d("Firestore", "Data game berhasil disimpan dengan ID: ${model.gameId}")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Terjadi kesalahan saat menyimpan data game: ", e)
                }
        } else {
            Log.w("Firestore", "ID game bernilai -1, penyimpanan dilewati")
        }
    }

    fun fetchGameModel() {
        gameModel.value?.apply {
            if (gameId != "-1") {
                Firebase.firestore.collection("games")
                    .document(gameId)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            Log.e("Firestore", "Gagal mengambil data game: ", error)
                            return@addSnapshotListener
                        }

                        val model = value?.toObject(GameModel::class.java)
                        if (model != null) {
                            Log.d("Firestore", "Data Game diperbarui: $gameId")
                            _gameModel.postValue(model)
                        } else {
                            Log.w("Firestore", "Tidak ditemukan data game untuk ID: $gameId")
                        }
                    }
            } else {
                Log.w("Firestore", "ID Game tidak valid, pengambilan data dilewati")
            }
        }
    }
}
