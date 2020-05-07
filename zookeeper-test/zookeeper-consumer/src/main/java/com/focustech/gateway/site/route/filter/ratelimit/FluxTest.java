package com.focustech.gateway.site.route.filter.ratelimit;

import reactor.core.publisher.Mono;

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
//        Flux.fromIterable(Arrays.asList("s1","s2","s3")).flatMap(s->convert(s)).reduceWith(()->true,(x,y)-> x && y.allowed).subscribe(System.out::println);
    }

    private static Mono<Response1> convert(String s){
        System.out.println("convert:"+s);
        try {
            Thread.sleep(5000L);
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
