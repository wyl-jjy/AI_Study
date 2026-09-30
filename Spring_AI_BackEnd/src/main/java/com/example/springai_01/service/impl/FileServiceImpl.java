package com.example.springai_01.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springai_01.Entity.Po.ChatDocRelate;
import com.example.springai_01.config.MinioConfig;
import com.example.springai_01.mapper.ChatDocRelateMapper;
import com.example.springai_01.service.IFileService;
import io.minio.*;
import io.minio.errors.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements IFileService {

    //    private final VectorStore vectorStore;
    private final VectorStoreByMongo vectorStoreByMongo;

    // 会话id 与 文件名的对应关系，方便查询会话历史时重新加载文件
    private final Properties chatFiles = new Properties();

    private final MinioClient minioClient;

    private final ChatDocRelateMapper chatDocRelateMapper;

    @Value("${minio.bucket-name}")
    private String bucket;


    @Override
    public boolean saveByMinIO(String chatId, MultipartFile file) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(file.getOriginalFilename())
                            .contentType(file.getContentType())
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .build()
            );
        } catch (Exception e) {
            return false;
        }
        //保存映射关系
        boolean res = chatDocRelateMapper.insertOrUpdate(ChatDocRelate.builder()
                .chatId(chatId)
                .docName(file.getOriginalFilename())
                .build());

        writeToVectorStore(file.getResource(), chatId);
        return res;
    }

    @Override
    public Resource getByJDBC(String chatId) {
        QueryWrapper<ChatDocRelate> listQueryWrapper = new QueryWrapper<>();
        List<ChatDocRelate> chatDocRelates = chatDocRelateMapper.selectList(listQueryWrapper
                .lambda()
                .eq(ChatDocRelate::getChatId, chatId));
        List<String> fileNameList = chatDocRelates.stream()
                .map(ChatDocRelate::getDocName)
                .toList();

        List<Resource> resources = new ArrayList<>();
        try {
            for (String filename : fileNameList) {
                GetObjectResponse response = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucket)
                                .object(filename)
                                .build()
                );
                resources.add(new InputStreamResource(response){
                    @Override
                    public String getFilename() {
                        return filename;
                    }
                });
            }
        } catch (Exception e) {
            log.error("查询Minio失败");
        }
        //TODO 暂时获取一个，后续实现获取所有
        return resources.get(0);
    }

    @Override
    public boolean save(String chatId, Resource resource) {
        // 1.保存到本地磁盘
        String filename = resource.getFilename();
        File target = new File(Objects.requireNonNull(filename));
        if (!target.exists()) {
            try {
                Files.copy(resource.getInputStream(), target.toPath());
            } catch (IOException e) {
                log.error("Failed to save PDF resource.", e);
                return false;
            }
        }
        // 2.保存映射关系
        chatFiles.put(chatId, filename);
        // 3.写入向量库
        writeToVectorStore(resource, chatId);
        return true;
    }

    @Override
    public Resource getFile(String chatId) {
        return new FileSystemResource(chatFiles.getProperty(chatId));
    }

    @PostConstruct
    private void init() {
        FileSystemResource pdfResource = new FileSystemResource("chat-pdf.properties");
        if (pdfResource.exists()) {
            try {
                chatFiles.load(new BufferedReader(new InputStreamReader(pdfResource.getInputStream(), StandardCharsets.UTF_8)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
//        FileSystemResource vectorResource = new FileSystemResource("chat-pdf.json");
//        if (vectorResource.exists()) {
//            SimpleVectorStore simpleVectorStore = (SimpleVectorStore) vectorStore;
//            simpleVectorStore.load(vectorResource);
//        }
    }

    @PreDestroy
    private void persistent() {
        try {
            chatFiles.store(new FileWriter("chat-pdf.properties"), LocalDateTime.now().toString());
//            if(vectorStore != null && vectorStore instanceof SimpleVectorStore simpleVectorStore) {
//                simpleVectorStore.save(new File("chat-pdf.json"));
//            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeToVectorStore(Resource resource, String chatId) {
        // 1.创建PDF的读取器
        PagePdfDocumentReader reader = new PagePdfDocumentReader(
                resource, // 文件源
                PdfDocumentReaderConfig.builder()
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.defaults())
                        .withPagesPerDocument(1) // 每1页PDF作为一个Document
                        .build()
        );
        // 2.读取PDF文档，拆分为Document
        List<Document> documents = reader.read();
        documents.forEach(document -> document.getMetadata().put("chat_id", chatId));
        // 3.写入向量库
        vectorStoreByMongo.add(documents);
    }
}