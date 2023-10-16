package sample.cafekiosk.spring.domain.product;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductType {

    HANDMADE("제조 음료"),
    BOTTLE("병 음료"),
    BAKERY("베이커리");

    private final String text;

    /**
     * 재고와 관련 있는 상품 타입은 병 음료, 베이커리이다
     */
    public static boolean containsStockType(ProductType type) {
        return List.of(BOTTLE, BAKERY).contains(type);
    }

}
