package com.nowcoder.community.service;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService implements CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)  //声明式事务 读提交隔离级别 事务传播机制
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }

        // 添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));     //对标签过滤
        comment.setContent(sensitiveFilter.filter(comment.getContent()));   //对敏感词过滤
        int rows = commentMapper.insertComment(comment);         //将评论添加到数据库

        // 更新帖子评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST) {   //用到常量 实现接口
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());  //判断是否为帖子的评论 判断评论类型
            discussPostService.updateCommentCount(comment.getEntityId(), count);    //更改帖子中评论数
        }

        //这里就用到了事务，添加评论和评论数加一放在了一个事务当中处理，要么都执行，要么都不执行
        //并且这里就需要考虑事务传播行为，而事务传播行为就是为了解决业务层中方法之间互相调用的事务问题。
        //在该方法中就调用了DiscussPost类中的updateCommentCount（）方法，需要进行事务管理。
        //将两者放到一个事务不能出现评论数加一了，而没有添加评论。

        return rows;
    }


    public Comment findCommentById(int id) {
        return commentMapper.selectCommentById(id);
    }

}
