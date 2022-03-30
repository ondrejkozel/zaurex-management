package cz.wildwest.zaurex.data.service;

import cz.wildwest.zaurex.data.entity.Invoice;
import cz.wildwest.zaurex.data.service.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService extends GenericService<Invoice, InvoiceRepository> {
    public InvoiceService(@Autowired InvoiceRepository mainRepository) {
        super(mainRepository);
    }
}
