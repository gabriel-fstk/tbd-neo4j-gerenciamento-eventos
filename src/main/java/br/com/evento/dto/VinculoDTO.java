package br.com.evento.dto;
// DTO para vincular participação (Neo4j)
public record VinculoDTO(
	String email, // pessoa
	String eventoId, // evento
	String tipo // "PARTICIPOU" ou "ORGANIZOU"
) {}
