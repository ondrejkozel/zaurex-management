package cz.wildwest.zaurex.data.service;

import cz.wildwest.zaurex.data.entity.Holiday;
import cz.wildwest.zaurex.data.service.repository.HolidayRepository;
import org.springframework.stereotype.Service;

@Service
public class HolidayService extends GenericService<Holiday, HolidayRepository> {

    public HolidayService(HolidayRepository mainRepository) {
        super(mainRepository);
    }
    
}
