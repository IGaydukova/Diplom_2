package User;
import com.github.javafaker.Faker;
import java.util.Random;
public class UserGenerate {

        public static User getDefaultUser(){
            Faker faker = new Faker();
            return new User(faker.internet().emailAddress(), faker.internet().password(), faker.name().username());
        }

        public static User getUserWithoutPassword(){
            Faker faker = new Faker();
            return new User(faker.internet().emailAddress(), null, faker.name().username());
        }

        public static User getUserWithoutName(){
            Faker faker = new Faker();
            return new User(faker.internet().emailAddress(), faker.internet().password(), null);
        }
    public static User getUserWithoutEmail(){
        Faker faker = new Faker();
        return new User(null, faker.internet().password(), faker.name().username());
    }

}
