package com.raar.facilicar.ui.campo

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private enum class SyncState { SYNCING, SYNCED }

@Composable
fun TelaColetaScreen(
    modifier: Modifier = Modifier,
    onCapturar: () -> Unit
) {
    var syncState      by remember { mutableStateOf(SyncState.SYNCING) }
    var chegouAoPonto  by remember { mutableStateOf(false) }

    // Simula download em background quando há rede na cidade
    LaunchedEffect(Unit) {
        delay(2800)
        syncState = SyncState.SYNCED
    }

    Column(modifier = modifier.fillMaxSize()) {

        // ── Header ────────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
            Text("Coleta de Campo", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(
                "Certidão CAR bloqueada — ground-truth necessário",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error
            )
        }

        // ── Card de Sincronização (Paradoxo do Offline) ───────
        AnimatedContent(
            targetState = syncState,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "sync"
        ) { state ->
            when (state) {
                SyncState.SYNCING -> SyncProgressCard()
                SyncState.SYNCED  -> SyncOkBanner()
            }
        }

        Spacer(Modifier.height(8.dp))

        // ── Visor de câmera ───────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1A1A2E))
        ) {
            // Crosshair
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Box(Modifier.size(60.dp, 2.dp).background(Color.White.copy(alpha = 0.8f)))
                Box(Modifier.size(2.dp, 60.dp).background(Color.White.copy(alpha = 0.8f)))
                Box(Modifier.size(20.dp, 2.dp).offset((-40).dp, (-40).dp).background(Color.White))
                Box(Modifier.size(2.dp, 20.dp).offset((-40).dp, (-40).dp).background(Color.White))
                Box(Modifier.size(20.dp, 2.dp).offset(20.dp, (-40).dp).background(Color.White))
                Box(Modifier.size(2.dp, 20.dp).offset(38.dp, (-40).dp).background(Color.White))
                Box(Modifier.size(20.dp, 2.dp).offset((-40).dp, 40.dp).background(Color.White))
                Box(Modifier.size(2.dp, 20.dp).offset((-40).dp, 22.dp).background(Color.White))
                Box(Modifier.size(20.dp, 2.dp).offset(20.dp, 40.dp).background(Color.White))
                Box(Modifier.size(2.dp, 20.dp).offset(38.dp, 22.dp).background(Color.White))

                // Anel verde de geofence quando dentro da área
                if (chegouAoPonto) {
                    Box(
                        Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .border(3.dp, Color(0xFF52B788), CircleShape)
                    )
                    Box(
                        Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, Color(0xFF52B788).copy(alpha = 0.4f), CircleShape)
                    )
                }
            }

            // GPS overlay
            Column(
                modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                horizontalAlignment = Alignment.End
            ) {
                Surface(color = Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(4.dp)) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text("📍 GPS", fontSize = 10.sp, color = Color.Green)
                        Text("-54.7321", fontSize = 10.sp, color = Color.White)
                        Text("-2.4456", fontSize = 10.sp, color = Color.White)
                        Text("±3m", fontSize = 9.sp, color = Color.Green)
                    }
                }
            }

            // Badge offline
            Surface(
                color = Color(0xFFF4D03F).copy(alpha = 0.9f),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.align(Alignment.TopStart).padding(12.dp)
            ) {
                Text(
                    "✈ OFFLINE",
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Legenda inferior
            Surface(
                color = Color.Black.copy(alpha = 0.5f),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
            ) {
                Text(
                    if (chegouAoPonto) "✅ Dentro da área — pode fotografar" else "Aponte para a área de conflito",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 11.sp,
                    color = if (chegouAoPonto) Color(0xFF52B788) else Color.White.copy(alpha = 0.85f)
                )
            }
        }

        // ── Trava Antifraude (Geofencing) ────────────────────
        GeofenceStatusCard(chegouAoPonto = chegouAoPonto)

        // ── Card do conflito ativo ─────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🛰", fontSize = 16.sp)
                    Text(
                        "APP Hídrica — Rio Boa Vista · Bloqueio ativo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                Text(
                    "Instrução: fotografe a margem do rio mostrando a vegetação nativa",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        // ── Botão Capturar (travado pelo geofence) ─────────────
        Button(
            onClick = onCapturar,
            enabled = chegouAoPonto,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (chegouAoPonto) MaterialTheme.colorScheme.primary else Color(0xFF9E9E9E),
                disabledContainerColor = Color(0xFFCCCCCC)
            )
        ) {
            Text(
                if (chegouAoPonto) "📷  Capturar" else "🔒  Capturar (fora da área de conflito)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Botão de demo para simular chegada/saída do ponto
        TextButton(
            onClick = { chegouAoPonto = !chegouAoPonto },
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
        ) {
            Text(
                if (chegouAoPonto) "← Simular saída da área (demo)"
                else "→ Simular chegada ao ponto · -54.7321, -2.4456 (demo)",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun SyncProgressCard() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1351B4).copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = Color(0xFF1351B4)
                )
                Column {
                    Text(
                        "Baixando dados da propriedade...",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1351B4)
                    )
                    Text(
                        "Mapa · Coordenadas do alerta · Cache offline",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(2.dp)),
                color = Color(0xFF1351B4),
                trackColor = Color(0xFF1351B4).copy(alpha = 0.15f)
            )
        }
    }
}

@Composable
private fun SyncOkBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        color = Color(0xFF52B788).copy(alpha = 0.12f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("✅", fontSize = 16.sp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Cache local atualizado · 14/06/2024 07:45",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2D6A4F)
                )
                Text(
                    "📦 Fazenda Boa Vista · 1 alerta crítico · coord. -54.7321, -2.4456",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun GeofenceStatusCard(chegouAoPonto: Boolean) {
    val distancia = if (chegouAoPonto) "12 m" else "847 m"
    val dentro = chegouAoPonto

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (dentro) Color(0xFF52B788).copy(alpha = 0.12f)
                             else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Ícone de status
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (dentro) Color(0xFF52B788).copy(alpha = 0.2f) else MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(if (dentro) "✅" else "🔒", fontSize = 18.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    if (dentro) "Dentro da área de conflito" else "Fora da área de conflito",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (dentro) Color(0xFF2D6A4F) else MaterialTheme.colorScheme.error
                )
                Text(
                    if (dentro) "Captura liberada pelo geofence (raio 50 m)"
                    else "Aproxime-se do ponto de conflito para liberar a captura",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Badge de distância
            Surface(
                color = if (dentro) Color(0xFF52B788).copy(alpha = 0.2f)
                        else MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        distancia,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (dentro) Color(0xFF2D6A4F) else MaterialTheme.colorScheme.error
                    )
                    Text("do conflito", fontSize = 9.sp, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}
