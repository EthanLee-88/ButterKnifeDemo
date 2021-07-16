package com.butterknife;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class MyButterKnife {
    private MyButterKnife() {
        throw new AssertionError("No instances.");
    }
    private static final String TAG = "ButterKnife";
    private static boolean DEBUG_MODE = true;
    @UiThread
    public static Unbinder bind(@NonNull Activity target) {
        // 利用反射生成帮助类的对象，框架唯一用到反射的地方
        View sourceView = target.getWindow().getDecorView();
        Class<?> targetClass = target.getClass();
        String clsName = targetClass.getName();
        Unbinder unbinder = Unbinder.EMPTY;
        if (DEBUG_MODE) Log.d(TAG, "clsName = " + clsName);
        try {
            // 生成类名
            Class<?> bindingClass = targetClass.getClassLoader().loadClass(clsName + "_ViewBinding");
            // 获取构造器
            Constructor<? extends Unbinder> constructor = (Constructor<? extends Unbinder>) bindingClass.getConstructor(targetClass);
            // 生成对象
            unbinder = constructor.newInstance(target);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return unbinder;
    }

    public static void setDebugMode(boolean debugMode) {
        DEBUG_MODE = debugMode;
    }
}