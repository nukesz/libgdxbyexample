package com.github.nukesz.samples

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.utils.viewport.FitViewport
import com.github.nukesz.LibGDXbyExample
import com.github.nukesz.graphics.clearScreen
import ktx.app.KtxInputAdapter
import ktx.assets.toInternalFile


class DragStageActorSample(game: LibGDXbyExample) : BaseScreen(game), KtxInputAdapter {
    companion object {
        private const val WORLD_WIDTH = 800f
        private const val WORLD_HEIGHT = 600f
    }

    private val camera = OrthographicCamera()
    private val viewport = FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera)
    private val stage = Stage(viewport)
    private val draggableActorLocal = DraggableActorLocal(viewport)
    private val draggableActorGlobal = DraggableActorGlobal(viewport)

    init {
        stage.addActor(draggableActorLocal)
        stage.addActor(draggableActorGlobal)
    }

    override fun show() {
        super.show()

        draggableActorLocal.setPosition(0f, 0f)
        draggableActorGlobal.setPosition(200f, 0f)
        setupInputProcessor(stage)
    }

    override fun render(delta: Float) {
        clearScreen()
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        viewport.update(width, height, true)
    }

    override fun dispose() {
        super.dispose()
        stage.dispose()
        draggableActorLocal.dispose()
        draggableActorGlobal.dispose()
    }
}

abstract class BaseDraggableActor(
    protected val viewport: FitViewport,
    texturePath: String = "raw/level-bg.png"
) : Actor() {
    protected val texture = Texture(texturePath.toInternalFile())
    protected val size = 128f
    protected var dragOffset = Vector2()
    protected var dragging = false

    init {
        setSize(size, size)
        addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                dragOffset.set(x, y)
                dragging = true
                return true
            }
            override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                if (dragging) {
                    handleDrag(x, y)
                }
            }
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                dragging = false
            }
        })
    }

    protected abstract fun handleDrag(x: Float, y: Float)

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.draw(texture, this.x, this.y, width, height)
    }

    fun dispose() {
        texture.dispose()
    }
}

class DraggableActorLocal(viewport: FitViewport) : BaseDraggableActor(viewport) {
    private val localCoords = Vector2()

    override fun handleDrag(x: Float, y: Float) {
        localCoords.x = x
        localCoords.y = y
        val parentCoords = localToParentCoordinates(localCoords)
        setPosition(parentCoords.x - dragOffset.x, parentCoords.y - dragOffset.y)
    }
}

class DraggableActorGlobal(viewport: FitViewport) : BaseDraggableActor(viewport) {
    private val screenCoords = Vector2()

    override fun handleDrag(x: Float, y: Float) {
        screenCoords.x = Gdx.input.x.toFloat()
        screenCoords.y = Gdx.input.y.toFloat()
        val worldCoords = viewport.unproject(screenCoords)
        setPosition(worldCoords.x - dragOffset.x, worldCoords.y - dragOffset.y)
    }
}
