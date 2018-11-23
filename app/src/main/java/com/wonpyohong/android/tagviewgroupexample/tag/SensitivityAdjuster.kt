package com.wonpyohong.android.tagviewgroupexample.tag

class SensitivityAdjuster {
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
            if (lastRightMostX - x > 100) {
                return DIRECTION.LEFT
            }
        } else if (isRight) {
            lastRightMostX = x
            if (x - lastLeftMostX > 100) {
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
            if (lastDownMostY - y > 100) {
                return DIRECTION.UP
            }
        } else if (isDown) {
            lastDownMostY = y
            if (y - lastUpMostY > 100) {
                return DIRECTION.DOWN
            }
        }

        return DIRECTION.NONE
    }

    enum class DIRECTION { LEFT, RIGHT, UP, DOWN, NONE }
}