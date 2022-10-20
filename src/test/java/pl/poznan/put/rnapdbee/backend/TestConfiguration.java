package pl.poznan.put.rnapdbee.backend;


import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

public class TestConfiguration {

    @Container
    public static MongoDBContainer container = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));

    @BeforeAll
    static void initAll() {
        container.start();
    }
}
