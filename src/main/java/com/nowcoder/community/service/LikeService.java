package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    // 点赞
    public void like(int userId, int entityType, int entityId, int entityUserId) {
        //哪个用户给哪个实体类型，实体id是什么，实体属于哪个作者，点的赞。
        redisTemplate.execute(new SessionCallback() { //保证事务性，里面包含两个更新操作
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);//被赞的人的userid
                //在这里没有再去数据库查，影响性能，直接在调用方法时传过来

                //判断用户是否已经点过赞
                //由于redis事务的特殊性，在事务开始前先查询，不在事务中查询
                boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);

                operations.multi(); //开启事务
                //事务的处理

                if (isMember) {
                    operations.opsForSet().remove(entityLikeKey, userId); //已经点过赞，即取消点赞
                    operations.opsForValue().decrement(userLikeKey); //上面取消点赞，这里数量要减一
                } else {
                    operations.opsForSet().add(entityLikeKey, userId); //没有点过赞
                    operations.opsForValue().increment(userLikeKey); //点赞，数量加一
                }

                return operations.exec(); //提交事务。执行事务
            }
        });
    }

    // 查询某实体点赞的数量
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);  //统计key对应的set集合大小，即实体的点赞数量
    }

    // 查询某人对某实体的点赞状态
    //返回值为int,更具有扩展性，能表示是否点赞、以及踩等多种状态
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;//查询该用户是否对实体点过赞
    }

    // 查询某个用户获得的赞
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count.intValue();
    }

}
