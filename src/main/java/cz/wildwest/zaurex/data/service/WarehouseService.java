package cz.wildwest.zaurex.data.service;

import cz.wildwest.zaurex.data.entity.WarehouseItem;
import cz.wildwest.zaurex.data.service.repository.WarehouseItemRepository;
import cz.wildwest.zaurex.data.service.repository.WarehouseItemVariantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseService extends GenericService<WarehouseItem, WarehouseItemRepository> {

    private final WarehouseItemVariantService warehouseItemVariantService;

    public WarehouseService(WarehouseItemRepository mainRepository, WarehouseItemVariantService warehouseItemVariantService) {
        super(mainRepository);
        this.warehouseItemVariantService = warehouseItemVariantService;
    }

    public List<WarehouseItem> findAllSellable() {
        return mainRepository.findAllBySellableEquals(true);
    }

    @Override
    public void delete(WarehouseItem item) {
        warehouseItemVariantService.deleteAll(item);
        super.delete(item);
    }

    @Override
    public void deleteAll(Iterable<WarehouseItem> objekty) {
        objekty.forEach(warehouseItemVariantService::deleteAll);
        super.deleteAll(objekty);
    }
}
