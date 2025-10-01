package com.github.nukesz.controller

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.math.Vector3
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * First-person camera controller.
 *
 * The camera behaves as if it were the player's eyes in a 3D world.
 * - The mouse controls where the player looks (yaw + pitch).
 * - The keyboard controls how the player moves relative to their current view direction (WASD).
 * - The world has a fixed "up" (Y axis), so the player cannot roll or tilt sideways.
 * - The mouse cursor is locked for immersion, but can be released with ESC.
 */
class FirstPersonCameraController(
    private val camera: PerspectiveCamera,
    private val moveSpeed: Float = 10f,     // Movement speed in units/second
    private val lookSensitivity: Float = 0.1f // How fast the view changes per pixel of mouse movement
) : InputAdapter() {

    // Horizontal rotation around the Y axis (turning left/right)
    private var yaw = 0f

    // Vertical rotation around the X axis (looking up/down)
    private var pitch = 0f

    // Last mouse position used to compute movement deltas
    private var lastX = Gdx.input.x.toFloat()
    private var lastY = Gdx.input.y.toFloat()

    // Ignore the first mouse event to prevent a sudden jump
    private var firstMouse = true

    init {
        // Initialize yaw/pitch based on the camera's starting direction
        // atan2 gives horizontal angle (yaw), asin gives vertical angle (pitch)
        val dir = camera.direction
        yaw = Math.toDegrees(atan2(dir.x.toDouble(), dir.z.toDouble())).toFloat()
        pitch = Math.toDegrees(asin(dir.y.toDouble())).toFloat()

        // Capture mouse so all movement is relative to the window center
        Gdx.input.isCursorCatched = true
    }

    /**
     * Updates the camera position each frame based on WASD keys.
     *
     * Key concept: movement is relative to where the camera is facing,
     * not the world axes. This makes "W" always mean "forward".
     */
    fun update(delta: Float) {
        // Forward = current view direction
        val dir = camera.direction.cpy().nor()

        // Right = perpendicular to forward and world up
        // Used for strafing left/right without drifting vertically
        val right = dir.cpy().crs(Vector3.Y).nor()

        // Up is fixed to the world’s up axis, since FPS cameras don’t tilt sideways
        val up = Vector3.Y

        // Move forward/back
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            camera.position.add(dir.scl(moveSpeed * delta))
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            camera.position.sub(dir.scl(moveSpeed * delta))
        }

        // Strafe left/right
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.position.sub(right.scl(moveSpeed * delta))
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            camera.position.add(right.scl(moveSpeed * delta))
        }

        // Apply changes to the camera
        camera.update()
    }

    /**
     * Handles mouse movement to update where the camera is looking.
     *
     * Concept:
     * - Mouse delta moves the view by changing yaw and pitch.
     * - Pitch is clamped to avoid flipping upside down.
     * - After applying yaw/pitch, we rebuild a full orthogonal basis:
     *   forward, right, and up vectors. This keeps the camera upright.
     */
    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        if (firstMouse) {
            lastX = screenX.toFloat()
            lastY = screenY.toFloat()
            firstMouse = false
        }

        // Mouse deltas
        val dx = (screenX - lastX) * lookSensitivity
        val dy = (screenY - lastY) * lookSensitivity

        // Update orientation angles
        yaw -= dx
        pitch -= dy

        // Prevent flipping: looking straight up or down is allowed, but not beyond
        pitch = pitch.coerceIn(-89f, 89f)

        // Convert yaw/pitch to radians for trig
        val radYaw = Math.toRadians(yaw.toDouble()).toFloat()
        val radPitch = Math.toRadians(pitch.toDouble()).toFloat()

        // Forward vector derived from spherical coordinates
        val forward = Vector3(
            cos(radPitch) * sin(radYaw),
            sin(radPitch),
            cos(radPitch) * cos(radYaw)
        ).nor()

        // Right = perpendicular to forward and world up
        val right = Vector3(forward).crs(Vector3.Y).nor()

        // Up = perpendicular to forward and right
        // This keeps the camera upright and prevents unwanted rolling
        val up = Vector3(right).crs(forward).nor()

        // Apply new orientation
        camera.direction.set(forward)
        camera.up.set(up)

        // Store current mouse position for next frame
        lastX = screenX.toFloat()
        lastY = screenY.toFloat()

        return true
    }

    /**
     * Releases the mouse cursor so the user can interact with the OS/window.
     * Conceptually: ESC exits "immersive mode".
     */
    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Input.Keys.ESCAPE) {
            Gdx.input.isCursorCatched = false
        }
        return false
    }

    /**
     * Recaptures the mouse when clicking inside the window.
     * Conceptually: clicking brings the player "back into the game".
     */
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        Gdx.input.isCursorCatched = true
        return true
    }
}
