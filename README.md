# Android-StatusBar-Util
`Android`状态栏设置工具类
<br><br>

### 引用：
```
implementation("com.fpliu:Android-StatusBar-Util:1.0.0")
```

### 使用：
```
StatusBarUtil.setRootViewFitsSystemWindows(activity, true)
//设置状态栏透明
StatusBarUtil.setTranslucentStatus(activity)
//一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
//所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
if (!StatusBarUtil.setStatusBarDarkTheme(activity, true)) {
    //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
    //这样半透明+白=灰, 状态栏的文字能看得清
    StatusBarUtil.setStatusBarColor(activity, 0x55000000)
}
```

最好放在`override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?)`中调用，不要与`Activity`的业务代码耦合。具体可以参考`sample`模块中的`MyApp`类中的设置。
