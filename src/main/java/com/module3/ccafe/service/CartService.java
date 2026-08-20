package com.module3.ccafe.service;

import com.module3.ccafe.dto.Cart;
import com.module3.ccafe.dto.CartItem;
import com.module3.ccafe.entity.CafeTable;
import com.module3.ccafe.entity.Order;
import com.module3.ccafe.entity.OrderDetail;
import com.module3.ccafe.entity.Product;
import com.module3.ccafe.entity.enums.OrderStatus;
import com.module3.ccafe.entity.enums.ProductStatus;
import com.module3.ccafe.entity.enums.TableStatus;
import com.module3.ccafe.repository.CafeTableRepository;
import com.module3.ccafe.repository.OrderDetailRepository;
import com.module3.ccafe.repository.OrderRepository;
import com.module3.ccafe.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartService {
    private static final String CART_SESSION_KEY = "CART";
    private static final String ORDER_ID_SESSION_KEY = "ORDER_ID";
    private static final String TABLE_ID_SESSION_KEY = "TABLE_ID";

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CafeTableRepository cafeTableRepository;

    public Cart getCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new Cart();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    public void addItem(Integer productId, HttpSession session) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        if (product.getStatus() != ProductStatus.AVAILABLE) {
            throw new RuntimeException("Sản phẩm hiện không khả dụng");
        }
        Cart cart = getCart(session);
        CartItem cartItem = CartItem.builder().productId(product.getProductId()).productName(product.getName()).image(product.getImage()).price(product.getPrice()).quantity(1).build();
        cart.addItem(cartItem);
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void increase(Integer productId, HttpSession session) {
        Cart cart = getCart(session);
        cart.increase(productId);
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void decrease(Integer productId, HttpSession session) {
        Cart cart = getCart(session);
        cart.decrease(productId);
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void removeItem(Integer productId, HttpSession session) {
        Cart cart = getCart(session);
        cart.removeItem(productId);
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    @Transactional
    public void placeOrder(HttpSession session,
                           Map<Integer, String> notes) {
        Cart cart = getCart(session);
        if (cart.isEmpty()) {
            throw new RuntimeException("Giỏ hàng đang trống");
        }
        Integer tableId = (Integer) session.getAttribute(TABLE_ID_SESSION_KEY);
        if (tableId == null) {
            throw new RuntimeException("Vui lòng quét mã QR tại bàn để thực hiện gọi món");
        }
        Integer orderId = (Integer) session.getAttribute(ORDER_ID_SESSION_KEY);
        Order order = null;
        if (orderId != null) {
            order = orderRepository.findById(orderId).orElse(null);
            if (order != null && order.getStatus() == OrderStatus.PAID) {
                session.removeAttribute(ORDER_ID_SESSION_KEY);
                orderId = null;
                order = null;
            }
        }
        if (orderId == null) {
            CafeTable table = cafeTableRepository.findById(tableId).orElseThrow(() -> new RuntimeException("Bàn không tồn tại"));
            table.setStatus(TableStatus.IN_SERVICE);
            cafeTableRepository.save(table);
            order = new Order();
            order.setTable(table);
            order.setCreatedAt(LocalDateTime.now());
            order.setStatus(OrderStatus.OPEN);
            order = orderRepository.save(order);
            session.setAttribute(ORDER_ID_SESSION_KEY, order.getOrderId());
        } else {
            if (order == null) {
                order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));
            }
            if (order.getStatus() != OrderStatus.OPEN) {
                throw new RuntimeException("Đơn hàng đã đóng, vui lòng quét lại mã QR");
            }
        }
        for (CartItem item : cart.getItems()) {
            Product product = productRepository.findById(item.getProductId()).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm: " + item.getProductName()));
            if (product.getStatus() != ProductStatus.AVAILABLE) {
                throw new RuntimeException("Sản phẩm không còn khả dụng: " + product.getName());
            }
            OrderDetail orderDetail = OrderDetail.builder().
                    order(order).
                    product(product).
                    quantity(item.getQuantity()).
                    price(item.getPrice()).
                    note(notes.get(item.getProductId())).build();
            orderDetailRepository.save(orderDetail);
        }
        cart.clear();
        session.setAttribute(CART_SESSION_KEY, cart);
    }
}
