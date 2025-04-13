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


class Load3DSceneSample(game: LibGDXbyExample) : BaseScreen(game) {

    private lateinit var cam: PerspectiveCamera
    private lateinit var camController: CameraInputController
    private lateinit var environment: Environment
    private lateinit var modelBatch: ModelBatch
    private lateinit var assets: AssetManager
    private val modelInstances = mutableListOf<ModelInstance>()
    private val blockInstances = mutableListOf<ModelInstance>()
    private val invaderInstances = mutableListOf<ModelInstance>()
    private var space: ModelInstance? = null
    private var ship: ModelInstance? = null
    private var loading = false

    override fun show() {
        super.show()

        environment = Environment().apply {
            set(ColorAttribute.createAmbientLight(0.4f, 0.4f, 0.4f, 1f))
            add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))
        }

        modelBatch = ModelBatch()
        cam = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        cam.position.set(0f, 7f, 7f)
        cam.lookAt(0f, 0f, 0f)
        cam.near = 1f
        cam.far = 300f
        cam.update()

        camController = CameraInputController(cam)
        setupInputProcessor(camController)

        assets = AssetManager()
        assets.load("scene/invaderscene.g3db", Model::class.java)
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
            modelBatch.render(instance, environment)
        }
        if (space != null) {
            modelBatch.render(space)
        }
        modelBatch.end()
        renderGui(delta)
    }

    private fun doneLoading() {
        val model = assets.get("scene/invaderscene.g3db", Model::class.java)
        for (i in 0 until model.nodes.size) {
            val id = model.nodes[i].id
            val instance = ModelInstance(model, id)
            val node = instance.getNode(id)

            instance.transform.set(node.globalTransform)
            node.translation.set(0f, 0f, 0f)
            node.scale.set(1f, 1f, 1f)
            node.rotation.idt()
            instance.calculateTransforms()

            if (id == "space") {
                space = instance
                continue
            }

            modelInstances.add(instance)

            if (id == "ship") {
                ship = instance
            } else if (id.startsWith("block")) {
                blockInstances.add(instance)
            } else if (id.startsWith("invader")) {
                invaderInstances.add(instance)
            }
        }
        loading = false
    }

    override fun dispose() {
        modelBatch.dispose()
        assets.dispose()
        modelInstances.clear()
        blockInstances.clear()
        invaderInstances.clear()
    }
}
