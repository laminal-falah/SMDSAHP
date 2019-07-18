package pd.sahang.mas.palembang.smds.ahp.utils;

public final class Config {

    private static final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private static final String serverKey = "key=AAAAGQD8IE8:APA91bHMklDVLldFv4o_6jBSNmtqBv6KrsGvb8VmIfw6n_qW6XlpbWo6dIow101surmI66_VWRpMvL6A6wyeOzhIAvUqoCvZ-0Yo51sO1dSCao1g6z1Fhej9-SpYpQX-VaEZqiAdqtkq";
    private static final String contentType = "application/json";

    public static String getFcmApi() {
        return FCM_API;
    }

    public static String getServerKey() {
        return serverKey;
    }

    public static String getContentType() {
        return contentType;
    }
}
