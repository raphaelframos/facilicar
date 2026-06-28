# Documentação de Negócio — CAR Inteligente

## 1. Contexto e Problema

### O Cadastro Ambiental Rural (CAR)

O CAR é o maior banco de dados ambiental da América Latina, instituído pela Lei 12.651/2012 (Código Florestal). Reúne informações sobre **8,2 milhões de imóveis rurais** em todo o Brasil, cobrindo mais de 7 milhões de km².

O problema central: **os dados são autodeclarados pelo proprietário rural**. Não há verificação automática e sistemática entre o que o produtor registra e o que efetivamente existe no território. Isso cria lacunas críticas:

- Sobreposições não detectadas com Unidades de Conservação e Terras Indígenas
- APPs (Áreas de Preservação Permanente) declaradas incorretamente ou omitidas
- Desmatamentos que não constam no cadastro
- Divergências de área superiores a 15% entre o declarado e o calculado por satélite
- Cadastros desatualizados por anos sem revisão

Apenas **61% dos imóveis têm dados atualizados**. Analistas estaduais como os das SEMAs (Secretarias de Meio Ambiente) precisam validar manualmente centenas de alertas gerados por cruzamento com imagens de satélite, mas as ferramentas disponíveis são lentas, fragmentadas e não funcionam offline em campo.

### O Custo do Modelo Atual

O modelo vigente é top-down: satélite detecta → analista investiga → produz notificação → produtor (eventualmente) atualiza o cadastro. O ciclo dura meses e depende inteiramente da boa vontade do proprietário para correção voluntária. Sem incentivo real, o passivo ambiental se acumula.

---

## 2. Solução: Crowdsourced Ground-Truth com Validação por IA

### Inversão de Lógica

O CAR Inteligente propõe uma inversão: em vez de esperar o analista detectar e notificar, o próprio **produtor rural é ativado como coletor de dados de campo** (ground-truth) — com incentivo real para participar.

```
Sentinel-2 detecta conflito ou divergência
            ↓
Sistema bloqueia automaticamente a Certidão CAR daquela área
            ↓
Produtor recebe notificação e acessa o app (offline-first)
            ↓
Produtor fotografa e classifica o ponto de conflito in loco
            ↓
Agente de IA compara a foto com a imagem Sentinel-2 da mesma área
            ↓
Confiança > 90% → aprovação automática, certidão liberada em 24h
Confiança ≤ 90% → encaminhado ao analista humano (prazo 5 dias úteis)
            ↓
SICAR atualizado com dado verificado
            ↓
Produtor recebe Certificado de Colaborador Ambiental
```

### Por Que Funciona

| Mecanismo | Explicação |
|-----------|------------|
| **Incentivo real** | O bloqueio da Certidão CAR impede acesso a crédito rural, licenças e comercialização. O produtor tem interesse direto em regularizar rápido. |
| **Sincronização prévia (Paradoxo do Offline)** | Quando o produtor ainda está na cidade com Wi-Fi ou sinal celular, o app baixa automaticamente em segundo plano o mapa da propriedade, as coordenadas exatas do alerta e o cache de instruções. No campo, sem rede, todos esses dados já estão disponíveis localmente. |
| **Trava antifraude (Geofencing)** | O botão "Capturar" só é liberado quando o dispositivo está dentro de um raio de 50 metros da coordenada exata do conflito apontado pelo satélite. Impede que o produtor fotografe uma área preservada distante e submeta como prova da área em disputa. |
| **Validação formativa** | Quando a foto é rejeitada, o produtor recebe instrução visual de como corrigir — não apenas uma mensagem de erro. Reduz retrabalho e frustração. |
| **Reforço positivo** | O Certificado de Colaborador Ambiental pode ser anexado ao processo de regularização no SICAR, criando valor tangível para o produtor. |
| **Agente de IA** | Compara espectro Sentinel-2 (NDVI, padrão de cobertura, presença hídrica) com a classificação declarada. Reduz a carga sobre analistas humanos. |

### Referências Internacionais

- **EU Copernicus — Checks by Monitoring (CbM):** validação automática de subvenções agrícolas por satélite, substituindo vistorias presenciais.
- **NRM Australiano:** aplicativos offline para mapeamento de vegetação nativa por agricultores, com validação posterior.
- **PSA Costa Rica:** pagamento por serviços ambientais condicionado a evidências georreferenciadas coletadas pelo próprio produtor.

---

## 3. Personas

### Luana Santos — Analista Ambiental (Modo Analista)

- **Cargo:** Analista Ambiental, Secretaria de Meio Ambiente do Estado do Pará (SEMA/PA)
- **Acesso:** Funcionária do órgão estadual, autenticada como `Analista` via gov.br
- **Responsabilidades:** Monitorar alertas geoespaciais, revisar submissões com baixa confiança de IA, aprovar ou rejeitar dados de ground-truth, atualizar o SICAR
- **Dor principal:** Volume alto de alertas, ferramentas desconexas, dificuldade de priorizar o que realmente precisa de atenção humana
- **Ganho com o produto:** Apenas as submissões com confiança ≤ 90% chegam até ela — o resto é tratado pela IA. Seu tempo vai para os casos que realmente precisam de julgamento

### José Raimundo Silva — Produtor Rural (Modo Campo)

- **Perfil:** Produtor rural, Fazenda Boa Vista, Santarém/PA, 1.240 ha
- **Acesso:** Produtor rural com CPF cadastrado no gov.br, autenticado como `Produtor`
- **Situação:** Tem um alerta de desmatamento detectado pelo Sentinel-2 em parte de sua fazenda. A certidão CAR está bloqueada
- **Dor principal:** Não entende o processo burocrático, tem conectividade limitada, não sabe como provar que a área está preservada
- **Ganho com o produto:** Fotografa o ponto, classifica com 3 toques, recebe confirmação em horas (quando IA aprova automaticamente) e o Certificado desbloqueio a certidão

---

## 4. Autenticação

### Tela de Login gov.br

**Primeira tela do aplicativo.** O sistema usa a identidade digital gov.br como porta de entrada, integrado ao ecossistema federal.

**Fluxo:**
1. Usuário informa CPF e senha gov.br
2. O sistema identifica o papel vinculado ao CPF:
   - CPF de funcionário de órgão ambiental → redirecionado para **Modo Analista**
   - CPF de produtor rural cadastrado no SICAR → redirecionado para **Modo Campo**
3. Não há tela de seleção manual de modo — o papel é determinado pelo cadastro gov.br

**Perfis de demonstração disponíveis:**
| Perfil | CPF | Destino |
|--------|-----|---------|
| Luana Santos (Analista SEMA/PA) | 123.456.789-00 | Dashboard do Analista |
| José Raimundo Silva (Produtor Rural) | 987.654.321-00 | Tela de Coleta |

---

## 5. Jornada do Analista (Modo Analista)

O analista acessa o sistema via desktop ou tablet para monitorar alertas e validar submissões de campo.

### 5.1 Dashboard (Painel CAR)

**Tela inicial após login de funcionário.**

Exibe os indicadores nacionais do CAR em tempo real:

| KPI | Valor demonstração |
|-----|--------------------|
| Total de imóveis CAR | 8.247.312 |
| % com dados atualizados | 61% |
| Alertas abertos | 34.891 |
| Acurácia do agente de IA | 89% |
| Ground-truths validados | 1.234.567 |
| Estados integrados | 26 |

Abaixo dos KPIs: lista dos 3 conflitos mais recentes com badge de severidade (🔴 Crítico / 🟡 Atenção / 🟢 Info).

Ações disponíveis:
- **Ver Mapa** → navega para o Mapa de Conflitos
- **Fila de Validação** → navega para a Fila de Validação

### 5.2 Mapa de Conflitos

**Visualização geoespacial dos alertas ativos.**

- Exibe imagem real do território obtida via Sentinel-2 (substituiu a visualização ilustrativa)
- Marcadores coloridos sobrepostos indicam alertas: vermelho (crítico), amarelo (atenção), verde (info)
- Filtros por camada:
  - **Alertas** — liga/desliga os marcadores de conflito
  - **Validados** — exibe pontos já validados na base
- Rodapé indica a data da imagem de satélite utilizada
- Toque em um marcador → abre o Detalhe da Validação correspondente
- Lista de alertas abaixo do mapa com coordenadas GPS e tipo de conflito

**Alertas cadastrados no sistema (demonstração):**

| ID | Propriedade | Tipo | Severidade |
|----|-------------|------|------------|
| a1 | Fazenda Boa Vista / Santarém-PA | Desmatamento detectado (23 ha) | Crítico |
| a2 | Fazenda Esperança / Canarana-MT | Divergência de APP hídrica (800m) | Crítico |
| a3 | Sítio Recanto Verde / Altamira-PA | Sobreposição com UC municipal | Atenção |
| a4 | Chácara Beira Rio / Rondonópolis-MT | Divergência de área > 15% | Atenção |
| a5 | Chácara Primavera / Balsas-MA | Prazo de atualização vencendo | Info |
| a6 | Fazenda Nova Aliança / Querência-MT | Inconsistência de coordenadas | Atenção |
| a7 | Sítio Caminho do Sol / Novo Progresso-PA | Corpo hídrico não declarado | Info |
| a8 | Fazenda São João / Sinop-MT | Silvicultura não registrada | Info |

### 5.3 Fila de Validação

**Central de triagem das submissões de ground-truth.**

A fila é organizada em três seções:

**Seção 1 — Aguardando revisão humana**
Submissões com confiança de IA ≤ 90% e status PENDENTE. São os únicos itens que exigem ação do analista. Cada card exibe:
- Propriedade, produtor, classificação declarada
- Badge de confiança de IA (verde > 90%, amarelo > 70%, vermelho ≤ 70%)
- Rota no pipeline: "→ Fila humana"
- Botões inline: **Rejeitar** / **Aprovar**
- Clique no card → abre Detalhe para análise aprofundada

**Seção 2 — Auto-aprovados pela IA** (colapsável)
Submissões com confiança > 90% já aprovadas automaticamente. Disponíveis para auditoria, mas sem necessidade de ação.

**Seção 3 — Resolvidos pelo operador**
Histórico de itens já decididos (aprovados ou rejeitados) pelo analista na sessão atual.

Ao aprovar: snackbar "✅ Aprovado e enviado ao SICAR"
Ao rejeitar: snackbar "❌ Rejeitado — produtor será notificado"

### 5.4 Detalhe da Validação

**Análise aprofundada de uma submissão individual.**

Exibe:
1. **Dados da propriedade:** nome, município/UF, área em hectares, produtor, data de envio
2. **Pipeline de validação em 3 etapas:**
   - Etapa 1 ✓ Proprietário — classificação declarada e data
   - Etapa 2 → Agente de IA — confiança em %, rota tomada (auto-aprovado ou encaminhado)
   - Etapa 3 → Operador Humano — status da decisão
3. **Comparativo visual:** imagem Sentinel-2 da área × foto enviada pelo produtor com coordenadas GPS
4. **Alerta associado:** tipo de conflito que originou a solicitação de ground-truth
5. **Histórico temporal da área:** linha do tempo com eventos (2022 → 2023 → 2024)
6. **Ações** (somente para itens pendentes de revisão humana): Rejeitar / Aprovar e Atualizar SICAR

---

## 6. Jornada do Produtor (Modo Campo)

O produtor acessa o app pelo celular, muitas vezes em área rural com pouca ou nenhuma conectividade.

### 6.1 Tela de Coleta

**Tela inicial após login de produtor.**

Esta tela resolve dois problemas técnicos fundamentais antes de qualquer fotografia ser tirada:

#### Paradoxo do Offline — Sincronização Prévia na Cidade

O produtor rural enfrenta o **Paradoxo do Offline**: para usar o app no campo (onde não há sinal), ele precisa dos dados do conflito — mas esses dados estão na nuvem. A solução é a **sincronização prévia automática**: quando o produtor ainda tem Wi-Fi ou sinal celular (geralmente em casa ou no escritório antes de sair para o campo), o app detecta a conexão e inicia automaticamente em segundo plano o download de:

- Mapa offline da propriedade (tiles em cache)
- Coordenadas exatas do alerta detectado pelo satélite
- Instruções específicas para aquele tipo de conflito
- Imagem Sentinel-2 da área (para referência visual)

Na tela, isso é representado por um **card de sincronização animado** que transita de:

```
[🔄 Baixando dados da propriedade...]       ← estado SYNCING (com barra de progresso)
         ↓  (~2,8 segundos)
[✅ Cache local atualizado · 14/06 07:45]   ← estado SYNCED (resumo do que foi baixado)
```

No campo, com o modo avião ativo, o badge "✈ OFFLINE" fica visível no visor — e todos os dados já estão disponíveis localmente porque foram sincronizados antes.

#### Trava Antifraude — Geofencing de 50 Metros

O **maior risco de fraude no sistema** é o infrator tirar foto de uma área preservada qualquer de sua fazenda (longe do desmatamento real) e submeter como comprovação do ponto em conflito. Para bloquear isso, o sistema implementa uma **cerca virtual (geofence)**:

- O GPS do dispositivo é monitorado continuamente
- O botão **Capturar** fica **travado (cinza, desabilitado)** enquanto o produtor estiver a mais de 50 metros da coordenada exata do conflito detectado pelo satélite
- Um **card de geofence** sempre visível exibe a distância atual do ponto de conflito e o status da trava:

| Estado | Distância | Visual | Botão |
|--------|-----------|--------|-------|
| **Fora da área** | 847 m | 🔒 Card vermelho, distância em vermelho | Desabilitado |
| **Dentro da área** | 12 m | ✅ Card verde, anel verde no visor | Habilitado |

Quando o produtor está dentro do raio de 50 metros, o visor da câmera exibe **dois anéis concêntricos verdes** ao redor do crosshair, sinalizando visualmente que a captura está autorizada naquele ponto exato.

**Elementos da tela (após sincronização):**
- Card de sync (banner verde compacto)
- Visor da câmera com crosshair + overlay de GPS + badge offline + anel de geofence (quando dentro da área)
- Card de geofence (distância + status de trava)
- Card do conflito ativo: tipo de divergência, instrução de fotografia
- Botão **Capturar** — travado ou liberado pelo geofence → avança para a Classificação

### 6.2 Classificação da Foto

**Triagem do tipo de cobertura do solo fotografado.**

Exibe:
- Miniatura da foto capturada com timestamp e coordenadas GPS sobrepostos
- Grid 2×3 com as 6 categorias de cobertura (toque único para selecionar):
  - 🌳 Mata Nativa
  - 🌾 Pastagem
  - 🌽 Agricultura
  - 💧 Corpo Hídrico
  - 🌿 APP
  - ⚠️ Área Degradada
- Seleção destacada com cor da categoria
- **Validação formativa:** se o usuário tentar enviar sem selecionar, exibe card explicativo (não apenas erro)

**Ações:**
- **Salvar localmente** — salva o ponto para sincronização posterior (uso offline). Exibe snackbar confirmando que será sincronizado ao recuperar sinal
- **Enviar para Validação** — inicia o pipeline de 3 etapas

### 6.3 Validação em 3 Etapas

**Tela animada que mostra o processamento da submissão em tempo real.**

#### Etapa 1 — Proprietário
Confirmação imediata de que os dados foram recebidos. Exibe ✓ verde.

#### Etapa 2 — Agente de IA (Sentinel-2)
O agente executa 3 sub-passos visíveis sequencialmente:

| Sub-passo | Delay | O que acontece |
|-----------|-------|----------------|
| 1 | t = 0,6s | ✓ Imagem Sentinel-2 localizada — data da captura |
| 2 | t = 1,4s | ✓ Cobertura do solo comparada com declaração |
| 3 | t = 2,2s | ✓ Relatório de correspondência gerado |

Após os sub-passos, surge o **card de resultado da comparação**:
- 🛰 **Satélite detectou:** descrição espectral da área (NDVI, padrão de cobertura, presença hídrica)
- 📷 **Produtor declarou:** classificação escolhida na etapa anterior
- **Correspondência:** nível e percentual de confiança

| Classificação | Satélite detecta | Correspondência | Confiança |
|---------------|-----------------|-----------------|-----------|
| Mata Nativa | Vegetação nativa densa — NDVI 0.82 | Alta | 94% → Auto-aprovado |
| APP | Vegetação ripária no buffer de 30m | Alta | 91% → Auto-aprovado |
| Corpo Hídrico | Superfície hídrica com cobertura parcial | Moderada | 87% → Fila humana |
| Agricultura | Talhões regulares, exposição sazonal | Moderada | 79% → Fila humana |
| Pastagem | Vegetação baixa, solo exposto em 35% | Baixa | 65% → Fila humana |
| Área Degradada | Vegetação nativa densa — NDVI 0.75 | Divergência | 43% → Fila humana |

#### Etapa 3 — Operador Humano
- **Confiança > 90%:** "Aprovado automaticamente — sem necessidade de revisão manual"
- **Confiança ≤ 90%:** "Encaminhado ao analista ambiental (prazo: 5 dias úteis)"

**Ações pós-validação:**
- Se auto-aprovado: card de sucesso + botão **Ver Certificado** + botão **Coletar Outro Ponto**
- Se encaminhado: card com próximos passos e prazo + botão **Coletar Outro Ponto**

### 6.4 Histórico de Coletas

**Consulta de todas as submissões do produtor.**

Acessível pela aba 📋 Histórico na barra inferior.

Exibe todas as submissões ordenadas por data (mais recentes primeiro), com chips de resumo no topo:
- ✅ Total aprovados
- ⏳ Total pendentes
- ❌ Total rejeitados

Cada card de submissão contém:
- Miniatura representativa, nome da propriedade, classificação e data
- Badges de status e de confiança de IA
- **Seção de resposta:**
  - 🤖 Auto-aprovado pela IA (confiança X%)
  - ✅ Aprovado pelo operador. Dados integrados ao SICAR.
  - ❌ Rejeitado: [motivo + instrução formativa para reenvio]
  - ⏳ Aguardando análise do operador ambiental (prazo: 5 dias úteis)

### 6.5 Certificado de Colaborador Ambiental

**Acessível pela aba 🏅 Certificado ou pelo botão pós-aprovação automática.**

Card estilizado com:
- Nome do produtor e propriedade
- Estatísticas de contribuição: pontos coletados, pontos validados, APPs confirmadas
- Texto: "Este certificado pode ser anexado ao processo de regularização ambiental do imóvel no SICAR"
- **Benefícios desbloqueados:**
  - Certidão CAR liberada para análise
  - Documento válido para processos no órgão estadual
  - Elegível para linha de crédito rural verde
  - Contribuição registrada no painel nacional do CAR
- Botões: **Coletar Mais** / **Compartilhar**

---

## 7. Regras de Negócio

### Pipeline de Validação

```
Confiança de IA > 90%  →  Auto-aprovado  →  SICAR atualizado automaticamente
Confiança de IA ≤ 90%  →  Fila humana    →  Analista decide em até 5 dias úteis
```

### Roteamento por Papel (gov.br)

```
CPF de funcionário de órgão ambiental  →  Modo Analista (Dashboard)
CPF de produtor rural no SICAR         →  Modo Campo (Tela de Coleta)
```

### Sincronização Prévia (Paradoxo do Offline)

- Quando o dispositivo detecta Wi-Fi ou rede celular, o app inicia download automático em background
- Conteúdo sincronizado: mapa offline da propriedade, coordenadas do alerta, imagem Sentinel-2 de referência, instruções do conflito
- No campo (modo avião / sem sinal), todos os dados já estão disponíveis localmente
- A tela de coleta exibe o status da sincronização e a data do último cache bem-sucedido

### Geofencing Antifraude

- Raio de verificação: **50 metros** da coordenada exata do alerta detectado pelo satélite
- O botão "Capturar" fica desabilitado quando o GPS indica distância > 50 m do ponto de conflito
- O card de geofence exibe a distância em tempo real e o estado da trava (🔒 fora / ✅ dentro)
- O visor da câmera exibe anéis verdes concêntricos como confirmação visual quando dentro da área
- A verificação usa a coordenada armazenada no cache local (não requer rede no momento da coleta)

### Salvamento Offline

- O produtor pode salvar a classificação localmente sem enviar (mesmo dentro do geofence)
- Dados ficam em fila local com indicação de status de sincronização
- Snackbar confirma: "[Classificação] salvo localmente — será sincronizado ao recuperar sinal"
- Ao recuperar conectividade, o envio ocorre em background

### Validação Formativa (não punitiva)

- Tentativa de envio sem classificação → exibe card explicativo, não apenas mensagem de erro
- Submissão rejeitada pelo operador → o produtor recebe instrução específica de como corrigir a foto e reenviar
- O sistema nunca apresenta rejeição sem orientação de próximo passo

### Classificações de Cobertura do Solo

| Categoria | Ícone | Cor de referência |
|-----------|-------|-------------------|
| Mata Nativa | 🌳 | Verde escuro (#2D6A4F) |
| Pastagem | 🌾 | Dourado (#D4A017) |
| Agricultura | 🌽 | Amarelo (#E9C46A) |
| Corpo Hídrico | 💧 | Azul (#0077B6) |
| APP | 🌿 | Verde (#52B788) |
| Área Degradada | ⚠️ | Vermelho (#E63946) |

---

## 8. Arquitetura do Produto

### Stack Tecnológico

| Camada | Tecnologia |
|--------|-----------|
| Plataforma | Kotlin Multiplatform (KMP) — Android + iOS |
| UI | Compose Multiplatform 1.11.1 |
| Design System | Material 3 |
| Dados | Mockados (sem banco de dados ou conexão web) |
| Imagens de satélite | Sentinel-2 / Copernicus (mockado com imagem real) |
| Autenticação | gov.br (mockada) |
| Integração | Módulo Offline SICAR / Dataprev (mockada) |

### Estrutura de Navegação

```
Login gov.br
    ├── [Funcionário] → Dashboard
    │       ├── Mapa de Conflitos
    │       │       └── Detalhe da Validação
    │       └── Fila de Validação
    │               └── Detalhe da Validação
    └── [Produtor] → Tela de Coleta
            ├── Classificação da Foto
            │       └── Validação em 3 Etapas
            │               └── Certificado
            ├── Histórico de Coletas
            └── Certificado
```

### Dados Mockados

**Propriedades:** 10 imóveis rurais em PA, MT e MA com status variados (Atualizado, Pendente, Irregular)

**Submissões de ground-truth:** 7 submissões com distribuição deliberada de resultados:
- 3 auto-aprovadas pela IA (confiança > 90%)
- 3 pendentes de revisão humana
- 1 rejeitada pelo operador

**Alertas:** 8 conflitos geoespaciais com severidades mistas (2 críticos, 3 de atenção, 3 informativos)

---

## 9. Proposta de Valor por Stakeholder

| Stakeholder | Ganho |
|-------------|-------|
| **Produtor rural** | Regularização mais rápida, sem deslocamentos a órgãos, com incentivo tangível (Certificado + crédito verde) |
| **Analista ambiental** | Redução do volume de revisão manual em até 89% (itens auto-aprovados pela IA). Foco nos casos complexos |
| **Órgão estadual (SEMA)** | Dados de campo mais precisos e atualizados. Redução de passivo de notificações sem resposta |
| **SICAR / SFB** | Base de dados enriquecida com ground-truth verificado. Redução de autodeclarações não conferidas |
| **Sociedade** | Monitoramento ambiental mais eficaz. Rastreabilidade para mercados internacionais exigentes (EU Deforestation Regulation) |

---

## 10. Limitações do Protótipo (Hackathon)

Este protótipo é uma **prova de conceito** para o haCARthon — Desafio 2 (Melhorar o acesso a dados geoespaciais do CAR). Não implementa:

- Conexão real com APIs do SICAR ou gov.br
- Câmera real (substituída por mockup visual)
- GPS real (coordenadas fixas no mock)
- Banco de dados persistente (estado em memória)
- Integração real com Sentinel-2 / Copernicus
- Sincronização offline real
- Notificações push

Todos os dados são mockados para fins demonstrativos. A arquitetura está desenhada para substituição progressiva dos mocks por integrações reais sem alteração na lógica de negócio ou na interface.
