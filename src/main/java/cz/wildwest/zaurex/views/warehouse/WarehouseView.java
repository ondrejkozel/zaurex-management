package cz.wildwest.zaurex.views.warehouse;

import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.wildwest.zaurex.components.Badge;
import cz.wildwest.zaurex.components.gridd.GenericDataProvider;
import cz.wildwest.zaurex.components.gridd.Gridd;
import cz.wildwest.zaurex.data.entity.WarehouseItem;
import cz.wildwest.zaurex.data.service.WarehouseService;
import cz.wildwest.zaurex.views.MainLayout;

import javax.annotation.security.RolesAllowed;

@PageTitle("Sklad")
@Route(value = "warehouse/all", layout = MainLayout.class)
@RolesAllowed({"SALESMAN", "MANAGER"})
public class WarehouseView extends VerticalLayout {

    private final WarehouseService warehouseService;
    private final Gridd<WarehouseItem> grid;

    public WarehouseView(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
        //
        setSizeFull();
        //
        grid = new Gridd<>(WarehouseItem.class);
        grid.setDataProvider(new GenericDataProvider<>(warehouseService, WarehouseItem.class));
        grid.setNewObjectSupplier(WarehouseItem::new);
        configureColumns();
        configureEditor();
        //
        add(grid);
    }

    private void configureColumns() {
        grid.addEditColumn("Název", WarehouseItem::getTitle, new TextRenderer<>(WarehouseItem::getTitle))
                .text((item, title) -> {
                    if (title.length() > 0 && title.length() <= 100) item.setTitle(title);
                });
        grid.addColumn("Krátký popis", new TextRenderer<>(WarehouseItem::getBriefDescription));
        grid.addColumn("Celková hodnota", new NumberRenderer<>(WarehouseItem::getTotalValue, "%.2f Kč"));
        grid.addColumn("Celkový počet", new NumberRenderer<>(WarehouseItem::getTotalQuantity, "%d ks"));
        grid.addColumn("Upozornění", new ComponentRenderer<>(item -> {
            HorizontalLayout badgeLayout = new HorizontalLayout();
            badgeLayout.addClassName("badge-container");
            if (item.isOutOfStock()) {
                Badge soldOut = new Badge("Vyprodáno", Badge.BadgeVariant.ERROR);
                soldOut.setTitle("Některá z variant produktu byla vyprodána.");
                badgeLayout.add(soldOut);
            }
            if (item.getVariants().isEmpty()) {
                Badge noVariants = new Badge("Žádné varianty", Badge.BadgeVariant.DEFAULT);
                noVariants.setTitle("Nebyly vytvořeny žádné varianty produktu.");
                badgeLayout.add(noVariants);
            }
            if (!item.isSellable()) {
                Badge notForSale = new Badge("Neprodejné", Badge.BadgeVariant.CONTRAST);
                notForSale.setTitle("Tato položka není určena k prodeji.");
                badgeLayout.add(notForSale);
            }
            return badgeLayout;
        }));
    }

    @SuppressWarnings("FieldCanBeLocal")
    private TextField title;
    @SuppressWarnings("FieldCanBeLocal")
    private TextArea briefDescription;

    private void configureEditor() {
        title = new TextField("Název");
        title.setRequired(true);
        briefDescription = new TextArea("Krátký popis");
        briefDescription.setClearButtonVisible(true);
        //
        Binder<WarehouseItem> binder = new BeanValidationBinder<>(WarehouseItem.class);
        binder.bindInstanceFields(this);
        CrudEditor<WarehouseItem> editor = new BinderCrudEditor<>(binder, new FormLayout(title, briefDescription));
        grid.setEditor(editor, "Nové zboží", "Upravit zboží", "Odstranit zboží");
    }


}
