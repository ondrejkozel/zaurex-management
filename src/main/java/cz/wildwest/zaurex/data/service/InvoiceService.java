package cz.wildwest.zaurex.data.service;

import cz.wildwest.zaurex.data.entity.Invoice;
import cz.wildwest.zaurex.data.service.repository.InvoiceRepository;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService extends GenericService<Invoice, InvoiceRepository> {
    public InvoiceService(InvoiceRepository mainRepository) {
        super(mainRepository);
    }
    
    @Override
    public List<Invoice> findAll() {
        List<Invoice> all = super.findAll().stream().sorted(Comparator.comparing(Invoice::getIssuedAt)).collect(Collectors.toList());
        Collections.reverse(all);
        return all;
    }
}
