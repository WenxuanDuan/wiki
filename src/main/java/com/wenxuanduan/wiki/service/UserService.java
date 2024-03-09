package com.wenxuanduan.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wenxuanduan.wiki.domain.User;
import com.wenxuanduan.wiki.domain.UserExample;
import com.wenxuanduan.wiki.exception.BusinessException;
import com.wenxuanduan.wiki.exception.BusinessExceptionCode;
import com.wenxuanduan.wiki.mapper.UserMapper;
import com.wenxuanduan.wiki.req.UserLoginReq;
import com.wenxuanduan.wiki.req.UserQueryReq;
import com.wenxuanduan.wiki.req.UserResetPasswordReq;
import com.wenxuanduan.wiki.req.UserSaveReq;
import com.wenxuanduan.wiki.resp.UserLoginResp;
import com.wenxuanduan.wiki.resp.UserQueryResp;
import com.wenxuanduan.wiki.resp.PageResp;
import com.wenxuanduan.wiki.util.CopyUtil;
import com.wenxuanduan.wiki.util.SnowFlake;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class UserService {
    
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    @Resource
    private UserMapper userMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<UserQueryResp> list(UserQueryReq req) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        if (!ObjectUtils.isEmpty(req.getLoginName())) {
            criteria.andLoginNameEqualTo(req.getLoginName());
        }
        PageHelper.startPage(req.getPage(), req.getSize());
        List<User> userList = userMapper.selectByExample(userExample);

        PageInfo<User> pageInfo = new PageInfo<>(userList);
        LOG.info("Total columns: {}", pageInfo.getTotal());
        LOG.info("Total pages: {}", pageInfo.getPages());

//        List<UserResp> respList = new ArrayList<>();
//        for (User user : userList) {
////            UserResp userResp = new UserResp();
////            BeanUtils.copyProperties(user, userResp);
//            // copy object
//            UserResp userResp = CopyUtil.copy(user, UserResp.class);
//
//            respList.add(userResp);
//        }

        // copy list
        List<UserQueryResp> list = CopyUtil.copyList(userList, UserQueryResp.class);

        PageResp<UserQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    /**
     * Save
     */
    public void save(UserSaveReq req) {
        User user = CopyUtil.copy(req, User.class);
        if (ObjectUtils.isEmpty(req.getId())) {

            User userDB = selectByLoginName(req.getLoginName());
            if (ObjectUtils.isEmpty(userDB)) {
                // add a new user
                user.setId(snowFlake.nextId());
                userMapper.insert(user);
            }
            else {
                // the same loginName already exists
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
            }
        }
        else {
            // update previous user
            user.setLoginName(null);
            user.setPassword(null);
            userMapper.updateByPrimaryKeySelective(user);
        }
    }


    /**
     * Delete
     * @param id
     */
    public void delete(Long id) {
        userMapper.deleteByPrimaryKey(id);
    }

    public User selectByLoginName(String loginName) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andLoginNameEqualTo(loginName);
        List<User> userList = userMapper.selectByExample(userExample);
        if (CollectionUtils.isEmpty(userList)) {
            return null;
        }
        else {
            return userList.get(0);
        }
    }

    /**
     * reset password
     */
    public void resetPassword(UserResetPasswordReq req) {
        User user = CopyUtil.copy(req, User.class);
        userMapper.updateByPrimaryKeySelective(user);
    }

    /**
     * login
     */
    public UserLoginResp login(UserLoginReq req) {
        User userDb = selectByLoginName(req.getLoginName());
        if (ObjectUtils.isEmpty(userDb)) {
            // loginName doesn't exist
            LOG.info("LoginName doesn't exist, {}", req.getLoginName());
            throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
        }
        else {
            if (userDb.getPassword().equals(req.getPassword())) {
                // login successfully
                UserLoginResp userLoginResp = CopyUtil.copy(userDb, UserLoginResp.class);
                return userLoginResp;
            }
            else {
                // password incorrect
                LOG.info("Incorrect password, input password: {}, restored password: {}", req.getPassword(), userDb.getPassword());
                throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
            }
        }
    }


}
