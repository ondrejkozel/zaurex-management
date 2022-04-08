package cz.wildwest.zaurex.data.service;

import cz.wildwest.zaurex.data.entity.Shift;
import cz.wildwest.zaurex.data.service.repository.ShiftRepository;
import org.springframework.stereotype.Service;

@Service
public class ShiftService extends GenericService<Shift, ShiftRepository> {
    public ShiftService(ShiftRepository mainRepository) {
        super(mainRepository);
    }
}
