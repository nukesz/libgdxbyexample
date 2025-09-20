package com.github.nukesz.samples

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.github.nukesz.LibGDXbyExample
import com.github.nukesz.graphics.clearScreen


class Basic3DSample(game: LibGDXbyExample) : BaseScreen(game) {

    private val cam: Camera by lazy { PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()) }
    private val camController: CameraInputController by lazy { CameraInputController(cam) }
    private val environment: Environment by lazy {
        Environment().apply {
            set(ColorAttribute.createAmbientLight(0.8f, 0.8f, 0.8f, 1f))
            add(DirectionalLight().set(1f, 1f, 1f, -1f, -0.8f, -0.2f))
        }
    }
    private val modelBatch: ModelBatch by lazy { ModelBatch() }
    private lateinit var model: Model
    private lateinit var instance: ModelInstance

    override fun show() {
        super.show()

        cam.position.set(10f, 10f, 10f)
        cam.lookAt(0f, 0f, 0f)
        cam.near = 1f
        cam.far = 300f
        cam.update()

        setupInputProcessor(camController)

        val modelBuilder = ModelBuilder()
        model = modelBuilder.createBox(
            5f, 5f, 5f,
            Material(ColorAttribute.createDiffuse(Color.GREEN)),
            Usage.Position.toLong() or Usage.Normal.toLong()
        )
        instance = ModelInstance(model)
    }

    override fun render(delta: Float) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        clearScreen()

        camController.update()

        modelBatch.begin(cam)
        modelBatch.render(instance, environment)
        modelBatch.end()

        instance.transform.rotate(Vector3(0f, 1f, 0f), 20f * delta)

        renderGui(delta)
    }

    override fun dispose() {
        modelBatch.dispose()
        if (::model.isInitialized) {
            model.dispose()
        }
    }
}
