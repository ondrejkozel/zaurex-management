package cz.wildwest.zaurex.data.service;

import cz.wildwest.zaurex.data.entity.WarehouseItem;
import cz.wildwest.zaurex.data.service.repository.WarehouseItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public Stream<WarehouseItem> fetchTransientVariants(Stream<WarehouseItem> warehouseItemStream) {
        List<WarehouseItem.Variant> all = warehouseItemVariantService.findAll();
        return warehouseItemStream.peek(item -> item.setTransientVariants(all.stream().filter(variant -> variant.getOf().equals(item)).collect(Collectors.toSet())));
    }

    public List<WarehouseItem> fetchTransientVariants(List<WarehouseItem> list) {
        List<WarehouseItem.Variant> all = warehouseItemVariantService.findAll();
        list.forEach(item -> item.setTransientVariants(all.stream().filter(variant -> variant.getOf().equals(item)).collect(Collectors.toSet())));
        return list;
    }
}
