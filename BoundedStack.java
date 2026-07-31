import java.util.NoSuchElementException;

public class BoundedStack {
    // Abstraction Function (AF):
    //   AF(r) = สแตกที่มีสมาชิกเก็บอยู่เรียงตามลำดับจากล่างสุดไปบนสุด คือ 
    //           r.elements[0], r.elements[1], ..., r.elements[r.size - 1]
    //           โดยมีความจุสูงสุด (Capacity) เท่ากับ r.elements.length
    //
    // Representation Invariant (RI):
    //   - r.elements ต้องไม่เป็น null
    //   - 0 <= r.size <= r.elements.length
    //   - สำหรับทุกดัชนี i โดยที่ r.size <= i < r.elements.length, 
    //     r.elements[i] ต้องเป็น null เสมอ (ไม่มี Reference รั่วไหล)

 

    private final Object[] elements;
    private int size;

    // Creator
     
    public BoundedStack(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero.");
        }
        this.elements = new Object[capacity];
        this.size = 0;
        checkRep();
    }

    // รับประกันว่าค่าคงที่ของการแสดงผลนั้นเป็นจริง
     
    private void checkRep() {
        assert elements != null : "Array cannot be null.";
        assert size >= 0 && size <= elements.length : "Invalid size bounds.";
        for (int i = size; i < elements.length; i++) {
            assert elements[i] == null : "Leaked reference detected past stack size.";
        }
    }

    // Mutator & Producer

    public void push(Object element) {
        if (element == null) {
            throw new NullPointerException("Cannot push null element.");
        }
        if (isFull()) {
            throw new IllegalStateException("Stack is full.");
        }
        elements[size++] = element;
        checkRep();
    }

    // Mutator
     
    public Object pop() {
        if (isEmpty()) {
            throw new NoSuchElementException("Stack is empty.");
        }
        size--;
        Object topElement = elements[size];
        elements[size] = null; // Prevent memory leaks
        checkRep();
        return topElement;
    }

    // Observer
     
    public Object peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Stack is empty.");
        }
        return elements[size - 1];
    }

    // Observers for Stack State
    public int size() { return this.size; }
    public boolean isEmpty() { return this.size == 0; }
    public boolean isFull() { return this.size == elements.length; }

    // Producer (เพิ่มเข้ามาเพื่อให้ตรงตามเงื่อนไข C2 และรองรับการทดสอบใน TestRunner)
    public BoundedStack filter(java.util.function.Predicate<Object> predicate) {
        if (predicate == null) {
            throw new NullPointerException("Predicate cannot be null.");
        }
        BoundedStack newStack = new BoundedStack(this.elements.length);
        for (int i = 0; i < this.size; i++) {
            if (predicate.test(this.elements[i])) {
                newStack.push(this.elements[i]);
            }
        }
        return newStack;
    }
}