package com.github.nukesz

import com.github.nukesz.samples.*
import ktx.app.KtxGame
import ktx.app.KtxScreen

class LibGDXbyExample : KtxGame<KtxScreen>() {

    override fun create() {
        addScreen(InitScreen(this))
        addScreen(ViewportSample(this))
        addScreen(LightsAndShadowsSample(this))
        addScreen(Basic3DSample(this))
        addScreen(Ship3DModelSample(this))
        addScreen(Load3DSceneSample(this))

        setScreen<Load3DSceneSample>()
    }

     fun getScreenNames() = screens.map { it.key.simpleName }

    fun changeScreen(screenName: String) {
        val first = screens.first {
            it.key.simpleName.equals(screenName)
        }
        setScreen(first.key)
    }
}
