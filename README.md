# Android Dark Theme in Action

## 背景

从Android10（API 29）开始，Google开始官方支持深色模式，到目前为止，我们从用户数据分析**50%**以上的用户已经使用上了Android10系统。深色模式可以节省电量、在环境亮度较暗的时候保护视力，更是夜间活跃用户的强烈需求。对深色模式的适配有利于提升用户口碑。

深色模式在安卓上可以分为以下四种场景：

* 强制深色模式
* 强制浅色模式
* 跟随系统

* 低电量自动切换深色

以下将介绍如何设置深色模式以及如何对深色模式进行适配。

## 资源配置限定符

我们常见的需要设置的资源有`drawable`、`layout`、`mipmap`和`values`等，对于这些资源，我们可以用一些限定符来表示提供一些备用资源，例如`drawable-xhdpi`表示超密度屏幕使用的资源，或者`layout-land`表示横向状态使用的布局。

同样的深色模式可以使用资源的限定符`-night`来表示在深色模式中使用的资源。如下图所示：

<img src="https://raw.githubusercontent.com/shenguojun/ImageServer/master/uPic/image-20200908154537790.png" alt="image-20200908154537790" style="zoom:50%;" />

<img src="https://raw.githubusercontent.com/shenguojun/ImageServer/master/uPic/image-20200908154603960.png" alt="image-20200908154603960" style="zoom:50%;" />

使用了`-night`限定符的文件夹里面的资源我们称为`night`资源，没有使用`-night`限定符的资源我们称为`notnight`资源。

其中`drawable-night-xhdpi`可以放置对应超密度屏幕使用的深色模式的图片，`values-night`可以声明对应深色模式使用的色值和主题。

所有的资源限定符定义以及添加的顺序（例如`-night`必须在`-xhdpi`之前）可查看[应用资源概览](https://developer.android.com/guide/topics/resources/providing-resources)中的[配置限定符名称表](https://developer.android.com/guide/topics/resources/providing-resources#QualifierRules)。

## 深色模式判断&设置

### 判断当前是否深色模式

[Configuration.uiMode ](https://developer.android.com/reference/android/content/res/Configuration#uiMode)有三种NIGHT的模式

* [UI_MODE_NIGHT_NO ](https://developer.android.com/reference/android/content/res/Configuration#UI_MODE_NIGHT_NO)表示当前使用的是`notnight`模式资源
* [UI_MODE_NIGHT_YES](https://developer.android.com/reference/android/content/res/Configuration#UI_MODE_NIGHT_YES) 表示当前使用的是`night`模式资源
* [UI_MODE_NIGHT_UNDEFINED](https://developer.android.com/reference/android/content/res/Configuration#UI_MODE_NIGHT_UNDEFINED) 表示当前没有设置模式

可以通过以下的代码来判断当前是否处于深色模式：

```kotlin
/**
 * 判断当前是否深色模式
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

### 判断当前深色模式场景

通过[AppCompatDelegate.getDefaultNightMode()](https://developer.android.com/reference/androidx/appcompat/app/AppCompatDelegate#getDefaultNightMode())可以获取五种深色模式场景：

* [MODE_NIGHT_AUTO_BATTERY](https://developer.android.com/reference/androidx/appcompat/app/AppCompatDelegate#MODE_NIGHT_AUTO_BATTERY) 低电量模式自动开启深色模式
* [MODE_NIGHT_FOLLOW_SYSTEM](https://developer.android.com/reference/androidx/appcompat/app/AppCompatDelegate#MODE_NIGHT_FOLLOW_SYSTEM) 跟随系统开启和关闭深色模式（默认）
* [MODE_NIGHT_NO](https://developer.android.com/reference/androidx/appcompat/app/AppCompatDelegate#MODE_NIGHT_NO) 强制使用`notnight`资源，表示非深色模式
* [MODE_NIGHT_YES](https://developer.android.com/reference/androidx/appcompat/app/AppCompatDelegate#MODE_NIGHT_YES) 强制使用`night`资源
* [MODE_NIGHT_UNSPECIFIED](https://developer.android.com/reference/androidx/appcompat/app/AppCompatDelegate#MODE_NIGHT_UNSPECIFIED) 配合 [setLocalNightMode(int)](https://developer.android.com/reference/androidx/appcompat/app/AppCompatDelegate#setLocalNightMode(int)) 使用，表示由Activity通过[AppCompactActivity.getDelegate()](https://developer.android.com/reference/kotlin/androidx/appcompat/app/AppCompatActivity#getdelegate)来单独设置页面的深色模式，不设置全局模式

### 模式设置

通过[AppCompatDelegate.setDefaultNightMode(int)](https://developer.android.com/reference/androidx/appcompat/app/AppCompatDelegate#setDefaultNightMode(int))可以设置深色模式，源码如下：

```java
public static void setDefaultNightMode(@NightMode int mode) {
  if (DEBUG) {
    Log.d(TAG, String.format("setDefaultNightMode. New:%d, Current:%d",
                             mode, sDefaultNightMode));
  }
  switch (mode) {
    case MODE_NIGHT_NO:
    case MODE_NIGHT_YES:
    case MODE_NIGHT_FOLLOW_SYSTEM:
    case MODE_NIGHT_AUTO_TIME:
    case MODE_NIGHT_AUTO_BATTERY:
      if (sDefaultNightMode != mode) {
        sDefaultNightMode = mode;
        applyDayNightToActiveDelegates();
      }
      break;
    default:
      Log.d(TAG, "setDefaultNightMode() called with an unknown mode");
      break;
  }
}
```

从源码可以看出设置 [MODE_NIGHT_UNSPECIFIED](https://developer.android.com/reference/androidx/appcompat/app/AppCompatDelegate#MODE_NIGHT_UNSPECIFIED) 模式是不会生效的。

*Tips：注意，深色模式变化会导致Activity重建。*

## 适配方案

### 主题自动适配

将Application和Activity的主题修改为集成自`Theme.AppCompat.DayNight`或者`Theme.MaterialComponents.DayNight`，就可以对于大部分的控件得到较好的深色模式支持。我们看下DayNight主题的定义：

![image-20200908171815954](https://raw.githubusercontent.com/shenguojun/ImageServer/master/uPic/image-20200908171815954.png)

##### res/values/values.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources xmlns:ns1="urn:oasis:names:tc:xliff:document:1.2">		
    <!-- ... -->
    <style name="Theme.AppCompat.DayNight" parent="Theme.AppCompat.Light"/>
    <style name="Theme.AppCompat.DayNight.DarkActionBar" parent="Theme.AppCompat.Light.DarkActionBar"/>
    <style name="Theme.AppCompat.DayNight.Dialog" parent="Theme.AppCompat.Light.Dialog"/>
    <style name="Theme.AppCompat.DayNight.Dialog.Alert" parent="Theme.AppCompat.Light.Dialog.Alert"/>
    <style name="Theme.AppCompat.DayNight.Dialog.MinWidth" parent="Theme.AppCompat.Light.Dialog.MinWidth"/>
    <style name="Theme.AppCompat.DayNight.DialogWhenLarge" parent="Theme.AppCompat.Light.DialogWhenLarge"/>
    <style name="Theme.AppCompat.DayNight.NoActionBar" parent="Theme.AppCompat.Light.NoActionBar"/>
    <!-- ... -->
</resources>
```

##### res/values-night-v8/values-night-v8.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.AppCompat.DayNight" parent="Theme.AppCompat"/>
    <style name="Theme.AppCompat.DayNight.DarkActionBar" parent="Theme.AppCompat"/>
    <style name="Theme.AppCompat.DayNight.Dialog" parent="Theme.AppCompat.Dialog"/>
    <style name="Theme.AppCompat.DayNight.Dialog.Alert" parent="Theme.AppCompat.Dialog.Alert"/>
    <style name="Theme.AppCompat.DayNight.Dialog.MinWidth" parent="Theme.AppCompat.Dialog.MinWidth"/>
    <style name="Theme.AppCompat.DayNight.DialogWhenLarge" parent="Theme.AppCompat.DialogWhenLarge"/>
    <style name="Theme.AppCompat.DayNight.NoActionBar" parent="Theme.AppCompat.NoActionBar"/>
    <style name="ThemeOverlay.AppCompat.DayNight" parent="ThemeOverlay.AppCompat.Dark"/>
</resources>
```

![image-20200908171758720](https://raw.githubusercontent.com/shenguojun/ImageServer/master/uPic/image-20200908171758720.png)

##### res/values/values.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources xmlns:ns1="urn:oasis:names:tc:xliff:document:1.2" xmlns:ns2="http://schemas.android.com/tools">
    <!-- ... -->
    <style name="Theme.MaterialComponents.DayNight" parent="Theme.MaterialComponents.Light"/>
    <style name="Theme.MaterialComponents.DayNight.BottomSheetDialog" parent="Theme.MaterialComponents.Light.BottomSheetDialog"/>
    <style name="Theme.MaterialComponents.DayNight.Bridge" parent="Theme.MaterialComponents.Light.Bridge"/>
    <style name="Theme.MaterialComponents.DayNight.DarkActionBar" parent="Theme.MaterialComponents.Light.DarkActionBar"/>
    <style name="Theme.MaterialComponents.DayNight.DarkActionBar.Bridge" parent="Theme.MaterialComponents.Light.DarkActionBar.Bridge"/>
    <style name="Theme.MaterialComponents.DayNight.Dialog" parent="Theme.MaterialComponents.Light.Dialog"/>
    <style name="Theme.MaterialComponents.DayNight.Dialog.Alert" parent="Theme.MaterialComponents.Light.Dialog.Alert"/>
    <style name="Theme.MaterialComponents.DayNight.Dialog.Alert.Bridge" parent="Theme.MaterialComponents.Light.Dialog.Alert.Bridge"/>
    <style name="Theme.MaterialComponents.DayNight.Dialog.Bridge" parent="Theme.MaterialComponents.Light.Dialog.Bridge"/>
    <style name="Theme.MaterialComponents.DayNight.Dialog.FixedSize" parent="Theme.MaterialComponents.Light.Dialog.FixedSize"/>
    <style name="Theme.MaterialComponents.DayNight.Dialog.FixedSize.Bridge" parent="Theme.MaterialComponents.Light.Dialog.FixedSize.Bridge"/>
    <style name="Theme.MaterialComponents.DayNight.Dialog.MinWidth" parent="Theme.MaterialComponents.Light.Dialog.MinWidth"/>
    <style name="Theme.MaterialComponents.DayNight.Dialog.MinWidth.Bridge" parent="Theme.MaterialComponents.Light.Dialog.MinWidth.Bridge"/>
    <style name="Theme.MaterialComponents.DayNight.DialogWhenLarge" parent="Theme.MaterialComponents.Light.DialogWhenLarge"/>
    <style name="Theme.MaterialComponents.DayNight.NoActionBar" parent="Theme.MaterialComponents.Light.NoActionBar"/>
    <style name="Theme.MaterialComponents.DayNight.NoActionBar.Bridge" parent="Theme.MaterialComponents.Light.NoActionBar.Bridge"/>
    <!-- ... -->
</resources>
```

##### res/values-night-v8/values-night-v8.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.MaterialComponents.DayNight" parent="Theme.MaterialComponents"/>
    <style name="Theme.MaterialComponents.DayNight.BottomSheetDialog" parent="Theme.MaterialComponents.BottomSheetDialog"/>
    <style name="Theme.MaterialComponents.DayNight.Bridge" parent="Theme.MaterialComponents.Bridge"/>
    <style name="Theme.MaterialComponents.DayNight.DarkActionBar" parent="Theme.MaterialComponents"/>
    <style name="Theme.MaterialComponents.DayNight.DarkActionBar.Bridge" parent="Theme.MaterialComponents.Bridge"/>
    <style name="Theme.MaterialComponents.DayNight.Dialog" parent="Theme.MaterialComponents.Dialog"/>
    <style name="Theme.MaterialComponents.DayNight.Dialog.Alert" parent="Theme.MaterialComponents.Dialog.Alert"/>
    <style name="Theme.MaterialComponents.DayNight.Dialog.Alert.Bridge" parent="Theme.MaterialComponents.Dialog.Alert.Bridge"/>
    <style name="Theme.MaterialComponents.DayNight.Dialog.Bridge" parent="Theme.MaterialComponents.Dialog.Bridge"/>
    <style name="Theme.MaterialComponents.DayNight.Dialog.FixedSize" parent="Theme.MaterialComponents.Dialog.FixedSize"/>
    <style name="Theme.MaterialComponents.DayNight.Dialog.FixedSize.Bridge" parent="Theme.MaterialComponents.Dialog.FixedSize.Bridge"/>
    <style name="Theme.MaterialComponents.DayNight.Dialog.MinWidth" parent="Theme.MaterialComponents.Dialog.MinWidth"/>
    <style name="Theme.MaterialComponents.DayNight.Dialog.MinWidth.Bridge" parent="Theme.MaterialComponents.Dialog.MinWidth.Bridge"/>
    <style name="Theme.MaterialComponents.DayNight.DialogWhenLarge" parent="Theme.MaterialComponents.DialogWhenLarge"/>
    <style name="Theme.MaterialComponents.DayNight.NoActionBar" parent="Theme.MaterialComponents.NoActionBar"/>
    <style name="Theme.MaterialComponents.DayNight.NoActionBar.Bridge" parent="Theme.MaterialComponents.NoActionBar.Bridge"/>
    <style name="ThemeOverlay.MaterialComponents.DayNight.BottomSheetDialog" parent="ThemeOverlay.MaterialComponents.BottomSheetDialog"/>
    <style name="Widget.MaterialComponents.ActionBar.PrimarySurface" parent="Widget.MaterialComponents.ActionBar.Surface"/>
    <style name="Widget.MaterialComponents.AppBarLayout.PrimarySurface" parent="Widget.MaterialComponents.AppBarLayout.Surface"/>
    <style name="Widget.MaterialComponents.BottomAppBar.PrimarySurface" parent="Widget.MaterialComponents.BottomAppBar"/>
    <style name="Widget.MaterialComponents.BottomNavigationView.PrimarySurface" parent="Widget.MaterialComponents.BottomNavigationView"/>
    <style name="Widget.MaterialComponents.TabLayout.PrimarySurface" parent="Widget.MaterialComponents.TabLayout"/>
    <style name="Widget.MaterialComponents.Toolbar.PrimarySurface" parent="Widget.MaterialComponents.Toolbar.Surface"/>
</resources>
```

*Tips: MaterialComponents.Bridge继承自AppCompat主题，并增加了Material Components的主题属性，如果项目之前是用的AppCompat，那么使用对应的Bridge主题可以快速切换到Material Design。*

从上面的分析可以看出，DayNight就是在values以及values-night中分别定义了浅色和深色的主题。如果我们的主题直接继承DayNight主题，那么就不需要重复地声明对应的`night`主题了。

如果我们想对深色模式主题添加自定义属性，那么我们可以不继承DayNight主题，并显示地声明主题对应的`night`资源，例如

##### res/values/themes.xml

```xml
<style name="Theme.MyApp" parent="Theme.MaterialComponents.Light">
    <!-- ... -->
    <item name="android:windowLightStatusBar">true</item>
</style>
```

##### res/values-night/themes.xml

```xml
<style name="Theme.MyApp" parent="Theme.MaterialComponents">
    <!-- ... -->
    <item name="android:windowLightStatusBar">false</item>
</style>
```

### Force Dark

## 源码分析

### 主题自动适配

### Material Design

### Force Dark

## 项目指导

### 旧项目改造

#### 1. XML中设置颜色

透明度处理（脚本自动生成）

#### 2. XML中设置图片

#### 3. 代码动态判断切换颜色&图片

#### 4. 主题修改

使用brdige主题

将主题复制一份到night文件夹中，并设置forcedark

### 新项目

DayNight并对所有颜色都处理night

## WebView处理

## Flutter 深色模式适配

## 参考

