package cz.wildwest.zaurex.views.warehouse;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.wildwest.zaurex.components.HtmlNotification;
import cz.wildwest.zaurex.components.VariantSelect;
import cz.wildwest.zaurex.data.entity.WarehouseItem;
import cz.wildwest.zaurex.data.service.WarehouseItemVariantService;
import cz.wildwest.zaurex.data.service.WarehouseService;
import cz.wildwest.zaurex.views.MainLayout;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@PageTitle("Rychle naskladnit")
@Route(value = "quick_add", layout = MainLayout.class)
@RolesAllowed({"WAREHOUSEMAN", "MANAGER"})
public class QuickAddView extends VerticalLayout {

    public QuickAddView(WarehouseService warehouseService, WarehouseItemVariantService warehouseItemVariantService) {
        List<WarehouseItem> all = warehouseService.findAllSellable();
        warehouseService.fetchTransientVariants(all);
        //
        VariantSelect variantSelect = new VariantSelect(all);
        //
        variantSelect.addSubmitButtonClickListener(event -> {
            WarehouseItem.Variant variant = variantSelect.generateModelValue();
            variant.setQuantity(variant.getQuantity() + variantSelect.getAmount());
            warehouseItemVariantService.save(variant);
            //
            HtmlNotification.show(String.format("<span>Nyní je na skladě %d kusů <b>%s</b> (<b>%s</b>).</span>", variant.getQuantity(), variant.getOf().getTitle(), variant.getColour()));
            variantSelect.setItems(warehouseService.fetchTransientVariants(warehouseService.findAll()));
        });
        //
        add(variantSelect);
        setSizeFull();
    }
}
