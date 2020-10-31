package ecnu.edu.wclong.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PathProcessorTest {

    private PathProcessor pathProcessor;

    @BeforeEach
    public void init() {
        this.pathProcessor = new PathProcessor(3, 5);
    }

    @Test
    public void testSubPath() {
        System.out.println(pathProcessor.isSubPath(13, 5));
    }

}
