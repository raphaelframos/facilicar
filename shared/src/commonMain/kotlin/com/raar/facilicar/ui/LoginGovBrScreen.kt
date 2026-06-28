package com.raar.facilicar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val GovAzul  = Color(0xFF1351B4)
private val GovAzulEscuro = Color(0xFF0C326F)
private val GovAmarelo = Color(0xFFFFCD07)

private data class Perfil(val nome: String, val cpf: String, val papel: String, val isFuncionario: Boolean)

private val perfis = listOf(
    Perfil("Luana Santos",        "123.456.789-00", "Analista Ambiental · SEMA/PA", isFuncionario = true),
    Perfil("José Raimundo Silva", "987.654.321-00", "Produtor Rural · Santarém/PA",  isFuncionario = false)
)

@Composable
fun LoginGovBrScreen(onLogin: (isFuncionario: Boolean) -> Unit) {
    var cpf   by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var erro  by remember { mutableStateOf<String?>(null) }

    fun tentar() {
        val perfil = perfis.find { it.cpf == cpf }
        when {
            cpf.isBlank()   -> erro = "Informe o CPF."
            senha.isBlank() -> erro = "Informe a senha."
            perfil == null  -> erro = "CPF não encontrado. Use um dos perfis de demonstração abaixo."
            senha.length < 3 -> erro = "Senha incorreta."
            else -> { erro = null; onLogin(perfil.isFuncionario) }
        }
    }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

        // ── Header gov.br ──────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(GovAzul)
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Logo gov.br
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(GovAmarelo),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("BR", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = GovAzulEscuro)
                    }
                    Text(
                        "gov.br",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = (-0.5).sp
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text("Acesse com sua conta gov.br", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                Text(
                    "Módulo CAR · Cadastro Ambiental Rural",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }
        }

        // ── Formulário ─────────────────────────────────────────
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = cpf,
                onValueChange = { cpf = it; erro = null },
                label = { Text("CPF") },
                placeholder = { Text("000.000.000-00") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                singleLine = true
            )

            OutlinedTextField(
                value = senha,
                onValueChange = { senha = it; erro = null },
                label = { Text("Senha") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { tentar() }),
                singleLine = true
            )

            if (erro != null) {
                Text(erro!!, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            Button(
                onClick = { tentar() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GovAzul),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Entrar com gov.br", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(onClick = {}) {
                    Text("Esqueci minha senha", fontSize = 12.sp, color = GovAzul)
                }
                TextButton(onClick = {}) {
                    Text("Criar conta gov.br", fontSize = 12.sp, color = GovAzul)
                }
            }

            HorizontalDivider()

            // ── Perfis de demonstração ─────────────────────────
            Text(
                "Acesso rápido — demonstração",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )

            perfis.forEach { perfil ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.5.dp, GovAzul.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                        .clickable { cpf = perfil.cpf; senha = "govbr123"; erro = null }
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(GovAzul.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(if (perfil.isFuncionario) "👩‍💼" else "👨‍🌾", fontSize = 22.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(perfil.nome, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text(perfil.papel, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                "CPF: ${perfil.cpf}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline,
                                fontStyle = FontStyle.Italic
                            )
                        }
                        Surface(
                            color = if (perfil.isFuncionario) GovAzul.copy(alpha = 0.12f) else Color(0xFF52B788).copy(alpha = 0.12f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                if (perfil.isFuncionario) "Analista" else "Produtor",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (perfil.isFuncionario) GovAzul else Color(0xFF2D6A4F)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "Seus dados são protegidos pela LGPD · Lei nº 13.709/2018",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}
