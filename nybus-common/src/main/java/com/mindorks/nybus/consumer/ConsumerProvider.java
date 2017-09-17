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

/**
 * Provides the consumer for different types of publishers.
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

    /**
     * The posting thread consumer.
     *
     * @return the posting thread consumer.
     */
    public Consumer<NYEvent> getPostingThreadConsumer() {
        return postingThreadConsumer;
    }

    /**
     * The posting thread consumer.
     *
     * @param postingThreadConsumer the posting thread consumer.
     */
    public void setPostingThreadConsumer(Consumer<NYEvent> postingThreadConsumer) {
        this.postingThreadConsumer = postingThreadConsumer;
    }

    /**
     * The main thread consumer.
     *
     * @return the main thread consumer.
     */
    public Consumer<NYEvent> getMainThreadConsumer() {
        return mainThreadConsumer;
    }

    /**
     * The main thread consumer.
     *
     * @param mainThreadConsumer the main thread consumer.
     */
    public void setMainThreadConsumer(Consumer<NYEvent> mainThreadConsumer) {
        this.mainThreadConsumer = mainThreadConsumer;
    }

    /**
     * The IO thread consumer.
     *
     * @return the IO thread consumer.
     */
    public Consumer<NYEvent> getIOThreadConsumer() {
        return iOThreadConsumer;
    }

    /**
     * The IO thread consumer.
     *
     * @param iOThreadConsumer the IO thread consumer.
     */
    public void setIOThreadConsumer(Consumer<NYEvent> iOThreadConsumer) {
        this.iOThreadConsumer = iOThreadConsumer;
    }

    /**
     * The computation thread consumer.
     *
     * @return the computation thread consumer.
     */
    public Consumer<NYEvent> getComputationThreadConsumer() {
        return computationThreadConsumer;
    }

    /**
     * The computation thread consumer.
     *
     * @param computationThreadConsumer the computation thread consumer.
     */
    public void setComputationThreadConsumer(Consumer<NYEvent> computationThreadConsumer) {
        this.computationThreadConsumer = computationThreadConsumer;
    }

    /**
     * The trampoline thread consumer.
     *
     * @return the trampoline thread consumer.
     */
    public Consumer<NYEvent> getTrampolineThreadConsumer() {
        return trampolineThreadConsumer;
    }

    /**
     * The trampoline thread consumer.
     *
     * @param trampolineThreadConsumer the trampoline thread consumer.
     */
    public void setTrampolineThreadConsumer(Consumer<NYEvent> trampolineThreadConsumer) {
        this.trampolineThreadConsumer = trampolineThreadConsumer;
    }

    /**
     * The executor thread consumer.
     *
     * @return the executor thread consumer.
     */
    public Consumer<NYEvent> getExecutorThreadConsumer() {
        return executorThreadConsumer;
    }

    /**
     * The executor thread consumer.
     *
     * @param executorThreadConsumer the executor thread consumer.
     */
    public void setExecutorThreadConsumer(Consumer<NYEvent> executorThreadConsumer) {
        this.executorThreadConsumer = executorThreadConsumer;
    }

    /**
     * The new thread consumer.
     *
     * @return the new thread consumer.
     */
    public Consumer<NYEvent> getNewThreadConsumer() {
        return newThreadConsumer;
    }

    /**
     * The new thread consumer.
     *
     * @param newThreadConsumer the new thread consumer.
     */
    public void setNewThreadConsumer(Consumer<NYEvent> newThreadConsumer) {
        this.newThreadConsumer = newThreadConsumer;
    }

}
