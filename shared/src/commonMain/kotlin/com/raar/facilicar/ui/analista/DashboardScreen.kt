package com.raar.facilicar.ui.analista

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raar.facilicar.MockData
import com.raar.facilicar.Severidade

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    onVerFila: () -> Unit,
    onVerMapa: () -> Unit
) {
    val stats = MockData.statsNacionais
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Painel CAR", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Analista: Luana — Órgão Estadual", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KpiCard("Imóveis CAR", stats["totalImoveis"] ?: "-", Modifier.weight(1f))
            KpiCard("Atualizados", "${stats["percentAtualizado"]}%", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KpiCard("Alertas Abertos", stats["alertasAbertos"] ?: "-", Modifier.weight(1f), danger = true)
            KpiCard("Acurácia IA", "${stats["acuraciaIA"]}%", Modifier.weight(1f))
        }

        Text("Conflitos Recentes", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        MockData.alertas.take(3).forEach { alerta ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        when (alerta.severidade) {
                            Severidade.CRITICO -> "🔴"
                            Severidade.ATENCAO -> "🟡"
                            Severidade.INFO -> "🟢"
                        },
                        fontSize = 20.sp
                    )
                    Column {
                        Text(alerta.tipo, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        Text(alerta.descricao, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                        Text(alerta.dataDetecao, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onVerMapa, modifier = Modifier.weight(1f)) {
                Text("Ver Mapa")
            }
            Button(onClick = onVerFila, modifier = Modifier.weight(1f)) {
                Text("Fila de Validação")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Submissões aguardando validação", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text(
                    "${MockData.submissoes.count { it.statusValidacao == com.raar.facilicar.StatusValidacao.PENDENTE }} pontos de ground-truth pendentes",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun KpiCard(label: String, value: String, modifier: Modifier = Modifier, danger: Boolean = false) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (danger) MaterialTheme.colorScheme.errorContainer
            else MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(value, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
