package io.github.linyimin0812.profiler.common.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author linyimin
 **/
public class BeanInitResult {

    private static final AtomicLong SEQUENCE_ID = new AtomicLong(1000);

    private final long id;
    private long parentId;
    private final String name;
    private long startMillis;
    private long duration;
    private long actualDuration;
    private final Map<String, String> tags;
    private final List<BeanInitResult> children;

    public BeanInitResult(String name) {
        this.id = SEQUENCE_ID.incrementAndGet();
        this.name = name;
        this.startMillis = System.currentTimeMillis();
        this.tags = new HashMap<>();
        this.children = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public long getParentId() {
        return parentId;
    }

    public String getName() {
        return name;
    }


    public long getDuration() {
        return duration;
    }

    public long getActualDuration() {
        return actualDuration;
    }

    public void duration() {
        this.duration = System.currentTimeMillis() - this.startMillis;
        long childrenDuration = this.children.stream().mapToLong(BeanInitResult::getActualDuration).sum();
        this.actualDuration = duration - childrenDuration;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public List<BeanInitResult> getChildren() {
        return children;
    }

    public void addChild(BeanInitResult child) {
        child.parentId = this.id;
        this.children.add(child);
    }

    public long getStartMillis() {
        return startMillis;
    }

    public void setStartMillis(long startMillis) {
        this.startMillis = startMillis;
    }

    public void setTags(Map<String, String> tags) {
        this.tags.putAll(tags);
    }
}
