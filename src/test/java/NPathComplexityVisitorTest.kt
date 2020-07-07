import com.gavwu.plugins.intellij.complexity.markers.NPathComplexityVisitor
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase

internal class NPathComplexityVisitorTest : JavaCodeInsightFixtureTestCase() {

    fun test_simpleForLoopIsCorrect() {
        //language=JAVA
        myFixture.addClass(
            """
            public class NPathComplexityTest {
                /**
                 * Just a simple for loop.
                 * Expected complexity is 3.
                 **/
                public void forLoopTest() {
                    // complexity is (init + cond + update + body + 1), = (0 + 0 + 0 + 2 + 1) = 3
                    for(int i = 0 ; i < 10 ; i++){
                        // provides a complexity of 2
                        if(i % 2 == 0){
                            System.out.println("even");
                        } else {
                            System.out.println("odd");
                        }
                    }
                }
            }
        """.trimIndent()
        )

        val testClass = myFixture.findClass("NPathComplexityTest")
        val testMethods = testClass.findMethodsByName("forLoopTest", false)
        assertEquals(1, testMethods.size)
        val testMethod = testMethods.first()

        val visitor = NPathComplexityVisitor()
        testMethod.accept(visitor)
        assertEquals(3, visitor.complexity)
    }

    fun test_forLoopAndThenIfStatement_multiplyComplexity() {
        //language=JAVA
        myFixture.addClass(
            """
            
            public class NPathComplexityTest {
                /**
                 * Just a simple for loop.
                 * Expected complexity is 3 * 2 = 6.
                 **/
                public void forLoopTest() {
                    // complexity is (init + cond + update + body + 1), = (0 + 0 + 0 + 2 + 1) = 3
                    for(int i = 0 ; i < 10 ; i++){
                        // provides a complexity of 2
                        if(i % 2 == 0){
                            System.out.println("even");
                        } else {
                            System.out.println("odd");
                        }
                    }
                    // expected complexity is 2
                    if("true" == true){
                        System.out.println("Unexpected");
                    } else {
                        System.out.println("Expected");
                    }
                }
            }
        """.trimIndent()
        )

        val testClass = myFixture.findClass("NPathComplexityTest")
        val testMethods = testClass.findMethodsByName("forLoopTest", false)
        assertEquals(1, testMethods.size)
        val testMethod = testMethods.first()

        val visitor = NPathComplexityVisitor()
        testMethod.accept(visitor)
        assertEquals(6, visitor.complexity)
    }


    fun test_simpleThreeBranchSwitchStatement_isSumOfBranches() {
        //language=JAVA
        myFixture.addClass(
            """
            
            public class NPathComplexityTest {
                /**
                 * Just a simple switch statement.
                 * Expected complexity is (0 + 3 + 2 + 1 + 1) = 7
                 **/
                public void simpleSwitchTest() {
                    // complexity is (cond + case 0 + case 1 + case 2 + default)
                    switch((int)(Math.random() * 3)) {
                        case 0:
                            if(Math.random() > 0.5){
                                // dummy
                            } else if(Math.random() > 0.5){
                                // dummy
                            } else {
                                // dummy
                            }
                        case 1:
                            if(Math.random() > 0.5) {
                                // doesn't matter
                            } else {
                                // also irrelevant
                            }
                            break;
                        case 2:
                        default:
                            // also doesn't matter
                    }
                }
            }
        """.trimIndent()
        )

        val testClass = myFixture.findClass("NPathComplexityTest")
        val testMethods = testClass.findMethodsByName("simpleSwitchTest", false)
        assertEquals(1, testMethods.size)
        val testMethod = testMethods.first()

        val visitor = NPathComplexityVisitor()
        testMethod.accept(visitor)
        assertEquals(7, visitor.complexity)
    }
    // TODO: make (and enforce) test that handles fallthroughs correctly


    fun test_simpleTryCatchIsTryTimes1PlusSumOfCatch() {
        //language=JAVA
        myFixture.addClass(
            """
            public class NPathComplexityTest {
                /**
                 * Just a simple try/catch/catch.
                 * Expected complexity is (3) * (1 + 2 + 1) = 12
                 **/
                public void tryCatchCatchTest() {
                    try {
                        // 3
                        if(true){
                            
                        } else if(true){
                            
                        }
                    } catch(RuntimeException e){
                        // 2
                        if(false) {
                            
                        }
                    } catch(Exception e){
                        // 1
                    }
                }
            }
        """.trimIndent()
        )

        val testClass = myFixture.findClass("NPathComplexityTest")
        val testMethods = testClass.findMethodsByName("tryCatchCatchTest", false)
        assertEquals(1, testMethods.size)
        val testMethod = testMethods.first()

        val visitor = NPathComplexityVisitor()
        testMethod.accept(visitor)
        assertEquals(12, visitor.complexity)
    }

    fun test_ternaryIsSumOfCondAndBranches() {
        //language=JAVA
        myFixture.addClass(
            """
            public class NPathComplexityTest {
                /**
                 * Just a simple ternary.
                 * Expected complexity is (2) * (3 + 4) = 14
                 **/
                public boolean simpleTernaryTest() {
                    return (this && that) ? // 2
                        ( a || b || c || d ) : // 3
                        (a || b || c || d || e); // 4
                }
            }
        """.trimIndent()
        )

        val testClass = myFixture.findClass("NPathComplexityTest")
        val testMethods = testClass.findMethodsByName("simpleTernaryTest", false)
        assertEquals(1, testMethods.size)
        val testMethod = testMethods.first()

        val visitor = NPathComplexityVisitor()
        testMethod.accept(visitor)
        assertEquals(2 * 3 * 4, visitor.complexity)
    }
}