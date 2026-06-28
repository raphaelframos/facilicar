package com.raar.facilicar.ui.analista

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raar.facilicar.MockData
import com.raar.facilicar.StatusValidacao
import kotlinx.coroutines.launch

@Composable
fun DetalheValidacaoScreen(
    submissaoId: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()
    val sub = MockData.submissoes.find { it.id == submissaoId } ?: MockData.submissoes.first()
    val prop = MockData.propriedades.find { it.id == sub.propriedadeId }
    val alerta = MockData.alertas.find { it.propriedadeId == sub.propriedadeId }

    val iaColor = when {
        sub.confiancaIA > 90 -> Color(0xFF52B788)
        sub.confiancaIA > 70 -> Color(0xFFF4D03F)
        else                 -> Color(0xFFE63946)
    }

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = onBack) { Text("← Voltar") }
            Text("Detalhe da Submissão", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        // Info da propriedade
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(prop?.nome ?: submissaoId, fontWeight = FontWeight.Bold)
                Text("${prop?.municipio}/${prop?.uf} · ${prop?.areaHa} ha", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Produtor: ${sub.produtor}", fontSize = 13.sp)
                Text("Enviado em: ${sub.dataEnvio}", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
            }
        }

        // ── Pipeline de Validação (3 etapas) ──────────────────
        Text("Pipeline de Validação", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(0.dp)) {

                // Etapa 1 — Proprietário
                DetalheEtapaRow(
                    icone = "✓",
                    corFundo = Color(0xFF52B788),
                    titulo = "Etapa 1 · Proprietário",
                    descricao = "Classificação: ${sub.classificacao.label}",
                    subtexto = sub.dataEnvio
                )

                ConectorDetalhe(cor = MaterialTheme.colorScheme.primary)

                // Etapa 2 — IA
                DetalheEtapaRow(
                    icone = if (sub.autoAprovadoPelaIA) "✓" else "→",
                    corFundo = iaColor,
                    titulo = "Etapa 2 · Análise de IA (Sentinel-2)",
                    descricao = if (sub.autoAprovadoPelaIA)
                        "Confiança ${sub.confiancaIA}% — acima de 90% → auto-aprovado"
                    else
                        "Confiança ${sub.confiancaIA}% — abaixo de 90% → encaminhado ao operador",
                    badge = "${sub.confiancaIA}%",
                    badgeCor = iaColor
                )

                ConectorDetalhe(
                    cor = if (sub.statusValidacao != StatusValidacao.PENDENTE || sub.autoAprovadoPelaIA)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outlineVariant
                )

                // Etapa 3 — Operador Humano
                val (etapa3Icone, etapa3Cor, etapa3Desc) = when {
                    sub.autoAprovadoPelaIA                          -> Triple("✓", Color(0xFF52B788), "Aprovado automaticamente pela IA — sem revisão manual necessária")
                    sub.statusValidacao == StatusValidacao.APROVADO  -> Triple("✓", Color(0xFF52B788), "Aprovado pelo operador e integrado ao SICAR")
                    sub.statusValidacao == StatusValidacao.REJEITADO -> Triple("✗", Color(0xFFE63946), "Rejeitado pelo operador — produtor notificado")
                    else                                             -> Triple("…", MaterialTheme.colorScheme.surfaceVariant, "Aguardando revisão do analista ambiental")
                }
                DetalheEtapaRow(
                    icone = etapa3Icone,
                    corFundo = etapa3Cor,
                    titulo = "Etapa 3 · Operador Humano",
                    descricao = etapa3Desc
                )
            }
        }

        // ── Comparativo satélite vs foto ──────────────────────
        Text("Comparativo: Satélite vs. Ground-truth", fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Imagem Satélite", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF2D6A4F)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(Modifier.size(50.dp, 40.dp).offset(20.dp, (-10).dp).background(Color(0xFFE63946).copy(alpha = 0.7f), RoundedCornerShape(4.dp)))
                    Box(Modifier.size(80.dp, 30.dp).offset((-10).dp, 20.dp).background(Color(0xFF52B788).copy(alpha = 0.8f), RoundedCornerShape(4.dp)))
                    Text("🛰", fontSize = 20.sp, modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp))
                }
            }
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Foto do Produtor", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF40916C)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📷", fontSize = 28.sp)
                    Text(
                        alerta?.coordenadas ?: "-54.7321, -2.4456",
                        fontSize = 8.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.align(Alignment.BottomCenter).padding(4.dp)
                    )
                }
            }
        }

        if (alerta != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    "⚠️  Alerta associado: ${alerta.tipo}",
                    modifier = Modifier.padding(12.dp),
                    fontSize = 13.sp
                )
            }
        }

        // ── Histórico ─────────────────────────────────────────
        Text("Histórico da Área", fontWeight = FontWeight.SemiBold)
        listOf(
            Triple("2022", "Vegetação nativa registrada no CAR", "✅"),
            Triple("2023", "Satélite detectou divergência na área", "⚠️"),
            Triple("2024", "Ground-truth submetido pelo produtor", "📷")
        ).forEach { (ano, desc, icon) ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Text(icon)
                Column {
                    Text(ano, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                    Text(desc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // ── Ações (só para itens que precisam de revisão humana) ──
        if (sub.statusValidacao == StatusValidacao.PENDENTE && !sub.autoAprovadoPelaIA) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { scope.launch { snackbarHostState.showSnackbar("❌ Rejeitado — produtor notificado com instrução formativa") } },
                    modifier = Modifier.weight(1f)
                ) { Text("Rejeitar") }
                Button(
                    onClick = { scope.launch { snackbarHostState.showSnackbar("✅ Aprovado e integrado ao SICAR") } },
                    modifier = Modifier.weight(1f)
                ) { Text("Aprovar e Atualizar SICAR") }
            }
        } else if (sub.autoAprovadoPelaIA) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF52B788).copy(alpha = 0.15f))
            ) {
                Text(
                    "🤖  Auto-aprovado pela IA (${sub.confiancaIA}% de confiança)",
                    modifier = Modifier.padding(12.dp),
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (sub.statusValidacao == StatusValidacao.APROVADO)
                        Color(0xFF52B788).copy(alpha = 0.2f) else Color(0xFFE63946).copy(alpha = 0.2f)
                )
            ) {
                Text(
                    if (sub.statusValidacao == StatusValidacao.APROVADO) "✅ Aprovado pelo operador e integrado ao SICAR" else "❌ Rejeitado pelo operador",
                    modifier = Modifier.padding(12.dp),
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun DetalheEtapaRow(
    icone: String,
    corFundo: Color,
    titulo: String,
    descricao: String,
    subtexto: String? = null,
    badge: String? = null,
    badgeCor: Color = Color.Gray
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background(corFundo),
            contentAlignment = Alignment.Center
        ) {
            Text(icone, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
        Column(modifier = Modifier.weight(1f).padding(top = 4.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(titulo, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                if (badge != null) {
                    Surface(color = badgeCor.copy(alpha = 0.18f), shape = RoundedCornerShape(3.dp)) {
                        Text(badge, modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp), fontSize = 10.sp, color = badgeCor, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Text(descricao, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (subtexto != null) Text(subtexto, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
private fun ConectorDetalhe(cor: Color) {
    Box(
        modifier = Modifier
            .padding(start = 17.dp)
            .width(2.dp)
            .height(20.dp)
            .background(cor)
    )
}
