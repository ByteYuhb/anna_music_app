package com.anna.lib_common_ui.base.constant;

import android.Manifest;

/**
 * @function: app常量
 */
public class Constant {

  /**
   * 权限常量相关
   */
  public static final int WRITE_READ_EXTERNAL_CODE = 0x01;
  public static final String[] WRITE_READ_EXTERNAL_PERMISSION = new String[] {
      Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
          Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,Manifest.permission.ACCESS_NOTIFICATION_POLICY
  };

  public static final int HARDWEAR_CAMERA_CODE = 0x02;
  public static final String[] HARDWEAR_CAMERA_PERMISSION =
      new String[] { Manifest.permission.CAMERA };

  /**
   * Router相关
   */
  public class Router {
    public static final String ROUTER_CAPTURE_ACTIVIYT = "/qrcode/capture_activity";
    public static final String ROUTER_MUSIC_ACTIVIYT = "/audio/music_activity";
    public static final String ROUTER_WEB_ACTIVIYT = "/webview/web_activity";
  }
}
