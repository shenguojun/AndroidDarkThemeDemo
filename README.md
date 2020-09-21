[toc]

![Hot Take: Dark Mode | The Ionic Blog](https://raw.githubusercontent.com/shenguojun/ImageServer/master/uPic/dark-mode-hot-take.png)

# Android Dark Theme in Action (Android深色模式实战)

## 背景

从Android10（API 29）开始，在原有的主题适配的基础上，Google开始提供了Force Dark机制，在系统底层直接对颜色和图片进行转换处理，原生支持深色模式。到目前为止，我们从用户数据分析**50%**以上的用户已经使用上了Android10系统。深色模式可以节省电量、改善弱势及强光敏感用户的可视性，并能在环境亮度较暗的时候保护视力，更是夜间活跃用户的强烈需求。对深色模式的适配有利于提升用户口碑。

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

深色模式设置可以从三个层级设置，分别是系统层、Applcation层以及Activity层。底层的设置会覆盖上层的设置，例如系统设置了深色模式，但是Application设置了浅色模式，那么应用会显示浅色主题。

<img src="https://raw.githubusercontent.com/shenguojun/ImageServer/master/uPic/1*2W_EKdaBH1NQZmIc58eQDA.png" alt="Image for post" style="zoom: 67%;" />

系统层是指系统设置中，根据不同产商的手机，可以在设置->显示中修改系统为深色模式。

Application层通过`AppCompatDelegate.setDefaultNightMode()`设置深色模式。

Activity层通过[getDelegate().setLocalNightMode()](https://developer.android.com/reference/androidx/appcompat/app/AppCompatDelegate.html#setLocalNightMode(int))设置深色模式。

当深色模式改变时，Activity会重建，如果不希望Activity重建，可以在`AndroidManifest.xml`中对对应的Activity设置`android:configChanges="uiMode"`，不过设置之后页面的颜色改变需要Activity在中通过监听`onConfigurationChanged`来动态改变。

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

### 自动适配

#### 1. 主题

将Application和Activity的主题修改为集成自`Theme.AppCompat.DayNight`或者`Theme.MaterialComponents.DayNight`，就可以对于大部分的控件得到较好的深色模式支持。我们看下DayNight主题的定义：

![image-20200908171815954](https://raw.githubusercontent.com/shenguojun/ImageServer/master/uPic/image-20200908171815954.png)

​	**res/values/values.xml**

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

​	**res/values-night-v8/values-night-v8.xml**

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

​	**res/values/values.xml**

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

​	**res/values-night-v8/values-night-v8.xml**

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

从上面的分析可以看出，DayNight就是在values以及values-night中分别定义了浅色和深色的主题。如果我们的主题直接继承DayNight主题，那么就不需要重复地声明对应的`night`主题资源了。

如果我们想对深色模式主题添加自定义属性，那么我们可以不继承DayNight主题，并显示地声明主题对应的`night`资源，例如

​	**res/values/themes.xml**

```xml
<style name="Theme.MyApp" parent="Theme.MaterialComponents.Light">
    <!-- ... -->
    <item name="android:windowLightStatusBar">true</item>
</style>
```

​	**res/values-night/themes.xml**

```xml
<style name="Theme.MyApp" parent="Theme.MaterialComponents">
    <!-- ... -->
    <item name="android:windowLightStatusBar">false</item>
</style>
```

*Tips: 若需要动态修改主题要在调用inflate之前调用，否则不会生效。*

#### 2. 色值

##### 主题自动切换颜色

除了定义不同模式使用不同的主题，我们还可以对主题设置自定义的色值。在设置主题色值之前，我们先了解一下Android主题的颜色系统。

<img src="https://raw.githubusercontent.com/shenguojun/ImageServer/master/uPic/k6WO1fd7T40A9JvSVfHqs0CPLFyTEDCecsVGxEDhOaTP0wUTPYOVVkxt60hKxBprgNoMqs8OyKqtlaQ4tDBtQJs-fTcZrpZEjxhUVQ=w1064-v0.png" alt="Diagram of Material color scheme displaying the baseline Material color theme" style="zoom: 67%;" />

* colorPrimary：主要品牌颜色，一般用于ActionBar背景
* colorPrimaryDark：默认用于顶部状态栏和底部导航栏
* colorPrimaryVariant：主要品牌颜色的可选颜色
* colorSecondary：第二品牌颜色
* colorSecondaryVariant：第二品牌颜色的可选颜色
* colorPrimarySurface：对应Light主题指向colorPrimary，Dark主题指向colorSurface
* colorOn[Primary, Secondary, Surface ...]，在Primary等这些背景的上面内容的颜色，例如ActioBar上面的文字颜色
* colorAccent：默认设置给colorControlActivated，一般是主要品牌颜色的明亮版本补充
* colorControlNormal：图标和控制项的正常状态颜色
* colorControlActivated：图标和控制项的选中颜色（例如Checked或者Switcher）
* colorControlHighlight：点击高亮效果（ripple或者selector）
* colorButtonNormal：按钮默认状态颜色
* colorSurface：cards, sheets, menus等控件的背景颜色
* colorBackground：页面的背景颜色
* colorError：展示错误的颜色
* textColorPrimary：主要文字颜色
* textColorSecondary：可选文字颜色

*Tips: 当某个属性同时可以通过 `?attr/xxx` 或者` ?android:attr/xxx`获取时，最好使用` ?attr/xxx`，因为`?android:attr/xxx`是通过系统获取，而`?attr/xxx`是通过静态库类似于AppCompat 或者 Material Design Component引入的。使用非系统版本的属性可以提高平台通用性。*

如果需要自定义主题颜色，我们可以对颜色分别定义`notnight`和`night`两份，放在`values`以及`values-night`资源文件夹中，并在自定义主题时，传入给对应的颜色属性。例如：

​	**res/values/styles.xml**

```xml
<resources>
    <style name="DayNightAppTheme" parent="Theme.MaterialComponents.DayNight.NoActionBar.Bridge">
        <item name="colorPrimary">@color/color_bg_1</item>
        <item name="colorPrimaryDark">@color/color_bg_1</item>
        <item name="colorAccent">@color/color_main_1</item>
    </style>
</resources>
```

​	**res/values/colors.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="color_main_1">#4D71FF</color>
    <color name="color_bg_1">#FFFFFF</color>
    <color name="color_text_0">#101214</color>
    <color name="color_light">#E0A62E</color>
</resources>
```

​	**res/values-night/colors.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="color_main_1">#FF584D</color>
    <color name="color_bg_1">#0B0C0D</color>
    <color name="color_text_0">#F5F7FA</color>
    <color name="color_light">#626469</color>
</resources>
```

##### 控件自动切换颜色

同样的，我们可以在布局的XML文件中直接使用定义好的颜色值，例如

```xml
<TextView 
      android:id="@+id/auto_color_text"
      android:text="自动变色文字"
      android:background="@drawable/bg_text"
      android:textColor="@color/color_text_0" />
```

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <stroke android:color="@color/color_text_0" android:width="2dp"/>
    <solid android:color="@color/color_bg_1" />
</shape>
```

这样这个文字就会自动在深色模式中展示为黑底白字，在非深色模式中展示为白底黑字。

<img src="https://raw.githubusercontent.com/shenguojun/ImageServer/master/uPic/image-20200909170254174.png" alt="image-20200909170254174" style="zoom:50%;" />

<img src="https://raw.githubusercontent.com/shenguojun/ImageServer/master/uPic/image-20200909170222408.png" alt="image-20200909170222408" style="zoom: 50%;" />

##### 动态设置颜色

如果需要代码设置颜色，如果色值已经设置过`notnight`和`night`两份，那么直接设置颜色就可以得到深色模式变色效果。

```kotlin
auto_color_text.setTextColor(ContextCompat.getColor(this, R.color.color_text_0))
```

如果色值是从服务接口获取，那么可以使用上述深色模式的判断设置。

```kotlin
auto_color_text.setTextColor(if (isNightMode()) {
  Color.parseColor(darkColorFromNetwork)
} else {
  Color.parseColor(colorFromNetwork)
})
```

#### 3. 图片&动画

##### 普通图片&Gif图片

将图片分为明亮模式和深色模式两份，分别放置在`drawable-night-xxx`以及`drawable-xxx`文件夹中，并在view中直接使用即可，当深色模式切换时，会自动使用对应深色模式的资源。如下图所示：

<img src="https://raw.githubusercontent.com/shenguojun/ImageServer/master/uPic/image-20200910101322619.png" alt="image-20200910101322619" style="zoom:50%;" />

```xml
<ImageView android:src="@drawable/round_fingerprint" />
```

##### Vector图片

在Vector资源定义时，通过指定画笔颜色来实现对深色模式的适配，例如：

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:tint="@color/color_light"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@android:color/white"
        android:pathData="M6.29,14.29L9,17v4c0,0.55 0.45,1 1,1h4c0.55,0 1,-0.45 1,-1v-4l2.71,-2.71c0.19,-0.19 0.29,-0.44 0.29,-0.71L18,10c0,-0.55 -0.45,-1 -1,-1L7,9c-0.55,0 -1,0.45 -1,1v3.59c0,0.26 0.11,0.52 0.29,0.7zM12,2c0.55,0 1,0.45 1,1v1c0,0.55 -0.45,1 -1,1s-1,-0.45 -1,-1L11,3c0,-0.55 0.45,-1 1,-1zM4.21,5.17c0.39,-0.39 1.02,-0.39 1.42,0l0.71,0.71c0.39,0.39 0.39,1.02 0,1.41 -0.39,0.39 -1.02,0.39 -1.41,0l-0.72,-0.71c-0.39,-0.39 -0.39,-1.02 0,-1.41zM17.67,5.88l0.71,-0.71c0.39,-0.39 1.02,-0.39 1.41,0 0.39,0.39 0.39,1.02 0,1.41l-0.71,0.71c-0.39,0.39 -1.02,0.39 -1.41,0 -0.39,-0.39 -0.39,-1.02 0,-1.41z" />
</vector>
```

其中`android:tint`为叠加颜色，`@color/color_light`已经分别定义好了`notnight`和`night`的色值。

##### Lottie

对于[Lottie](http://airbnb.io/lottie/#/README)动画，我们可以使用Lottie的[Dynamic Properties](http://airbnb.io/lottie/#/android?id=dynamic-properties)特性来针对深色模式进行颜色变化。例如我们有以下两个动画，左边是由颜色填充的机器人，右边是由描边生成的正在播放动画，我们可以调用`LottieAnimationView.resolveKeyPath()`方法获取动画的路径。

<img src="https://raw.githubusercontent.com/shenguojun/ImageServer/master/uPic/image-20200910152855484.png" alt="image-20200910152855484" style="zoom:67%;" />

<img src="https://raw.githubusercontent.com/shenguojun/ImageServer/master/uPic/image-20200910152923192.png" alt="image-20200910152923192" style="zoom:67%;" />

```kotlin
lottie_android_animate.addLottieOnCompositionLoadedListener {
  lottie_android_animate.resolveKeyPath(KeyPath("**")).forEach {
    Log.d(TAG, it.keysToString())
  }
  setupValueCallbacks()
}
```

对于机器小人打印的KeyPath如下：

```
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [MasterController]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [Head]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [Head, Group 3]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [Head, Group 3, Fill 1]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [Blink]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [Blink, Rectangle 2]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [Blink, Rectangle 2, Rectangle Path 1]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [Blink, Rectangle 2, Fill 1]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [Blink, Rectangle 1]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [Blink, Rectangle 1, Rectangle Path 1]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [Blink, Rectangle 1, Fill 1]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [Eyes]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [Eyes, Group 3]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [Eyes, Group 3, Fill 1]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [BeloOutlines]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [BeloOutlines, Group 1]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [BeloOutlines, Group 1, Stroke 1]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [Shirt]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [Shirt, Group 5]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [Shirt, Group 5, Fill 1]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [Body]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [Body, Group 4]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [Body, Group 4, Fill 1]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [LeftFoot]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [LeftFoot, Group 1]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [LeftFoot, Group 1, Fill 1]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [RightFoot]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [RightFoot, Group 2]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [RightFoot, Group 2, Fill 1]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [LeftArmWave]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [LeftArmWave, LeftArm]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [LeftArmWave, LeftArm, Group 6]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [LeftArmWave, LeftArm, Group 6, Fill 1]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [LeftArmWave, LeftArm, Group 5]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [LeftArmWave, LeftArm, Group 5, Fill 1]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [RightArm]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [RightArm, Group 6]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [RightArm, Group 6, Fill 1]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [RightArm, Group 5]
2020-09-10 15:30:55.762 29281-29281/com.shengj.androiddarkthemedemo D/DarkThemeDemo: [RightArm, Group 5, Fill 1]
```

我们抽取其中的某些形状来动态改变颜色，例如我们抽取左右手臂以及机器小人身上的T恤

```kotlin
private fun setupValueCallbacks() {
        // 机器人右手臂
        val rightArm = KeyPath("RightArm", "Group 6", "Fill 1")
        // 机器人左手臂
        val leftArm = KeyPath("LeftArmWave", "LeftArm", "Group 6", "Fill 1")
        // 机器人T恤
        val shirt = KeyPath("Shirt", "Group 5", "Fill 1")
        // 设置右手臂颜色
        lottie_android_animate.addValueCallback(rightArm, LottieProperty.COLOR) {
            ContextCompat.getColor(this, R.color.color_main_1)
        }
        // 设置左手臂颜色
        lottie_android_animate.addValueCallback(shirt, LottieProperty.COLOR) {
            ContextCompat.getColor(this, R.color.color_light)
        }
        // 设置T恤颜色
        lottie_android_animate.addValueCallback(leftArm, LottieProperty.COLOR) {
            ContextCompat.getColor(this, R.color.color_custom)
        }
  
        // 播放动画描边颜色
        lottie_playing_animate.addValueCallback(KeyPath("**"), LottieProperty.STROKE_COLOR) {
            ContextCompat.getColor(this, R.color.color_text_0)
        }
    }
```

由于`color_main_1`、`color_light`以及`color_custom`都已经定义过深色模式和明亮模式的色值，因此在深色模式切换时，Lottie动画的这个机器小人的左右手臂和T恤颜色会随着深色模式切换而变化。

同样的对于播放动画，我们也可以设置描边颜色，来达到深色模式切换的效果。

##### 网络获取图片

对于网络获取的图片，可以让服务接口分别给出明亮模式和深色模式两套素材，然后根据上述的深色模式判断来进行切换

```kotlin
Glide.with(this)
  .load(if(isNightMode() nightImageUrl else imageUrl))
  .into(imgView)
```

### Force Dark

看到这里可能会有人有疑问，对于大型的项目而言，里面已经hardcore了很多的颜色值，并且很多图片都没有设计成深色模式的，那做深色模式适配是不是一个不可能完成的任务呢？答案是否定的。对于大型项目而言，除了对所有的颜色和图片定义`night`资源的自动适配方法外，我们还可以对使用`Light`风格主题的页面进行进行**强制深色模式转换**。

我们可以分别对主题和View设置强制深色模式。对于主题，在`Light`主题中设置`android:forceDarkAllowed`，例如：

```xml
<style name="LightAppTheme" parent="Theme.MaterialComponents.Light.NoActionBar.Bridge">
	<!-- ... -->
  <item name="android:forceDarkAllowed">true</item>
</style>
```

对于View，设置[View.setForceDarkAllowed(boolean)](https://developer.android.com/reference/android/view/View#setForceDarkAllowed(boolean))或者xml来设置是否支持Force Dark，默认值是true。

```xml
<View
  android:layout_width="wrap_content"
  android:layout_height="wrap_content"
  android:forceDarkAllowed="false"/>
```

这里需要注意的是，Force Dark的设置有以下几个规则：

1. 要强制深色模式生效必须开启硬件加速（默认开启）
2. 主题设置的Force Dark仅对`Light`的主题有效，对非`Light`的主题不管是设置`android:forceDarkAllowed`为`true`或者设置`View.setForceDarkAllowed(true)`都是无效的。
3. 父节点设置了不支持Force Dark，那么子节点再设置支持Force Dark无效。例如主题设置了`android:forceDarkAllowed`为`false`，则View设置`View.setForceDarkAllowed(true)`无效。同样的，如果View本身设置了支持Force Dark，但是其父layout设置了不支持，那么该View不会执行Force Dark
4. 子节点设置不支持Force Dark不受父节点设置支持Force Dark影响。例如View设置了支持Force Dark，但是其子Layout设置了不支持，那么子Layout也不会执行Force Dark。

*Tips：一个比较容易记的规则就是不支持Force Dark优先，View 的 Force Dark设置一般会设置成 false，用于排除某些已经适配了深色模式的 View。*
下面我们从源码出发来理解Force Dark的这些行为，以及看看系统是怎么实现Force Dark的。

*Tips：善用 https://cs.android.com/ 源码搜索网站可以方便查看系统源码。*

#### 1. 主题

从主题设置的`forceDarkAllowed`入手查找，可以找到

  **[frameworks/base/core/java/android/view/ViewRootImpl.java](https://cs.android.com/android/platform/superproject/+/master:frameworks/base/core/java/android/view/ViewRootImpl.java?q=forceDarkAllowed&ss=android%2Fplatform%2Fsuperproject)**

```java
private void updateForceDarkMode() {
  if (mAttachInfo.mThreadedRenderer == null) return;
  // 判断当前是否深色模式
  boolean useAutoDark = getNightMode() == Configuration.UI_MODE_NIGHT_YES;
  // 如果当前是深色模式
  if (useAutoDark) {
    // 获取Force Dark的系统默认值
    boolean forceDarkAllowedDefault =
      SystemProperties.getBoolean(ThreadedRenderer.DEBUG_FORCE_DARK, false);
    TypedArray a = mContext.obtainStyledAttributes(R.styleable.Theme);
    // 判断主题是否浅色主题 并且 判断主题设置的forceDarkAllowed
    useAutoDark = a.getBoolean(R.styleable.Theme_isLightTheme, true)
      && a.getBoolean(R.styleable.Theme_forceDarkAllowed, forceDarkAllowedDefault);
    a.recycle();
  }
  // 将是否强制使用深色模式赋值给Renderer层
  if (mAttachInfo.mThreadedRenderer.setForceDark(useAutoDark)) {
    // TODO: Don't require regenerating all display lists to apply this setting
    invalidateWorld(mView);
  }
}
```

而这个方法正式在`ViewRootImpl.enableHardwareAcceleration()`方法中调用的，因此可以得到第一个结论：**强制深色模式只在硬件加速下才能生效**。直到这一步的调用链如下：

```mermaid
sequenceDiagram
    ActivityThread->>ActivityThread: handleResumeActivity()
    ActivityThread->>WindowManagerGlobal:addView()
    WindowManagerGlobal->>ViewRootImpl:setView()
    ViewRootImpl->>ViewRootImpl: enableHardwareAcceleration()
    ViewRootImpl->>ViewRootImpl: updateForceDarkMode()
```

`mAttachInfo.mThreadedRenderer`为`ThreadRenderer`，继承自`HardwareRenderer`，指定了接下来的渲染操作由RanderThread执行。继续跟踪`setForceDark()`方法：

  **[frameworks/base/graphics/java/android/graphics/HardwareRenderer.java](https://cs.android.com/android/platform/superproject/+/master:frameworks/base/graphics/java/android/graphics/HardwareRenderer.java;drc=master;l=523)**

```java
public boolean setForceDark(boolean enable) {
  // 如果强制深色模式变化
  if (mForceDark != enable) {
    mForceDark = enable;
    // 调用native层设置强制深色模式逻辑
    nSetForceDark(mNativeProxy, enable);
    return true;
  }
  return false;
}

private static native void nSetForceDark(long nativeProxy, boolean enabled);
```

查找`nSetForceDark()`方法

  **[frameworks/base/libs/hwui/jni/android_graphics_HardwareRenderer.cpp](https://cs.android.com/android/platform/superproject/+/master:frameworks/base/libs/hwui/jni/android_graphics_HardwareRenderer.cpp?q=nSetForceDark&ss=android%2Fplatform%2Fsuperproject)**

```cpp
static const JNINativeMethod gMethods[] = {
		// ... 
    // 在Android Runtime启动时，通过JNI动态注册
    { "nSetForceDark", "(JZ)V", (void*)android_view_ThreadedRenderer_setForceDark },
    { "preload", "()V", (void*)android_view_ThreadedRenderer_preload },
};
```

查找`android_view_ThreadedRenderer_setForceDark()`方法

  **[frameworks/base/libs/hwui/jni/android_graphics_HardwareRenderer.cpp](https://cs.android.com/android/platform/superproject/+/master:frameworks/base/libs/hwui/jni/android_graphics_HardwareRenderer.cpp;l=586;bpv=1;bpt=1?q=android_view_ThreadedRenderer_setForceDark&sq=&ss=android%2Fplatform%2Fsuperproject)**

```cpp
static void android_view_ThreadedRenderer_setForceDark(JNIEnv* env, jobject clazz,
        jlong proxyPtr, jboolean enable) {
    RenderProxy* proxy = reinterpret_cast<RenderProxy*>(proxyPtr);
    // 调用RenderProxy的setForceDark方法
    proxy->setForceDark(enable);
}
```

  **[frameworks/base/libs/hwui/renderthread/RenderProxy.cpp](https://cs.android.com/android/platform/superproject/+/master:frameworks/base/libs/hwui/renderthread/RenderProxy.cpp;drc=master;bpv=1;bpt=1;l=308)**

```cpp
void RenderProxy::setForceDark(bool enable) {
		// 调用CanvasContext的setForceDark方法
    mRenderThread.queue().post([this, enable]() { mContext->setForceDark(enable); });
}
```

  **[frameworks/base/libs/hwui/renderthread/CanvasContext.h](https://cs.android.com/android/platform/superproject/+/master:frameworks/base/libs/hwui/renderthread/CanvasContext.h;drc=master;bpv=1;bpt=1;l=189)**

```cpp
// Force Dark的默认值是false
bool mUseForceDark = false;
// 设置mUseForceDark标志
void setForceDark(bool enable) { mUseForceDark = enable; }
bool useForceDark() {
  return mUseForceDark;
}
```

接着查找调用`userForceDark()`方法的地方

  **[frameworks/base/libs/hwui/TreeInfo.cpp](https://cs.android.com/android/platform/superproject/+/master:frameworks/base/libs/hwui/TreeInfo.cpp;l=27;drc=master;bpv=0;bpt=1)**

```cpp
TreeInfo::TreeInfo(TraversalMode mode, renderthread::CanvasContext& canvasContext)
        : mode(mode)
        , prepareTextures(mode == MODE_FULL)
        , canvasContext(canvasContext)
        // 设置disableForceDark变量
        , disableForceDark(canvasContext.useForceDark() ? 0 : 1)
        , screenSize(canvasContext.getNextFrameSize()) {}

}  // namespace android::uirenderer
```

  **[frameworks/base/libs/hwui/TreeInfo.h](https://cs.android.com/android/platform/superproject/+/master:frameworks/base/libs/hwui/TreeInfo.h;drc=master;bpv=0;bpt=1;l=97)**

```cpp
int disableForceDark;
```

到了这里，可以看出，当设置了Force Dark之后，最终会设置到`TreeInfo`类中的`disableForceDark`变量，如果没有设置主题的Force Dark，那么根据false的默认值，变量会别设置成1，如果设置了使用强制深色模式，那么`disableForceDark`会变成0

这个变量最终会用在Render的过程中：

  **[frameworks/base/libs/hwui/RenderNode.cpp](https://cs.android.com/android/platform/superproject/+/master:frameworks/base/libs/hwui/RenderNode.cpp;l=235;drc=master;bpv=0;bpt=1)**

```c++
/**
 * Traverse down the the draw tree to prepare for a frame.
 *
 * MODE_FULL = UI Thread-driven (thus properties must be synced), otherwise RT driven
 *
 * While traversing down the tree, functorsNeedLayer flag is set to true if anything that uses the
 * stencil buffer may be needed. Views that use a functor to draw will be forced onto a layer.
 */
void RenderNode::prepareTreeImpl(TreeObserver& observer, TreeInfo& info, bool functorsNeedLayer) {
    if (mDamageGenerationId == info.damageGenerationId) {
        // We hit the same node a second time in the same tree. We don't know the minimal
        // damage rect anymore, so just push the biggest we can onto our parent's transform
        // We push directly onto parent in case we are clipped to bounds but have moved position.
        info.damageAccumulator->dirty(DIRTY_MIN, DIRTY_MIN, DIRTY_MAX, DIRTY_MAX);
    }
    info.damageAccumulator->pushTransform(this);

    if (info.mode == TreeInfo::MODE_FULL) {
        pushStagingPropertiesChanges(info);
    }

    if (!mProperties.getAllowForceDark()) {
        info.disableForceDark++;
    }

    uint32_t animatorDirtyMask = 0;
    if (CC_LIKELY(info.runAnimations)) {
        animatorDirtyMask = mAnimatorManager.animate(info);
    }

    bool willHaveFunctor = false;
    if (info.mode == TreeInfo::MODE_FULL && mStagingDisplayList) {
        willHaveFunctor = mStagingDisplayList->hasFunctor();
    } else if (mDisplayList) {
        willHaveFunctor = mDisplayList->hasFunctor();
    }
    bool childFunctorsNeedLayer =
            mProperties.prepareForFunctorPresence(willHaveFunctor, functorsNeedLayer);

    if (CC_UNLIKELY(mPositionListener.get())) {
        mPositionListener->onPositionUpdated(*this, info);
    }

    prepareLayer(info, animatorDirtyMask);
    if (info.mode == TreeInfo::MODE_FULL) {
        pushStagingDisplayListChanges(observer, info);
    }

    if (mDisplayList) {
        info.out.hasFunctors |= mDisplayList->hasFunctor();
        bool isDirty = mDisplayList->prepareListAndChildren(
                observer, info, childFunctorsNeedLayer,
                [](RenderNode* child, TreeObserver& observer, TreeInfo& info,
                   bool functorsNeedLayer) {
                    child->prepareTreeImpl(observer, info, functorsNeedLayer);
                });
        if (isDirty) {
            damageSelf(info);
        }
    }
    pushLayerUpdate(info);

    if (!mProperties.getAllowForceDark()) {
        info.disableForceDark--;
    }
    info.damageAccumulator->popTransform();
}
```



#### 2. View

## 项目指导

### 旧项目改造

上面提到的自动适配方案和Force Dark方案是否可以同时使用呢，答案是肯定的。下面我们来看对旧项目需要怎么一步步地进行改造，以及有哪些需要注意的问题。



#### 1. XML中设置颜色

透明度处理（脚本自动生成）

Selector api 23以上

```kotlin
not_define_night_color.setBackgroundColor(ContextCompat.getColor(this, R.color.color_main_1))
        not_define_night_color.backgroundTintList = ContextCompat.getColorStateList(this, R.color.color_main_1_alpha_20)
```

```xml
<TextView
        android:id="@+id/not_define_night_color"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginBottom="50dp"
        android:background="@color/color_main_1"
        android:backgroundTint="@color/color_main_1_alpha_20"
        android:padding="20dp"
        android:text="@string/not_define_night_color"
        android:textColor="@color/color_text_0"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" />
```



#### 2. XML中设置图片

#### 3. 代码动态判断切换颜色&图片

tint

#### 4. 主题修改

使用brdige主题

将主题复制一份到night文件夹中，并设置forcedark

由于针对`night`资源的自动深色模式适配在Andorid10之前的版本就已经存在，但是Android10之前的版本没有ForceDark，因此适配了部分的`night`资源可能会导致只有部分页面或控件变成深色，为了避免这个问题，对于旧项目而言添加`night`资源需要指定资源的ap等级为29。

ForceDark和`night`资源自动适配会产生叠加的效果，如果某些组件不希望被forcedark，那么需要单独设置`android:forceDarkAllowed="false"`

如果已经适配过night资源的空间，那么需要单独设置为`android:forceDarkAllowed="false"`

### 新项目Or新模块

DayNight并对所有颜色都处理night

### 最佳实践

* 尽量使用系统提供的Notification样式
* 避免hardcode颜色值
* 尽量使用Vector类型的Drawable并动态设置颜色
* 编写代码时刻考虑深色模式处理

## 其他处理

### WebView深色模式处理

### Flutter 深色模式适配

Bridge

## Demo

上述提到的代码可以到这个[Github项目](https://github.com/shenguojun/AndroidDarkThemeDemo)下载

## 参考

1. [Google Developers - Dark Theme](https://developer.android.com/guide/topics/ui/look-and-feel/darktheme#top_of_page)
2. [Material Design - Dark Theme](https://material.io/develop/android/theming/dark)
3. [Material Design - The color system](https://material.io/design/color/the-color-system.html)
4. [Android 10 暗黑模式适配，你需要知道的一切](https://juejin.im/post/6844904173788463112#heading-15)
5. [Android 10 Dark Theme: Getting Started](https://www.raywenderlich.com/6488033-android-10-dark-theme-getting-started)
6. [Android styling: themes vs styles](https://medium.com/androiddevelopers/android-styling-themes-vs-styles-ebe05f917578)
7. [Android styling: common theme attributes](https://medium.com/androiddevelopers/android-styling-common-theme-attributes-8f7c50c9eaba)
8. [Android Styling: prefer theme attributes](https://medium.com/androiddevelopers/android-styling-prefer-theme-attributes-412caa748774)
9. [Lottie - Dynamic Properties](http://airbnb.io/lottie/#/android?id=dynamic-properties)
10. [Lottie on Android: Part 3 — Dynamic properties](https://medium.com/comparethemarket/lottie-on-android-part-3-dynamic-properties-8aa566ba4fbf)
11. [MIUI 深色模式适配说明](https://dev.mi.com/console/doc/detail?pId=2298)
12. [OPPO 暗色模式适配说明](https://open.oppomobile.com/wiki/doc#id=10658)
13. [Android Q深色模式源码解析](https://www.jianshu.com/p/5005969bf37a)
14. [Moving to the Dark Side: Dark Theme Recap](https://proandroiddev.com/android-dark-theme-implementation-recap-4fcffb0c4bff)
15. [Android应用程序UI硬件加速渲染环境初始化过程分析](https://blog.csdn.net/luoshengyang/article/details/45769759)
16. [Android应用程序UI硬件加速渲染的Display List构建过程分析](https://blog.csdn.net/luoshengyang/article/details/45943255)
17. [Android应用程序UI硬件加速渲染的Display List渲染过程分析](https://blog.csdn.net/Luoshengyang/article/details/46281499)
18. [Drawn out: how Android renders (Google I/O '18)](https://www.youtube.com/watch?v=zdQRIYOST64&ab_channel=AndroidDevelopers)

