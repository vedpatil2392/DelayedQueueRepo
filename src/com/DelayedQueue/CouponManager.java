package com.DelayedQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

// Define the Coupon class implementing the Delayed interface
class Coupon implements Delayed 
{
    private final String code;
    private final long expiryTime; // in nanoseconds

    public Coupon(String code, long delay, TimeUnit unit)
    {
        this.code = code;
        this.expiryTime = System.nanoTime() + unit.toNanos(delay);
    }

    @Override
    public long getDelay(TimeUnit unit) 
    {
        long remainingTime = expiryTime - System.nanoTime();
        return unit.convert(remainingTime, TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed other) 
    {
        if (this.expiryTime < ((Coupon) other).expiryTime)
        {
            return -1;
        }
        if (this.expiryTime > ((Coupon) other).expiryTime)
        {
            return 1;
        }
        return 0;
    }

    public String getCode() 
    {
        return code;
    }

    @Override
    public String toString() 
    {
        return "Coupon{" +
                "code='" + code + '\'' +
                ", expiryTime=" + expiryTime +
                '}';
    }
}

// Main class to demonstrate the usage of DelayQueue with Coupon
public class CouponManager 
{
    public static void main(String[] args) 
    {
        DelayQueue<Coupon> couponQueue = new DelayQueue<>();

        // Adding coupons to the DelayQueue
        couponQueue.offer(new Coupon("DISCOUNT10", 5, TimeUnit.SECONDS));
        couponQueue.offer(new Coupon("SALE20", 10, TimeUnit.SECONDS));
        couponQueue.offer(new Coupon("OFFER30", 7, TimeUnit.SECONDS));

        // Separate thread to process expired coupons
        Thread couponProcessor = new Thread(() -> 
        {
            while (true)
            {
                try 
                {
                    Coupon expiredCoupon = couponQueue.take();
                    System.out.println("Expired coupon: " + expiredCoupon.getCode());
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        couponProcessor.start();

        // Adding delay to demonstrate the coupon expiry processing
        try
        {
            Thread.sleep(15000); // Sleep for 15 seconds
        }
        catch (InterruptedException e) 
        {
            Thread.currentThread().interrupt();
        }

        couponProcessor.interrupt();
    }
}


