package com.github.nukesz.screens

import com.github.nukesz.LibGDXbyExample
import com.github.nukesz.samples.BaseScreen

class InitScreen(game: LibGDXbyExample) : BaseScreen(game) {

    override fun render(delta: Float) {
        renderGui(delta)
    }
}
