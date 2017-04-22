package com.coco3g.daishu.data;

/**
 * Created by coco3g on 17/3/6.
 */

public class DataUrl {

    public static final String BASE_URL = "http://laisai.test.coco3g.com/v1/"; // 测试域名


    /**
     * 登录
     **/
    public static final String LOGIN = BASE_URL + "member/login";
    /**
     * 注册
     **/
    public static final String REGISTER = BASE_URL + "member/register";
    /**
     * 获取手机验证码
     **/
    public static final String GET_PHONE_CODE = BASE_URL + "index/sendsms";
    /**
     * 获取个人资料
     **/
    public static final String GET_USERINFO = BASE_URL + "member/memberinfo";
    /*
    * 获取首页轮播图和比赛情况
    * */
    public static final String GET_BANNER_IMAGE = BASE_URL + "news/index";
    /*
    * 获取首页比赛列表
    * */
    public static final String GET_EVENT_LIST = BASE_URL + "game/index";
    /*
     * 获取我的信息的4个h5
     * */
    public static final String GET_MY_INFO_H5 = BASE_URL + "member/h5member";
    /*
     * 忘记密码
     * */
    public static final String FORGET_PASSWORD = BASE_URL + "index/forgetpwd";
    /*
     * 上传图片
     * */
    public static final String UPLOAD_IMAGES = BASE_URL + "member/upload";

    /*
    * 获取H5
    * */
    public static final String GET_H5 = BASE_URL + "index/h5url";
    /*
     * 获取添加的搜索到的联系人列表
     * */
    public static final String GET_SEARCH_CONTACT_LIST = BASE_URL + "member/search";
    /*
     * 获取通讯录好友列表
     * */
    public static final String GET_CONTACTS_LIST = BASE_URL + "member_fans/index";
    /*
     * 添加联系人
     * */
    public static final String ADD_FRIEND = BASE_URL + "member_fans/add";
    /*
     * 获取任务书
     * */
    public static final String GET_MISSION_BOOK = BASE_URL + "game_member/get_game_task";
    /*
     * 获取排名
     * */
    public static final String GET_RANK = BASE_URL + "game_member/rank_list";
    /*
     * 获取融云token
     * */
    public static final String GET_RONGCLOUD_TOKEN = BASE_URL + "member/get_token";
    /**
     * 同意添加好友
     **/
    public static final String AGREE_ADD_FIREND = BASE_URL + "member_fans/applypass";
    /**
     * 同意添加好友
     **/
    public static final String DEL_CONTACT = BASE_URL + "member_fans/delfans";
    /**
     * 上传比赛站点的信息
     **/
    public static final String UPLOAD_EVENT_MISSION_INFO = BASE_URL + "task/synch_data";
    /**
     * 查看是否有任务书
     **/
    public static final String CHECKOUT_IF_HAS_MISSION = BASE_URL + "game_member/check_game";


}
