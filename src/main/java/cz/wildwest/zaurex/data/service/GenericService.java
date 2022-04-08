package cz.wildwest.zaurex.data.service;


import cz.wildwest.zaurex.data.AbstractEntity;
import cz.wildwest.zaurex.data.service.repository.GenericRepository;

import java.util.List;
import java.util.Optional;

public abstract class GenericService<T extends AbstractEntity, R extends GenericRepository<T>> implements Truncatable {

    protected final R mainRepository;

    public GenericService(R mainRepository) {
        this.mainRepository = mainRepository;
    }

    public Optional<T> find(long id) {
        return mainRepository.findById(id);
    }

    public List<T> findAll() {
        return mainRepository.findAll();
    }

    public void save(T objekt) {
        mainRepository.save(objekt);
    }

    public void saveAll(Iterable<T> objekty) {
        mainRepository.saveAll(objekty);
    }

    public long count() {
        return mainRepository.count();
    }

    public void delete(T objekt) {
        mainRepository.delete(objekt);
    }

    public void deleteAll(Iterable<T> objekty) {
        mainRepository.deleteAll(objekty);
    }

    @Override
    public void truncate() {
        mainRepository.deleteAll();
    }
}
