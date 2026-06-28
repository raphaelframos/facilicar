package com.raar.facilicar.ui.campo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raar.facilicar.MockData
import com.raar.facilicar.StatusValidacao
import com.raar.facilicar.SubmissaoGroundTruth

private fun respostaOperador(sub: SubmissaoGroundTruth): String = when {
    sub.autoAprovadoPelaIA && sub.statusValidacao == StatusValidacao.APROVADO ->
        "Auto-aprovado pela IA (${sub.confiancaIA}% de confiança)"
    sub.statusValidacao == StatusValidacao.APROVADO ->
        "Aprovado pelo operador. Dados integrados ao SICAR."
    sub.statusValidacao == StatusValidacao.REJEITADO ->
        "Rejeitado pelo operador: a foto não comprova suficientemente o tipo de cobertura declarado. " +
        "Tire nova foto na divisa da área, mostrando claramente a vegetação e o limite do imóvel."
    else ->
        "Aguardando análise do operador ambiental (prazo: 5 dias úteis)."
}

@Composable
fun HistoricoColetaScreen(modifier: Modifier = Modifier) {
    val submissoes = MockData.submissoes.sortedByDescending { it.dataEnvio }

    val totalAprovados = submissoes.count { it.statusValidacao == StatusValidacao.APROVADO }
    val totalRejeitados = submissoes.count { it.statusValidacao == StatusValidacao.REJEITADO }
    val totalPendentes = submissoes.count { it.statusValidacao == StatusValidacao.PENDENTE }

    Column(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text("Histórico de Coletas", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(
                "${submissoes.size} envios · $totalAprovados aprovados · $totalRejeitados rejeitados · $totalPendentes pendentes",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Chips resumo
        Row(
            modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ResumoChip("✅ $totalAprovados", Color(0xFF52B788))
            ResumoChip("⏳ $totalPendentes", Color(0xFFF4D03F))
            ResumoChip("❌ $totalRejeitados", Color(0xFFE63946))
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(submissoes) { sub ->
                HistoricoCard(sub)
            }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun HistoricoCard(sub: SubmissaoGroundTruth) {
    val prop = MockData.propriedades.find { it.id == sub.propriedadeId }

    val statusColor = when (sub.statusValidacao) {
        StatusValidacao.APROVADO  -> Color(0xFF52B788)
        StatusValidacao.REJEITADO -> Color(0xFFE63946)
        StatusValidacao.PENDENTE  -> Color(0xFFF4D03F)
    }
    val statusLabel = when (sub.statusValidacao) {
        StatusValidacao.APROVADO  -> if (sub.autoAprovadoPelaIA) "Auto-aprovado" else "Aprovado"
        StatusValidacao.REJEITADO -> "Rejeitado"
        StatusValidacao.PENDENTE  -> "Pendente"
    }
    val iaColor = when {
        sub.confiancaIA > 90 -> Color(0xFF52B788)
        sub.confiancaIA > 70 -> Color(0xFFF4D03F)
        else                 -> Color(0xFFE63946)
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {

            // Cabeçalho: foto + info + badges
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier.size(52.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF40916C)),
                    contentAlignment = Alignment.Center
                ) { Text("📷", fontSize = 20.sp) }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(prop?.nome ?: sub.propriedadeId, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(sub.classificacao.label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(sub.dataEnvio, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                }

                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Surface(color = statusColor.copy(alpha = 0.18f), shape = RoundedCornerShape(4.dp)) {
                        Text(
                            statusLabel,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                            fontSize = 11.sp, color = statusColor, fontWeight = FontWeight.Bold
                        )
                    }
                    Surface(color = iaColor.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                        Text(
                            "IA ${sub.confiancaIA}%",
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                            fontSize = 10.sp, color = iaColor, fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            HorizontalDivider()

            // Resposta do operador
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                Text(
                    when (sub.statusValidacao) {
                        StatusValidacao.APROVADO  -> if (sub.autoAprovadoPelaIA) "🤖" else "✅"
                        StatusValidacao.REJEITADO -> "❌"
                        StatusValidacao.PENDENTE  -> "⏳"
                    },
                    fontSize = 16.sp
                )
                Column {
                    Text(
                        when (sub.statusValidacao) {
                            StatusValidacao.APROVADO  -> if (sub.autoAprovadoPelaIA) "Resposta da IA" else "Resposta do Operador"
                            StatusValidacao.REJEITADO -> "Resposta do Operador"
                            StatusValidacao.PENDENTE  -> "Status"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        respostaOperador(sub),
                        fontSize = 12.sp,
                        color = when (sub.statusValidacao) {
                            StatusValidacao.REJEITADO -> MaterialTheme.colorScheme.error
                            else                      -> MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ResumoChip(label: String, cor: Color) {
    Surface(
        color = cor.copy(alpha = 0.12f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            fontSize = 12.sp,
            color = cor,
            fontWeight = FontWeight.SemiBold
        )
    }
}
