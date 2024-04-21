package com.bigdata.bigdata.entity;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 *  2024/4/10
 */
@Data
@Document("VideoData")
public class VideoData {
    @CsvBindByName(column = "视频名称")
    private String videoName; // 视频名称

    @CsvBindByName(column = "up主")
    private String up; // up主的昵称或账号名

    @CsvBindByName(column = "播放量")
    private long playCount; // 播放量

    @CsvBindByName(column = "评论数")
    private long commentCount; // 评论数

    @CsvBindByName(column = "收藏数")
    private long starCount; // 收藏数

    @CsvBindByName(column = "点赞数")
    private long likeCount; // 点赞数

    @CsvBindByName(column = "投币数")
    private long coinCount; // 投币数

    @CsvBindByName(column = "分享数")
    private long shareCount; // 分享数

    @CsvBindByName(column = "发布地点")
    private String publishLocation; // 发布地点

    @CsvBindByName(column = "发布时间")
    private Long publishTime; // 发布时间

    @CsvBindByName(column = "视频时长")
    private long videoDuration; // 视频时长

    @CsvBindByName(column = "推荐理由")
    private String reason; // 推荐理由
}
