package com.bigdata.bigdata.entity;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 2024/4/10
 */
@Data
@Document("Video")
public class VideoData {
    private String title;
    private String ownerName;
    private int views;
    private int comments;
    private int favorites;
    private int likes;
    private int coins;
    private int shares;
    private String pubLocation;
    private long uploadTime;
    private int duration;
    private String rcmdReason;
    private String bvid;
    private int rank;
    private String board;
}
