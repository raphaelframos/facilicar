package com.raar.facilicar

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import com.raar.facilicar.ui.*
import com.raar.facilicar.ui.analista.*
import com.raar.facilicar.ui.campo.*

sealed class Screen {
    object Login : Screen()
    object Dashboard : Screen()
    object MapaConflitos : Screen()
    object FilaValidacao : Screen()
    data class DetalheValidacao(val id: String) : Screen()
    object TelaColeta : Screen()
    object ClassificacaoFoto : Screen()
    data class ValidacaoEtapas(val classificacao: ClassificacaoSolo) : Screen()
    object HistoricoColeta : Screen()
    object Certificado : Screen()
}

sealed class Modo { object Analista : Modo(); object Campo : Modo() }

@Composable
fun App() {
    MaterialTheme {
        val backStack = remember { mutableStateListOf<Screen>(Screen.Login) }
        var modo by remember { mutableStateOf<Modo?>(null) }
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        val currentScreen = backStack.last()

        // Intercepta botão/gesto voltar do sistema quando há histórico
        PlatformBackHandler(enabled = backStack.size > 1) {
            backStack.removeLast()
        }

        fun navigateTo(screen: Screen) {
            backStack.add(screen)
        }

        // Navegação por aba: limpa a pilha até a raiz do modo e abre a aba
        fun navigateToTab(screen: Screen) {
            while (backStack.size > 1) backStack.removeLast()
            backStack.add(screen)
        }

        fun navigateBack() {
            if (backStack.size > 1) backStack.removeLast()
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                when {
                    modo == Modo.Analista && currentScreen !is Screen.Login ->
                        AnalistaBottomNav(currentScreen = currentScreen, onNavigate = { navigateToTab(it) })
                    modo == Modo.Campo && currentScreen !is Screen.Login ->
                        CampoBottomNav(currentScreen = currentScreen, onNavigate = { navigateToTab(it) })
                }
            }
        ) { padding ->
            when (val s = currentScreen) {
                is Screen.Login -> LoginGovBrScreen(
                    onLogin = { isFuncionario ->
                        if (isFuncionario) {
                            modo = Modo.Analista
                            navigateTo(Screen.Dashboard)
                        } else {
                            modo = Modo.Campo
                            navigateTo(Screen.TelaColeta)
                        }
                    }
                )
                is Screen.Dashboard -> DashboardScreen(
                    modifier = Modifier.padding(padding),
                    onVerFila = { navigateTo(Screen.FilaValidacao) },
                    onVerMapa = { navigateTo(Screen.MapaConflitos) }
                )
                is Screen.MapaConflitos -> MapaConflitosScreen(
                    modifier = Modifier.padding(padding),
                    onAlertaTap = { id -> navigateTo(Screen.DetalheValidacao(id)) }
                )
                is Screen.FilaValidacao -> FilaValidacaoScreen(
                    modifier = Modifier.padding(padding),
                    onDetalhe = { id -> navigateTo(Screen.DetalheValidacao(id)) },
                    snackbarHostState = snackbarHostState
                )
                is Screen.DetalheValidacao -> DetalheValidacaoScreen(
                    submissaoId = s.id,
                    modifier = Modifier.padding(padding),
                    onBack = { navigateBack() },
                    snackbarHostState = snackbarHostState
                )
                is Screen.TelaColeta -> TelaColetaScreen(
                    modifier = Modifier.padding(padding),
                    onCapturar = { navigateTo(Screen.ClassificacaoFoto) }
                )
                is Screen.ClassificacaoFoto -> ClassificacaoFotoScreen(
                    modifier = Modifier.padding(padding),
                    onEnviar = { classificacao -> navigateTo(Screen.ValidacaoEtapas(classificacao)) },
                    onSalvar = { classificacao ->
                        scope.launch {
                            snackbarHostState.showSnackbar("💾  ${classificacao.label} salvo localmente — será sincronizado ao recuperar sinal")
                        }
                    }
                )
                is Screen.ValidacaoEtapas -> ValidacaoEtapasScreen(
                    classificacao = s.classificacao,
                    modifier = Modifier.padding(padding),
                    onTentarNovamente = { navigateToTab(Screen.TelaColeta) },
                    onCertificado = { navigateTo(Screen.Certificado) }
                )
                is Screen.HistoricoColeta -> HistoricoColetaScreen(
                    modifier = Modifier.padding(padding)
                )
                is Screen.Certificado -> CertificadoScreen(
                    modifier = Modifier.padding(padding),
                    onVoltar = { navigateToTab(Screen.TelaColeta) }
                )
            }
        }
    }
}

@Composable
private fun AnalistaBottomNav(currentScreen: Screen, onNavigate: (Screen) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            selected = currentScreen is Screen.Dashboard,
            onClick = { onNavigate(Screen.Dashboard) },
            icon = { Text("📊") },
            label = { Text("Painel") }
        )
        NavigationBarItem(
            selected = currentScreen is Screen.MapaConflitos,
            onClick = { onNavigate(Screen.MapaConflitos) },
            icon = { Text("🗺") },
            label = { Text("Mapa") }
        )
        NavigationBarItem(
            selected = currentScreen is Screen.FilaValidacao || currentScreen is Screen.DetalheValidacao,
            onClick = { onNavigate(Screen.FilaValidacao) },
            icon = { Text("✅") },
            label = { Text("Validar") }
        )
    }
}

@Composable
private fun CampoBottomNav(currentScreen: Screen, onNavigate: (Screen) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            selected = currentScreen is Screen.TelaColeta || currentScreen is Screen.ClassificacaoFoto || currentScreen is Screen.ValidacaoEtapas,
            onClick = { onNavigate(Screen.TelaColeta) },
            icon = { Text("📷") },
            label = { Text("Coletar") }
        )
        NavigationBarItem(
            selected = currentScreen is Screen.HistoricoColeta,
            onClick = { onNavigate(Screen.HistoricoColeta) },
            icon = { Text("📋") },
            label = { Text("Histórico") }
        )
        NavigationBarItem(
            selected = currentScreen is Screen.Certificado,
            onClick = { onNavigate(Screen.Certificado) },
            icon = { Text("🏅") },
            label = { Text("Certificado") }
        )
    }
}
