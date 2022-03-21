package cz.wildwest.zaurex.data.service.repository;

import cz.wildwest.zaurex.data.entity.WarehouseItem;

import java.util.List;

public interface WarehouseItemRepository extends GenericRepository<WarehouseItem> {
    List<WarehouseItem> findAllBySellableEquals(boolean sellable);
}
