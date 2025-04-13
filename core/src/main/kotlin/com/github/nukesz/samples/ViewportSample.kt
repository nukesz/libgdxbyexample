package com.github.nukesz.samples

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.ArrayMap
import com.badlogic.gdx.utils.viewport.*
import com.github.nukesz.LibGDXbyExample
import com.github.nukesz.graphics.clearScreen
import ktx.app.KtxInputAdapter
import ktx.assets.toInternalFile
import ktx.graphics.LetterboxingViewport
import ktx.log.logger

class ViewportSample(game: LibGDXbyExample) : BaseScreen(game), KtxInputAdapter {

    companion object {
        private var log = logger<ViewportSample>()

        private const val WORLD_WIDTH = 1080F // world units -> 1 wu == 1 pixel
        private const val WORLD_HEIGHT = 720F // world units -> 1 wu == 1 pixel
    }

    private val camera: OrthographicCamera
    private val batch: SpriteBatch
    private val texture: Texture
    private val font: BitmapFont
    private lateinit var currentViewport: Viewport

    private val viewports = ArrayMap<String, Viewport>()
    private var currentViewportIndex = -1
    private var currentViewportName = ""

    init {
        Gdx.app.logLevel = Application.LOG_DEBUG
        log.debug { "create()" }

        camera = OrthographicCamera()
        batch = SpriteBatch()
        texture = Texture("raw/level-bg.png".toInternalFile())
        font = BitmapFont("fonts/oswald-32.fnt".toInternalFile())

        createViewports()
        selectNextViewport()
    }

    override fun show() {
        super.show()
        setupInputProcessor(this)
    }

    private fun createViewports() {
        viewports.put(StretchViewport::class.java.simpleName, StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera))
        viewports.put(FitViewport::class.java.simpleName, FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera))
        viewports.put(FillViewport::class.java.simpleName, FillViewport(WORLD_WIDTH, WORLD_HEIGHT, camera))
        viewports.put(ScreenViewport::class.java.simpleName, ScreenViewport(camera))
        viewports.put(ExtendViewport::class.java.simpleName, ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera))
        viewports.put(LetterboxingViewport::class.java.simpleName, LetterboxingViewport())
    }

    private fun selectNextViewport() {
        currentViewportIndex = (currentViewportIndex + 1) % viewports.size
        currentViewport = viewports.getValueAt(currentViewportIndex)
        resize(Gdx.graphics.width, Gdx.graphics.height)
        currentViewportName = viewports.getKeyAt(currentViewportIndex)

        log.debug{ "selected viewport= $currentViewportName" }
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        currentViewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        clearScreen()
        batch.projectionMatrix = camera.combined
        batch.begin()

        draw()

        batch.end()
        renderGui(delta)
    }

    private fun draw() {
        batch.draw(texture, 0F, 0F, WORLD_WIDTH, WORLD_HEIGHT)
        font.draw(batch, currentViewportName, 50F, 100F)
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        selectNextViewport()
        return false
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
        texture.dispose()
        font.dispose()
    }
}
