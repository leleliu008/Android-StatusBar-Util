package com.fpliu.newton.ui.statusbar.sample

import android.os.Bundle
import com.fpliu.newton.ui.base.BaseActivity

/**
 * BaseUI使用示例
 * @author 792793182@qq.com 2018-03-28.
 */
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = "StatusBar使用示例"

        //状态栏的设置不写在这里，在MyApp的全局拦截器里
        //这里只写业务相关的代码
    }
}