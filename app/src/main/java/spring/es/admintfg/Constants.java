package spring.es.admintfg;

public final class Constants {

    private Constants() {
    }

    public static final String IP_ADDRESS = "https://192.168.39.97/";
    //public static final String IP_ADDRESS = "http://192.168.1.220:8080/";

    public static final String PATH_ORDERS = "orders/";
    public static final String PATH_USERS = "users/";
    public static final String PATH_PRODUCTS = "products/";
    public static final String PATH_ORDER_LINES = "orderLines/";
    public static final String PATH_LOGIN = "login";
    public static final String PATH_SEARCH = "search/";
    public static final String PATH_SIGN_IN = "register/";
    public static final String PATH_EMAIL = "email/";
    public static final String PATH_TEMPORAL = "temporal/";
    public static final String PATH_IMAGES = "image/";

    public static final String PARAM_USER_ID = "?userId=";

    public static final String EURO = "€";
    public static final String EMPTY_STRING = "";

    public static final String PARAM_PAGE = "?page=";

    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_ADMIN = "isAdmin";

    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String TOKEN = "token";
    public static final String EXPIRED = "expired";

    public static final String USER_ID = "userId";
    public static final String PRODUCT_ID = "productId";
    public static final String ORDER_ID = "orderId";

    public static final String ORDER_NAME = "name";
    public static final String ORDER_PRICE_ASC = "price";
    public static final String ORDER_PRICE_DESC = "pricedesc";
    public static final String ORDER_STOCK = "stock";

    public static final String ADMIN_ROLE = "ADMIN";
    public static final String BASIC_ROLE = "BASIC";

    public static final String ORDER_STATUS_TEMPORAL = "TEMPORAL";
    public static final String ORDER_STATUS_RECEIVED = "RECEIVED";
    public static final String ORDER_STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String ORDER_STATUS_IN_DELIVERY = "IN_DELIVERY";
    public static final String ORDER_STATUS_DELIVERED = "DELIVERED";
    public static final String ORDER_STATUS_CANCELLED = "CANCELLED";

    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";

    public static final String IMAGE_TYPE = "image/*";
    public static final String IMAGE_JPG = "image/jpeg";
    public static final String IMAGE_PNG = "image/png";
}
