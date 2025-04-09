package com.github.nukesz.samples

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.nukesz.LibGDXbyExample


class LightsAndShadowsSample(game: LibGDXbyExample): BaseScreen(game) {

    private val camera: OrthographicCamera = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    private val viewport = ScreenViewport(camera)
    private val shapeRenderer: ShapeRenderer = ShapeRenderer()
    private val lightSource: Vector2 = Vector2(Gdx.graphics.width / 2f, Gdx.graphics.height / 2f)
    private var boxes: MutableList<Rectangle> = ArrayList()

    init {
        // Generate random boxes
        val boxCount = MathUtils.random(5, 10)
        for (i in 0 until boxCount) {
            val x = MathUtils.random(50, Gdx.graphics.width - 100).toFloat()
            val y = MathUtils.random(50, Gdx.graphics.height - 100).toFloat()
            val size = MathUtils.random(40, 80).toFloat()
            boxes.add(Rectangle(x, y, size, size))
        }
    }

    override fun render(delta: Float)  {
        shapeRenderer.projectionMatrix = camera.combined

        // Draw Boxes
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1f) // Dark gray for obstacles
        for (box in boxes) {
            shapeRenderer.rect(box.x, box.y, box.width, box.height)
        }
        shapeRenderer.end()

        // Cast rays and draw light & shadows
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(1f, 1f, 0f, 0.3f) // Semi-transparent yellow for light
        castLightRays()
        shapeRenderer.end()

        // Draw light source (Sun)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(1f, 1f, 0f, 1f)
        shapeRenderer.circle(lightSource.x, lightSource.y, 20f)
        shapeRenderer.end()

        renderGui(delta)
    }

    private fun castLightRays() {
        val rayCount = 360 // Number of rays
        val maxDist = 800f // Maximum ray distance

        for (i in 0 until rayCount) {
            val angle = Math.toRadians(i.toDouble()).toFloat()
            val rayEnd = Vector2(
                lightSource.x + MathUtils.cos(angle) * maxDist,
                lightSource.y + MathUtils.sin(angle) * maxDist
            )

            var closestIntersection: Vector2? = null
            var minDist: Float = Float.MAX_VALUE

            for (box in boxes) {
                val edges = arrayOf(
                    Vector2(box.x, box.y),
                    Vector2(box.x + box.width, box.y),
                    Vector2(box.x + box.width, box.y + box.height),
                    Vector2(box.x, box.y + box.height),
                )

                for (j in 0..3) {
                    val start = edges[j]
                    val end = edges[(j + 1) % 4] // Connect to the next point

                    val intersection = Vector2()
                    if (Intersector.intersectSegments(lightSource, rayEnd, start, end, intersection)) {
                        val dist = lightSource.dst(intersection)
                        if (dist < minDist) {
                            minDist = dist
                            closestIntersection = intersection
                        }
                    }
                }
            }

            if (closestIntersection != null) {
                shapeRenderer.triangle(
                    lightSource.x, lightSource.y,
                    closestIntersection.x, closestIntersection.y,
                    rayEnd.x, rayEnd.y
                )
            }
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun dispose() {
        super.dispose()
        shapeRenderer.dispose()
    }
}
