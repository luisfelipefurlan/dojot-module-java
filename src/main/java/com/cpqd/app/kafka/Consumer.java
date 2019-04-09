package com.cpqd.app.kafka;

import java.util.*;
import java.util.function.BiFunction;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import java.util.HashSet;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import com.cpqd.app.config.Config;

/**
 * KafkaConsumer wrapper
 */
public class Consumer implements Runnable {

    private KafkaConsumer<String, String> mConsumer;
    private HashSet<String> mTopics;
    private long mPollTime;
    boolean mKeepConsuming;
    BiFunction<String, String, Integer> mCallback;
    private final Semaphore mConsurmerGate = new Semaphore(0, true);
    boolean mShouldRegisterTopics;
    ReentrantLock mNewTopicLock;

    /**
     * Create a consumer instance
     *
     * @param pollTime time to polling (in ms)
     * @param callback Function to be called when receive a kafka message.
     */
    public Consumer(long pollTime, BiFunction<String, String, Integer> callback) {
        this.mNewTopicLock = new ReentrantLock();
        this.mKeepConsuming = true;
        this.mShouldRegisterTopics = false;
        this.mPollTime = pollTime;
        this.mTopics = new HashSet<String>();
        this.mCallback = callback;
        Properties props = new Properties();
        props.put("bootstrap.servers", Config.getInstance().getKafkaAddress());
        props.put("group.id", Config.getInstance().getKafkaDefaultGroupId());
        props.put("session.timeout.ms", Config.getInstance().getKafkaDefaultSessionTimeout());
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.mConsumer = new KafkaConsumer<>(props);
    }

    /**
     * Adds topic to subscribe.
     *
     * @param topic topic to subscribe.
     */
    public void addToSubscriberList(String topic) {
        this.mNewTopicLock.lock();
        this.mTopics.add(topic);
        this.mShouldRegisterTopics = true;
        this.mConsurmerGate.release();
        this.mNewTopicLock.unlock();
    }

    /**
     * Initiate the consumer thread.
     */
    @Override
    public void run() {
        try {
            while (true) {
                while (this.mKeepConsuming) {
                    if (this.mShouldRegisterTopics) {
                        this.mNewTopicLock.lock();
                        this.mConsumer.subscribe(this.mTopics, new ConsumerRebalanceListener() {
                            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                            }
                            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                            }
                        });
                        this.mShouldRegisterTopics = false;
                        this.mNewTopicLock.unlock();
                    }

                    try {
                        ConsumerRecords<String, String> records = mConsumer.poll(this.mPollTime);
                        for (ConsumerRecord<String, String> record : records) {
                            this.mCallback.apply(record.topic(), record.value());
                        }
                    } catch (IllegalStateException e) {
                        System.out.println("consumer without topic...");
                        break;
                    }
                }
                try {
                    this.mConsurmerGate.acquire();
                } catch (InterruptedException e) {
                    System.out.println("Thread has been interrupted...");
                }
            }
        } catch (WakeupException e) {
            // ignore for shutdown
            // This thread will only stop if this exception is thrown (which
            // is done by calling 'shutdown')
            mConsumer.close();
        } catch (Exception e) {
            System.out.println("Consumer error: " + e.getMessage());
            // maybe it is not the best approach, but is the simplest.
            System.exit(1);
        }
    }

    public void shutdown() {
        mConsumer.wakeup();
    }

    public synchronized void stopConsuming(boolean set) {
        if ( (this.mKeepConsuming == true) && (!set) ) {
            this.mKeepConsuming = set;
        } else if ( (this.mKeepConsuming == false) && (set) ) {
            this.mKeepConsuming = set;
            // allow the consumer continue
            this.mConsurmerGate.release();
        }
    }
}