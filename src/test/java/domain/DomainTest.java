package domain;

import org.junit.Test;
import tourGuide.domain.*;

import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;
import static pl.pojo.tester.api.assertion.Method.*;

public class DomainTest {

    @Test
    public void testPojoAttractions() {
        // Arrange
        final Class<?> classUnderTest = Attractions.class;

        // Assert
        assertPojoMethodsFor(classUnderTest)
                .testing(GETTER)
                .testing(SETTER)
                .testing(CONSTRUCTOR)
                .areWellImplemented();
    }

    @Test
    public void testPojoNearbyAttractions() {
        // Arrange
        final Class<?> classUnderTest = NearbyAttractions.class;

        // Assert
        assertPojoMethodsFor(classUnderTest)
                .testing(GETTER)
                .testing(SETTER)
                .testing(CONSTRUCTOR)
                .areWellImplemented();
    }

    @Test
    public void testPojoNearbyAttractionsForUser() {
        // Arrange
        final Class<?> classUnderTest = NearbyAttractionsForUser.class;

        // Assert
        assertPojoMethodsFor(classUnderTest)
                .testing(GETTER)
                .testing(SETTER)
                .areWellImplemented();
    }

    @Test
    public void testPojoUser() {
        // Arrange
        final Class<?> classUnderTest = User.class;

        // Assert
        assertPojoMethodsFor(classUnderTest)
                .testing(GETTER)
                .testing(CONSTRUCTOR)
                .areWellImplemented();
    }

    @Test
    public void testPojoUserPreferences() {
        // Arrange
        final Class<?> classUnderTest = UserPreferences.class;

        // Assert
        assertPojoMethodsFor(classUnderTest)
                .testing(GETTER)
                .testing(SETTER)
                .testing(CONSTRUCTOR)
                .areWellImplemented();
    }

    @Test
    public void testPojoUserReward() {
        // Arrange
        final Class<?> classUnderTest = UserReward.class;

        // Assert
        assertPojoMethodsFor(classUnderTest)
                .testing(GETTER)
                .testing(CONSTRUCTOR)
                .areWellImplemented();
    }
}

