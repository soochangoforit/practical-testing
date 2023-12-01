package sample.cafekiosk.spring.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static sample.cafekiosk.spring.domain.order.OrderStatus.CANCELED;
import static sample.cafekiosk.spring.domain.order.OrderStatus.PAYMENT_COMPLETED;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.SELLING;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sample.cafekiosk.spring.PersistenceJpaSupport;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

class OrderRepositoryTest extends PersistenceJpaSupport {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("특정 주문 상태에 따라 주문을 조회한다")
    @Test
    void findOrdersBy() {
        //given
        Product product1 = createProduct("아메리카노", 3000, HANDMADE, SELLING);
        Product product2 = createProduct("카페라떼", 4000, HANDMADE, SELLING);
        Product product3 = createProduct("카푸치노", 5000, HANDMADE, SELLING);
        List<Product> products = List.of(product1, product2, product3);

        LocalDateTime startTime = LocalDateTime.of(2023, 10, 19, 0, 0);
        LocalDateTime orderTime = LocalDateTime.of(2023, 10, 19, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2023, 10, 20, 0, 0);

        Order completedOrder = createOrder(orderTime, PAYMENT_COMPLETED, products);
        Order canceledOrder = createOrder(orderTime, CANCELED, products);

        // when
        List<Order> orders = orderRepository.findOrdersBy(startTime, endTime, PAYMENT_COMPLETED);

        // then
        assertThat(orders).hasSize(1)
                .extracting("id", "orderStatus", "totalPrice", "registeredDateTime")
                .containsExactlyInAnyOrder(
                        tuple(1L, PAYMENT_COMPLETED, 12000, orderTime)
                );
    }

    private Product createProduct(String name, int price, ProductType productType, ProductSellingStatus productSellingStatus) {
        Product product = Product.builder()
                .name(name)
                .price(price)
                .type(productType)
                .sellingStatus(productSellingStatus)
                .build();
        return productRepository.save(product);
    }

    private Order createOrder(LocalDateTime now, OrderStatus orderStatus, List<Product> products) {
        Order order = Order.builder()
                .products(products)
                .orderStatus(orderStatus)
                .registeredDateTime(now)
                .build();
        return orderRepository.save(order);
    }

    @DisplayName("찾고자 하는 시간 안에 있는 주문을 조회한다")
    @Test
    void findOrdersBy2() {
        //given
        Product product1 = createProduct("아메리카노", 3000, HANDMADE, SELLING);
        Product product2 = createProduct("카페라떼", 4000, HANDMADE, SELLING);
        Product product3 = createProduct("카푸치노", 5000, HANDMADE, SELLING);
        List<Product> products = List.of(product1, product2, product3);

        LocalDateTime startTime = LocalDateTime.of(2023, 10, 19, 0, 0);
        LocalDateTime orderTime = LocalDateTime.of(2023, 10, 19, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2023, 10, 20, 0, 0);
        LocalDateTime overTime = LocalDateTime.of(2023, 10, 20, 10, 0);

        Order completedOrder = createOrder(orderTime, PAYMENT_COMPLETED, products);
        Order overTimeOrder = createOrder(overTime, PAYMENT_COMPLETED, products);

        // when
        List<Order> orders = orderRepository.findOrdersBy(startTime, endTime, PAYMENT_COMPLETED);

        // then
        assertThat(orders).hasSize(1)
                .extracting("id", "orderStatus", "totalPrice", "registeredDateTime")
                .containsExactlyInAnyOrder(
                        tuple(1L, PAYMENT_COMPLETED, 12000, orderTime)
                );
    }

}
