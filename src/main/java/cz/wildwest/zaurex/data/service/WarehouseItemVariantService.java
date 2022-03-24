package cz.wildwest.zaurex.data.service;

import cz.wildwest.zaurex.data.entity.WarehouseItem;
import cz.wildwest.zaurex.data.service.repository.WarehouseItemVariantRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class WarehouseItemVariantService extends GenericService<WarehouseItem.Variant, WarehouseItemVariantRepository> {

    public WarehouseItemVariantService(WarehouseItemVariantRepository mainRepository) {
        super(mainRepository);
    }

    public List<WarehouseItem.Variant> findAllByItem(WarehouseItem item) {
        return mainRepository.findAllByOfEquals(item);
    }

    @Transactional
    public void deleteAll(WarehouseItem item) {
        mainRepository.deleteAllByOfEquals(item);
    }
}
