package com.anna.lib_keepalive.service;

import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.anna.lib_keepalive.forground.ForgroundNF;
import com.anna.lib_keepalive.utils.PhoneUtils;
import com.anna.lib_keepalive.utils.Utils;

/**
 * 创建一个JobService用于提高应用优先级
 * 这种前台保活方式，目前一定需要一个明确的通知显示，
 * 这个提示最好友好点，不然系统会提示一个后台运行的通知，很容易引导用户去关闭。
 * 如果是音乐类型的，可以直接使用音乐的服务MusicPlayerService挂接通知即可做的一定程度的保活
 *
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class KeepAliveService extends JobService {
    private static final String TAG = KeepAliveService.class.getSimpleName();
    private static final int JOB_ID = 1;
    private static final String strategyKey = "key";

    private JobScheduler mJobScheduler;
    private ComponentName JOB_PG;
    private int NOTIFICATION_ID = 10;
    private ForgroundNF mForgroundNF;
    private static AliveStrategy strategy;

    /**
     * 启动策略
     */
    public enum AliveStrategy{
        NONE,
        BATTERYOPTIMIZATION,
        RESTARTACTION,
        JOB_SERVICE,
        ALL
    }

    private Handler mJobHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            Log.d(TAG, "pull alive.");
            jobFinished((JobParameters) msg.obj, true);
            return true;
        }
    });
    @Override
    public void onCreate() {
        super.onCreate();
        mJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JOB_PG = new ComponentName(getPackageName(),KeepAliveService.class.getName());
        mForgroundNF = new ForgroundNF(this);
        initStrategy();
    }

    /**外部只需要调用这句话就可以启动一个保活状态
     * @param context 启动服务的上下文
     * @param _strategy 启动保活服务的策略
     *
     *                 AliveStrategy.BATTERYOPTIMIZATION：启动电量优化保活
     *
     *                 AliveStrategy.RESTARTACTION：启动自启动白名单保活
     *
     *                 AliveStrategy.ALL:启动电量优化，启动白名单保活
     *
     *                 AliveStrategy.NONE：不启动上面两个任何一个
     *
     *                  AliveStrategy.JOB_SERVICE：只使用JobService进行保活
     *
     */
    public static void start(Context context,AliveStrategy _strategy){
        strategy = _strategy;
        Intent intent = new Intent(context,KeepAliveService.class);
//        intent.putExtra(strategyKey,strategy.ordinal());
        context.startService(intent);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG,"onStartJob");
        mJobHandler.sendMessage(Message.obtain(mJobHandler, 1, params));
        return true;
    }

    /**系统回调使用，说明触发了job条件
     * @param params
     * @return
     */
    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG,"onStopJob");
        mJobHandler.sendEmptyMessage(1);
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        JobInfo job = initJob();
        mJobScheduler.cancel(JOB_ID);
        mJobScheduler.schedule(job);
        if(strategy != AliveStrategy.NONE && strategy != AliveStrategy.JOB_SERVICE)
            startNotificationForGround();
        return super.onStartCommand(intent,flags,startId);
    }

    /**获取启动策略
     */
    private void initStrategy() {
        if(strategy == AliveStrategy.NONE || strategy == AliveStrategy.JOB_SERVICE){
            return;
        }
        if(strategy == AliveStrategy.BATTERYOPTIMIZATION){
            Utils.requestIgnoreBatteryOptimizations(this);
        }else if (strategy == AliveStrategy.RESTARTACTION){
            PhoneUtils.setReStartAction(this);
        }else {
            Utils.requestIgnoreBatteryOptimizations(this);
            PhoneUtils.setReStartAction(this);
        }
    }

    /**
     * 大于18可以使用一个取消Notification的服务
     */
    private void startNotificationForGround(){
        if(Build.VERSION.SDK_INT<18){
            mForgroundNF.startForegroundNotification();
        }else{
            mForgroundNF.startForegroundNotification();
            Intent it = new Intent(this, CancelNotifyervice.class);
            startService(it);
        }
    }

    /**初始化Job任务
     * @return
     */
    private JobInfo initJob() {
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, JOB_PG);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            builder.setMinimumLatency(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS); //执行的最小延迟时间
            builder.setOverrideDeadline(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS);  //执行的最长延时时间
            builder.setBackoffCriteria(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS,
                    JobInfo.BACKOFF_POLICY_LINEAR);//线性重试方案
        }else {
            builder.setPeriodic(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS);
        }
        builder.setPersisted(false);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
        builder.setRequiresCharging(false);
        return builder.build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mForgroundNF!=null)
            mForgroundNF.stopForegroundNotification();
    }



}
