package com.fpliu.newton.ui.statusbar

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.ColorInt

import androidx.annotation.IntDef

import com.readystatesoftware.systembartint.SystemBarTintManager

object StatusBarUtil {

    const val TYPE_MIUI = 0
    const val TYPE_FLYME = 1
    //6.0
    const val TYPE_M = 3

    @IntDef(TYPE_MIUI, TYPE_FLYME, TYPE_M)
    @Retention(AnnotationRetention.SOURCE)
    internal annotation class ViewType

    /**
     * 修改状态栏颜色
     * 支持4.4以上版本
     * @param color 颜色
     */
    fun setStatusBarColor(activity: Activity, @ColorInt color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.statusBarColor = color
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //使用SystemBarTintManager,需要先将状态栏设置为透明
            setTranslucentStatus(activity)
            SystemBarTintManager(activity).run {
                //显示状态栏
                isStatusBarTintEnabled = true
                //设置状态栏颜色
                setStatusBarTintColor(color)
            }
        }
    }

    /**
     * 设置状态栏透明
     */
    @TargetApi(19)
    fun setTranslucentStatus(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
            val window = activity.window
            val decorView = window.decorView
            //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = option
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
            //导航栏颜色也可以正常设置
            //window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val window = activity.window
            val attributes = window.attributes
            val flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            attributes.flags = attributes.flags or flagTranslucentStatus
            //int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
            //attributes.flags |= flagTranslucentNavigation;
            window.attributes = attributes
        }
    }


    /**
     * 代码实现android:fitsSystemWindows
     */
    fun setRootViewFitsSystemWindows(activity: Activity, fitSystemWindows: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val winContent = activity.findViewById<ViewGroup>(android.R.id.content)
            if (winContent.childCount > 0) {
                val rootView = winContent.getChildAt(0)
                if (rootView is ViewGroup) {
                    rootView.setFitsSystemWindows(fitSystemWindows)
                }
            }
        }
    }


    /**
     * 设置状态栏深色浅色切换
     */
    fun setStatusBarDarkTheme(activity: Activity, dark: Boolean): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> setStatusBarFontIconDark(activity, TYPE_M, dark)
                OSUtils.isMiui -> setStatusBarFontIconDark(activity, TYPE_MIUI, dark)
                OSUtils.isFlyme -> setStatusBarFontIconDark(activity, TYPE_FLYME, dark)
                else -> return false
            }
            return true
        }
        return false
    }

    /**
     * 设置 状态栏深色浅色切换
     */
    fun setStatusBarFontIconDark(activity: Activity, @ViewType type: Int, dark: Boolean): Boolean = when (type) {
            TYPE_MIUI -> setMiuiUI(activity, dark)
            TYPE_FLYME -> setFlymeUI(activity, dark)
            TYPE_M -> setCommonUI(activity, dark)
            else -> setCommonUI(activity, dark)
        }

    //设置6.0 状态栏深色浅色切换
    private fun setCommonUI(activity: Activity, dark: Boolean): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decorView = activity.window.decorView
            if (decorView != null) {
                var vis = decorView.systemUiVisibility
                if (dark) {
                    vis = vis or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    vis = vis and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                }
                if (decorView.systemUiVisibility != vis) {
                    decorView.systemUiVisibility = vis
                }
                return true
            }
        }
        return false
    }

    //设置Flyme 状态栏深色浅色切换
    private fun setFlymeUI(activity: Activity, dark: Boolean): Boolean {
        try {
            val window = activity.window
            val lp = window.attributes
            val darkFlag = WindowManager.LayoutParams::class.java.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
            val meizuFlags = WindowManager.LayoutParams::class.java.getDeclaredField("meizuFlags")
            darkFlag.isAccessible = true
            meizuFlags.isAccessible = true
            val bit = darkFlag.getInt(null)
            var value = meizuFlags.getInt(lp)
            if (dark) {
                value = value or bit
            } else {
                value = value and bit.inv()
            }
            meizuFlags.setInt(lp, value)
            window.attributes = lp
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    //设置MIUI 状态栏深色浅色切换
    private fun setMiuiUI(activity: Activity, dark: Boolean): Boolean {
        try {
            val window = activity.window
            val clazz = activity.window.javaClass
            @SuppressLint("PrivateApi") val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
            val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
            val darkModeFlag = field.getInt(layoutParams)
            val extraFlagField = clazz.getDeclaredMethod("setExtraFlags", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
            extraFlagField.isAccessible = true
            if (dark) {    //状态栏亮色且黑色字体
                extraFlagField.invoke(window, darkModeFlag, darkModeFlag)
            } else {
                extraFlagField.invoke(window, 0, darkModeFlag)
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}