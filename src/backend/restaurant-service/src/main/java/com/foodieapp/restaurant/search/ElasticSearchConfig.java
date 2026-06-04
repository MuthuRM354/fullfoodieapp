package com.foodieapp.restaurant.search;

import org.springframework.context.annotation.Configuration;

/**
 * Stub for Elasticsearch configuration.
 * Currently using database-level full-text search via JPA queries.
 * In production, wire in Spring Data Elasticsearch.
 */
@Configuration
public class ElasticSearchConfig {
    // No Elasticsearch client needed - using DB search
}
