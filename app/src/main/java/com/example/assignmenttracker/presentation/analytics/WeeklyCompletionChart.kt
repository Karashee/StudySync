package com.example.assignmenttracker.presentation.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun WeeklyCompletionChart(
    weeklyData: List<WeekData>,
    modifier: Modifier = Modifier
) {
    val maxValue = weeklyData.maxOfOrNull { it.completedCount } ?: 1
    val barColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onSurface
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(16.dp)
    ) {
        Text(
            text = "Weekly Completion Trend",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            weeklyData.forEach { week ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Bar
                    Canvas(
                        modifier = Modifier
                            .width(40.dp)
                            .fillMaxHeight(0.8f)
                    ) {
                        val barHeight = if (maxValue > 0) {
                            (week.completedCount.toFloat() / maxValue) * size.height
                        } else {
                            0f
                        }
                        
                        drawRect(
                            color = barColor,
                            topLeft = Offset(0f, size.height - barHeight),
                            size = Size(size.width, barHeight)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Count label
                    Text(
                        text = week.completedCount.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Week labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            weeklyData.forEach { week ->
                Text(
                    text = week.weekLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
