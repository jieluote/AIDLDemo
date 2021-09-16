﻿####介绍
此项目包含client和server端,需要分为两个独立项目来运行

跨进程通讯是Android中非常重要的知识点,虽然普通的应用开发中很少用到,但
实际上几乎所有重要的组件都离不开跨进程通讯,正是Android很好的封装,让我们
感受不到它的存在。本Demo我们来了解下基于Binder的跨进程通讯的常见方式

 主要创建了绑定服务三种定义接口方式 (也可以理解为访问服务的三种方式):
  1. 继承Binder类    本应用内
  2. 使用Messenger   跨进程 - 单线程
  3. 使用AIDL        跨进程 - 多线程
  通过本Demo 我们可以了解到Service的生命周期及这三种方式的异同，
  但不会去介绍这三个内部的实现细节,更多的是从使用的角度上
  理解Binder,理解跨进程通讯。
 
 ####备注
 运行结果不反应在页面上,都打印在logcat中(TEST_TAG),这样更方便查看整体流程