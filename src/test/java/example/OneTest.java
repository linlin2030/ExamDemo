package example;

import static org.junit.Assert.*;
import org.junit.Test;

public class OneTest {
  @Test
  public void testFoo() throws Exception {
    One one = new One();
    //Test foo
    assertEquals("foo", one.foo());
  }


    @Test
    public void testFoo2() throws Exception {
        One one = new One();
        assertEquals("foo", one.foo());
        //Test foo
        assertEquals("foo", one.foo());
    }
}