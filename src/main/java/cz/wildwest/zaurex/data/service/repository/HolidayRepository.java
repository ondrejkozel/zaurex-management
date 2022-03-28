package cz.wildwest.zaurex.data.service.repository;

import cz.wildwest.zaurex.data.entity.Holiday;
import cz.wildwest.zaurex.data.entity.User;

import java.util.List;

public interface HolidayRepository extends GenericRepository<Holiday> {
    List<Holiday> findAllByOwnerEqualsOrderByFromDateDesc(User owner);
    List<Holiday> findAllByStatusEqualsOrderByFromDateDesc(Holiday.Status status);
    List<Holiday> deleteAllByOwnerEquals(User owner);
}
