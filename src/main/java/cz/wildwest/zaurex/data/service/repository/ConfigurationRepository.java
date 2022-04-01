package cz.wildwest.zaurex.data.service.repository;

import cz.wildwest.zaurex.data.entity.Configuration;

import java.util.Optional;

public interface ConfigurationRepository extends GenericRepository<Configuration> {
    Optional<Configuration> findByKeyEquals(String key);
}
