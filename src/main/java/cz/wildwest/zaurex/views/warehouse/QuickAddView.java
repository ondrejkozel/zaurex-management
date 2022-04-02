package cz.wildwest.zaurex.views.warehouse;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.wildwest.zaurex.components.HtmlNotification;
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
        List<WarehouseItem> all = warehouseService.findAll();
        warehouseService.fetchTransientVariants(all);
        //
        VariantSelect variantSelect = new VariantSelect(all);
        //
        IntegerField integerField = buildAmountChange(variantSelect);
        Button button = variantSelect.addSubmitButton();
        button.addClickListener(event -> {
            WarehouseItem.Variant variant = variantSelect.generateModelValue();
            variant.setQuantity(variant.getQuantity() + integerField.getValue());
            warehouseItemVariantService.save(variant);
            //
            HtmlNotification.show(String.format("<span>Nyní je na skladě %d kusů <b>%s</b> (<b>%s</b>).</span>", variant.getQuantity(), variant.getOf().getTitle(), variant.getColour()));
            variantSelect.setItems(warehouseService.fetchTransientVariants(warehouseService.findAll()));
        });
        //
        add(variantSelect);
        setSizeFull();
    }

    private IntegerField buildAmountChange(VariantSelect variantSelect) {
        IntegerField amountChange = new IntegerField("Změna počtu");
        amountChange.addValueChangeListener(event -> {
            if (event.getValue() != null && event.getValue() < amountChange.getMin()) amountChange.setValue(amountChange.getMin());
        });
        amountChange.getStyle().set("width", "10em");
        amountChange.setHasControls(true);
        variantSelect.addVariantDependentNumberField(amountChange, variant -> amountChange.setMin(-variant.getQuantity()));
        return amountChange;
    }
}
