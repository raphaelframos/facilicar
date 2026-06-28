package com.raar.facilicar.ui.campo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raar.facilicar.ClassificacaoSolo
import kotlinx.coroutines.delay

private enum class EtapaStatus { PENDENTE, PROCESSANDO, APROVADO, ENCAMINHADO }

private data class AnaliseAgente(
    val sateliteDetectou: String,
    val correspondencia: String,
    val nivelLabel: String,
    val confianca: Int,
    val autoAprovado: Boolean
)

private fun analisePara(c: ClassificacaoSolo): AnaliseAgente = when (c) {
    ClassificacaoSolo.MATA_NATIVA -> AnaliseAgente(
        sateliteDetectou = "Vegetação nativa densa — NDVI 0.82, sem supressão recente",
        correspondencia  = "Cobertura detectada confirma mata nativa declarada pelo produtor",
        nivelLabel       = "Alta",
        confianca        = 94,
        autoAprovado     = true
    )
    ClassificacaoSolo.APP -> AnaliseAgente(
        sateliteDetectou = "Vegetação ripária no buffer de 30 m do corpo hídrico",
        correspondencia  = "Perfil espectral compatível com APP hídrica declarada",
        nivelLabel       = "Alta",
        confianca        = 91,
        autoAprovado     = true
    )
    ClassificacaoSolo.CORPO_HIDRICO -> AnaliseAgente(
        sateliteDetectou = "Superfície hídrica com cobertura parcial por vegetação flutuante",
        correspondencia  = "Correspondência moderada — área de transição, necessita verificação temporal",
        nivelLabel       = "Moderada",
        confianca        = 87,
        autoAprovado     = false
    )
    ClassificacaoSolo.AGRICULTURA -> AnaliseAgente(
        sateliteDetectou = "Talhões regulares com exposição sazonal de solo identificados",
        correspondencia  = "Correspondência moderada com área agrícola declarada",
        nivelLabel       = "Moderada",
        confianca        = 79,
        autoAprovado     = false
    )
    ClassificacaoSolo.PASTAGEM -> AnaliseAgente(
        sateliteDetectou = "Vegetação herbácea baixa com solo exposto em 35% da área",
        correspondencia  = "Baixa correspondência — histórico indica cobertura mais densa anteriormente",
        nivelLabel       = "Baixa",
        confianca        = 65,
        autoAprovado     = false
    )
    ClassificacaoSolo.AREA_DEGRADADA -> AnaliseAgente(
        sateliteDetectou = "Vegetação nativa densa — NDVI 0.75, sem alteração nos últimos 12 meses",
        correspondencia  = "DIVERGÊNCIA — satélite não confirma área degradada declarada",
        nivelLabel       = "Divergência",
        confianca        = 43,
        autoAprovado     = false
    )
}

@Composable
fun ValidacaoEtapasScreen(
    classificacao: ClassificacaoSolo,
    modifier: Modifier = Modifier,
    onTentarNovamente: () -> Unit,
    onCertificado: () -> Unit
) {
    val analise = remember { analisePara(classificacao) }

    var etapa2Status     by remember { mutableStateOf(EtapaStatus.PROCESSANDO) }
    var etapa3Status     by remember { mutableStateOf(EtapaStatus.PENDENTE) }
    var sub1Ok           by remember { mutableStateOf(false) }
    var sub2Ok           by remember { mutableStateOf(false) }
    var sub3Ok           by remember { mutableStateOf(false) }
    var mostrarResultado by remember { mutableStateOf(false) }
    var mostrarBotoes    by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(600);  sub1Ok = true
        delay(800);  sub2Ok = true
        delay(800);  sub3Ok = true
        delay(600);  mostrarResultado = true
        delay(600);  etapa2Status = if (analise.autoAprovado) EtapaStatus.APROVADO else EtapaStatus.ENCAMINHADO
        delay(500);  etapa3Status  = if (analise.autoAprovado) EtapaStatus.APROVADO else EtapaStatus.ENCAMINHADO
        delay(400);  mostrarBotoes = true
    }

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        Text("Validação em 3 Etapas", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(
            "Classificação: ${classificacao.label}",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(Modifier.height(40.dp))

        // ── Etapa 1: Proprietário ──────────────────────────────
        EtapaRow(
            numero = 1,
            titulo = "Proprietário",
            descricao = "Foto e classificação enviadas via Módulo Offline",
            status = EtapaStatus.APROVADO
        )

        Conector(ativo = true)

        // ── Etapa 2: Agente de IA ──────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Círculo de status do agente
            val circleBg = when (etapa2Status) {
                EtapaStatus.APROVADO    -> Color(0xFF52B788)
                EtapaStatus.ENCAMINHADO -> Color(0xFFF4D03F)
                else                   -> MaterialTheme.colorScheme.primaryContainer
            }
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(circleBg),
                contentAlignment = Alignment.Center
            ) {
                when (etapa2Status) {
                    EtapaStatus.PROCESSANDO -> CircularProgressIndicator(
                        modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    EtapaStatus.APROVADO    -> Text("✓", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    EtapaStatus.ENCAMINHADO -> Text("→", color = Color(0xFF333300), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    else                   -> Text("2", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                }
            }

            Column(modifier = Modifier.weight(1f).padding(top = 6.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Agente de IA  (Sentinel-2)", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)

                // Sub-passos animados
                AnimatedVisibility(visible = sub1Ok, enter = fadeIn()) {
                    SubPasso(concluido = true, texto = "Imagem Sentinel-2 localizada — 13/06/2024")
                }
                AnimatedVisibility(visible = sub2Ok, enter = fadeIn()) {
                    SubPasso(concluido = true, texto = "Cobertura do solo comparada com declaração")
                }
                AnimatedVisibility(visible = sub3Ok, enter = fadeIn()) {
                    SubPasso(concluido = true, texto = "Relatório de correspondência gerado")
                }

                // Card de resultado do agente
                AnimatedVisibility(
                    visible = mostrarResultado,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
                ) {
                    ResultadoAgenteCard(analise = analise, classificacao = classificacao)
                }
            }
        }

        Conector(ativo = etapa2Status != EtapaStatus.PROCESSANDO)

        // ── Etapa 3: Operador Humano ───────────────────────────
        EtapaRow(
            numero = 3,
            titulo = "Operador Humano",
            descricao = when (etapa3Status) {
                EtapaStatus.PENDENTE    -> "Aguardando resultado do agente..."
                EtapaStatus.APROVADO    -> "Aprovado automaticamente — sem necessidade de revisão manual"
                else                   -> "Encaminhado ao analista ambiental (prazo: 5 dias úteis)"
            },
            status = etapa3Status
        )

        Spacer(Modifier.height(40.dp))

        // ── Botões de ação ─────────────────────────────────────
        AnimatedVisibility(
            visible = mostrarBotoes,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (analise.autoAprovado) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF52B788).copy(alpha = 0.15f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🎉", fontSize = 22.sp)
                            Column {
                                Text("Certidão liberada!", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("Processamento em até 24h no SICAR.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    Button(onClick = onCertificado, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                        Text("Ver Certificado", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(onClick = onTentarNovamente, modifier = Modifier.fillMaxWidth()) {
                        Text("Coletar Outro Ponto")
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Próximos passos", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("Um analista ambiental revisará sua submissão em até 5 dias úteis.", fontSize = 13.sp)
                            Text("Você será notificado pelo SICAR quando houver atualização.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Button(onClick = onTentarNovamente, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                        Text("Coletar Outro Ponto", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun SubPasso(concluido: Boolean, texto: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(if (concluido) "✓" else "○", fontSize = 12.sp, color = if (concluido) Color(0xFF52B788) else MaterialTheme.colorScheme.outline)
        Text(texto, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ResultadoAgenteCard(analise: AnaliseAgente, classificacao: ClassificacaoSolo) {
    val nivelColor = when (analise.nivelLabel) {
        "Alta"       -> Color(0xFF52B788)
        "Moderada"   -> Color(0xFFF4D03F)
        "Baixa"      -> Color(0xFFE9631A)
        else         -> Color(0xFFE63946) // Divergência
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = nivelColor.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Resultado da Comparação", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)

            // Linha satélite
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                Text("🛰", fontSize = 14.sp)
                Column {
                    Text("Satélite detectou", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    Text(analise.sateliteDetectou, fontSize = 12.sp)
                }
            }

            // Linha produtor
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                Text("📷", fontSize = 14.sp)
                Column {
                    Text("Produtor declarou", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    Text(classificacao.label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }

            HorizontalDivider()

            // Veredito
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(analise.correspondencia, fontSize = 12.sp, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                Surface(color = nivelColor.copy(alpha = 0.18f), shape = RoundedCornerShape(4.dp)) {
                    Text(
                        "${analise.nivelLabel} · ${analise.confianca}%",
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        fontSize = 11.sp,
                        color = nivelColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun EtapaRow(
    numero: Int,
    titulo: String,
    descricao: String,
    status: EtapaStatus
) {
    val circleBg = when (status) {
        EtapaStatus.APROVADO    -> Color(0xFF52B788)
        EtapaStatus.ENCAMINHADO -> Color(0xFFF4D03F)
        EtapaStatus.PROCESSANDO -> MaterialTheme.colorScheme.primaryContainer
        EtapaStatus.PENDENTE    -> MaterialTheme.colorScheme.surfaceVariant
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier.size(44.dp).clip(CircleShape).background(circleBg),
            contentAlignment = Alignment.Center
        ) {
            when (status) {
                EtapaStatus.PROCESSANDO -> CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp, color = MaterialTheme.colorScheme.primary)
                EtapaStatus.APROVADO    -> Text("✓", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                EtapaStatus.ENCAMINHADO -> Text("→", color = Color(0xFF333300), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                EtapaStatus.PENDENTE    -> Text("$numero", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
        Column(modifier = Modifier.weight(1f).padding(top = 6.dp)) {
            Text(titulo, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(descricao, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 3.dp))
        }
    }
}

@Composable
private fun Conector(ativo: Boolean) {
    Box(
        modifier = Modifier
            .padding(start = 21.dp)
            .width(2.dp)
            .height(28.dp)
            .background(if (ativo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
    )
}
