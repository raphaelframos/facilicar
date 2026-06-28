package com.raar.facilicar

data class Propriedade(
    val id: String,
    val nome: String,
    val municipio: String,
    val uf: String,
    val areaHa: Double,
    val status: StatusCAR
)

enum class StatusCAR { ATUALIZADO, PENDENTE, IRREGULAR }

data class AlertaConflito(
    val id: String,
    val propriedadeId: String,
    val tipo: String,
    val severidade: Severidade,
    val descricao: String,
    val dataDetecao: String,
    val coordenadas: String
)

enum class Severidade { CRITICO, ATENCAO, INFO }

data class SubmissaoGroundTruth(
    val id: String,
    val propriedadeId: String,
    val produtor: String,
    val classificacao: ClassificacaoSolo,
    val dataEnvio: String,
    val confiancaIA: Int,
    val statusValidacao: StatusValidacao
) {
    val autoAprovadoPelaIA: Boolean get() = confiancaIA > 90
}

enum class ClassificacaoSolo(val label: String) {
    MATA_NATIVA("Mata Nativa"),
    PASTAGEM("Pastagem"),
    AGRICULTURA("Agricultura"),
    CORPO_HIDRICO("Corpo Hídrico"),
    APP("APP"),
    AREA_DEGRADADA("Área Degradada")
}

enum class StatusValidacao { PENDENTE, APROVADO, REJEITADO }

object MockData {
    val propriedades = listOf(
        Propriedade("p1", "Fazenda Boa Vista", "Santarém", "PA", 1240.5, StatusCAR.IRREGULAR),
        Propriedade("p2", "Sítio Recanto Verde", "Altamira", "PA", 87.3, StatusCAR.PENDENTE),
        Propriedade("p3", "Fazenda São João", "Sinop", "MT", 3420.0, StatusCAR.ATUALIZADO),
        Propriedade("p4", "Chácara Primavera", "Balsas", "MA", 210.8, StatusCAR.PENDENTE),
        Propriedade("p5", "Fazenda Esperança", "Canarana", "MT", 5800.0, StatusCAR.IRREGULAR),
        Propriedade("p6", "Sitio São Pedro", "Paragominas", "PA", 340.0, StatusCAR.ATUALIZADO),
        Propriedade("p7", "Fazenda Nova Aliança", "Querência", "MT", 2100.0, StatusCAR.PENDENTE),
        Propriedade("p8", "Chácara Beira Rio", "Rondonópolis", "MT", 55.0, StatusCAR.IRREGULAR),
        Propriedade("p9", "Fazenda Santa Cruz", "Imperatriz", "MA", 980.0, StatusCAR.ATUALIZADO),
        Propriedade("p10", "Sítio Caminho do Sol", "Novo Progresso", "PA", 150.0, StatusCAR.PENDENTE)
    )

    val alertas = listOf(
        AlertaConflito("a1", "p1", "Desmatamento detectado", Severidade.CRITICO, "Supressão de 23 ha de mata nativa detectada pelo Sentinel-2", "12/06/2024", "-54.7321, -2.4456"),
        AlertaConflito("a2", "p5", "Divergência de APP hídrica", Severidade.CRITICO, "Vegetação de APP do Rio Suiá-Missu removida em trecho de 800m", "10/06/2024", "-52.1234, -13.5678"),
        AlertaConflito("a3", "p2", "Sobreposição de áreas", Severidade.ATENCAO, "Perímetro do imóvel sobrepõe Unidade de Conservação municipal", "08/06/2024", "-52.2087, -3.1190"),
        AlertaConflito("a4", "p8", "Divergência de área > 15%", Severidade.ATENCAO, "Área declarada (55 ha) difere 18% da área calculada pelo satélite (65 ha)", "07/06/2024", "-54.0001, -16.4705"),
        AlertaConflito("a5", "p4", "Prazo de atualização vencendo", Severidade.INFO, "Cadastro não atualizado há 11 meses", "06/06/2024", "-46.0521, -7.5328"),
        AlertaConflito("a6", "p7", "Inconsistência de coordenadas", Severidade.ATENCAO, "Ponto de referência do imóvel coincide com leito do Rio das Mortes", "05/06/2024", "-52.7801, -12.3309"),
        AlertaConflito("a7", "p10", "Corpo hídrico não declarado", Severidade.INFO, "Igarapé identificado dentro do perímetro não mapeado como APP", "03/06/2024", "-55.3894, -7.1178"),
        AlertaConflito("a8", "p3", "Silvicultura não registrada", Severidade.INFO, "Plantio de eucalipto de ~40 ha identificado, não constante no CAR", "01/06/2024", "-55.5034, -11.8604")
    )

    // confiancaIA > 90 → auto-aprovado pela IA sem passar pelo operador humano
    val submissoes = listOf(
        SubmissaoGroundTruth("s1", "p1", "José Raimundo Silva",    ClassificacaoSolo.AREA_DEGRADADA, "13/06/2024 08:32", 43, StatusValidacao.PENDENTE),
        SubmissaoGroundTruth("s2", "p2", "Maria das Graças Ferreira", ClassificacaoSolo.CORPO_HIDRICO, "11/06/2024 14:17", 78, StatusValidacao.PENDENTE),
        SubmissaoGroundTruth("s3", "p5", "Antônio Carlos Souza",   ClassificacaoSolo.PASTAGEM,       "09/06/2024 10:55", 43, StatusValidacao.REJEITADO),
        SubmissaoGroundTruth("s4", "p6", "Cleuza Aparecida Nunes", ClassificacaoSolo.MATA_NATIVA,    "07/06/2024 16:02", 94, StatusValidacao.APROVADO),   // auto-aprovado
        SubmissaoGroundTruth("s5", "p10","Benedito Lima Farias",   ClassificacaoSolo.APP,            "04/06/2024 09:44", 65, StatusValidacao.PENDENTE),
        SubmissaoGroundTruth("s6", "p3", "Silvana Matos Costa",    ClassificacaoSolo.MATA_NATIVA,    "03/06/2024 11:20", 96, StatusValidacao.APROVADO),   // auto-aprovado
        SubmissaoGroundTruth("s7", "p9", "Francisco Torres Lima",  ClassificacaoSolo.APP,            "02/06/2024 15:05", 92, StatusValidacao.APROVADO)    // auto-aprovado
    )

    val statsNacionais = mapOf(
        "totalImoveis" to "8.247.312",
        "percentAtualizado" to "61",
        "alertasAbertos" to "34.891",
        "acuraciaIA" to "89",
        "groundTruthValidados" to "1.234.567",
        "estadosIntegrados" to "26"
    )

    val rankingEstados = listOf(
        Pair("MT", 78), Pair("PA", 62), Pair("MA", 55), Pair("GO", 81), Pair("BA", 49)
    )
}
