package com.wonpyohong.android.tagviewgroupexample.tag

class SensitivityAdjuster {
    private val horizontalSensitivity = 100
    private val verticalSensitivity = 50

    private var prevX = 0f
    private var prevY = 0f

    private var lastLeftMostX = 0f
    private var lastRightMostX = 0f
    private var lastUpMostY = 0f
    private var lastDownMostY = 0f

    fun init() {
        lastLeftMostX = 0f
        lastRightMostX = Float.MAX_VALUE
        lastUpMostY = 0f
        lastDownMostY = Float.MAX_VALUE
    }

    fun setPrevPoint(x: Float, y: Float) {
        prevX = x
        prevY = y
    }

    fun isEnoughHorizontalMove(x: Float): DIRECTION {
        val isRight = prevX - x < 0
        val isLeft = prevX - x > 0

        if (isLeft) {
            lastLeftMostX = x
            if (lastRightMostX - x > horizontalSensitivity) {
                return DIRECTION.LEFT
            }
        } else if (isRight) {
            lastRightMostX = x
            if (x - lastLeftMostX > horizontalSensitivity) {
                return DIRECTION.RIGHT
            }
        }

        return DIRECTION.NONE
    }

    fun isEnoughVerticalMove(y: Float): DIRECTION {
        val isUp = prevY - y > 0
        val isDown = prevY - y < 0

        if (isUp) {
            lastUpMostY = y
            if (lastDownMostY - y > verticalSensitivity) {
                return DIRECTION.UP
            }
        } else if (isDown) {
            lastDownMostY = y
            if (y - lastUpMostY > verticalSensitivity) {
                return DIRECTION.DOWN
            }
        }

        return DIRECTION.NONE
    }

    enum class DIRECTION { LEFT, RIGHT, UP, DOWN, NONE }
}