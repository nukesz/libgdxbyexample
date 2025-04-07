package com.github.nukesz.samples

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.math.Vector3
import com.github.nukesz.LibGDXbyExample
import com.github.nukesz.graphics.clearScreen


class Ship3DModelSample(game: LibGDXbyExample) : BaseScreen(game) {

    private lateinit var cam: PerspectiveCamera
    private lateinit var camController: CameraInputController
    private lateinit var environment: Environment
    private lateinit var modelBatch: ModelBatch
    private lateinit var assets: AssetManager
    private val modelInstances = mutableListOf<ModelInstance>()
    private var loading = false

    override fun show() {
        super.show()

        environment = Environment().apply {
            set(ColorAttribute.createAmbientLight(0.4f, 0.4f, 0.4f, 1f))
            add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))
        }

        modelBatch = ModelBatch()
        cam = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        cam.position.set(7f, 7f, 7f);
        cam.lookAt(0f, 0f, 0f)
        cam.near = 1f
        cam.far = 300f
        cam.update()

        camController = CameraInputController(cam)
        setupInputProcessor(camController)

        assets = AssetManager()
        assets.load("ship/ship.g3db", Model::class.java)
        loading = true
    }

    override fun render(delta: Float) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        clearScreen()

        if (loading && assets.update()) {
            doneLoading()
        }

        camController.update()

        modelBatch.begin(cam)
        for (instance in modelInstances) {
            instance.transform.rotate(Vector3(0f, 1f, 0f), 20f * delta)
            modelBatch.render(instance, environment)
        }
        modelBatch.end()



        renderGui(delta)
    }

    private fun doneLoading() {
        val ship = assets.get("ship/ship.g3db", Model::class.java)
        for (x in -5..5 step 2) {
            for (z in -5..5 step 2) {
                val instance = ModelInstance(ship)
                instance.transform.setToTranslation(x.toFloat(), 0f, z.toFloat())
                modelInstances.add(instance)
            }
        }
        loading = false
    }

    override fun dispose() {
        modelBatch.dispose()
        assets.dispose()
        modelInstances.clear()
    }
}
