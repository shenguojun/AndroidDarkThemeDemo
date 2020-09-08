# Android Dark Theme in Action

## 背景

从Android10（API 29）开始，Google开始官方支持深色模式，到目前为止，我们从用户数据分析**50%**以上的用户已经使用上了Android10系统。深色模式可以节省电量、在环境亮度较暗的时候保护视力，更是夜间活跃用户的强烈需求。对深色模式的适配有利于提升用户口碑。

深色模式在安卓上可以分为以下四种场景：

* 强制深色模式
* 强制浅色模式
* 跟随系统

* 低电量深色

以下将介绍如何设置深色模式以及如何对深色模式进行适配

## 深色模式设置

### 模式获取&判断

#### 1. 判断当前是否深色模式

[Configuration.uiMode ](https://developer.android.com/reference/android/content/res/Configuration#uiMode)有三种NIGHT的模式

* [UI_MODE_NIGHT_NO ](https://developer.android.com/reference/android/content/res/Configuration#UI_MODE_NIGHT_NO)表示当前使用的是notnight模式资源
* [UI_MODE_NIGHT_YES](https://developer.android.com/reference/android/content/res/Configuration#UI_MODE_NIGHT_YES) 表示当前使用的是night模式资源
* [UI_MODE_NIGHT_UNDEFINED](https://developer.android.com/reference/android/content/res/Configuration#UI_MODE_NIGHT_UNDEFINED) 表示当前没有设置模式

```kotlin
/**
 * 获取当前是否深色模式
 *
 * @return 深色模式返回 true，否则返回false
 */
fun isNightMode(): Boolean {
  return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
    Configuration.UI_MODE_NIGHT_YES -> true
    else -> false
  }
}
```

*Tips: 对于一些从网络接口服务获取的需要对深色模式区分的色值或者图片，可以使用上述的判断来获取对应的资源。*

#### 2. 判断当前深色模式场景



### 模式设置

注意，动态设置深色模式会导致Activity重建

## 适配方案

### 主题自动适配

### Material Design

### Force Dark

## 源码分析

### 主题自动适配

### Material Design

### Force Dark

## 项目指导

### 旧项目改造

#### 1. XML中设置颜色

#### 2. XML中设置图片

#### 3. 代码动态判断切换颜色&图片

#### 4. 

### 新项目

## WebView处理

## Flutter 深色模式适配

## 参考

