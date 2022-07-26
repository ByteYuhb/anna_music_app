package com.anna.lib_common_ui;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author majinxin
 * @description 通用消息对话框
 * @date 2015年8月20日
 */
public class CommonDialog extends Dialog {
  private ViewGroup contentView;
  private TextView btnComfirm;
  private TextView btnCancle;
  private TextView signalBtnConfirm;
  private TextView titleView;
  private TextView contentTextView;

  public interface DialogClickListener {
    public void onDialogClick();
  }

  public CommonDialog(Context context, String titleMsg, String contentMsg, String confirmText,
                      int dialogwidth, DialogClickListener confirmListener) {
    this(context, titleMsg, contentMsg, confirmText, null, dialogwidth, confirmListener, null);
  }

  public CommonDialog(Context context, String titleMsg, String contentMsg, String confirmText,
                      DialogClickListener confirmListener) {

    this(context, titleMsg, contentMsg, confirmText, null, dip2px(context, 320), confirmListener,
        null);
  }

  public CommonDialog(Context context, String msg, String contentMsg, String confirmText,
                      int dialogWidth, DialogClickListener confirmClick, DialogClickListener cancelClick) {
    this(context, msg, contentMsg, confirmText, null, dialogWidth, confirmClick, cancelClick);
  }

  public CommonDialog(Context context, String msg, String contentMsg, String confirmText,
                      String cancelText, int dialogWidth, DialogClickListener confirmClick) {
    this(context, msg, contentMsg, confirmText, cancelText, dialogWidth, confirmClick, null);
  }

  public CommonDialog(Context context, String msg, String contentMsg, String confirmText,
                      String cancelText, DialogClickListener confirmClick) {
    this(context, msg, contentMsg, confirmText, cancelText, dip2px(context, 320), confirmClick,
        null);
  }

  public CommonDialog(Context context, String msg, String contentMsg, String confirmText,
                      String cancelText, int dialogWidth, DialogClickListener confirmClick,
                      DialogClickListener cancelClick) {
    super(context, R.style.Base_Theme_AppCompat_Dialog);
    initDialogStyle(msg, contentMsg, confirmText, cancelText, dialogWidth, confirmClick,
        cancelClick);
  }

  private void initDialogStyle(String msg, String contentMsg, String confirmText, String cancelText,
                               int dialogWidth, final DialogClickListener confirmClick,
                               final DialogClickListener cancelClick) {
    setContentView(createDialogView(R.layout.dialog_common_layout));
    setParams(dialogWidth, LayoutParams.WRAP_CONTENT);
    LinearLayout layout1 = (LinearLayout) findChildViewById(R.id.all_layout);
    LinearLayout layout2 = (LinearLayout) findChildViewById(R.id.signal_layout);
    titleView = (TextView) findChildViewById(R.id.title_view);
    contentTextView = (TextView) findChildViewById(R.id.message);
    contentTextView.setText(contentMsg);
    contentTextView.setGravity(Gravity.CENTER);
    if (!TextUtils.isEmpty(msg)) {
      titleView.setText(msg);
    }
    if (cancelText == null) {
      layout1.setVisibility(View.GONE);
      layout2.setVisibility(View.VISIBLE);
      signalBtnConfirm = (TextView) findChildViewById(R.id.signal_confirm_btn);
      signalBtnConfirm.setText(confirmText);
      signalBtnConfirm.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          dismiss();
          confirmClick.onDialogClick();
        }
      });
    } else {
      layout1.setVisibility(View.VISIBLE);
      layout2.setVisibility(View.GONE);
      btnComfirm = (TextView) findChildViewById(R.id.confirm_btn);
      btnCancle = (TextView) findChildViewById(R.id.cancle_btn);
      btnComfirm.setText(confirmText);
      btnCancle.setText(cancelText);
      btnCancle.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          dismiss();
          if (cancelClick != null) {
            cancelClick.onDialogClick();
          }
        }
      });
      btnComfirm.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          dismiss();
          confirmClick.onDialogClick();
        }
      });
    }
  }

  public void setParams(int width, int height) {
    WindowManager.LayoutParams dialogParams = this.getWindow().getAttributes();
    dialogParams.width = width;
    dialogParams.height = height;
    this.getWindow().setAttributes(dialogParams);
  }

  private ViewGroup createDialogView(int layoutId) {
    contentView = (ViewGroup) LayoutInflater.from(getContext()).inflate(layoutId, null);
    return contentView;
  }

  public View findChildViewById(int id) {
    return contentView.findViewById(id);
  }

  public void setPositiveButtonColor(int color) {
    signalBtnConfirm.setTextColor(color);
  }

  public static int dip2px(Context context, float dpValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale);
  }

  public static int px2dip(Context context, float pxValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (pxValue / scale);
  }
}