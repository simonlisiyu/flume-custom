/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.jcloud.flume.source.heartbeat;

import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.instrumentation.SourceCounter;
import org.apache.flume.source.AbstractPollableSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartBeatGeneratorSource extends AbstractPollableSource implements
        Configurable {

  private static final Logger logger = LoggerFactory
      .getLogger(org.apache.flume.source.SequenceGeneratorSource.class);

  private static final long msForMinute = 60000L;

  private SourceCounter sourceCounter;
  private long intervalMs;
  private long currentTs;
  private long eventsSentTs = 0L;


  /**
   * Read parameters from context
   * <li>intervalMs = type long that defines the interval(ms) of event sent
   */
  @Override
  protected void doConfigure(Context context) throws FlumeException {
    intervalMs = context.getLong("intervalMs", msForMinute);
    if (sourceCounter == null) {
      sourceCounter = new SourceCounter(getName());
    }
  }

  @Override
  protected Status doProcess() throws EventDeliveryException {
    Status status = Status.READY;
    currentTs = System.currentTimeMillis();
    try {
      if ((eventsSentTs+intervalMs) < currentTs) {
        getChannelProcessor().processEvent(
                EventBuilder.withBody(String.valueOf(currentTs).getBytes()));
        sourceCounter.incrementEventAcceptedCount();
        eventsSentTs = currentTs;
      } else {
        status = Status.BACKOFF;
      }

    } catch (ChannelException ex) {
      eventsSentTs = 0L;
      logger.error( getName() + " source could not write to channel.", ex);
    }
    return status;
  }

  @Override
  protected void doStart() throws FlumeException {
    logger.info("HeartBeat generator source do starting");
    sourceCounter.start();
    logger.debug("HeartBeat generator source do started");
  }

  @Override
  protected void doStop() throws FlumeException {
    logger.info("HeartBeat generator source do stopping");

    sourceCounter.stop();

    logger.info("HeartBeat generator source do stopped. Metrics:{}",getName(), sourceCounter);
  }

}
