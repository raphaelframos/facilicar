# CAR Inteligente

> O CAR Inteligente é um aplicativo que ajuda produtores rurais a desbloquear e regularizar a Certidão CAR de forma simples, permitindo comprovar o estado real de sua propriedade com uma foto tirada no local — verificada automaticamente e integrada ao cadastro ambiental.

---

Desenvolvido para o **haCARthon — Desafio 2: Melhorar o acesso a dados geoespaciais do CAR**, promovido pelo Serviço Florestal Brasileiro (SFB).

---

## O Problema

O Cadastro Ambiental Rural (CAR) reúne **8,2 milhões de imóveis rurais** no Brasil — mas os dados são autodeclarados pelo proprietário, sem verificação sistemática. Apenas **61% dos imóveis têm dados atualizados**.

Quando o satélite Sentinel-2 detecta uma divergência (desmatamento, APP removida, área incorreta), a Certidão CAR do produtor é bloqueada. Sem a certidão, ele perde acesso a crédito rural, licenças e comercialização. O ciclo de notificação e correção no modelo atual dura meses — e depende da iniciativa do próprio órgão estadual.

## A Solução

**Inversão de lógica:** em vez de esperar o analista, o produtor é o agente da regularização.

```
Satélite detecta divergência → Certidão bloqueada → Produtor fotografa o ponto exato
→ IA compara com imagem de satélite → Aprovação automática ou revisão humana
→ SICAR atualizado → Certidão desbloqueada
```

O ciclo que antes durava meses pode ser concluído em **menos de 24 horas**.

---

## Funcionalidades

### Para o Produtor Rural

| Funcionalidade | Descrição |
|----------------|-----------|
| **Login gov.br** | Acesso com a identidade digital federal. O papel (produtor/analista) é determinado automaticamente pelo cadastro. |
| **Sincronização prévia** | Quando há Wi-Fi ou sinal celular, o app baixa em segundo plano o mapa da propriedade e as coordenadas do conflito. No campo, sem rede, os dados já estão disponíveis. |
| **Trava antifraude (Geofencing)** | O botão de captura só é liberado quando o produtor está dentro de 50 metros do ponto exato do conflito detectado pelo satélite. Impede fotos de áreas preservadas distantes sendo usadas como prova. |
| **Classificação simplificada** | Grade de 6 ícones para classificar o tipo de cobertura do solo com um toque: Mata Nativa, Pastagem, Agricultura, Corpo Hídrico, APP ou Área Degradada. |
| **Validação em 3 etapas** | A submissão passa por Proprietário → Agente de IA → Operador Humano, com acompanhamento visual animado em tempo real. |
| **Histórico de coletas** | Todas as submissões com status, data, resposta do operador e instruções de correção em caso de rejeição. |
| **Certificado de Colaborador Ambiental** | Documento emitido ao produtor que pode ser anexado ao processo de regularização no SICAR. Libera acesso a crédito rural verde. |
| **Offline-first** | Salva fotos e classificações localmente e sincroniza quando o sinal retorna. |

### Para o Analista Ambiental

| Funcionalidade | Descrição |
|----------------|-----------|
| **Painel CAR** | KPIs nacionais: total de imóveis, alertas abertos, acurácia da IA e submissões pendentes. |
| **Mapa de conflitos** | Imagem real de satélite com marcadores de alerta sobrepostos, filtráveis por tipo e status. Toque no marcador abre o detalhe. |
| **Fila de validação** | Apenas os itens com baixa confiança de IA chegam ao analista. Os demais são auto-aprovados. Revisão com botões inline de Aprovar ou Rejeitar. |
| **Detalhe da validação** | Comparativo lado a lado da imagem de satélite e da foto do produtor. Pipeline de 3 etapas, histórico da área e ações de aprovação. |

---

## Diferenciais

**Sem fraude possível.** A trava de geofencing garante que a foto foi tirada exatamente no ponto de conflito, não em outra área da fazenda.

**Sem dependência de rede no campo.** A sincronização acontece antes de o produtor sair para o campo. No local, o app funciona inteiramente offline.

**Sem burocracia extra para o analista.** Submissões com confiança de IA acima de 90% são aprovadas automaticamente. O analista só vê o que realmente precisa de julgamento humano.

**Incentivo real.** O bloqueio da certidão cria motivação imediata para o produtor participar. O certificado de colaborador cria valor tangível ao final.

---

## Fluxos de Navegação

```
Login gov.br
├── Funcionário → Painel CAR
│   ├── Mapa de Conflitos → Detalhe da Validação
│   └── Fila de Validação → Detalhe da Validação
└── Produtor Rural → Coleta de Campo
    ├── Classificação da Foto → Validação em 3 Etapas → Certificado
    ├── Histórico de Coletas
    └── Certificado
```

---

## Stack Técnica

| Camada | Tecnologia |
|--------|-----------|
| Plataforma | Kotlin Multiplatform (KMP) — Android + iOS |
| UI | Compose Multiplatform 1.11.1 |
| Design System | Material 3 |
| Autenticação | gov.br |
| Imagens de satélite | Sentinel-2 / Copernicus |
| Integração | Módulo Offline SICAR / Dataprev |
| Android mínimo | API 24 (Android 7.0) |
| Kotlin | 2.4.0 |

> Este repositório é um protótipo de hackathon. Todos os dados são mockados — não há conexão com banco de dados, APIs externas ou câmera real.

---

## Como Executar

### Android

```bash
./gradlew :androidApp:assembleDebug
```

Instale o APK gerado em `androidApp/build/outputs/apk/debug/` em qualquer dispositivo Android 7.0+.

### iOS

Abra `/iosApp` no Xcode e execute no simulador ou dispositivo.

---

## Perfis de Demonstração

| Perfil | CPF | Destino |
|--------|-----|---------|
| Luana Santos — Analista SEMA/PA | `123.456.789-00` | Painel do Analista |
| José Raimundo Silva — Produtor Rural | `987.654.321-00` | Tela de Coleta |

Senha: qualquer valor com 3+ caracteres.

---

## Estrutura do Projeto

```
shared/src/commonMain/kotlin/com/raar/facilicar/
├── App.kt                    # Navegação e back stack
├── MockData.kt               # Dados de demonstração
├── BackHandler.kt            # expect/actual para botão voltar
├── ui/
│   ├── LoginGovBrScreen.kt   # Autenticação
│   ├── analista/
│   │   ├── DashboardScreen.kt
│   │   ├── MapaConflitosScreen.kt
│   │   ├── FilaValidacaoScreen.kt
│   │   └── DetalheValidacaoScreen.kt
│   └── campo/
│       ├── TelaColetaScreen.kt       # Geofencing + Sync
│       ├── ClassificacaoFotoScreen.kt
│       ├── ValidacaoEtapasScreen.kt  # Agente de IA
│       ├── HistoricoColetaScreen.kt
│       └── CertificadoScreen.kt
```

---

## Documentação

- [`business.md`](./business.md) — Documentação completa de negócio, personas, jornadas e regras de negócio.
