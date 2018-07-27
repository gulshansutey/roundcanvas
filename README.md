## RoudnCanvas ImageView
A round image view with custom stroke and solid color also allows selector drawables for different states.
 

![alt text](https://github.com/adam-p/markdown-here/raw/master/src/common/images/icon48.png "Logo Title Text 1")

 
## Usage
Use in your xml like this:

```xml
<gulshansutey.oldmirrorimageview.RoundCanvas
                android:layout_width="100dp"
                android:layout_gravity="center"
                app:its_stroke_color="@color/colorPrimaryDark"
                app:its_stroke_width="5dp"
                android:src="@drawable/my_picture"
                app:its_solid_color="@color/textColorPrimary"
                android:layout_height="100dp" />
```

You can set selector drawable to change the tint of the image inside it for different state.

```xml
		app:its_tint_color_state_drawable="@color/bg_color_selector"
```

 
bg_color_selector.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
     <item android:color="@color/colorPrimaryDark" android:state_focused="true" android:state_enabled="false"/>
    <item android:color="@color/colorPrimary" android:state_focused="true" android:state_enabled="true"/>
 <item android:color="@color/colorPrimaryDark" android:state_enabled="false"/>
    <item android:color="@color/colorPrimary" android:state_pressed="true"/>
    <item android:color="@color/colorPrimaryDark" />
</selector>
```

## Deployment

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	} 
  
Add the dependency in your app level build.gradle file

	dependencies {
	        implementation 'com.github.gulshansutey:roundcanvas:v1.0.1'
	}
 [![](https://jitpack.io/v/gulshansutey/roundcanvas.svg)](https://jitpack.io/#gulshansutey/roundcanvas)

 
