package com.raar.facilicar.ui.analista

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.raar.facilicar.MockData
import com.raar.facilicar.StatusValidacao
import com.raar.facilicar.SubmissaoGroundTruth
import kotlinx.coroutines.launch

@Composable
fun FilaValidacaoScreen(
    modifier: Modifier = Modifier,
    onDetalhe: (String) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()
    var submissoes by remember { mutableStateOf(MockData.submissoes) }

    // Etapa 2 → IA: itens que a IA não auto-aprovou (confiancaIA ≤ 90) e ainda PENDENTES
    val filaHumana = submissoes.filter { !it.autoAprovadoPelaIA && it.statusValidacao == StatusValidacao.PENDENTE }
    // Etapa 2 → IA: auto-aprovados (confiancaIA > 90), independente do status
    val autoAprovados = submissoes.filter { it.autoAprovadoPelaIA && it.statusValidacao == StatusValidacao.APROVADO }
    // Resolvidos pelo humano (aprovados/rejeitados com confiança ≤ 90)
    val resolvidosHumano = submissoes.filter { !it.autoAprovadoPelaIA && it.statusValidacao != StatusValidacao.PENDENTE }

    var expandirAutoAprovados by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Fila de Validação", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Etapa 3 — Revisão do operador humano", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Resumo do pipeline
        Row(
            modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PipelineChip(
                label = "Auto-aprovados IA",
                valor = "${autoAprovados.size}",
                cor = Color(0xFF52B788),
                modifier = Modifier.weight(1f)
            )
            PipelineChip(
                label = "Aguardando humano",
                valor = "${filaHumana.size}",
                cor = Color(0xFFF4D03F),
                modifier = Modifier.weight(1f)
            )
            PipelineChip(
                label = "Resolvidos",
                valor = "${resolvidosHumano.size}",
                cor = MaterialTheme.colorScheme.outline,
                modifier = Modifier.weight(1f)
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Fila Humana ──────────────────────────────────────
            if (filaHumana.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF52B788).copy(alpha = 0.1f))
                    ) {
                        Text(
                            "✅  Nenhum item aguardando revisão humana",
                            modifier = Modifier.padding(16.dp),
                            fontSize = 14.sp,
                            color = Color(0xFF2D6A4F)
                        )
                    }
                }
            } else {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFFF4D03F)))
                        Text("Aguardando sua revisão (${filaHumana.size})", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                }
                items(filaHumana) { sub ->
                    SubmissaoCard(
                        sub = sub,
                        onDetalhe = { onDetalhe(sub.id) },
                        onAprovar = {
                            submissoes = submissoes.map { if (it.id == sub.id) it.copy(statusValidacao = StatusValidacao.APROVADO) else it }
                            scope.launch { snackbarHostState.showSnackbar("✅ Aprovado e enviado ao SICAR") }
                        },
                        onRejeitar = {
                            submissoes = submissoes.map { if (it.id == sub.id) it.copy(statusValidacao = StatusValidacao.REJEITADO) else it }
                            scope.launch { snackbarHostState.showSnackbar("❌ Rejeitado — produtor será notificado") }
                        }
                    )
                }
            }

            // ── Auto-aprovados pela IA ────────────────────────────
            item { Spacer(Modifier.height(8.dp)) }
            item {
                TextButton(
                    onClick = { expandirAutoAprovados = !expandirAutoAprovados },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFF52B788)))
                        Text(
                            "${if (expandirAutoAprovados) "▾" else "▸"}  Auto-aprovados pela IA (${autoAprovados.size}) — confiança > 90%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            if (expandirAutoAprovados) {
                items(autoAprovados) { sub ->
                    SubmissaoCard(sub = sub, onDetalhe = { onDetalhe(sub.id) }, onAprovar = {}, onRejeitar = {})
                }
            }

            // ── Resolvidos pelo humano ────────────────────────────
            if (resolvidosHumano.isNotEmpty()) {
                item { Spacer(Modifier.height(4.dp)) }
                item {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(MaterialTheme.colorScheme.outline))
                        Text("Resolvidos pelo operador (${resolvidosHumano.size})", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                items(resolvidosHumano) { sub ->
                    SubmissaoCard(sub = sub, onDetalhe = { onDetalhe(sub.id) }, onAprovar = {}, onRejeitar = {})
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun PipelineChip(label: String, valor: String, cor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = cor.copy(alpha = 0.12f))
    ) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(valor, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = cor)
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SubmissaoCard(
    sub: SubmissaoGroundTruth,
    onDetalhe: () -> Unit,
    onAprovar: () -> Unit,
    onRejeitar: () -> Unit
) {
    val prop = MockData.propriedades.find { it.id == sub.propriedadeId }

    val statusColor = when (sub.statusValidacao) {
        StatusValidacao.PENDENTE  -> Color(0xFFF4D03F)
        StatusValidacao.APROVADO  -> Color(0xFF52B788)
        StatusValidacao.REJEITADO -> Color(0xFFE63946)
    }
    val iaColor = when {
        sub.confiancaIA > 90 -> Color(0xFF52B788)
        sub.confiancaIA > 70 -> Color(0xFFF4D03F)
        else                 -> Color(0xFFE63946)
    }

    Card(modifier = Modifier.fillMaxWidth(), onClick = onDetalhe) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier.size(52.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF40916C)),
                    contentAlignment = Alignment.Center
                ) { Text("📷", fontSize = 20.sp) }
                Column(modifier = Modifier.weight(1f)) {
                    Text(prop?.nome ?: sub.propriedadeId, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(sub.produtor, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(sub.classificacao.label, fontSize = 12.sp)
                }
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Surface(color = statusColor.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                        Text(
                            sub.statusValidacao.name,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp, color = statusColor, fontWeight = FontWeight.Bold
                        )
                    }
                    if (sub.autoAprovadoPelaIA) {
                        Surface(color = Color(0xFF52B788).copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                            Text(
                                "🤖 IA",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp, color = Color(0xFF2D6A4F), fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Linha do pipeline resumida
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Confiança IA:", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                Surface(color = iaColor.copy(alpha = 0.15f), shape = RoundedCornerShape(3.dp)) {
                    Text(
                        "${sub.confiancaIA}%",
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                        fontSize = 11.sp, color = iaColor, fontWeight = FontWeight.Bold
                    )
                }
                if (sub.autoAprovadoPelaIA) {
                    Text("→ Auto-aprovado", fontSize = 11.sp, color = Color(0xFF52B788))
                } else {
                    Text("→ Fila humana", fontSize = 11.sp, color = Color(0xFFB07800))
                }
                Spacer(Modifier.weight(1f))
                Text(sub.dataEnvio, fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
            }

            if (sub.statusValidacao == StatusValidacao.PENDENTE && !sub.autoAprovadoPelaIA) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onRejeitar, modifier = Modifier.weight(1f)) {
                        Text("Rejeitar", fontSize = 13.sp)
                    }
                    Button(onClick = onAprovar, modifier = Modifier.weight(1f)) {
                        Text("Aprovar", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
