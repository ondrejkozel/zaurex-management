package cz.wildwest.zaurex.views.quickAdd;

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
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@PageTitle("Rychle naskladnit")
@Route(value = "quick_add", layout = MainLayout.class)
@RolesAllowed({"WAREHOUSEMAN", "MANAGER"})
public class QuickAddView extends VerticalLayout {

    /**
     * @param afterSaveNotification list of consumers which accept notification html
     *                              last minute workaround of a bug when notifications haven't been showing in Warehouse view
     */
    public QuickAddView(WarehouseService warehouseService, WarehouseItemVariantService warehouseItemVariantService, Consumer<String>... afterSaveNotification) {
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
            String notificationHtml = String.format("<span>Nyní je na skladě <b>%d</b> kusů %s (%s).</span>", variant.getQuantity(), variant.getOf().getTitle(), variant.getColour());
            if (afterSaveNotification.length == 0) HtmlNotification.show(notificationHtml);
            Arrays.stream(afterSaveNotification).forEach(stringConsumer -> stringConsumer.accept(notificationHtml));
            //
            variantSelect.setItems(warehouseService.fetchTransientVariants(warehouseService.findAll()));
        });
        //
        add(variantSelect);
        setSizeFull();
    }
}
