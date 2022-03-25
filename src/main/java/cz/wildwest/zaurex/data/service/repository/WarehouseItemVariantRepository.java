package cz.wildwest.zaurex.data.service.repository;

import cz.wildwest.zaurex.data.entity.WarehouseItem;

import java.util.List;

public interface WarehouseItemVariantRepository extends GenericRepository<WarehouseItem.Variant> {
    List<WarehouseItem.Variant> findAllByOfEquals(WarehouseItem of);
    void deleteAllByOfEquals(WarehouseItem of);
}
