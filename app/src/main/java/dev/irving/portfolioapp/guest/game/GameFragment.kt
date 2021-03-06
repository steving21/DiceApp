/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.irving.portfolioapp.guest.game

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment.findNavController
import dev.irving.portfolioapp.R
import dev.irving.portfolioapp.databinding.GameFragmentBinding

/**
 * Fragment where the game is played
 */
class GameFragment : Fragment() {

    lateinit var viewModel: GameViewModel

    private lateinit var binding: GameFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.game_fragment,
            container,
            false
        )
        viewModel = ViewModelProviders.of(this).get(GameViewModel::class.java)
        binding.lifecycleOwner = this
        binding.gameViewModel = viewModel

        viewModel.eventGameFinish.observe(this, Observer { isGameFinished ->
            if (isGameFinished) {
                viewModel.onGameFinishComplete()
                gameFinished()
            }
        })
        viewModel.buzzer.observe(this, Observer { buzz(it.pattern) })
        return binding.root

    }

    /**
     * Called when the game is finished
     */
    private fun gameFinished() {
        val newScore = viewModel.score.value ?: 0
        val action = GameFragmentDirections.actionGameToScore()
        action.score = newScore
        findNavController(this).navigate(action)
    }

    private fun buzz(pattern: LongArray) {
        val buzzer = activity?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
        buzzer?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                buzzer.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                buzzer.vibrate(pattern, -1)
            }
        }
    }
}
