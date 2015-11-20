package com.listviewaddheader.common;

import android.os.Environment;

/**
 * ��·����صĳ���
 *
 * @author admin
 */
public class PathCommonDefines {
    public final static String SERVER_ADDRESS = "http://xianshisong.cn/service.php";
    public final static String SERVER_IMAGE_ADDRESS = "http://xianshisong.cn/service.php";

    public final static String MESSAGE_URL = "";
    // System
    public final static String API_POST_DEV_BASIC = "/common/devices/add";
    public final static String API_POST_PRODUCT_ANALYSIS = "/common/user_behaviors/add";
    public final static String APP_PREF_FILE = "sssconf";
    public static final String APP_FOLDER_ON_SD = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/TianXiang/TianXiang";

    public static final String PHOTOCACHE_FOLDER = APP_FOLDER_ON_SD
            + "/photo_cache";
    public static final String MY_FAVOURITE_FOLDER = APP_FOLDER_ON_SD
            + "/my_favourite";
    public static final String INSTALL_APK_PATH = APP_FOLDER_ON_SD
            + "/TianXiang.apk";
    /**
     * �����û�ʱ��ͷ�񻺴�
     */
    public static final String USER_AVATAR_FOLDER = APP_FOLDER_ON_SD
            + "/avatar";

    public final static String FOTOCACHE_MAPPING_FILE = "fotocache_mapping";

    public final static String API_GET_CONFIGURE = "?action=appupdate";
    public final static String API_GET_PRODUCT_CATEGORY_LIST = "?action=getProductCategoryList";
    public final static String API_GET_ADVERT_PRODUCT_LIST = "?action=getAdvertProductList";
    public final static String API_PRODUCT_LIST = "?action=getProductList";
    public final static String API_GET_PRODUCT_BY_ID = "?action=getProductById";
    public final static String API_GET_COMMENT = "?action=getcomment";
    public final static String API_GET_ENTERPRISE_ABSTRACT = "?action=getEnterpriseAbstract";
    public final static String API_GET_ABOUT = "?action=about";
    public final static String API_LOGIN = "?action=login";
    public final static String API_REGISTER = "?action=reg";
    public final static String API_GET_ADD_ORDER_FORM = "?action=postorders";
    public final static String API_EDIT_ADDRESS = "?action=editaddress";
    public final static String API_DELETE_ADDRESS = "?action=deladdress";
    public final static String API_SET_ACQUIESCE_ADDRESS = "?action=setaddress";
    public final static String API_GET_ADDRESS_LIST = "?action=getaddresslist";
    public final static String API_GET_REGION_ADDRESS_LIST = "?action=getarea";
    public final static String API_GET_PEISONG_TYPE = "?action=allPsType";
    public final static String API_GET_PAY_TYPE = "?action=getPayType";
    public final static String API_GET_ORDER_LIST = "?action=getorderlist";
    public final static String API_POST_ADVICE = "?action=postAdvice";
    public final static String API_GET_YHQ = "?action=getYhq";
    public final static String API_ADD_COMMENT = "?action=addcomment";
    public final static String API_GUEST = "?action=guest";
    public final static String API_MODIFY_PASSWORD = "?action=modify_password";
    public final static String API_CARD_PAY = "?action=cardPay";
}
