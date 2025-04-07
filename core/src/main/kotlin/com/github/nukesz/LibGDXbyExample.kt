package com.github.nukesz

import com.github.nukesz.samples.Basic3DSample
import com.github.nukesz.samples.LightsAndShadowsSample
import com.github.nukesz.samples.ViewportSample
import com.github.nukesz.samples.InitScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen

class LibGDXbyExample : KtxGame<KtxScreen>() {

    override fun create() {
        addScreen(InitScreen(this))
        addScreen(ViewportSample(this))
        addScreen(LightsAndShadowsSample(this))
        addScreen(Basic3DSample(this))

        setScreen<LightsAndShadowsSample>()
    }

     fun getScreenNames() = screens.map { it.key.simpleName }

    fun changeScreen(screenName: String) {
        val first = screens.first {
            it.key.simpleName.equals(screenName)
        }
        setScreen(first.key)
    }
}
