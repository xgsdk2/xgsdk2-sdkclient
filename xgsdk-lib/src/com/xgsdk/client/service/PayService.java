
package com.xgsdk.client.service;

import com.xgsdk.client.ProductInfo;
import com.xgsdk.client.core.http.HttpUtils;
import com.xgsdk.client.core.util.MD5Util;
import com.xgsdk.client.core.util.XGLogger;
//import com.xgsdk.client.util.ProductConfig;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.app.Activity;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class PayService {

    // 创建订单URI
    public static String PAY_NEW_ORDER_URI = "/xgsdk/apiXgsdkPay/createOrder";
    // 更新订单URI
    public static String PAY_UPDATE_ORDER_URI = "/xgsdk/apiXgsdkPay/updateOrder";
    // 取消订单URI
    public static String PAY_CANCEL_ORDER_URI = "/xgsdk/apiXgsdkPay/cancelOrder";
    // 刷新余额接口
    public static String PAY_REFRESHBALANCE_URI = "/xgsdk/apiXgsdkPay/refreshBalance";

    public static String PAY_VERIFY_ORDER_URI = "/xgsdk/apiXgsdkPay/verifyOrder";

    private static final int THREAD_JOIN_TIME_OUT = 30000;

    // 单独线程运行方式
    private static String createOrderInThread(final Activity activity,
            final String appId, final String appKey, final String channelId,
            final String uId, final String productId, final String productName,
            final String productDec, final String amount,
            final String totalPrice, final String serverId,
            final String roleId, final String roleName,
            final String currencyName, final String payExt) throws Exception {

        Callable<String> callable = new Callable<String>() {
            public String call() throws Exception {
                return PayService.createOrder(activity, appId, appKey,
                        channelId, uId, productId, productName, productDec,
                        amount, totalPrice, serverId, roleId, roleName,
                        currencyName, payExt);
            }
        };
        FutureTask<String> future = new FutureTask<String>(callable);
        Thread thread = new Thread(future);
        thread.start();
        thread.join(THREAD_JOIN_TIME_OUT);
        return future.get();
    }

    public static String createOrderInThread(final Activity activity,
            final String uId, final String productId, final String productName,
            final String productDec, final String amount,
            final String totalPrice, final String serverId,
            final String roleId, final String roleName,
            final String currencyName, final String payExt) throws Exception {
        return createOrderInThread(activity, ProductInfo.getXGAppId(activity),
                ProductInfo.getXGAppKey(activity), ProductInfo.getChannelId(),
                uId, productId, productName, productDec, amount, totalPrice,
                serverId, roleId, roleName, currencyName, payExt);
    }

    // 单独线程运行方式
    private static String createOrderInThreadForOriginal(
            final Activity activity, final String appId, final String appKey,
            final String channelId, final String uId, final String productId,
            final String productName, final String productDec,
            final String amount, final String totalPrice,
            final String serverId, final String roleId, final String roleName,
            final String currencyName, final String payExt) throws Exception {
        Callable<String> callable = new Callable<String>() {
            public String call() throws Exception {
                return PayService.createOrderForOriginal(activity, appId,
                        appKey, channelId, uId, productId, productName,
                        productDec, amount, totalPrice, serverId, roleId,
                        roleName, currencyName, payExt);
            }
        };
        FutureTask<String> future = new FutureTask<String>(callable);
        Thread thread = new Thread(future);
        thread.start();
        thread.join(THREAD_JOIN_TIME_OUT);
        orderId = future.get();
        return orderId;
    }

    public static String createOrderInThreadForOriginal(
            final Activity activity, final String uId, final String productId,
            final String productName, final String productDec,
            final String amount, final String totalPrice,
            final String serverId, final String roleId, final String roleName,
            final String currencyName, final String payExt) throws Exception {
        return createOrderInThreadForOriginal(activity,
                ProductInfo.getXGAppId(activity),
                ProductInfo.getXGAppKey(activity), ProductInfo.getChannelId(),
                uId, productId, productName, productDec, amount, totalPrice,
                serverId, roleId, roleName, currencyName, payExt);
    }

    public static String orderId = "";

    // 单独线程运行方式
    private static void updateOrderInThread(final Activity activity,
            final String appId, final String appKey, final String channelId,
            final String orderId, final String uId, final String productId,
            final String productName, final String productDec,
            final String amount, final String totalPrice,
            final String serverId, final String roleId, final String roleName,
            final String currencyName, final String payExt) throws Exception {
        PayService.orderId = orderId;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PayService.updateOrder(activity, appId, appKey, channelId,
                            orderId, uId, productId, productName, productDec,
                            amount, totalPrice, serverId, roleId, roleName,
                            currencyName, payExt);
                } catch (Exception ex) {
                    XGLogger.e(ex.getMessage(), ex);
                }
            }
        });
        thread.start();
        thread.join(THREAD_JOIN_TIME_OUT);
    }

    public static void updateOrderInThread(final Activity activity,
            final String orderId, final String uId, final String productId,
            final String productName, final String productDec,
            final String amount, final String totalPrice,
            final String serverId, final String roleId, final String roleName,
            final String currencyName, final String payExt) throws Exception {
        updateOrderInThread(activity, ProductInfo.getXGAppId(activity),
                ProductInfo.getXGAppKey(activity), ProductInfo.getChannelId(),
                orderId, uId, productId, productName, productDec, amount,
                totalPrice, serverId, roleId, roleName, currencyName, payExt);
    }

    // 单独线程运行方式
    private static void cancelOrderInThread(final Activity activity,
            final String appId, final String appKey, final String orderId)
            throws Exception {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PayService.cancelOrder(activity, appId, appKey, orderId);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    XGLogger.e(ex.getMessage(), ex);
                }
            }
        });
        thread.start();
        thread.join(THREAD_JOIN_TIME_OUT);
    }

    public static void cancelOrderInThread(final Activity activity,
            String orderId) throws Exception {
        cancelOrderInThread(activity, ProductInfo.getXGAppId(activity),
                ProductInfo.getXGAppKey(activity), orderId);
    }

    /**
     * 通知服务端生成订单
     * 
     * @param uId
     * @param productId
     * @param productName
     * @param productDec
     * @param price
     * @param amount
     * @param totalPrice
     * @param serverId
     * @param roleId
     * @param roleName
     * @param currencyName
     * @param payExt
     * @return 订单号
     * @throws Exception
     */
    private static String createOrder(final Activity activity,
            final String appId, final String appKey, final String channelId,
            final String uId, final String productId, final String productName,
            final String productDec, final String amount,
            final String totalPrice, final String serverId,
            final String roleId, final String roleName,
            final String currencyName, final String payExt) throws Exception {
        // 发送请求
        String result = createOrderForOriginal(activity, appId, appKey,
                channelId, uId, productId, productName, productDec, amount,
                totalPrice, serverId, roleId, roleName, currencyName, payExt);
        JSONObject jsonResult = new JSONObject(result);
        if (!"1".equals(jsonResult.getString("code"))) {
            throw new Exception("response exception:"
                    + jsonResult.getString("msg"));
        }
        JSONObject jsonData = jsonResult.getJSONObject("data");
        if (null == jsonData) {
            throw new Exception("response exception:"
                    + jsonResult.getString("msg"));
        }
        PayService.orderId = jsonData.getString("orderId");
        return jsonData.getString("orderId");
    }

    public static String createOrder(final Activity activity, final String uId,
            final String productId, final String productName,
            final String productDec, final String amount,
            final String totalPrice, final String serverId,
            final String roleId, final String roleName,
            final String currencyName, final String payExt) throws Exception {
        return createOrder(activity, ProductInfo.getXGAppId(activity),
                ProductInfo.getXGAppKey(activity), ProductInfo.getChannelId(),
                uId, productId, productName, productDec, amount, totalPrice,
                serverId, roleId, roleName, currencyName, payExt);
    }

    /**
     * 通知服务端生成订单
     * 
     * @param uId
     * @param productId
     * @param productName
     * @param productDec
     * @param price
     * @param amount
     * @param totalPrice
     * @param serverId
     * @param roleId
     * @param roleName
     * @param currencyName
     * @param payExt
     * @return 订单号
     * @throws Exception
     */
    private static String createOrderForOriginal(final Activity activity,
            final String appId, final String appKey, final String channelId,
            final String uId, final String productId, final String productName,
            final String productDec, final String amount,
            final String totalPrice, final String serverId,
            final String roleId, final String roleName,
            final String currencyName, final String payExt) throws Exception {
        // 排序签名
        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new BasicNameValuePair("sdkAppid", appId));
        requestParams.add(new BasicNameValuePair("channelId", channelId));
        if (!TextUtils.isEmpty(uId)) {
            requestParams.add(new BasicNameValuePair("sdkUid", uId));
        }
        requestParams.add(new BasicNameValuePair("totalPrice", totalPrice));
        requestParams.add(new BasicNameValuePair("originalPrice", totalPrice));
        if (!TextUtils.isEmpty(amount)) {
            requestParams.add(new BasicNameValuePair("appGoodsAmount", amount));
        }
        if (!TextUtils.isEmpty(productId)) {
            requestParams.add(new BasicNameValuePair("appGoodsId", productId));
        }
        if (!TextUtils.isEmpty(productName)) {
            requestParams.add(new BasicNameValuePair("appGoodsName",
                    productName));
        }
        if (!TextUtils.isEmpty(productDec)) {
            requestParams
                    .add(new BasicNameValuePair("appGoodsDesc", productDec));
        }
        if (!TextUtils.isEmpty(serverId)) {
            requestParams.add(new BasicNameValuePair("serverId", serverId));
        }
        if (!TextUtils.isEmpty(roleId)) {
            requestParams.add(new BasicNameValuePair("roleId", roleId));
        }
        if (!TextUtils.isEmpty(roleName)) {
            requestParams.add(new BasicNameValuePair("roleName", roleName));
        }
        if (!TextUtils.isEmpty(currencyName)) {
            requestParams.add(new BasicNameValuePair("currencyName",
                    currencyName));
        }
        if (!TextUtils.isEmpty(payExt)) {
            requestParams.add(new BasicNameValuePair("custom", payExt));
        }
        Collections.sort(requestParams, new Comparator<NameValuePair>() {
            @Override
            public int compare(NameValuePair lhs, NameValuePair rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
        // 生成MD5签名
        StringBuilder strSign = new StringBuilder();
        for (int i = 0; i < requestParams.size(); i++) {
            NameValuePair nvPair = requestParams.get(i);
            strSign.append(nvPair.getName()).append("=")
                    .append(nvPair.getValue());
            if (i < requestParams.size() - 1) {
                strSign.append("&");
            }
        }
        String requestContent = URLEncodedUtils.format(requestParams,
                HTTP.UTF_8);
        String sign = MD5Util.md5(strSign.toString() + appId + appKey);
        XGLogger.i("before sign:" + strSign.toString() + appId + appKey);
        XGLogger.i("after sign:" + sign);
        // 生成请求
        StringBuilder getUrl = new StringBuilder();
        getUrl.append(ProductInfo.getXGRechargeUrl(activity))
                .append(PAY_NEW_ORDER_URI).append("/").append(channelId)
                .append("/").append(appId).append("?");
        getUrl.append(requestContent);
        getUrl.append("&sign=").append(sign);
        // 发送请求
        String result = HttpUtils.executeHttpGet(getUrl.toString());
        // 返回结果为空
        if (TextUtils.isEmpty(result)) {
            // 生成订单失败
            throw new Exception("request:" + getUrl.toString()
                    + ",response is null.");
        }
        return result;
    }

    public static String createOrderForOriginal(final Activity activity,
            final String uId, final String productId, final String productName,
            final String productDec, final String amount,
            final String totalPrice, final String serverId,
            final String roleId, final String roleName,
            final String currencyName, final String payExt) throws Exception {
        return createOrderForOriginal(activity,
                ProductInfo.getXGAppId(activity),
                ProductInfo.getXGAppKey(activity), ProductInfo.getChannelId(),
                uId, productId, productName, productDec, amount, totalPrice,
                serverId, roleId, roleName, currencyName, payExt);
    }

    /**
     * 通知服务端更新订单
     * 
     * @param uId
     * @param productId
     * @param productName
     * @param productDec
     * @param price
     * @param amount
     * @param totalPrice
     * @param serverId
     * @param roleId
     * @param roleName
     * @param currencyName
     * @param payExt
     * @return
     * @throws Exception
     */
    private static void updateOrder(final Activity activity,
            final String appId, final String appKey, final String channelId,
            final String orderId, final String uId, final String productId,
            final String productName, final String productDec,
            final String amount, final String totalPrice,
            final String serverId, final String roleId, final String roleName,
            final String currencyName, final String payExt) throws Exception {
        PayService.orderId = orderId;
        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new BasicNameValuePair("orderId", orderId));
        requestParams.add(new BasicNameValuePair("sdkAppid", appId));
        requestParams.add(new BasicNameValuePair("channelId", channelId));
        if (!TextUtils.isEmpty(uId)) {
            requestParams.add(new BasicNameValuePair("sdkUid", uId));
        }
        if (!TextUtils.isEmpty(totalPrice)) {
            requestParams.add(new BasicNameValuePair("totalPrice", totalPrice));
            requestParams.add(new BasicNameValuePair("originalPrice",
                    totalPrice));
        }
        if (!TextUtils.isEmpty(amount)) {
            requestParams.add(new BasicNameValuePair("appGoodsAmount", amount));
        }
        if (!TextUtils.isEmpty(productId)) {
            requestParams.add(new BasicNameValuePair("appGoodsId", productId));
        }
        if (!TextUtils.isEmpty(productName)) {
            requestParams.add(new BasicNameValuePair("appGoodsName",
                    productName));
        }
        if (!TextUtils.isEmpty(productDec)) {
            requestParams
                    .add(new BasicNameValuePair("appGoodsDesc", productDec));
        }
        if (!TextUtils.isEmpty(serverId)) {
            requestParams.add(new BasicNameValuePair("serverId", serverId));
        }
        if (!TextUtils.isEmpty(roleId)) {
            requestParams.add(new BasicNameValuePair("roleId", roleId));
        }
        if (!TextUtils.isEmpty(roleName)) {
            requestParams.add(new BasicNameValuePair("roleName", roleName));
        }
        if (!TextUtils.isEmpty(currencyName)) {
            requestParams.add(new BasicNameValuePair("currencyName",
                    currencyName));
        }
        if (!TextUtils.isEmpty(payExt)) {
            requestParams.add(new BasicNameValuePair("custom", payExt));
        }
        Collections.sort(requestParams, new Comparator<NameValuePair>() {
            @Override
            public int compare(NameValuePair lhs, NameValuePair rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
        // 生成MD5签名
        StringBuilder strSign = new StringBuilder();
        for (int i = 0; i < requestParams.size(); i++) {
            NameValuePair nvPair = requestParams.get(i);
            strSign.append(nvPair.getName()).append("=")
                    .append(nvPair.getValue());
            if (i < requestParams.size() - 1) {
                strSign.append("&");
            }
        }
        String requestContent = URLEncodedUtils.format(requestParams,
                HTTP.UTF_8);
        String sign = MD5Util.md5(strSign.toString() + appId + appKey);
        XGLogger.d("before sign:" + strSign.toString());
        XGLogger.d("after sign:" + sign);
        // 生成请求
        StringBuilder getUrl = new StringBuilder();
        getUrl.append(ProductInfo.getXGRechargeUrl(activity))
                .append(PAY_UPDATE_ORDER_URI).append("/").append(channelId)
                .append("/").append(appId).append("?");
        getUrl.append(requestContent);
        getUrl.append("&sign=").append(sign);
        // 发送请求
        String result = HttpUtils.executeHttpGet(getUrl.toString());
        // 返回结果为空
        if (TextUtils.isEmpty(result)) {
            // 生成订单失败
            throw new Exception("request:" + getUrl.toString()
                    + ",response is null.");
        }
        JSONObject jsonResult = new JSONObject(result);
        if ("1".equals(jsonResult.getString("code"))) {
            return;
        } else {
            throw new Exception("response exception:"
                    + jsonResult.getString("msg"));
        }
    }

    public static void updateOrder(final Activity activity,
            final String orderId, final String uId, final String productId,
            final String productName, final String productDec,
            final String amount, final String totalPrice,
            final String serverId, final String roleId, final String roleName,
            final String currencyName, final String payExt) throws Exception {
        updateOrder(activity, ProductInfo.getXGAppId(activity),
                ProductInfo.getXGAppKey(activity), ProductInfo.getChannelId(),
                orderId, uId, productId, productName, productDec, amount,
                totalPrice, serverId, roleId, roleName, currencyName, payExt);
    }

    /**
     * 通知服务端取消订单
     * 
     * @param orderId
     * @return
     */
    private static void cancelOrder(Activity activity, final String appId,
            final String appKey, final String orderId) throws Exception {
        String strSign = "orderId=" + orderId;
        String sign = MD5Util.md5(strSign + appId + appKey);
        XGLogger.d("before sign:" + strSign);
        XGLogger.d("after sign:" + sign);
        // 生成请求
        StringBuilder getUrl = new StringBuilder();
        getUrl.append(ProductInfo.getXGRechargeUrl(activity))
                .append(PAY_CANCEL_ORDER_URI).append("/")
                .append(ProductInfo.getChannelId()).append("/").append(appId)
                .append("?");
        getUrl.append(strSign);
        getUrl.append("&sign=").append(sign);
        // 发送请求
        String result = HttpUtils.executeHttpGet(getUrl.toString());
        // 返回结果为空
        if (TextUtils.isEmpty(result)) {
            // 生成订单失败
            throw new Exception("request:" + getUrl.toString()
                    + ",response is null.");
        }
        JSONObject jsonResult = new JSONObject(result);
        if ("1".equals(jsonResult.getString("code"))) {
            return;
        } else {
            throw new Exception(jsonResult.getString("msg"));
        }
    }

    public static void cancelOrder(Activity activity, final String orderId)
            throws Exception {
        cancelOrder(activity, ProductInfo.getXGAppId(activity),
                ProductInfo.getXGAppKey(activity), orderId);
    }

    public static void refreshBalanceInThread(final Activity activity,
            final String appId, final String appKey, final String openid,
            final String openkey, final String pay_token, final String appid,
            final String pf, final String pfkey, final String serverId,
            final String sdkAppid, final String channelId) throws Exception {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PayService.refreshBalance(activity, appId, appKey, openid,
                            openkey, pay_token, appid, pf, pfkey, serverId,
                            sdkAppid, channelId);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    XGLogger.e(ex.getMessage(), ex);
                }
            }
        });
        thread.start();
        thread.join(THREAD_JOIN_TIME_OUT);
    }

    private static void refreshBalance(final Activity activity,
            final String appId, final String appKey, String openid,
            String openkey, String pay_token, String appid, String pf,
            String pfkey, String serverId, String sdkAppid, String channelId)
            throws Exception {
        // 排序签名
        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new BasicNameValuePair("openid", openid));
        requestParams.add(new BasicNameValuePair("openkey", openkey));
        if (!TextUtils.isEmpty(pay_token)) {
            requestParams.add(new BasicNameValuePair("pay_token", pay_token));
        }
        requestParams.add(new BasicNameValuePair("appid", appid));
        requestParams.add(new BasicNameValuePair("pf", pf));
        requestParams.add(new BasicNameValuePair("pfkey", pfkey));
        if (!TextUtils.isEmpty(serverId)) {
            requestParams.add(new BasicNameValuePair("zoneid", serverId));
        }
        requestParams.add(new BasicNameValuePair("sdkAppid", sdkAppid));
        requestParams.add(new BasicNameValuePair("channelId", channelId));
        Collections.sort(requestParams, new Comparator<NameValuePair>() {
            @Override
            public int compare(NameValuePair lhs, NameValuePair rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
        // 生成MD5签名
        StringBuilder strSign = new StringBuilder();
        for (int i = 0; i < requestParams.size(); i++) {
            NameValuePair nvPair = requestParams.get(i);
            strSign.append(nvPair.getName()).append("=")
                    .append(nvPair.getValue());
            if (i < requestParams.size() - 1) {
                strSign.append("&");
            }
        }
        String requestContent = URLEncodedUtils.format(requestParams,
                HTTP.UTF_8);
        String sign = MD5Util.md5(strSign.toString() + appId + appKey);
        XGLogger.d("before sign:" + strSign.toString());
        XGLogger.d("after sign:" + sign);
        // 生成请求
        StringBuilder getUrl = new StringBuilder();
        getUrl.append(ProductInfo.getXGRechargeUrl(activity))
                .append(PAY_REFRESHBALANCE_URI).append("/").append(channelId)
                .append("/").append(appId).append("?");
        getUrl.append(requestContent);
        getUrl.append("&sign=").append(sign);
        // 发送请求
        String result = HttpUtils.executeHttpGet(getUrl.toString());
        // 返回结果为空
        if (TextUtils.isEmpty(result)) {
            // 失败
            throw new Exception("request:" + getUrl.toString()
                    + ",response is null.");
        }
        if (!("0".equals(result))) {
            throw new Exception("refresh balance failed,request:"
                    + getUrl.toString());
        }
    }

    public static void refreshBalance(final Activity activity, String openid,
            String openkey, String pay_token, String appid, String pf,
            String pfkey, String serverId, String sdkAppid, String channelId)
            throws Exception {
        refreshBalance(activity, ProductInfo.getXGAppId(activity),
                ProductInfo.getXGAppKey(activity), openid, openkey, pay_token,
                appid, pf, pfkey, serverId, sdkAppid, channelId);
    }

    public static String verifyPay(Activity activity, String orderId) {

        if (TextUtils.isEmpty(orderId)) {
            orderId = PayService.orderId;
        }
        StringBuilder getUrl = new StringBuilder();
        // http://onsite.recharge.xgsdk.com:8180/xgsdk/apiXgsdkPay/verifyOrder/{channelId}/{sdkAppid}
        getUrl.append(ProductInfo.getXGRechargeUrl(activity))
                .append(PAY_VERIFY_ORDER_URI).append("/")
                .append(ProductInfo.getChannelId()).append("/")
                .append(ProductInfo.getXGAppId(activity)).append("?orderId=")
                .append(orderId).append("&sign=");
        String ret = "";
        try {
            ret = HttpUtils.doGetInThread(getUrl.toString());
            XGLogger.i(ret);
        } catch (Exception e) {
            XGLogger.e("verify pay error:", e);
        }
        return ret;
    }

    // 单独线程运行方式
    public static String getResponseInThread(final String url) throws Exception {

        Callable<String> callable = new Callable<String>() {
            public String call() throws Exception {
                return PayService.getResponse(url);
            }
        };
        FutureTask<String> future = new FutureTask<String>(callable);
        Thread thread = new Thread(future);
        thread.start();
        thread.join(THREAD_JOIN_TIME_OUT);
        return future.get();
    }

    public static String getResponse(String url) throws Exception {

        // 发送请求
        String result = HttpUtils.executeHttpGet(url);
        // 返回结果为空
        if (TextUtils.isEmpty(result)) {
            // 生成订单失败
            throw new Exception("request:" + url + ",response is null.");
        }

        return result;
    }
}