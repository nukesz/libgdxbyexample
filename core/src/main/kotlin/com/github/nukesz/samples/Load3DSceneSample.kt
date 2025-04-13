package com.github.nukesz.samples

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.github.nukesz.LibGDXbyExample
import com.github.nukesz.graphics.clearScreen


class Load3DSceneSample(game: LibGDXbyExample) : BaseScreen(game) {

    private lateinit var cam: PerspectiveCamera
    private lateinit var camController: CameraInputController
    private lateinit var environment: Environment
    private lateinit var modelBatch: ModelBatch
    private lateinit var assets: AssetManager
    private val modelInstances = mutableListOf<GameObject>()
    private val blockInstances = mutableListOf<GameObject>()
    private val invaderInstances = mutableListOf<GameObject>()
    private var space: GameObject? = null
    private var ship: GameObject? = null
    private var loading = false
    private val position = Vector3()
    private var visibleCount = 0

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

        visibleCount = 0
        modelBatch.begin(cam)
        modelInstances.filter { isVisible(cam, it) }.forEach {
            modelBatch.render(it, environment)
            visibleCount++
        }
        if (space != null) {
            modelBatch.render(space)
        }
        modelBatch.end()
        renderGui(delta, visibleCount)
    }

    // Implement frustum culling
    private fun isVisible(cam: Camera, instance: GameObject): Boolean {
        instance.transform.getTranslation(position)
        position.add(instance.center)
        return cam.frustum.sphereInFrustum(position, instance.radius)
    }

    private fun doneLoading() {
        val model = assets.get("scene/invaderscene.g3db", Model::class.java)
        for (i in 0 until model.nodes.size) {
            val id = model.nodes[i].id
            val instance = GameObject(model, id, true)

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

    class GameObject(model: Model, rootNode: String, mergeTransform: Boolean): ModelInstance(model, rootNode, mergeTransform) {
        private val dimensions: Vector3 = Vector3()
        val center: Vector3 = Vector3()
        var radius: Float = 0f

        init {
            calculateBoundingBox(bounds)
            bounds.getCenter(center)
            bounds.getDimensions(dimensions)
            radius = dimensions.len() / 2f;
        }

        companion object {
            private val bounds = BoundingBox()
        }
    }
}
