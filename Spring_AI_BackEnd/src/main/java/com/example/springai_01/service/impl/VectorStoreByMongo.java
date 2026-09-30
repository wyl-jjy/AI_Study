package com.example.springai_01.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.mongodb.atlas.MongoDBAtlasVectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class VectorStoreByMongo implements VectorStore {

    private final MongoDBAtlasVectorStore mongoDBAtlasVectorStore;

    @Override
    public void add(List<Document> documents) {
        mongoDBAtlasVectorStore.add(documents);
    }


    @Override
    public List<Document> similaritySearch(SearchRequest request) {
        return mongoDBAtlasVectorStore.similaritySearch(request);
    }

    @Override
    public void delete(List<String> idList) {
        mongoDBAtlasVectorStore.delete(idList);
    }

    @Override
    public void delete(Filter.Expression filterExpression) {
        mongoDBAtlasVectorStore.delete(filterExpression);
    }
}
