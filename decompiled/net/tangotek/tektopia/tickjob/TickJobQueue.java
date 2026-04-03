/*
 * Decompiled with CFR 0.152.
 */
package net.tangotek.tektopia.tickjob;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import net.tangotek.tektopia.tickjob.TickJob;

public class TickJobQueue {
    private List<TickJob> jobs = new ArrayList<TickJob>();
    private List<TickJob> pendingJobs = new ArrayList<TickJob>();

    public void addJob(TickJob job) {
        this.pendingJobs.add(job);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void tick() {
        this.jobs.addAll(this.pendingJobs);
        this.pendingJobs.clear();
        List<TickJob> list = this.jobs;
        synchronized (list) {
            ListIterator<TickJob> itr = this.jobs.listIterator();
            while (itr.hasNext()) {
                TickJob job = itr.next();
                job.tick();
                if (!job.isComplete()) continue;
                itr.remove();
            }
        }
    }

    public void clear() {
        this.jobs.clear();
        this.pendingJobs.clear();
    }
}

