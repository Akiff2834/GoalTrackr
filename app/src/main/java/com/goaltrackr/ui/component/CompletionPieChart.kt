package com.goaltrackr.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape

@Composable
fun CompletionPieChart(completed: Int, uncompleted: Int) {
    val total = completed + uncompleted
    if (total == 0) return // hiç hedef yoksa çizme

    val chartData = listOf(
        completed.toFloat(),
        uncompleted.toFloat()
    )
    val colors = listOf(Color(0xFF4CAF50), Color(0xFFF44336)) // Yeşil / Kırmızı

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(modifier = Modifier.size(200.dp)) {
            var startAngle = -90f
            chartData.forEachIndexed { index, value ->
                val sweepAngle = (value / total) * 360f
                drawArc(
                    color = colors[index],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true
                )
                startAngle += sweepAngle
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(12.dp).clip(CircleShape).background(Color(0xFF4CAF50)))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tamamlanan")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(12.dp).clip(CircleShape).background(Color(0xFFF44336)))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tamamlanmamış")
            }
        }
    }
}
