package com.nowcoder.community.util;

public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";

    // 某个实体的赞  //实体可以是帖子、评论
    // like:entity:entityType:entityId -> set(userId)  //redis中key:value分别是什么
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    // 某个用户的赞
    // like:user:userId -> int
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    // 某个用户关注的实体
    // followee:userId:entityType -> zset(entityId,now)  //根据关注时间会进行排序，满足更多业务需求
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    // 某个实体拥有的粉丝
    // follower:entityType:entityId -> zset(userId,now) //按照时间进行统计
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }


    // 登录验证码
    //kaptcha:owner -> String(页面生成的验证码） 其中ticket内容是临时登录凭证
    //    //场景：一个用户一个验证码，但是当前没登陆，那就生成临时登录凭证
    public static String getKaptchaKey(String owner) { //此时用户还没有登录，owner是随机字符串，作为临时的登录凭证，很快就会过期在redis中
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    // 登录的凭证
    //ticket:ticket -> String（LoginTicket对象）
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    // 用户
    //user:userId -> String（User对象）
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }

}
