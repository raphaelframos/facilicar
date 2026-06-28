package com.raar.facilicar.ui.analista

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raar.facilicar.MockData
import com.raar.facilicar.Severidade
import androidx.compose.foundation.Image
import facilicar.shared.generated.resources.Res
import facilicar.shared.generated.resources.mapa
import org.jetbrains.compose.resources.painterResource

@Composable
fun MapaConflitosScreen(
    modifier: Modifier = Modifier,
    onAlertaTap: (String) -> Unit
) {
    var showAlertas by remember { mutableStateOf(true) }
    var showValidados by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text("Mapa de Conflitos", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Sentinel-2/Copernicus | Atualizado há 5 dias", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
        }

        Row(
            modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(showAlertas, { showAlertas = !showAlertas }, label = { Text("Alertas", fontSize = 12.sp) })
            FilterChip(showValidados, { showValidados = !showValidados }, label = { Text("Validados", fontSize = 12.sp) })
        }

        // Mapa com imagem real + marcadores sobrepostos
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            Image(
                painter = painterResource(Res.drawable.mapa),
                contentDescription = "Mapa de conflitos CAR",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            if (showAlertas) {
                MockData.alertas.forEachIndexed { i, alerta ->
                    val x = (30 + (i % 4) * 75).dp
                    val y = (80 + (i / 4) * 100).dp
                    val color = when (alerta.severidade) {
                        Severidade.CRITICO -> Color(0xFFE63946)
                        Severidade.ATENCAO -> Color(0xFFF4D03F)
                        Severidade.INFO -> Color(0xFF52B788)
                    }
                    Box(
                        modifier = Modifier
                            .offset(x, y)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(2.dp, Color.White, CircleShape)
                            .clickable { onAlertaTap(alerta.id) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
            if (showValidados) {
                Box(Modifier.offset(150.dp, 120.dp).size(20.dp).clip(CircleShape).background(Color(0xFF023E8A).copy(alpha = 0.85f)).border(2.dp, Color.White, CircleShape))
                Box(Modifier.offset(220.dp, 80.dp).size(20.dp).clip(CircleShape).background(Color(0xFF023E8A).copy(alpha = 0.85f)).border(2.dp, Color.White, CircleShape))
            }
            Text(
                "Sentinel-2 · 13/06/2024",
                modifier = Modifier.align(Alignment.BottomStart).padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                fontSize = 10.sp,
                color = Color.White
            )
        }

        // Legenda
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LegendaItem(Color(0xFFE63946), "Crítico")
            LegendaItem(Color(0xFFF4D03F), "Atenção")
            LegendaItem(Color(0xFF52B788), "Info")
            if (showValidados) LegendaItem(Color(0xFF023E8A), "Validado")
        }

        HorizontalDivider()
        Text("Toque em um marcador para ver detalhes", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

        MockData.alertas.take(4).forEach { alerta ->
            ListItem(
                headlineContent = { Text(alerta.tipo, fontSize = 14.sp) },
                supportingContent = { Text(alerta.coordenadas, fontSize = 11.sp) },
                leadingContent = {
                    Text(
                        when (alerta.severidade) {
                            Severidade.CRITICO -> "🔴"
                            Severidade.ATENCAO -> "🟡"
                            Severidade.INFO -> "🟢"
                        }
                    )
                },
                modifier = Modifier.clickable { onAlertaTap(alerta.id) }
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun LegendaItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(Modifier.size(12.dp).clip(CircleShape).background(color))
        Text(label, fontSize = 11.sp)
    }
}
