package com.example.hungrytoad.model

import com.example.hungrytoad.R

sealed class FallingObject(
    val points: Int,
    val drawableRes: Int,
    val rotationSpeed: Float = 180f
) {
    object Bomb : FallingObject(-20, R.drawable.ic_bomb,  360f)
    object Beetle : FallingObject(10, R.drawable.ic_bug,  250f)
    object Caterpillar : FallingObject(15, R.drawable.ic_caterpillar, 150f)
    object Ladybug : FallingObject(20, R.drawable.ic_insect,  180f)
    object Mosquito : FallingObject(5, R.drawable.ic_mosquito, 200f)
    object GravityBonus : FallingObject(0, R.drawable.ic_gravity_bonus, 10f)
    object GoldBeetle : FallingObject(0, R.drawable.ic_gold_bug, 10f)
}
data class FallingObjectInstance(
    val type: FallingObject,
    var x: Float,
    var y: Float,
    var rotation: Float = 0f,
    val speed: Float,
    var isCollected: Boolean = false,
    val id: Int = System.currentTimeMillis().hashCode() + (Math.random() * 1000).toInt()
)