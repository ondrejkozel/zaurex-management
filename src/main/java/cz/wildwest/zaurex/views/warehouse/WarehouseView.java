package cz.wildwest.zaurex.views.warehouse;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.wildwest.zaurex.components.Badge;
import cz.wildwest.zaurex.components.gridd.GenericDataProvider;
import cz.wildwest.zaurex.components.gridd.GridFilter;
import cz.wildwest.zaurex.components.gridd.Gridd;
import cz.wildwest.zaurex.data.Role;
import cz.wildwest.zaurex.data.entity.WarehouseItem;
import cz.wildwest.zaurex.data.service.WarehouseService;
import cz.wildwest.zaurex.security.AuthenticatedUser;
import cz.wildwest.zaurex.views.MainLayout;

import javax.annotation.security.RolesAllowed;
import java.util.Set;
import java.util.stream.Stream;

@PageTitle("Sklad")
@Route(value = "warehouse/all", layout = MainLayout.class)
@RolesAllowed({"SALESMAN", "MANAGER"})
public class WarehouseView extends VerticalLayout {

    private final Gridd<WarehouseItem> grid;

    public WarehouseView(WarehouseService warehouseService, AuthenticatedUser authenticatedUser) {
        Set<Role> roles = authenticatedUser.get().orElseThrow().getRoles();
        //
        setSizeFull();
        //
        boolean editable = roles.contains(Role.MANAGER);
        grid = new Gridd<>(WarehouseItem.class,
                editable ?
                        new GenericDataProvider<>(warehouseService, WarehouseItem.class) :
                        new GenericDataProvider<>(warehouseService, WarehouseItem.class) {
                            @Override
                            //if manager isn't logged in, only sellable items are shown
                            protected Stream<WarehouseItem> fetchFromBackEnd(Query<WarehouseItem, GridFilter> query) {
                                return super.fetchFromBackEnd(query).filter(WarehouseItem::isSellable);
                            }
                        },
                WarehouseItem::new,
                editable,
                buildEditor(editable),
                "Nové zboží",
                "Zboží", "Odstranit zboží");
        configureColumns();
        //
        add(grid);
    }

    private void configureColumns() {
        grid.addColumn("Název", new TextRenderer<>(WarehouseItem::getTitle)).setFrozen(true);
        grid.addColumn("Krátký popis", new TextRenderer<>(WarehouseItem::getBriefDescription));
        grid.addColumn("Celková hodnota", new NumberRenderer<>(WarehouseItem::getTotalValue, "%.2f Kč"));
        grid.addColumn("Celkový počet", new NumberRenderer<>(WarehouseItem::getTotalQuantity, "%d ks"));
        grid.addColumn("Kategorie", new TextRenderer<>(item -> item.getCategory().getTitle()));
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
    @SuppressWarnings("FieldCanBeLocal")
    private Select<WarehouseItem.Category> category;
    @SuppressWarnings("FieldCanBeLocal")
    private Checkbox sellable;

    private BinderCrudEditor<WarehouseItem> buildEditor(boolean editable) {
        title = new TextField("Název");
        title.setRequired(true);
        briefDescription = new TextArea("Krátký popis");
        briefDescription.setClearButtonVisible(true);
        category = new Select<>(WarehouseItem.Category.values());
        category.setLabel("Kategorie");
        category.setRequiredIndicatorVisible(true);
        sellable = new Checkbox("Prodejné");
        sellable.setVisible(editable);
        //
        Binder<WarehouseItem> binder = new BeanValidationBinder<>(WarehouseItem.class);
        binder.bindInstanceFields(this);
        FormLayout formLayout = new FormLayout(title, category, briefDescription, sellable);
        formLayout.setColspan(briefDescription, 2);
        formLayout.setColspan(sellable, 2);
        return new BinderCrudEditor<>(binder, formLayout);
    }


}
