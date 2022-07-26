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
      Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
  };

  public static final int HARDWEAR_CAMERA_CODE = 0x02;
  public static final String[] HARDWEAR_CAMERA_PERMISSION =
      new String[] { Manifest.permission.CAMERA };
}
