package com.raar.facilicar.ui.campo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ResultadoEnvioScreen(
    aceito: Boolean,
    modifier: Modifier = Modifier,
    onTentarNovamente: () -> Unit,
    onCertificado: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(100.dp).clip(CircleShape)
                .background(if (aceito) Color(0xFF52B788) else Color(0xFFE63946)),
            contentAlignment = Alignment.Center
        ) {
            Text(if (aceito) "✓" else "✗", fontSize = 48.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(24.dp))

        Text(
            if (aceito) "Foto Validada!" else "Foto Rejeitada",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = if (aceito) Color(0xFF2D6A4F) else MaterialTheme.colorScheme.error
        )

        Spacer(Modifier.height(8.dp))

        Text(
            if (aceito)
                "Sua contribuição foi aceita pelo sistema ML com 89% de confiança. A certidão CAR será processada em até 48h."
            else
                "A imagem não comprova APP hídrica. O sistema ML identificou divergência com a imagem de satélite.",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        if (!aceito) {
            // Bloco de instrução formativa
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Como tirar uma foto válida:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    InstrucaoItem("1", "Posicione-se na beira do corpo hídrico")
                    InstrucaoItem("2", "Mostre a divisa entre a vegetação e a água")
                    InstrucaoItem("3", "Garanta que o GPS esteja com precisão < 5m")
                    InstrucaoItem("4", "Evite contra-luz — fotografe de costas para o sol")

                    Spacer(Modifier.height(4.dp))
                    // Exemplo visual mockado
                    Box(
                        modifier = Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF0077B6).copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("💧", fontSize = 24.sp)
                            Text("→", fontSize = 20.sp)
                            Text("🌿", fontSize = 24.sp)
                            Text("Exemplo de foto correta", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        if (aceito) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF52B788).copy(alpha = 0.15f))
            ) {
                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🏅")
                    Column {
                        Text("Ponto de Colaboração +1", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text("Você ajudou a atualizar o mapa do CAR!", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        if (aceito) {
            Button(onClick = onCertificado, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                Text("Ver Meu Certificado", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onTentarNovamente, modifier = Modifier.fillMaxWidth()) {
                Text("Coletar Outro Ponto")
            }
        } else {
            Button(onClick = onTentarNovamente, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                Text("Tentar Novamente", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun InstrucaoItem(numero: String, texto: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
        Surface(color = MaterialTheme.colorScheme.secondary, shape = CircleShape) {
            Text(numero, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Bold)
        }
        Text(texto, fontSize = 13.sp, modifier = Modifier.weight(1f))
    }
}
