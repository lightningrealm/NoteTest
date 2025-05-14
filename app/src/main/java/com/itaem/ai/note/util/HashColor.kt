package notecom.itaem.ai.note.util

import android.graphics.Color
import kotlin.math.abs

object HashColor {
    fun generatePleasantColorFromHash(hash: Int): Int {
        // 确保hash为正数
        var hash = hash
        hash = abs(hash.toDouble()).toInt()
        // 使用hash生成HSV颜色值
        val hue = (hash % 360).toFloat() // 色调 0-360
        val saturation = 0.7f + 0.2f * ((hash / 360) % 10) / 10f // 饱和度 0.7-0.9
        val value = 0.8f + 0.1f * ((hash / 3600) % 10) / 10f // 亮度 0.8-0.9
        // 将HSV转换为RGB颜色
        val color = Color.HSVToColor(floatArrayOf(hue, saturation, value))
        // 设置不透明度
        return -0x1000000 or (color and 0x00FFFFFF)
    }
}