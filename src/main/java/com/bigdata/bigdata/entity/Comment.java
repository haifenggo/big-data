package com.bigdata.bigdata.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 2024/5/20
 */
@Data
@Document("Comment")
public class Comment {
    private String content;
    private String videoTime;
    private String commentRealTime;
}
