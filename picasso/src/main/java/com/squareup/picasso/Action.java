/*
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.picasso;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import static com.squareup.picasso.Picasso.Priority;

/**
 * 如果 RequestHandler 是图片加载的开始阶段，Action 则是结束阶段，
 * Action 是抽象类，他决定了图片的最后一个环节：
 * 如何将图片渲染在目标容器中（如 ImageView 和 RemoteViews 等），
 * 由于目标容器有多种情况，因此也有多个子类。
 * <p>
 * Request对象的引用，还需要Picasso实例，是否重试加载等等
 * Action有个需要关注的点，那就是WeakReference<T> target,它持有的是Target(比如ImageView..)的弱引用，
 * 这样可以保证加载时间很长的情况下
 *
 * @param <T>
 */
abstract class Action<T> {
    static class RequestWeakReference<M> extends WeakReference<M> {
        final Action action;

        public RequestWeakReference(Action action, M referent, ReferenceQueue<? super M> q) {
            super(referent, q);
            this.action = action;
        }
    }

    final Picasso picasso;
    final Request request;
    final WeakReference<T> target;
    final boolean noFade;
    final int memoryPolicy;
    final int networkPolicy;
    final int errorResId;
    final Drawable errorDrawable;
    final String key;
    final Object tag;

    boolean willReplay;
    boolean cancelled;

    Action(Picasso picasso, T target, Request request, int memoryPolicy, int networkPolicy,
           int errorResId, Drawable errorDrawable, String key, Object tag, boolean noFade) {
        this.picasso = picasso;
        this.request = request;
        this.target =
                target == null ? null : new RequestWeakReference<>(this, target, picasso.referenceQueue);
        this.memoryPolicy = memoryPolicy;
        this.networkPolicy = networkPolicy;
        this.noFade = noFade;
        this.errorResId = errorResId;
        this.errorDrawable = errorDrawable;
        this.key = key;
        this.tag = (tag != null ? tag : this);
    }

    /**
     * 完成加载（图片数据和来源）
     *
     * @param result
     * @param from
     */
    abstract void complete(Bitmap result, Picasso.LoadedFrom from);

    /**
     * 加载数据失败
     *
     * @param e
     */
    abstract void error(Exception e);

    /**
     * 取消加载
     */
    void cancel() {
        cancelled = true;
    }

    Request getRequest() {
        return request;
    }

    T getTarget() {
        return target == null ? null : target.get();
    }

    String getKey() {
        return key;
    }

    boolean isCancelled() {
        return cancelled;
    }

    boolean willReplay() {
        return willReplay;
    }

    int getMemoryPolicy() {
        return memoryPolicy;
    }

    int getNetworkPolicy() {
        return networkPolicy;
    }

    Picasso getPicasso() {
        return picasso;
    }

    Priority getPriority() {
        return request.priority;
    }

    Object getTag() {
        return tag;
    }
}
