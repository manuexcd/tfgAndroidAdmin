package spring.es.admintfg.pagination;

import spring.es.admintfg.dto.ProductDTO;

public class ProductsPage extends Page<ProductDTO> {
    @Override
    public Class<ProductDTO> getClazz() {
        return ProductDTO.class;
    }
}
