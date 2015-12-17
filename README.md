# gMission App

This is a rebuilt gMission Android client. You can modify or build it with android studio 2.0 or later version.

The home page of gMission project is http://www.gmissionhkust.com. You can find more information about gMission project and other related open-source project there.


Some used techniques:

1. RxJava (RxAndroid)

[RxJava](https://github.com/ReactiveX/RxJava) is a pretty nice implementation of Observer Pattern. You can use it to solve a huge mass of task with a similar process. If you have not hear about that, try it!
Here are some usefully tutorials:

* An [introduction](http://gank.io/post/560e15be2dca930e00da1083) of RxJava (in Chinese).
* A [tutorial](https://newcircle.com/s/post/1744/2015/06/29/learning-rxjava-for-android-by-example) of RxJava by Kaushik Gopal with 5 interesting examples.
* An [example project](https://github.com/kaushikgopal/RxJava-Android-Samples) of RxAndroid by Kaushik Gopal.

2. Otto (event bus)

[Otto](http://square.github.io/otto/) is an event bus designed to decouple different parts of your application while still allowing them to communicate efficiently.

Forked from Guava, Otto adds unique functionality to an already refined event bus as well as specializing it to the Android platform.