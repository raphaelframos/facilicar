package com.raar.facilicar.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SelecaoModoScreen(
    onAnalistaSelected: () -> Unit,
    onCampoSelected: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CAR Inteligente",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Ground-truth colaborativo para o SICAR",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 48.dp)
        )

        Card(
            onClick = onAnalistaSelected,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("👩‍💼", fontSize = 36.sp)
                Spacer(Modifier.height(8.dp))
                Text("Modo Analista", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    "Validar submissões, aprovar ground-truth e exportar dados para o SICAR",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Card(
            onClick = onCampoSelected,
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("👨‍🌾", fontSize = 36.sp)
                Spacer(Modifier.height(8.dp))
                Text("Modo Campo", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    "Fotografar e classificar pontos da propriedade para validação ambiental",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(Modifier.height(32.dp))
        Text(
            "Módulo integrado ao SICAR / Módulo Offline Dataprev",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )
    }
}
