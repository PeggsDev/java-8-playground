import interfaces.IFunctionalInterface;
import interfaces.Person;
import interfaces.PersonFactoryInterface;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;


/**
 * Created by pedro.francis on 08/08/2016.
 */
public class Streams {

    private final static Consumer<Object> prn = System.out::println; //This is a function definition with no return type - used for side effects

    private static void testingStreams() {
        Function<String, String> fn = s -> s + " Function";

        prn.accept(fn.apply("Help Me"));

        Arrays.asList(1, 2, 3, 4, 5, 6, 6, 6, 7, 6)
                .stream()
                .filter(i -> i == 6)
                .map(i -> i * 2)
                .sorted()
                .reduce(Integer::sum /* (a, b) -> a + b */)
                .ifPresent(prn);

        Stream.of(1, 2, 3, 4, 5, 6, 6, 6, 7, 6)
                .map(i -> i * 2)
                .sorted()
                .distinct()
                .forEach(prn);
    }

    private static void testingFunctionalStreams() {
        Function<List<Integer>, Optional<Integer>> fn = l -> l
                .stream()
                .reduce(Integer::sum);

        fn.apply(Arrays.asList(1, 2, 3, 4, 5)).ifPresent(prn);
    }

    private static void testingFunctionalInterfaces() {

        PersonFactoryInterface<Person> personFactory = Person::new;
        Person person = personFactory.create("Peter", "Parker");
        prn.accept(person.getFirstName() + " " + person.getLastName());

        IFunctionalInterface<String> functionalInterface = str -> str.concat(" - testing stuff");
        prn.accept(functionalInterface.callMe("i have been called not implemented!!!"));

    }

    private static void testingJ8Concurrency() throws ExecutionException, InterruptedException {
        //Vanilla Threading
        Runnable task = () -> {
            try {
                String threadName = Thread.currentThread().getName();
                prn.accept("Vanilla Foo" + threadName);
                TimeUnit.SECONDS.sleep(1);
                prn.accept("Vanilla Bar" + threadName);
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        };

        Thread thread = new Thread(task);
        thread.start();

        //Concurrency API - Java 5 +

        //Executors
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            String threadName = Thread.currentThread().getName();
            prn.accept("Executor " + threadName);
        });


        //Callables and Futures
        Callable<Integer> callableTask = () -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                return 123;
            }
            catch (InterruptedException ex) {
                throw new IllegalStateException("task interrupted", ex);
            }
        };

        Future<Integer> future = executor.submit(callableTask);

        System.out.println("future done? " + future.isDone());

        Integer result = future.get();

        System.out.println("future done? " + future.isDone());
        System.out.print("result: " + result);

        //Shutting down Executors
        try {
            System.out.println("attempt to shutdown executor");
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            System.err.println("tasks interrupted");
        }
        finally {
            if (!executor.isTerminated()) {
                System.err.println("cancel non-finished tasks");
            }
            executor.shutdownNow();
            System.out.println("shutdown finished");
        }
    }

    public static void main(String[] args) {
        testingStreams();
        testingFunctionalInterfaces();
        testingFunctionalStreams();

        try {
            testingJ8Concurrency();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}