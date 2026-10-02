package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RepairRecord
import com.example.data.model.RepairStatus
import com.example.ui.theme.Amber400
import com.example.ui.theme.AmberDark
import com.example.ui.theme.Emerald400
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.MonospaceCodeMedium
import com.example.ui.theme.MonospacePrice
import com.example.ui.theme.Sky400
import com.example.ui.theme.SkyDark
import com.example.ui.theme.Zinc100
import com.example.ui.theme.Zinc400
import com.example.ui.theme.Zinc500
import com.example.ui.theme.Zinc700
import com.example.ui.theme.Zinc800
import com.example.ui.theme.Zinc850
import com.example.ui.theme.Zinc900
import com.example.util.AppLanguage
import com.example.util.Strings

@Composable
fun WorkbenchHeader(
    repairs: List<RepairRecord>,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val waitingCount = repairs.count { it.status == RepairStatus.WAITING.id }
    val repairingCount = repairs.count { it.status == RepairStatus.REPAIRING.id }
    val readyCount = repairs.count { it.status == RepairStatus.READY.id }
    val deliveredCount = repairs.count { it.status == RepairStatus.DELIVERED.id }
    val inShopCount = waitingCount + repairingCount + readyCount

    val pendingTotal = repairs
        .filter { it.status != RepairStatus.DELIVERED.id }
        .sumOf { it.price }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Zinc900)
            .border(1.dp, Zinc800, RoundedCornerShape(16.dp))
            .padding(14.dp)
            .testTag("workbench_header")
    ) {
        Column {
            // Metrics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricChip(
                    modifier = Modifier.weight(1f),
                    title = Strings.totalInShop(language),
                    count = inShopCount,
                    icon = Icons.Default.HourglassEmpty,
                    accentColor = Amber400,
                    containerColor = AmberDark
                )
                MetricChip(
                    modifier = Modifier.weight(1f),
                    title = Strings.totalRepairing(language),
                    count = repairingCount,
                    icon = Icons.Default.Build,
                    accentColor = Sky400,
                    containerColor = SkyDark
                )
                MetricChip(
                    modifier = Modifier.weight(1f),
                    title = Strings.totalReady(language),
                    count = readyCount,
                    icon = Icons.Default.CheckCircle,
                    accentColor = Emerald400,
                    containerColor = EmeraldDark
                )
                MetricChip(
                    modifier = Modifier.weight(1f),
                    title = Strings.totalDelivered(language),
                    count = deliveredCount,
                    icon = Icons.Default.Inventory2,
                    accentColor = Zinc400,
                    containerColor = Zinc850
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sub-strip: Pending shop revenue & workbench status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Zinc850)
                    .border(1.dp, Zinc700.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (inShopCount > 0) Amber400 else Emerald400)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == AppLanguage.AR) "المحل مفتوح • قيد المعالجة" else "Atelier Ouvert • En Traitement",
                        color = Zinc400,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (language == AppLanguage.AR) "مستحقات معلقة: " else "Encours atelier: ",
                        color = Zinc500,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "${pendingTotal.toInt()} ${Strings.currency(language)}",
                        style = MonospacePrice.copy(fontSize = 13.sp, color = Amber400)
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricChip(
    modifier: Modifier = Modifier,
    title: String,
    count: Int,
    icon: ImageVector,
    accentColor: Color,
    containerColor: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(containerColor)
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .padding(vertical = 8.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "$count",
                style = MonospaceCodeMedium.copy(fontSize = 17.sp, color = Zinc100)
            )
            Text(
                text = title,
                color = Zinc400,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}
