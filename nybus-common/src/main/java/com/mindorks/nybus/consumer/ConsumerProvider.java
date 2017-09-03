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

import com.mindorks.nybus.event.NYEvent;

import io.reactivex.functions.Consumer;

/**
 * Created by amitshekhar on 28/08/17.
 */

public class ConsumerProvider {

    private Consumer<NYEvent> postingThreadConsumer;
    private Consumer<NYEvent> mainThreadConsumer;
    private Consumer<NYEvent> iOThreadConsumer;
    private Consumer<NYEvent> computationThreadConsumer;
    private Consumer<NYEvent> trampolineThreadConsumer;
    private Consumer<NYEvent> executorThreadConsumer;
    private Consumer<NYEvent> newThreadConsumer;

    public ConsumerProvider() {
    }

    public Consumer<NYEvent> getPostingThreadConsumer() {
        return postingThreadConsumer;
    }

    public void setPostingThreadConsumer(Consumer<NYEvent> postingThreadConsumer) {
        this.postingThreadConsumer = postingThreadConsumer;
    }

    public Consumer<NYEvent> getMainThreadConsumer() {
        return mainThreadConsumer;
    }

    public void setMainThreadConsumer(Consumer<NYEvent> mainThreadConsumer) {
        this.mainThreadConsumer = mainThreadConsumer;
    }

    public Consumer<NYEvent> getIOThreadConsumer() {
        return iOThreadConsumer;
    }

    public void setIOThreadConsumer(Consumer<NYEvent> iOThreadConsumer) {
        this.iOThreadConsumer = iOThreadConsumer;
    }

    public Consumer<NYEvent> getComputationThreadConsumer() {
        return computationThreadConsumer;
    }

    public void setComputationThreadConsumer(Consumer<NYEvent> computationThreadConsumer) {
        this.computationThreadConsumer = computationThreadConsumer;
    }

    public Consumer<NYEvent> getTrampolineThreadConsumer() {
        return trampolineThreadConsumer;
    }

    public void setTrampolineThreadConsumer(Consumer<NYEvent> trampolineThreadConsumer) {
        this.trampolineThreadConsumer = trampolineThreadConsumer;
    }

    public Consumer<NYEvent> getExecutorThreadConsumer() {
        return executorThreadConsumer;
    }

    public void setExecutorThreadConsumer(Consumer<NYEvent> executorThreadConsumer) {
        this.executorThreadConsumer = executorThreadConsumer;
    }

    public Consumer<NYEvent> getNewThreadConsumer() {
        return newThreadConsumer;
    }

    public void setNewThreadConsumer(Consumer<NYEvent> newThreadConsumer) {
        this.newThreadConsumer = newThreadConsumer;
    }

}
