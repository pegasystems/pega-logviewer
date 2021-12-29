
package com.pega.gcs.logviewer.systemstate.model;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestorsResult {

    private TreeSet<RequestorPool> requestorPoolSet;

    private Link selfLink;

    private Link clearASinglePoolLink;

    private Link clearAllPoolsLink;

    public RequestorsResult() {
        requestorPoolSet = new TreeSet<>();
    }

    public Set<RequestorPool> getRequestorPoolSet() {
        return Collections.unmodifiableSet(requestorPoolSet);
    }

    public Link getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(Link selfLink) {
        this.selfLink = selfLink;
    }

    public Link getClearASinglePoolLink() {
        return clearASinglePoolLink;
    }

    public void setClearASinglePoolLink(Link clearASinglePoolLink) {
        this.clearASinglePoolLink = clearASinglePoolLink;
    }

    public Link getClearAllPoolsLink() {
        return clearAllPoolsLink;
    }

    public void setClearAllPoolsLink(Link clearAllPoolsLink) {
        this.clearAllPoolsLink = clearAllPoolsLink;
    }

    public void addRequestorPool(RequestorPool requestorPool) {
        requestorPoolSet.add(requestorPool);
    }

    public void postProcess() {

        if (requestorPoolSet != null) {

            AtomicInteger index = new AtomicInteger(0);

            for (RequestorPool requestorPool : requestorPoolSet) {

                requestorPool.setIndex(index.incrementAndGet());
            }
        }
    }
}
