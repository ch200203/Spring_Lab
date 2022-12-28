package hello.core.sigleton;

public class StatefulService {
    
//     private int price; // 상태를 유지하는 필드 10000 -> 20000

    public int order(String name, int price) {
        System.out.println("name = " + name + " price = " + price);
//         this.price = price; // 여기서 문제가 발생함
        return price;
    }


//    public int getPrice() {
//        return price;
//    }
}
