package br.com.evento.dto;
import java.util.List;

// DTO para criar Evento (MongoDB e Neo4j)
public record EventoDTO(
	String id, // UUID ou ObjectId
	String nome,
	String data, // ISO 8601
	String local,
	List<String> anexos // lista de URLs ou nomes de arquivos (opcional)
) {}
