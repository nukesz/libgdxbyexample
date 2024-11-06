package com.github.nukesz

import com.github.nukesz.samples.ViewportSample
import com.github.nukesz.screens.InitScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen

class LibGDXbyExample: KtxGame<KtxScreen>() {

    override fun create() {
        addScreen(ViewportSample())
        setScreen<ViewportSample>()
    }
}
