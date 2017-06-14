package com.coco3g.daishu.data;

/**
 * Created by coco3g on 17/3/6.
 */

public class DataUrl {

    public static final String BASE_URL = "http://daishu.test.coco3g.com/v1/"; // 测试域名

    public static final String BASE_XIEYI_URL = "http://daishu.test.coco3g.com/"; // 协议(注册)

    public static final String BASE_REPAIR_URL = "http://daishu.test.coco3g.com/index/index/weixiudian/id/"; // 维修点详情拼接url


    /**
     * 登录
     **/
    public static final String LOGIN = BASE_URL + "member/login";
    /**
     * 注册
     **/
    public static final String REGISTER = BASE_URL + "member/register";
    /*
     * 注册协议wap页面
     * */
    public static final String REGISTER_XIEYI = BASE_XIEYI_URL + "index/news/protocol";
    /**
     * 获取手机验证码
     **/
    public static final String GET_PHONE_CODE = BASE_URL + "index/sendsms";
    /**
     * 获取个人资料
     **/
    public static final String GET_USERINFO = BASE_URL + "member/memberinfo";
    /*
    * 获取banner
    * */
    public static final String GET_BANNER_IMAGE = BASE_URL + "lunbo/index";
    /*
    * 获取车辆维修（店铺）
    * */
    public static final String GET_REPAIR_STORE = BASE_URL + "store/getlist";
    /*
     * 拨打电话上传经纬度
     * */
    public static final String TAKE_PHONE_UPLOAD_LATLNG = BASE_URL + "member/rescue_phone";
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
     * 维修等级列表
     * */
    public static final String GET_REPAIR_GRAGE_LIST = BASE_URL + "store/get_type";
    /*
     * 收益列表
     * */
    public static final String GET_INCOME_LIST = BASE_URL + "index/earn";
    /*
     * 获取车载用品分类
     * */
    public static final String GET_CAR_CATEGORY_LIST = BASE_URL + "goods/get_goods_cates";
    /*
     * 获取首页广告列表
     * */
    public static final String GET_HOME_GUANG_GAO_LIST = BASE_URL + "index/get_adverts";
    /*
     * 获取车载用品商品列表
     * */
    public static final String GET_CAR_GOODS_LIST = BASE_URL + "goods/index";
    /**
     * 获取商品筛选的类型
     **/
    public static final String GET_SHAI_XUAN_TYPE = BASE_URL + "goods/get_screen";
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
    /**
     * 新版本检测
     **/
    public static final String GET_NEW_VERSION = BASE_URL + "index/version";


}
