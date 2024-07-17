package com.example.orderservice;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    private OrderRepository orderRepository;
    private PaymentService paymentService;
    private OrderService orderService;

    @Before
    public void setUp() {
        orderRepository = Mockito.mock(OrderRepository.class);
        paymentService = Mockito.mock(PaymentService.class);
        orderService = new OrderService(orderRepository, paymentService);
    }

    @Test
    public void testPlaceOrder_Success() {
        Order order = new Order();
        order.setAmount(100.0);

        when(paymentService.processPayment(order.getAmount())).thenReturn(true);

        boolean result = orderService.placeOrder(order);

        assertTrue(result);
        verify(orderRepository).save(order);
    }

    @Test
    public void testPlaceOrder_Failure() {
        Order order = new Order();
        order.setAmount(100.0);

        when(paymentService.processPayment(order.getAmount())).thenReturn(false);

        boolean result = orderService.placeOrder(order);

        assertFalse(result);
        verify(orderRepository, never()).save(order);
    }

    @Test
    public void testGetOrderById() {
        Order order = new Order();
        order.setId(1);

        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCancelOrder_OrderNotFound() {
        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        orderService.cancelOrder(1);
    }

    @Test
    public void testCancelOrder_Success() {
        Order order = new Order();
        order.setId(1);

        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        orderService.cancelOrder(1);

        verify(orderRepository).delete(order);
    }

    @Test
    public void testListAllOrders() {
        orderService.listAllOrders();

        verify(orderRepository).findAll();
    }
}