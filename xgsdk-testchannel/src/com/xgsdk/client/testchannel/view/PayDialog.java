
package com.xgsdk.client.testchannel.view;

import com.xgsdk.client.api.XGErrorCode;
import com.xgsdk.client.api.callback.PayCallBack;
import com.xgsdk.client.api.entity.PayInfo;
import com.xgsdk.client.core.service.PayService;
import com.xgsdk.client.core.utils.XGLog;
import com.xgsdk.client.testchannel.util.CommonStr;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.Gravity;
import android.widget.TextView;

public class PayDialog {

    public PayDialog() {

    }

    public void showDialog(final Activity activity, final PayInfo payment,
            final PayCallBack payCallBack) {
        AlertDialog.Builder builder = new Builder(activity);
        TextView title = new TextView(activity);
        title.setText(CommonStr.PAY_ORDER);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setTextSize(22);
        builder.setCustomTitle(title);
        final AlertDialog payDialog = builder.create();
        OrderDetailLayout orderLayout = new OrderDetailLayout(activity, payment);
        builder.setView(orderLayout);
        builder.setPositiveButton(CommonStr.PAY_SUCCESS, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                payCallBack.onSuccess("pay success!");

                try {
                    String xgOrderId = payment.getXgOrderId();
                    PayService.testChannelNotify(activity, xgOrderId, null);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        builder.setNeutralButton(CommonStr.PAY_CANCEL, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String xgOrderId = payment.getXgOrderId();
                try {
                    PayService.cancelOrder(activity, xgOrderId, null);
                } catch (Exception e) {
                    XGLog.e("cancel order error", e);
                }
                payCallBack.onCancel("pay cancel!");
                payDialog.dismiss();
            }
        });
        builder.setNegativeButton(CommonStr.PAY_FAIL, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                payCallBack
                        .onFail(XGErrorCode.OTHER_ERROR, "sorry,pay failed!");
            }
        });
        builder.show();
    }

}
