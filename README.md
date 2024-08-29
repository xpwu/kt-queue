# kt-queue


## 代码库的引用
1、可以使用 jitpack 直接依赖 github 代码   [![](https://jitpack.io/v/xpwu/kt-queue.svg)](https://jitpack.io/#xpwu/kt-queue)

2、在 settings.grable 加入
```
dependencyResolutionManagement {
	repositories {
		google()
		mavenCentral()
		// 在依赖库列表的最后加入jit依赖库
		maven { url 'https://jitpack.io' }
	}
}
```

3、在 module build.grable 加入
```

dependencies {
  // 加入如下依赖
  implementation 'com.github.xpwu:kt-queue:<version>'
}
```
< version>: 替换为使用的版本 
