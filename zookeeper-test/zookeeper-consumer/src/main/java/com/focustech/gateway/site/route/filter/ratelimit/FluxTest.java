package com.focustech.gateway.site.route.filter.ratelimit;

import com.focustech.gateway.site.route.data.ApiRateLimit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class FluxTest {
    public static void main(String[] args) {
//        TimeUnit.SECONDS.toNanos(100);
//        System.out.println( TimeUnit.SECONDS.toSeconds(100));
//        System.out.println(Instant.now().getLong(ChronoField.MILLI_OF_SECOND));

//        Flux<String> flux = Flux.generate(
//                () -> 0,
//                (state, sink) -> {
//                    sink.next("3 x " + state + " = " + 3*state);
//                    if (state == 10) sink.complete();
//                    return state + 1;
//                });

//        Flux<String> flux = Flux.generate(
//                AtomicLong::new,
//                (state, sink) -> {
//                    long i = state.getAndIncrement();
//                    sink.next("3 x " + i + " = " + 3*i);
//                    if (i == 10) sink.complete();
//                    return state;
//                }, (state) -> System.out.println("state: " + state));


//        Mono<Response1> response = Flux.fromIterable(Arrays.asList("s1","s2","s3")).map(s->convert(s)).takeUntil(s->!s.block().allowed).blockFirst();
//        System.out.println("last: "+response.block().allowed + ":" + response.block().source);

//        Mono<Response1> response = Flux.fromIterable(Arrays.asList("s1","s2","s3")).map(s->convert(s)).takeUntil(s->!s.block().allowed).blockLast();
//        System.out.println("last: "+response.block().allowed + ":" + response.block().source);


//        Flux.fromIterable(Arrays.asList("s1","s2","s3")).flatMap(s->convert(s)).takeWhile(s->s.allowed).subscribe(System.out::println);

//        System.out.println(Flux.fromIterable(Arrays.asList("s1","s2","s3")).blockLast());
    }

    private static Mono<Response1> convert(String s){
        System.out.println("convert:"+s);
        try {
            Thread.sleep(1000L);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(s.equals("s1")){
            return Mono.just(new Response1(true,s));
        }else if(s.equals("s2")){
            return Mono.just(new Response1(false,s));
        }
        return Mono.just(new Response1(true,s));
    }

    private static class Response1{
        private boolean allowed;
        private String source;
        public Response1(boolean allowed,String source){
            this.allowed = allowed;
            this.source = source;
        }
    }
}
