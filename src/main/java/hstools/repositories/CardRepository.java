package hstools.repositories;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import hstools.domain.entities.Card;

@Repository
public interface CardRepository extends Neo4jRepository<Card, Long> {
}