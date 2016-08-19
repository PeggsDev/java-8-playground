package interfaces;

/**
 * Created by pedro.francis on 08/08/2016.
 */
@FunctionalInterface
public interface PersonFactoryInterface<P extends Person> {
    P create(String firstName, String lastName);
}
