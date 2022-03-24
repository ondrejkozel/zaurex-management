package cz.wildwest.zaurex.data.service;

import cz.wildwest.zaurex.data.entity.Holiday;
import cz.wildwest.zaurex.data.entity.User;
import cz.wildwest.zaurex.data.service.repository.HolidayRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HolidayService extends GenericService<Holiday, HolidayRepository> {

    public HolidayService(HolidayRepository mainRepository) {
        super(mainRepository);
    }

    public List<Holiday> findAll(User user) {
        return mainRepository.findAllByOwnerEqualsOrderByFromDateDesc(user);
    }

    public List<Holiday> findAllPending() {
        return mainRepository.findAllByStatusEqualsOrderByFromDateDesc(Holiday.Status.PENDING);
    }

}
