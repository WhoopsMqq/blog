package com.whoops.blog.pojo;

import com.github.rjeschke.txtmark.Processor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.List;

@Entity
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "标题不能为空")
    @Size(min = 2,max = 50)
    @Column(nullable = false,length = 50)
    private String title;

    @NotEmpty(message = "摘要不能为空")
    @Size(min = 2,max = 300)
    @Column(nullable = false)
    private String summary;

    @Lob //大对象,映射MySQL的LongText类型
    @Basic(fetch = FetchType.LAZY) //懒加载
    @NotEmpty(message = "内容不能为空")
    @Size(min = 2)
    @Column(nullable = false)
    private String content;//存储的是Markdown里的内容

    @Lob //大对象,映射MySQL的LongText类型
    @Basic(fetch = FetchType.LAZY) //懒加载
    @NotEmpty(message = "内容不能为空")
    @Size(min = 2)
    @Column(nullable = false)
    private String htmlContent;//存储的是将Markdown的内容转化成HTML里的内容

    @OneToOne(cascade = CascadeType.DETACH,fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    @org.hibernate.annotations.CreationTimestamp //由数据库自动创建时间
    private Timestamp createTime;

    @Column
    private Integer readSize = 0;//访问量

    @Column
    private Integer commentSize = 0;//评论量

    @Column
    private Integer voteSize = 0;//点赞量

    @Column(length = 100)
    private String tags;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinTable(
        name = "blog_comment", joinColumns = @JoinColumn(name = "blog_id",referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "comment_id", referencedColumnName = "id")
    )
    private List<Comment> commentList;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinTable(
        name = "blog_vote",joinColumns = @JoinColumn(name = "blog_id",referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "vote_id",referencedColumnName = "id")
    )
    private List<Vote> voteList;

    @OneToOne(cascade = CascadeType.DETACH,fetch = FetchType.LAZY)
    @JoinColumn(name = "catalog_id")
    private Catalog catalog;

    protected Blog(){}

    public Blog(@NotEmpty(message = "标题不能为空") @Size(min = 2, max = 50) String title, @NotEmpty(message = "摘要不能为空") @Size(min = 2, max = 300) String summary, @NotEmpty(message = "内容不能为空") @Size(min = 2) String content) {
        this.title = title;
        this.summary = summary;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        this.htmlContent = Processor.process(content);
    }

    public String getHtmlContent() {
        return htmlContent;
    }

//    public void setHtmlContent(String content) {
//        this.htmlContent = Processor.process(content);
//    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
        this.commentSize = this.commentList.size();
    }

    public List<Vote> getVoteList() {
        return voteList;
    }

    public void setVoteList(List<Vote> voteList) {
        this.voteList = voteList;
        this.voteSize = this.voteList.size();
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    /**
     * 添加评论
     */
    public void addComment(Comment comment){
        this.commentList.add(comment);
        this.commentSize = this.commentList.size();
    }
    /**
     * 删除评论
     */
    public void delComment(Long commentId){
        for (int i = 0 ; i < this.commentList.size() ; i++){
            if(this.commentList.get(i).getId() == commentId){
                this.commentList.remove(i);
                break;
            }
        }
        this.commentSize = this.commentList.size();
    }

    /**
     * 点赞
     */
    public boolean addVote(Vote vote){
        boolean isExist = false;
        //判断是否已经存在
        for(Vote v:voteList){
            if(v.getUser().getId() == vote.getUser().getId()){
                isExist = true;
                break;
            }
        }
        if (isExist){
            return false;
        }else{
            this.voteList.add(vote);
            this.voteSize = this.voteList.size();
            return true;
        }
    }

    /**
     * 取消点赞
     */
    public void removeVote(Long voteId){
        for (int i = 0 ; i < this.voteList.size() ; i++){
            if(this.voteList.get(i).getId() == voteId){
                this.voteList.remove(i);
                break;
            }
        }
        this.voteSize = this.voteList.size();
    }

    @Override
    public String toString() {
        return "Blog{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", content='" + content + '\'' +
                ", htmlContent='" + htmlContent + '\'' +
                ", user=" + user +
                ", createTime=" + createTime +
                ", readSize=" + readSize +
                ", commentSize=" + commentSize +
                ", voteSize=" + voteSize +
                ", tags='" + tags + '\'' +
                ", commentList=" + commentList +
                '}';
    }
}






























