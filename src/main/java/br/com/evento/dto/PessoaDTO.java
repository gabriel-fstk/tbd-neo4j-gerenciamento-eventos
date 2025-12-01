package br.com.evento.dto;
// DTO para criar Pessoa (MongoDB e Neo4j)
public record PessoaDTO(
	String email, // identificador único
	String nome
) {}
