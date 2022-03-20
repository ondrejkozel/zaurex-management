package cz.wildwest.zaurex.data.service;

import cz.wildwest.zaurex.data.entity.WarehouseItem;
import cz.wildwest.zaurex.data.service.repository.WarehouseItemRepository;
import org.springframework.stereotype.Service;

@Service
public class WarehouseItemService extends GenericService<WarehouseItem, WarehouseItemRepository> {

    public WarehouseItemService(WarehouseItemRepository mainRepository) {
        super(mainRepository);
    }
}
