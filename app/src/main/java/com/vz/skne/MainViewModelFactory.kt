package com.vz.skne

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vz.skne.data.repository.SpotifyRepository

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(private val spotifyRepository: SpotifyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(spotifyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}