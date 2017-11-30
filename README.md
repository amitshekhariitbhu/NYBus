<p align="center">
<img alt="NYBus" src=https://raw.githubusercontent.com/MindorksOpenSource/NYBus/master/assets/nybus.png />
</p>

# NYBus(RxBus) - A pub-sub library for Android and Java applications.

[![Mindorks](https://img.shields.io/badge/mindorks-opensource-blue.svg)](https://mindorks.com/open-source-projects)
[![Mindorks Community](https://img.shields.io/badge/join-community-blue.svg)](https://mindorks.com/join-community)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

### This pub-sub library NYBus(RxBus) uses RxJava(RxJava2) for creating RxBus(RxBus2). You must have used EventBus. NYBus is very similar to the EventBus. NYBus is implemented using RxJava(RxJava2).

### Overview of NYBus(RxBus) pub-sub library
* NYBus is used for posting any type of event to subscribe class in Android and Java applications.
* NYBus also support channel to avoid the problem of event getting received at undesirable places.
* NYBus also support thread customization(thread in which the event should be posted).
* NYBus is built on RxJava(RxJava2).

## Using NYBus(RxBus) Library in your application

## Android
```groovy
compile 'com.mindorks.nybus:nybus-android:1.0.0'
```
## Java
```groovy
compile 'com.mindorks.nybus:nybus-java:1.0.0'
```

## To run all the test cases
```groovy
gradlew connectedAndroidTest test
```

## Simple Usage

### Register on default channel
```java
NYBus.get().register(this);
```

### Unregister from default channel
```java
NYBus.get().unregister(this);
```

### Post on default channel
```java
NYBus.get().post(event);
```

### Receive on default channel
```java
@Subscribe
public void onEvent(Event event) {

}
```

## Usage with specific channel

### Register on specific channel
```java
NYBus.get().register(this, Channel.ONE);
```

### Register on more than one channel
```java
NYBus.get().register(this, Channel.ONE, Channel.TWO);
```

### Unregister from channel
```java
NYBus.get().unregister(this, Channel.ONE);
```

### Unregister from more than one channel
```java
NYBus.get().unregister(this, Channel.ONE, Channel.TWO);
```

### Post on a specific channel
```java
NYBus.get().post(event, Channel.ONE);
```

### Receive on a specific channel
```java
@Subscribe(channelId = Channel.ONE)
public void onEvent(Event event) {

}
```

### Receive on more than one channel
```java
@Subscribe(channelId = {Channel.ONE, Channel.TWO})
public void onEvent(Event event) {

}
```
## Usage with specific thread

### Receive on specific thread
```java
@Subscribe(threadType = NYThread.MAIN)
public void onEvent(Event event) {

}
```

### Receive on a specific channel and a specific thread
```java
@Subscribe(channelId = Channel.ONE, threadType = NYThread.IO)
public void onEvent(Event event) {

}
```

### Enable Logging
```java
NYBus.get().enableLogging();
```
It will log: D/NYBus: No target found for the eventclass com.mindorks.Event

## If this library helps you in anyway, show your love :heart: by putting a :star: on this project :v:

[Check out Mindorks awesome open source projects here](https://mindorks.com/open-source-projects)

### License
```
   Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```

### Contributing to NYBus
All pull requests are welcome, make sure to follow the [contribution guidelines](CONTRIBUTING.md)
when you submit pull request.
