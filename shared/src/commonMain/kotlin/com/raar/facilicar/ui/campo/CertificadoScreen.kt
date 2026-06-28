package com.raar.facilicar.ui.campo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
fun CertificadoScreen(
    modifier: Modifier = Modifier,
    onVoltar: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Meu Certificado", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        // Card do certificado estilizado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1B4332))
                .border(3.dp, Color(0xFFD4AF37), RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("🏅", fontSize = 48.sp)
                Text(
                    "CERTIFICADO DE\nCOLABORADOR AMBIENTAL",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD4AF37),
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.sp
                )
                HorizontalDivider(color = Color(0xFFD4AF37).copy(alpha = 0.5f))
                Text(
                    "José Raimundo Silva",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Fazenda Boa Vista · Santarém/PA",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
                HorizontalDivider(color = Color(0xFFD4AF37).copy(alpha = 0.5f))

                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    ConquistaItem("7", "Pontos\nColetados")
                    ConquistaItem("5", "Pontos\nValidados")
                    ConquistaItem("3", "APPs\nConfirmadas")
                }

                HorizontalDivider(color = Color(0xFFD4AF37).copy(alpha = 0.5f))
                Text("CAR Inteligente · Campanha 2024", fontSize = 11.sp, color = Color(0xFFD4AF37))
                Text(
                    "Este certificado pode ser anexado ao processo\nde regularização ambiental do imóvel no SICAR",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Benefícios desbloqueados
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Benefícios Desbloqueados", fontWeight = FontWeight.SemiBold)
                BeneficioItem("✅", "Certidão CAR liberada para análise")
                BeneficioItem("📄", "Documento válido para processos no órgão estadual")
                BeneficioItem("💳", "Elegível para linha de crédito rural verde")
                BeneficioItem("🌿", "Contribuição registrada no painel nacional do CAR")
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = onVoltar, modifier = Modifier.weight(1f)) {
                Text("Coletar Mais")
            }
            Button(onClick = {}, modifier = Modifier.weight(1f)) {
                Text("Compartilhar")
            }
        }
    }
}

@Composable
private fun ConquistaItem(valor: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD4AF37))
        Text(label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f), textAlign = TextAlign.Center)
    }
}

@Composable
private fun BeneficioItem(emoji: String, texto: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(emoji)
        Text(texto, fontSize = 13.sp)
    }
}
