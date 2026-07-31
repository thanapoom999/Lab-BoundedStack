import java.util.NoSuchElementException;

public class TestRunner {

    private static int totalTests = 0;
    private static int passedTests = 0;

    public static void main(String[] args) {
        System.out.println("=== Starting BoundedStack Test Suite (25 Target Cases) ===");

        testInitialization();
        testPushOperations();
        testPopAndPeekOperations();
        testStateTransitionsAndCycles();
        testProducerFilter(); // <-- เปิดใช้งานการทดสอบตัว Producer ตรงนี้

        System.out.println("\n=== Test Results Summary ===");
        System.out.println("Total Tests Run: " + totalTests);
        System.out.println("Passed: " + passedTests);
        System.out.println("Failed: " + (totalTests - passedTests));
    }

    private static void check(boolean condition, String testName) {
        totalTests++;
        if (condition) {
            System.out.println("[PASS] " + testName);
            passedTests++;
        } else {
            System.err.println("[FAIL] " + testName);
        }
    }

    // กำหนดขอบเขตข้อจำกัด pass 1-4
    private static void testInitialization() {
        BoundedStack stack = new BoundedStack(5); // สร้างstack5 เช็คว่าว่างไหม
        check(stack.isEmpty(), "Init: Stack should be empty.");
        check(stack.size() == 0, "Init: Size should be 0."); //เช็คว่าข้อมูลเป็น0ไหม

        try {
            new BoundedStack(0); //พยายามสร้าง Stack ขนาด 0 ระบบต้องโยน IllegalArgumentException ออกมาอย่างถูกต้อง  
            check(false, "Init: Capacity 0 should fail.");
        } catch (IllegalArgumentException e) {
            check(true, "Init: Capacity 0 correctly throws IllegalArgumentException.");
        }

        try {
            new BoundedStack(-3); //พยายามสร้าง Stack ขนาดติดลบ (-3) ระบบต้องโยน IllegalArgumentException ออกมาอย่างถูกต้อง  
            check(false, "Init: Negative capacity should fail.");
        } catch (IllegalArgumentException e) {
            check(true, "Init: Negative capacity correctly throws IllegalArgumentException.");
        }
    }

    // --- ทดสอบขอบเขต 5-11
    private static void testPushOperations() {
        BoundedStack stack = new BoundedStack(2);
        
        stack.push("A"); 
        check(stack.size() == 1, "Push: Size is 1 after pushing 'A'."); //ใส่ A ลงไป แล้วเช็คว่าจำนวนข้อมูลเพิ่มขึ้นเป็น 1
        check(stack.peek().equals("A"), "Push: Top element is 'A'."); //เช็คว่าข้อมูลบนสุด ณ ตอนนั้นคือ "A" (

        stack.push("B");
        check(stack.isFull(), "Push: Stack is full at capacity."); //ใส่พิ่มอีกตัว B ใน Stack ที่มีความจุ 2 แล้วเช็คว่าสถานะคือเต็มแล้ว (isFull() เป็น true)  
        check(stack.size() == 2, "Push: Size matches capacity."); //เช็คว่าจำนวนข้อมูลเท่ากับความจุสูงสุดพอดี

        try { 
            stack.push("C"); //เมื่อ Stack เต็มแล้ว (ความจุ 2) พยายามใส่ข้อมูล "C" เพิ่มเข้าไป ระบบต้องโยน IllegalStateException ออกมา (ป้องกันข้อมูลล้น)
            check(false, "Push: Overflow should fail.");
        } catch (IllegalStateException e) {
            check(true, "Push: Overflow correctly throws IllegalStateException.");
        }

        try {
            stack.push(null); //พยายามใส่ข้อมูลที่เป็นค่าว่าง (null) ระบบต้องโยน NullPointerException ออกมา (ห้ามใส่ null)  
            check(false, "Push: Null element should fail.");
        } catch (NullPointerException e) {
            check(true, "Push: Null item correctly throws NullPointerException.");
        }

        BoundedStack capOne = new BoundedStack(1); //ตรวจสอบขอบเขตล่างสุดของความจุ โดยสร้าง Stack ขนาด 1 แล้วใส่ "X" เข้าไป จากนั้นเช็คว่ามันขึ้นสถานะเต็มทันที (isFull() เป็น true)
        capOne.push("X");
        check(capOne.isFull(), "Push: Boundary check - capacity 1 is full.");
    }

    //ทดสอบการทำงานของ Pop และ Peek pass12-18
    private static void testPopAndPeekOperations() { 
        BoundedStack stack = new BoundedStack(3);
        stack.push("First");
        stack.push("Second"); //ใส่ "First" และ "Second" ลงไป แล้วเช็คว่า peek() ได้คำว่า "Second"

        check(stack.peek().equals("Second"), "Peek: Returns top element without removing.");
        check(stack.size() == 2, "Peek: Size remains unchanged."); //เช็คว่าหลังจาก peek() แล้ว จำนวนข้อมูลต้องคงเดิม ไม่ลดลง (size() == 2)

        check(stack.pop().equals("Second"), "Pop: Returns 'Second' (LIFO)."); //เรียก pop() แล้วเช็คว่าข้อมูลที่หลุดออกมาคือ "Second" (ข้อมูลล่าสุดต้องออกก่อน)
        check(stack.size() == 1, "Pop: Size decreased to 1."); //เช็คว่าหลังจาก pop() ข้อมูลออกไปแล้ว จำนวนข้อมูลต้องลดลงเหลือ 1 (size() == 1)
        check(stack.peek().equals("First"), "Pop: New top is 'First'."); //เช็คว่าข้อมูลตัวบนสุดถัดมากลายเป็น "First" (peek().equals("First"))

        stack.pop(); // Emptying stack
        try {
            stack.pop();
            check(false, "Pop: Underflow should fail.");
        } catch (NoSuchElementException e) {
            check(true, "Pop: Underflow correctly throws NoSuchElementException."); //หลังจากเคลียร์ข้อมูลจน Stack ว่างแล้ว พยายามสั่ง pop() อีกครั้ง ระบบต้องโยน NoSuchElementException ออกมา (ดึงข้อมูลจากคลังว่างเปล่าไม่ได้)
        }

        try {
            stack.peek();
            check(false, "Peek: Peeking empty stack should fail.");
        } catch (NoSuchElementException e) {
            check(true, "Peek: Peeking empty stack correctly throws NoSuchElementException."); //พยายามสั่ง peek() ในขณะที่ Stack ว่างเปล่า ระบบต้องโยน NoSuchElementException ออกมา  
        }
    }

    // --- 4. Test State Transitions & Cycles pass19-25
    private static void testStateTransitionsAndCycles() {
        BoundedStack stack = new BoundedStack(2);
        check(stack.isEmpty() && !stack.isFull(), "Cycle: Initial Empty State."); //Stack ต้องว่างและต้องไม่เต็มไปพร้อม ๆ กัน
        
        stack.push(1);
        check(!stack.isEmpty() && !stack.isFull(), "Cycle: Partially Filled State."); //ใส่ข้อมูลไป 1 ตัว (จากความจุ 2) แล้วเช็คสถานะกึ่งกลาง ว่าต้องไม่ว่างและต้องไม่เต็ม
        
        stack.push(2);
        check(!stack.isEmpty() && stack.isFull(), "Cycle: Full State."); //ใส่ข้อมูลตัวที่ 2 จนเต็ม แล้วเช็คสถานะเต็ม (Full State) ว่าต้องไม่ว่างและต้องเต็ม

        // Sequential Push/Pop Verification
        check(stack.pop().equals(2), "Cycle: Pop 1 matches."); //เรียก pop() ออกมา ต้องได้ค่า 2 ตามลำดับที่ใส่เข้าไป
        stack.push(3);
        check(stack.peek().equals(3), "Cycle: Peek matches after mix."); //ลองใส่ข้อมูล 3 สลับเข้าไปใหม่ แล้วเช็คว่า peek() ต้องได้ค่า 3
        check(stack.pop().equals(3), "Cycle: Pop 2 matches."); //เรียก pop() ออกมา ต้องได้ค่า 3 อย่างถูกต้อง
        check(stack.pop().equals(1), "Cycle: Pop 3 matches."); //เรียก pop() อีกครั้งเพื่อเอาตัวแรกสุดออก ต้องได้ค่า 1
    }

    private static void testProducerFilter() {
        BoundedStack stack = new BoundedStack(4);
        stack.push("Apple");
        stack.push("Banana");
        stack.push("Avocado");
        stack.push("Cherry");

        // กรองเอาเฉพาะข้อมูลที่ขึ้นต้นด้วยตัวอักษร "A"
        BoundedStack filtered = stack.filter(obj -> obj.toString().startsWith("A"));

        check(filtered.size() == 2, "Producer: Filtered stack size should be 2.");
        check(filtered.pop().equals("Avocado"), "Producer: Top filtered element is 'Avocado'.");
        check(filtered.pop().equals("Apple"), "Producer: Next filtered element is 'Apple'.");
        check(filtered.isEmpty(), "Producer: Filtered stack is now empty.");
        
        // ตรวจสอบว่าสแตกต้นทาง (Original Stack) ต้องไม่โดนดัดแปลงหรือพัง
        check(stack.size() == 4, "Producer: Original stack size must remain unchanged.");
    }
}