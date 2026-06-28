package com.raar.facilicar.ui.campo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raar.facilicar.ClassificacaoSolo

private data class OpcaoClassificacao(val solo: ClassificacaoSolo, val emoji: String, val cor: Color)

private val opcoes = listOf(
    OpcaoClassificacao(ClassificacaoSolo.MATA_NATIVA, "🌳", Color(0xFF2D6A4F)),
    OpcaoClassificacao(ClassificacaoSolo.PASTAGEM, "🌾", Color(0xFFD4A017)),
    OpcaoClassificacao(ClassificacaoSolo.AGRICULTURA, "🌽", Color(0xFFE9C46A)),
    OpcaoClassificacao(ClassificacaoSolo.CORPO_HIDRICO, "💧", Color(0xFF0077B6)),
    OpcaoClassificacao(ClassificacaoSolo.APP, "🌿", Color(0xFF52B788)),
    OpcaoClassificacao(ClassificacaoSolo.AREA_DEGRADADA, "⚠️", Color(0xFFE63946))
)

@Composable
fun ClassificacaoFotoScreen(
    modifier: Modifier = Modifier,
    onEnviar: (ClassificacaoSolo) -> Unit,
    onSalvar: (ClassificacaoSolo) -> Unit
) {
    var selecionado by remember { mutableStateOf<ClassificacaoSolo?>(null) }
    var tentouEnviarSemSelecao by remember { mutableStateOf(false) }

    // Outer column: conteúdo scrollável em cima, botões fixos embaixo
    Column(modifier = modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Classificar Foto", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            // Miniatura da foto capturada
            Box(
                modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF40916C)),
                contentAlignment = Alignment.Center
            ) {
                Text("📷", fontSize = 48.sp)
                Row(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp).background(Color.Black.copy(0.4f), RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("📍 -54.7321, -2.4456", fontSize = 10.sp, color = Color.White)
                    Text("13/06/2024 08:32", fontSize = 10.sp, color = Color.White)
                }
            }

            Text("O que você vê nesta foto?", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text("Toque para classificar (3 toques max)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            // Grid 2x3 de classificações
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                opcoes.chunked(3).forEach { linha ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        linha.forEach { opcao ->
                            val isSelected = selecionado == opcao.solo
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) opcao.cor else opcao.cor.copy(alpha = 0.15f))
                                    .border(2.dp, if (isSelected) opcao.cor else Color.Transparent, RoundedCornerShape(12.dp))
                                    .clickable { selecionado = opcao.solo; tentouEnviarSemSelecao = false },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(opcao.emoji, fontSize = 28.sp)
                                    Text(
                                        opcao.solo.label,
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.Center,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Feedback formativo se tentou enviar sem selecionar
            if (tentouEnviarSemSelecao) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("⚠️ Selecione uma categoria", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            "A captura precisa de classificação para ser aceita pelo sistema ML. Escolha a opção que melhor representa o que a foto mostra.",
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // Botões fixos na parte inferior — sempre visíveis
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    if (selecionado == null) tentouEnviarSemSelecao = true
                    else onSalvar(selecionado!!)
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Salvar localmente", fontSize = 15.sp)
            }
            Button(
                onClick = {
                    if (selecionado == null) tentouEnviarSemSelecao = true
                    else onEnviar(selecionado!!)
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Enviar para Validação", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
