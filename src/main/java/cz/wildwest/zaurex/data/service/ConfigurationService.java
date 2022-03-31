package cz.wildwest.zaurex.data.service;

import cz.wildwest.zaurex.data.entity.Configuration;
import cz.wildwest.zaurex.data.service.repository.ConfigurationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConfigurationService {

    private final ConfigurationRepository mainRepository;

    public ConfigurationService(ConfigurationRepository mainRepository) {
        this.mainRepository = mainRepository;
    }

    public Optional<String> getValue(String key) {
        Optional<Configuration> optional = mainRepository.findByKeyEquals(key);
        return optional.map(Configuration::getValue);
    }

    public Optional<String> getValue(Configuration.StandardKey key) {
        return getValue(key.getKey());
    }

    public void save(Configuration configuration) {
        mainRepository.save(configuration);
    }

    public void save(String key, String value) {
        Optional<Configuration> optional = mainRepository.findByKeyEquals(key);
        if (optional.isPresent()) {
            Configuration configuration = optional.get();
            configuration.setValue(value);
            mainRepository.save(configuration);
        }
        else {
            mainRepository.save(new Configuration(key, value));
        }
    }

    public void save(Configuration.StandardKey key, String value) {
        save(key.getKey(), value);
    }

    public void saveAll(Iterable<Configuration> iterable) {
        mainRepository.saveAll(iterable);
    }
}
