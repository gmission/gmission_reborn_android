## The Android client for gMission
- gMission is a general spatial crowdsourcing platform for researchers.
- gMission is opensource: https://github.com/gmission/gmission
- The Android client for gMission(client in below) is an Android app.
- The client is also open sourced. 


## Features:
- Join campaigns and submit answers. 
- Long press on the map to request new Question.
- Notifications when there are new answers of your questions and new answers requested near you.


## Android development:
- Android Studio is used to develop this app.
- Baidu push service is used to push notifications to devices.
- Google map provides the map service.
- Technical details are introduced at bottom of this page.

## Try the latest version
- We upload the latest app to [our server](http://lccpu4.cse.ust.hk/indoorLocalizationService/gmission.apk). You can download it directly.

##Screenshots

- Home Page:
- 
<img src="https://www.dropbox.com/s/503vuotz92lxf9m/homepage.png?dl=1" alt="Home Page" width="320"/>

- campaign:
- 
<img src="https://www.dropbox.com/s/cniegfb75sngbeq/campaign.png?dl=1" alt="campaign" width="320"/>

- Task Map:
- 
<img src="https://www.dropbox.com/s/r25hyay9e3ue0n0/task_map.png?dl=1" alt="Task Map" width="320"/>

- Ask Question:
- 
<img src="https://www.dropbox.com/s/bxavwizxj62xlvp/askQuestion.png?dl=1" alt="Ask Question" width="320"/>

- messages:
- 
<img src="https://www.dropbox.com/s/6zfsrdekg7tpwxp/message.png?dl=1" alt="messages" width="320"/>

- Answers:
- 
<img src="https://www.dropbox.com/s/j4mgrocbpgu3tiw/answers.png?dl=1" alt="Answers" width="320"/>





## Technical details of gMission Android App 

This is an rebuilt gMission Android client. You can modify or build it with [Android Studio](http://developer.android.com/sdk/index.html?gclid=Cj0KEQiAwNmzBRCaw9uR3dGt950BEiQAnbK962IP5pZlhxCC6jHLgVQErBvAQmCXz1YXYcn_F8AJjsEaAmlq8P8HAQ) in version 2.0 (current is [preview version](http://tools.android.com/download/studio/builds/android-studio-2-0-preview-4)) or later version. I recommend you throw away Eclipse+plugins, which is too old. If you are still using Eclipse, try Android Studio, which is definitely more efficient.

You can find more information about gMission project and other related open-source project on [gMission's Home Page](http://www.gmissionhkust.com).

This project is evolved from [Android Bootstrap project](http://www.androidbootstrap.com).

The architecture of this android project can be find in this [article](https://medium.com/ribot-labs/android-application-architecture-8b6e34acda65#.ynyp3cazw) ([Chinese Version](http://huxian99.github.io/2015/12/07/Android-应用架构/)).

Some used techniques:

1. RxJava (RxAndroid)

    [RxJava](https://github.com/ReactiveX/RxJava) is a pretty nice implementation of Observer Pattern. You can use it to resolve a huge mass of task with a similar process. If you have not hear about that, try it!
    Here are some usefully tutorials:
    
    * [Tutorial](http://reactivex.io/tutorials.html) from official website.
    * An [introduction](http://gank.io/post/560e15be2dca930e00da1083) of RxJava (in Chinese).
    * A [tutorial](https://newcircle.com/s/post/1744/2015/06/29/learning-rxjava-for-android-by-example) of RxJava by Kaushik Gopal with 5 interesting examples.
    * An [example project](https://github.com/kaushikgopal/RxJava-Android-Samples) of RxAndroid by Kaushik Gopal.
    * [One nice Blog](http://huxian99.github.io) that translates good technical articles to Chinese.
    * An [article](http://joluet.github.io/blog/2014/07/07/rxjava-retrofit/) about using Retrofit with RxJava.

2. Otto (event bus)

    [Otto](http://square.github.io/otto/) is an event bus designed to decouple different parts of your application while still allowing them to communicate efficiently.Forked from Guava, Otto adds unique functionality to an already refined event bus as well as specializing it to the Android platform.
    
3. Dagger (Dependency Injector)
    [Dagger](http://square.github.io/dagger/) is a fast dependency injector for Android and Java.
    
    
    
 ##License
 
 Everyone can use this open source project for learning or study. If one use this project, please mention [gMission project](http://gmission.github.io) on its home page. 
 For research usage, please cite our related spatial crowdsourcing papers listed on the [home page of gMission](http://gmission.github.io). 
 For any questions or suggestions, please contact us with methods provided on the [home page of gMission](http://gmission.github.io). 