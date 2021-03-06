package com.mycompany.storeapi.controller;

import com.mycompany.storeapi.dto.CreateOrderDto;
import com.mycompany.storeapi.dto.OrderDto;
import com.mycompany.storeapi.dto.UpdateOrderDto;
import com.mycompany.storeapi.model.Order;
import com.mycompany.storeapi.service.OrderService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final MapperFacade mapperFacade;

    public OrderController(OrderService orderService, MapperFacade mapperFacade) {
        this.orderService = orderService;
        this.mapperFacade = mapperFacade;
    }

    @GetMapping
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders()
                .stream()
                .map(order -> mapperFacade.map(order, OrderDto.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public OrderDto getOrder(@PathVariable UUID id) {
        Order order = orderService.validateAndGetOrderById(id.toString());
        return mapperFacade.map(order, OrderDto.class);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public OrderDto createOrder(@Valid @RequestBody CreateOrderDto createOrderDto) {
        Order order = mapperFacade.map(createOrderDto, Order.class);
        order.setId(UUID.randomUUID().toString());
        order = orderService.saveOrder(order);
        return mapperFacade.map(order, OrderDto.class);
    }

    @PatchMapping("/{id}")
    public OrderDto updateOrder(@PathVariable UUID id, @Valid @RequestBody UpdateOrderDto updateOrderDto) {
        Order order = orderService.validateAndGetOrderById(id.toString());
        mapperFacade.map(updateOrderDto, order);
        order = orderService.saveOrder(order);
        return mapperFacade.map(order, OrderDto.class);
    }

}
