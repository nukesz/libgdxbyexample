package com.github.nukesz.samples

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.nukesz.LibGDXbyExample
import ktx.app.KtxScreen
import ktx.collections.toGdxArray

open class BaseScreen(private val game: LibGDXbyExample): KtxScreen {

    private val stage: Stage
    private val skin: Skin
    private val selectBox: SelectBox<String>
    protected var multiplexer: InputMultiplexer

    init {
        stage = Stage(ScreenViewport())
        multiplexer = InputMultiplexer()
        multiplexer.addProcessor(stage)

        skin = Skin(Gdx.files.internal("uiskin.json"))

        selectBox = SelectBox<String>(skin)
        selectBox.setPosition(0f, stage.height - 30f)
        selectBox.setSize(200f, 30f)
        selectBox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                println("Changed ${selectBox.selected}")
                game.changeScreen(selectBox.selected)
            }
        })
        stage.addActor(selectBox)
    }

    override fun show() {
        println("Show BaseScreen")
        Gdx.input.inputProcessor = multiplexer
        selectBox.items = game.getScreenNames().toGdxArray()
        selectBox.selected = game.shownScreen::class.java.simpleName
    }

    protected fun renderGui(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
        skin.dispose()
    }
}
