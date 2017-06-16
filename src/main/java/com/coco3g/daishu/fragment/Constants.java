package com.coco3g.daishu.fragment;

/**
 * Created by coco3g on 17/3/8.
 */

public class Constants {


    // 支付方式
    public static final int WEIXIN_PAY_MODE = 1; // 微信支付
    public static final int ALIPAY_PAY_MODE = 2; // 支付宝支付
    public static final int BALANCE_PAY_MODE = 3; // 余额支付
    // 密码管理配置模式
    public static final int PASSWORD_MANAGE_MODE_RESET_LOGINPWD = 101; // 重置登录密码
    public static final int PASSWORD_MANAGE_MODE_SET_PAYPWD = 102; // 设置支付密码
    public static final int PASSWORD_MANAGE_MODE_RESET_PAYPWD = 103; // 重置支付密码


    /* 支付宝相关 */
    // 商户PID
    public static final String APPID = "2017051607250524";
    // 商户收款账号
    public static final String SELLER = "xuanhongapp@163.com";
    // 商户私钥，pkcs8格式
    public static final String RSA2_PRIVATE = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCDNDroRBZwB74q2wIYhoJ9z1jXfzjpN2SAWbwi64Um4W4PBs7EplHecG0tVQRhrrq3ipOVbBLz7D/vwHYc+BU0Y150YFpMC5dC1lLoKA6jRdgVMPUOLDRoMnFgdoX/W0rSrbt12WS1s2ra+RHQ+RKOlV7ruaPF4Yc25BFG4qikhWIatEI1nIVhBDyHcwtHhPzf7eBrtn63m8EaSi7BCMbEJ1VMOalmAnRdex2DKLid+FoQHV/WMe0rcZ0oVfbB49sLDSGqy2RBs/eLmI0kBhYvoEyInS/+dJS5MWW9JB0od8bccaseFwM+UaUzZhUBRHbgXCj8ML/la2tqQRXqWkHLAgMBAAECggEATavE160/DzE2Pc6LDhPK+kmeUWxqN33oTCdTPeTpXVwEuHaSP04qlyE9MKHsxxsWRAOQXNuu2KHaM8de5MVkYDAXQZOswOn3sMSKLg9Qow183wate6dlmu2Jus5Pgxz7TX81w41flkVRk7Hw/yID/06j865hKorZbgBTzf/uT/4e2Z4tS0VC5ppJLzPfG3dRElUG+q4vaW8xGxzAstxmA4mAac71Hdl1tIFH/N2E+cP0PVKbaba/wfUVlV6OnHrgkedkzO09h/xqT9Pi2gle6cSpIY/jPrXvSQUGMUYOrAGykxW+olV3MuTgOGnM+mESTv2X6jGy/oQf4HLd+HVweQKBgQDVRBTSzfN6slmiupSLX5Wnzg9xw5Sob5OQNNH+TGbbGQNBDjrTgKWkvQgv92EaB9MqPvQbx7/LBdBgjs0HOc2lv7VEQ/n5BafJU4O9f19LX/Z03mMzw2F48YSrymKONOhNedb9rs5GZ45D/1C9Mg1dIqw6eKAIjvtfRZvECBQs3wKBgQCdfpzNj9P4WJoDgRKIb4D57zGBMZi4feyUW/+T1l46WU23MgX+zr7wg366sokjy0olCIlQieAWObyBKUZKncd7VOlFLF5kRz7ZGqTxcODudjD5BMUxqurKwgfvcJ6t6uGMZZCXHkSJmy9xFYGKsgsNJpVakJ6cewTJfQpQHQRclQKBgQCW3jTE9EEcOXNDtS5EcngSX6l5V1yg013WlCdB+JaCxG0S5pgi601C/x79n8knewWdSy+xP1ukqhZBWIQeanval4YKJvR9f8y0lcoKkkK9tcNJ5dDT25Hm1yR9ywxxgWgc9uZqS/FMyRFRdRqO3QurKU4hhxffNhcJzNpmkTiEjQKBgHPENqRPHaOGFSeQP7w0Ih0VelEZ0MWEIltnSHU19Kt1K1AGoCuNcwK91XCMfYvnCUgxfyXJQwNzSyGx4i5EzfXVE2a6V2Pkjsjs8eNbcqs2C4dvvzdkCBQkDcsuACWc6k/V9DNwlzaC8JtyEHePqdnvy4b3mO8y6fDlTahXu/YFAoGBAL7eeiu9M1sbiF0UQFGz3/LCleB0t/O+YrisNQeYB8yJoJWxmaSXp8keEPKOlw0vZ7HODb8r/R1Vt1osjqbds/bS/8pQodeaNWyVQe+czOzw7TN6innkJbjD9xxqA9luPWi07Y4I6YAmdnZNHnZZopLowHcXDBVW9pEHku+QFU79";
    // 支付宝公钥
    public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

    //
    public static String JPUSH_REGISTERID = "";

    //
    public final static int SearchByNull = 1, SearchByName = 2, SearchByPhoneNumber = 3;


    //Intent请求码和返回码
    public static final int REGISTER_REQUEST_CODE = 111;//注册的请求码
    public static final int REGISTER_SUCCESS_RETURN_CODE = 222;//注册成功后的返回码
    public static final int RESULT_SCAN = 200;      //二维码扫描有效返回


    //qq登录：App key
    public static final String QQ_APP_ID = "1106181956";
    // 微信登录
    public static final String WEIXIN_APP_ID = "wx14fa09e63bd0ccad";
    public static final String WEIXIN_APP_SECRET = "0acbbfb009ea33c5a46d2b1e8c56da20";
    // 商户号
    public static final String MCH_ID = "1480633502";
    // API密钥，在商户平台设置
    public static final String API_KEY = "5ACC061AC05C377A285E7BE27CC26871";


}