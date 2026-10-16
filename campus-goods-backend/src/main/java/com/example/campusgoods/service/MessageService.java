package com.example.campusgoods.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.campusgoods.dto.MessageDTO;
import com.example.campusgoods.entity.Goods;
import com.example.campusgoods.entity.Message;
import com.example.campusgoods.entity.User;
import com.example.campusgoods.mapper.GoodsMapper;
import com.example.campusgoods.mapper.MessageMapper;
import com.example.campusgoods.mapper.UserMapper;
import com.example.campusgoods.utils.Validators;
import com.example.campusgoods.vo.MessageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 留言业务
 * 留言模型：一条留言属于某个商品，from_user_id 是作者，to_user_id 表示"这条留言说给谁听"。
 * 商品留言板对该商品下的所有留言可见，因此卖家可以回复买家，买家也能看到卖家的回复。
 */
@Service
public class MessageService extends ServiceImpl<MessageMapper, Message> {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OperationLogService operationLogService;

    /**
     * 发送留言 / 回复
     * 接收人规则：
     * 1. 显式传入 toUserId 时，对方必须是商品发布人，或是在该商品下留言过的用户；
     * 2. 未传入时默认发给商品发布人（卖家在自家商品下留言时接收人即自己，视为公开回复）。
     */
    public void send(Long fromUserId, MessageDTO dto) {
        Validators.notNull(fromUserId, "登录状态");
        Validators.notNull(dto.getGoodsId(), "商品");
        String content = Validators.requiredText(dto.getContent(), "留言内容", 500);

        Goods goods = goodsMapper.selectById(dto.getGoodsId());
        if (goods == null) {
            throw new RuntimeException("商品不存在");
        }
        Long ownerId = goods.getUserId();
        Long toUserId = dto.getToUserId() == null ? ownerId : dto.getToUserId();

        if (!toUserId.equals(ownerId) && !hasPosted(dto.getGoodsId(), toUserId)) {
            throw new RuntimeException("只能回复商品发布人或该商品下留言过的用户");
        }
        // 普通买家给自己留言没有意义；卖家在自己商品下留言属于正常的公开回复
        if (fromUserId.equals(toUserId) && !fromUserId.equals(ownerId)) {
            throw new RuntimeException("不能给自己留言");
        }

        Message message = new Message();
        message.setGoodsId(dto.getGoodsId());
        message.setFromUserId(fromUserId);
        message.setToUserId(toUserId);
        message.setContent(content);
        message.setIsRead(0);
        message.setCreateTime(LocalDateTime.now());
        this.save(message);
        // 指定了回复对象才算"回复"，否则是在商品下新发一条留言
        operationLogService.record(OperationLogService.MODULE_MESSAGE,
                dto.getToUserId() == null ? "发布商品留言" : "回复留言", "留言", message.getId(),
                "商品#" + dto.getGoodsId() + "：" + content);
    }

    /**
     * 商品留言板：该商品下的全部留言，按时间正序（公开接口，无需登录）
     */
    public List<MessageVO> listByGoods(Long goodsId) {
        Validators.notNull(goodsId, "商品");
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new RuntimeException("商品不存在");
        }
        List<Message> messages = this.lambdaQuery()
                .eq(Message::getGoodsId, goodsId)
                .orderByAsc(Message::getCreateTime)
                .list();
        return toVOList(messages);
    }

    /**
     * 我的留言：我发出的 + 我收到的，按时间倒序。
     * 先查询再标记已读，保证本次返回的 isRead 是真实状态（否则前端永远看不到未读标记）。
     */
    public List<MessageVO> listMy(Long userId) {
        Validators.notNull(userId, "登录状态");
        List<Message> messages = this.lambdaQuery()
                .and(w -> w.eq(Message::getFromUserId, userId)
                        .or(w2 -> w2.eq(Message::getToUserId, userId)
                                .ne(Message::getFromUserId, userId)))
                .orderByDesc(Message::getCreateTime)
                .list();

        List<MessageVO> result = toVOList(messages);

        // 本次返回之后再把这批"我收到的"标记为已读
        boolean hasUnread = messages.stream().anyMatch(m -> userId.equals(m.getToUserId())
                && !userId.equals(m.getFromUserId())
                && (m.getIsRead() == null || m.getIsRead() == 0));
        if (hasUnread) {
            this.update(new LambdaUpdateWrapper<Message>()
                    .eq(Message::getToUserId, userId)
                    .ne(Message::getFromUserId, userId)
                    .eq(Message::getIsRead, 0)
                    .set(Message::getIsRead, 1));
        }
        return result;
    }

    /** 删除某商品下的全部留言（商品被删除时调用） */
    public void removeByGoods(Long goodsId) {
        this.remove(new LambdaQueryWrapper<Message>().eq(Message::getGoodsId, goodsId));
    }

    /** 判断某用户是否在该商品下留言过（用于校验回复对象） */
    private boolean hasPosted(Long goodsId, Long userId) {
        Long count = this.lambdaQuery()
                .eq(Message::getGoodsId, goodsId)
                .eq(Message::getFromUserId, userId)
                .count();
        return count != null && count > 0;
    }

    /**
     * 留言批量转 VO：一次性查出商品标题与用户昵称，避免循环查库
     */
    public List<MessageVO> toVOList(List<Message> messages) {
        List<MessageVO> result = new ArrayList<>();
        if (messages == null || messages.isEmpty()) {
            return result;
        }
        Set<Long> goodsIds = new HashSet<>();
        Set<Long> userIds = new HashSet<>();
        for (Message m : messages) {
            if (m.getGoodsId() != null) {
                goodsIds.add(m.getGoodsId());
            }
            if (m.getFromUserId() != null) {
                userIds.add(m.getFromUserId());
            }
            if (m.getToUserId() != null) {
                userIds.add(m.getToUserId());
            }
        }
        Map<Long, Goods> goodsMap = goodsIds.isEmpty() ? Collections.emptyMap()
                : goodsMapper.selectBatchIds(goodsIds).stream()
                    .collect(Collectors.toMap(Goods::getId, g -> g));
        Map<Long, User> userMap = userIds.isEmpty() ? Collections.emptyMap()
                : userMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(User::getId, u -> u));

        for (Message m : messages) {
            MessageVO vo = new MessageVO();
            vo.setId(m.getId());
            vo.setGoodsId(m.getGoodsId());
            Goods goods = goodsMap.get(m.getGoodsId());
            vo.setGoodsTitle(goods == null ? "商品已删除" : goods.getTitle());
            vo.setFromUserId(m.getFromUserId());
            vo.setFromName(displayName(userMap.get(m.getFromUserId())));
            vo.setToUserId(m.getToUserId());
            vo.setToName(displayName(userMap.get(m.getToUserId())));
            vo.setContent(m.getContent());
            vo.setIsRead(m.getIsRead() == null ? 0 : m.getIsRead());
            vo.setCreateTime(m.getCreateTime());
            result.add(vo);
        }
        return result;
    }

    /** 昵称优先，其次用户名 */
    private String displayName(User user) {
        if (user == null) {
            return "未知用户";
        }
        return (user.getNickname() != null && !user.getNickname().isEmpty())
                ? user.getNickname() : user.getUsername();
    }
}
