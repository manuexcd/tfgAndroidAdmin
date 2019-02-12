package spring.es.admintfg.pagination;

import spring.es.admintfg.dto.OrderDTO;

public class OrdersPage extends Page<OrderDTO> {
    @Override
    public Class<OrderDTO> getClazz() {
        return OrderDTO.class;
    }
}
