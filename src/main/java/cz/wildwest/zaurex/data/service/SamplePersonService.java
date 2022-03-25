package cz.wildwest.zaurex.data.service;

import cz.wildwest.zaurex.data.entity.SamplePerson;
import java.util.Optional;
import java.util.UUID;

import cz.wildwest.zaurex.data.service.repository.SamplePersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SamplePersonService extends GenericService<SamplePerson, SamplePersonRepository> {

    public SamplePersonService(SamplePersonRepository mainRepository) {
        super(mainRepository);
    }
}
