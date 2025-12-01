package br.com.evento.service;

import br.com.evento.dto.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bson.Document;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.AuthTokens;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EventoService {

    private final MongoTemplate mongoTemplate;
    private Driver neo4jDriver;

    // Injeção de valores do properties
    @Value("${neo4j.uri}") private String uri;
    @Value("${neo4j.username}") private String user;
    @Value("${neo4j.password}") private String password;

    public EventoService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void initNeo4j() {
        this.neo4jDriver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    // 1. Criar Evento (Mongo + Neo4j)
    public void criarEvento(EventoDTO dto) {
        // Mongo
        Document doc = new Document("evento_id", dto.id())
                .append("nome", dto.nome())
                .append("data", dto.data())
                .append("local", dto.local());
        mongoTemplate.save(doc, "Eventos");

        // Neo4j (banco padrão)
        try (Session session = neo4jDriver.session()) { //ou SessionConfig.forDatabase("sistema-eventos")
            session.run("MERGE (e:Evento {id: $id, nome: $nome})",
                    Map.of("id", dto.id(), "nome", dto.nome()));
        }
    }

    // 2. Criar Pessoa (Mongo + Neo4j)
    public void criarPessoa(PessoaDTO dto) {
        mongoTemplate.save(new Document("email", dto.email()).append("nome", dto.nome()), "Pessoas");
        try (Session session = neo4jDriver.session()) {
            session.run("MERGE (p:Pessoa {email: $email, nome: $nome})",
                    Map.of("email", dto.email(), "nome", dto.nome()));
        }
    }

    // 3. Vincular (Participante/Organizador)
    public void registrarParticipacao(String email, String eventoId, String tipo) {
        // tipo: "PARTICIPOU" ou "ORGANIZOU"
        String query = String.format(
                "MATCH (p:Pessoa {email: $email}), (e:Evento {id: $eid}) MERGE (p)-[:%s]->(e)",
                tipo.toUpperCase());

        try (Session session = neo4jDriver.session()) {
            session.run(query, Map.of("email", email, "eid", eventoId));
        }
    }

    // 4. Listar do Neo4j (Dashboard)
    public List<Map<String, Object>> listarRedeEvento(String eventoId) {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Session session = neo4jDriver.session()) {
            var result = session.run(
                "MATCH (p:Pessoa)-[r]->(e:Evento {id: $eid}) RETURN p.nome as nome, TYPE(r) as funcao",
                Map.of("eid", eventoId)
            );
            result.list().forEach(record -> lista.add(record.asMap()));
        }
        return lista;
    }

    // 5. Filtros Dinâmicos MongoDB
    public List<Document> buscarEventos(String criterio, String valor) {
        Query query = new Query();
        if ("keyword".equalsIgnoreCase(criterio)) {
            query.addCriteria(Criteria.where("nome").regex(valor, "i"));
        } else {
            // para 'local' ou 'data'
            query.addCriteria(Criteria.where(criterio.toLowerCase()).is(valor));
        }
        return mongoTemplate.find(query, Document.class, "Eventos");
    }

    // 6. Exportação SQL (Retorna String para o Controller)
    public String gerarSQL() {
        StringBuilder sql = new StringBuilder();
        List<Document> eventos = mongoTemplate.findAll(Document.class, "Eventos");
        
        sql.append("CREATE TABLE IF NOT EXISTS Eventos (id VARCHAR(50), nome VARCHAR(100));\n");
        for (Document doc : eventos) {
            sql.append(String.format("INSERT INTO Eventos VALUES ('%s', '%s');\n",
                    doc.getString("evento_id"), doc.getString("nome")));
        }
        return sql.toString();
    }
    
    // 7. Exportação JSON
    public String gerarJSON() {
        List<Document> eventos = mongoTemplate.findAll(Document.class, "Eventos");
        return new GsonBuilder().setPrettyPrinting().create().toJson(eventos);
    }
}