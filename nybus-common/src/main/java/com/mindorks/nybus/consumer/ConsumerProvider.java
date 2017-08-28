/*
 *    Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.mindorks.nybus.consumer;

import com.mindorks.nybus.event.Event;

import io.reactivex.functions.Consumer;

/**
 * Created by amitshekhar on 28/08/17.
 */

public class ConsumerProvider {

    private Consumer<Event> postingThreadConsumer;
    private Consumer<Event> mainThreadConsumer;
    private Consumer<Event> iOThreadConsumer;
    private Consumer<Event> computationThreadConsumer;
    private Consumer<Event> trampolineThreadConsumer;
    private Consumer<Event> executorThreadConsumer;
    private Consumer<Event> newThreadConsumer;

    public ConsumerProvider() {
    }

    public Consumer<Event> getPostingThreadConsumer() {
        return postingThreadConsumer;
    }

    public void setPostingThreadConsumer(Consumer<Event> postingThreadConsumer) {
        this.postingThreadConsumer = postingThreadConsumer;
    }

    public Consumer<Event> getMainThreadConsumer() {
        return mainThreadConsumer;
    }

    public void setMainThreadConsumer(Consumer<Event> mainThreadConsumer) {
        this.mainThreadConsumer = mainThreadConsumer;
    }

    public Consumer<Event> getIOThreadConsumer() {
        return iOThreadConsumer;
    }

    public void setIOThreadConsumer(Consumer<Event> iOThreadConsumer) {
        this.iOThreadConsumer = iOThreadConsumer;
    }

    public Consumer<Event> getComputationThreadConsumer() {
        return computationThreadConsumer;
    }

    public void setComputationThreadConsumer(Consumer<Event> computationThreadConsumer) {
        this.computationThreadConsumer = computationThreadConsumer;
    }

    public Consumer<Event> getTrampolineThreadConsumer() {
        return trampolineThreadConsumer;
    }

    public void setTrampolineThreadConsumer(Consumer<Event> trampolineThreadConsumer) {
        this.trampolineThreadConsumer = trampolineThreadConsumer;
    }

    public Consumer<Event> getExecutorThreadConsumer() {
        return executorThreadConsumer;
    }

    public void setExecutorThreadConsumer(Consumer<Event> executorThreadConsumer) {
        this.executorThreadConsumer = executorThreadConsumer;
    }

    public Consumer<Event> getNewThreadConsumer() {
        return newThreadConsumer;
    }

    public void setNewThreadConsumer(Consumer<Event> newThreadConsumer) {
        this.newThreadConsumer = newThreadConsumer;
    }

}
