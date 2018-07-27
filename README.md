# roundcanvas
A round image view with custom stroke and solid color also allows selector drawables for different states.
 
##Usage
Use in your xml like this:
<gulshansutey.oldmirrorimageview.RoundCanvas
                android:layout_width="100dp"
                android:layout_gravity="center"
                app:its_stroke_color="@color/colorPrimaryDark"
                app:its_stroke_width="5dp"
                android:src="@drawable/my_picture"
                app:its_solid_color="@color/textColorPrimary"
                android:layout_height="100dp" />

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

 
