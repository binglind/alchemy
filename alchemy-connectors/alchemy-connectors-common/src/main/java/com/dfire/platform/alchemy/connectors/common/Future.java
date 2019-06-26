package com.dfire.platform.alchemy.connectors.common;

import org.apache.flink.streaming.api.functions.async.ResultFuture;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.apache.flink.util.Preconditions.checkArgument;

/**
 * @author congbai
 * @date 2019/5/27
 */
public class Future<T> {

    private final T future;

    public Future(T future) {
        checkArgument( future !=null , "future can't be null");
        checkArgument( (future instanceof ResultFuture || future instanceof CompletableFuture), "can't support "+future.getClass().getName());
        this.future = future;
    }

    public boolean completeExceptionally(Throwable error){
        if (future instanceof ResultFuture){
            ResultFuture resultFuture = (ResultFuture) future;
            resultFuture.completeExceptionally(error);
            return true;
        }else{
            return ((CompletableFuture)future).completeExceptionally(error);
        }
    }

    public <I> void  complete(Collection<I> result){
        if (future instanceof ResultFuture){
            ResultFuture<I> resultFuture = (ResultFuture) future;
            resultFuture.complete(result);
        }else{
            ((CompletableFuture<Collection<I>>)future).complete(result);
        }
    }

    public <I> I get() throws ExecutionException, InterruptedException {
        if (future instanceof CompletableFuture){
            return ((CompletableFuture<I>)future).get();
        }
        throw new UnsupportedOperationException();
    }


}
