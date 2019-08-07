package com.whoops.blog.pojo.es;

import com.whoops.blog.pojo.Blog;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.Timestamp;

@Document(indexName = "blogs" ,type = "blogs")
@XmlRootElement
public class EsBlog implements Serializable {
    private static final long serialVersionUID = 1l;

    @Id
    private String id;
    @Field(type = FieldType.Keyword ,index = true)//keyword:关键字，不分词
    private Long blogId;
    @Field(type = FieldType.Text ,index = true ,analyzer = "ik_smart")//text ：会自动分词
    private String title;
    @Field(type = FieldType.Text ,index = true ,analyzer = "ik_smart")
    private String summary;
    @Field(type = FieldType.Text ,index = true ,analyzer = "ik_smart")//analyzer：所使用的分词器名称
    private String content;
    @Field(type = FieldType.Keyword ,index = false)
    private String username;
    @Field(type = FieldType.Keyword ,index = false)
    private String avatar;
    @Field(type = FieldType.Keyword ,index = false)
    private Timestamp createTime;
    @Field(type = FieldType.Keyword ,index = false)
    private Integer readSize = 0;
    @Field(type = FieldType.Keyword ,index = false)
    private Integer commentSize = 0;
    @Field(type = FieldType.Keyword ,index = false)
    private Integer voteSize = 0;
    @Field(type = FieldType.Text ,index = true ,analyzer = "ik_smart")
    private String tags;

    protected EsBlog(){};

    public EsBlog(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public EsBlog(Long blogId, String title, String summary, String content, String username, String avatar, Timestamp createTime, Integer readSize, Integer commentSize, Integer voteSize, String tags) {
        this.blogId = blogId;
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.username = username;
        this.avatar = avatar;
        this.createTime = createTime;
        this.readSize = readSize;
        this.commentSize = commentSize;
        this.voteSize = voteSize;
        this.tags = tags;
    }

    public EsBlog(Blog blog){
        this.blogId = blog.getId();
        this.title = blog.getTitle();
        this.summary = blog.getSummary();
        this.content = blog.getContent();
        this.username = blog.getUser().getUsername();
        this.avatar = blog.getUser().getAvatar();
        this.createTime = blog.getCreateTime();
        this.readSize = blog.getReadSize();
        this.commentSize = blog.getCommentSize();
        this.voteSize = blog.getVoteSize();
        this.tags = blog.getTags();
    }

    public void update(Blog blog){
        this.blogId = blog.getId();
        this.title = blog.getTitle();
        this.summary = blog.getSummary();
        this.content = blog.getContent();
        this.username = blog.getUser().getUsername();
        this.avatar = blog.getUser().getAvatar();
        this.createTime = blog.getCreateTime();
        this.readSize = blog.getReadSize();
        this.commentSize = blog.getCommentSize();
        this.voteSize = blog.getVoteSize();
        this.tags = blog.getTags();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getBlogId() {
        return blogId;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Integer getReadSize() {
        return readSize;
    }

    public void setReadSize(Integer readSize) {
        this.readSize = readSize;
    }

    public Integer getCommentSize() {
        return commentSize;
    }

    public void setCommentSize(Integer commentSize) {
        this.commentSize = commentSize;
    }

    public Integer getVoteSize() {
        return voteSize;
    }

    public void setVoteSize(Integer voteSize) {
        this.voteSize = voteSize;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "EsBlog{" +
                "blogId=" + blogId +
                ", title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", content='" + content + '\'' +
                ", username='" + username + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }
}





























