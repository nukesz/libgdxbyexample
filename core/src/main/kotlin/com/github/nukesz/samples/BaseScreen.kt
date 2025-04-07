package com.github.nukesz.samples

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
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
    private val multiplexer: InputMultiplexer
    private lateinit var selectBox: SelectBox<String>

    init {
        stage = Stage(ScreenViewport())
        multiplexer = InputMultiplexer()
        skin = Skin(Gdx.files.internal("uiskin.json"))
    }

    override fun show() {
        println("BaseScreen show")
        setupInputProcessor()
        stage.clear()
        selectBox = SelectBox<String>(skin)
        selectBox.setPosition(0f, stage.height - 30f)
        selectBox.setSize(200f, 30f)
        selectBox.items = game.getScreenNames().toGdxArray()
        selectBox.selected = game.shownScreen::class.java.simpleName
        selectBox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                println("Changed ${selectBox.selected}")
                if (selectBox.selected != game.shownScreen::class.java.simpleName) {
                    game.changeScreen(selectBox.selected)
                }
            }
        })
        stage.addActor(selectBox)
    }

    protected fun renderGui(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    protected fun setupInputProcessor(processor: InputProcessor = InputAdapter()) {
        multiplexer.clear()
        multiplexer.addProcessor(stage)
        multiplexer.addProcessor(processor)
        Gdx.input.inputProcessor = multiplexer
    }

    override fun dispose() {
        stage.dispose()
        skin.dispose()
    }
}
