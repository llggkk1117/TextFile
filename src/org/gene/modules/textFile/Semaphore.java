package org.gene.modules.textFile;

public class Semaphore
{
	private Thread currentOwner;
	public synchronized void acquire() throws InterruptedException
	{
		while(currentOwner != null)
		{
        	wait();
		}
		this.currentOwner = Thread.currentThread();
    }

    public synchronized void release()
    {
        if (this.currentOwner == Thread.currentThread())
        {
        	this.currentOwner = null;
        	notifyAll();
        }
    }
}