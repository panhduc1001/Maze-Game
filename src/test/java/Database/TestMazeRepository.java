package Database;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestMazeRepository {
    @Test
    void testSingleton() {
        MazeRepository mr = MazeRepository.getInstance();
        MazeRepository mr2 = MazeRepository.getInstance();
        assertEquals(mr, mr2, "Multiple maze repositories were created (should only be one)");
    }

    // Test with manually obtained expected id (making a mock database would be basically pointless for this test)
//    @Test
//    void testGetNextId() {
//        assertEquals(MazeRepository.getInstance().GetNextId(), 21);
//    }
}
