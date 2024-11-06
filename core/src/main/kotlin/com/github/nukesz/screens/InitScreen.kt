package com.github.nukesz.screens

import ktx.app.KtxScreen
import ktx.app.clearScreen

class InitScreen : KtxScreen {


    override fun render(delta: Float) {
        clearScreen(1f, 1f, 1f, 0f)
    }
}
