/*
 * Copyright 2016 Kejun Xia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shipdream.lib.android.mvc;

import com.shipdream.lib.poke.Graph;
import com.shipdream.lib.poke.Provides;
import com.shipdream.lib.poke.exception.ProvideException;
import com.shipdream.lib.poke.exception.ProviderConflictException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Mvc {
    static MvcGraph graph;

    /**
     * Get the {@link MvcGraph} managing injectable objects. It comes with a default
     * {@link MvcComponent}.
     * @return
     */
    public static MvcGraph graph() {
        if (graph == null) {
            final MvcComponent rootComponent = new MvcComponent("MvcRootComponent");

            try {
                rootComponent.register(new Object() {
                    @Provides
                    @com.shipdream.lib.android.mvc.event.bus.annotation.EventBusC
                    public com.shipdream.lib.android.mvc.event.bus.EventBus providesEventBusC() {
                        return new com.shipdream.lib.android.mvc.event.bus.internal.EventBusImpl();
                    }

                    @Provides
                    @com.shipdream.lib.android.mvc.event.bus.annotation.EventBusV
                    public com.shipdream.lib.android.mvc.event.bus.EventBus providesEventBusV() {
                        return new com.shipdream.lib.android.mvc.event.bus.internal.EventBusImpl();
                    }

                    @Provides
                    public ExecutorService providesExecutorService() {
                        return Executors.newFixedThreadPool(10, new ThreadFactory() {
                            @Override
                            public Thread newThread(final Runnable r) {
                                return new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Thread.currentThread().setPriority(Thread.NORM_PRIORITY - 1);
                                        r.run();
                                    }
                                }, "MvcBackgroundThread");
                            }
                        });
                    }
                });
            } catch (ProvideException e) {
                e.printStackTrace();
            } catch (ProviderConflictException e) {
                e.printStackTrace();
            }

            graph = new MvcGraph();
            try {
                graph.setRootComponent(rootComponent);
            } catch (Graph.IllegalRootComponentException e) {
                e.printStackTrace();
            }
        }
        return graph;
    }

}
