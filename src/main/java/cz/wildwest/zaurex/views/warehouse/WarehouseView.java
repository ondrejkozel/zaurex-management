package cz.wildwest.zaurex.views.warehouse;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.*;
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
import cz.wildwest.zaurex.data.AbstractEntity;
import cz.wildwest.zaurex.data.Role;
import cz.wildwest.zaurex.data.entity.WarehouseItem;
import cz.wildwest.zaurex.data.service.WarehouseItemVariantService;
import cz.wildwest.zaurex.data.service.WarehouseService;
import cz.wildwest.zaurex.security.AuthenticatedUser;
import cz.wildwest.zaurex.views.MainLayout;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@PageTitle("Sklad")
@Route(value = "warehouse/all", layout = MainLayout.class)
@RolesAllowed({"SALESMAN", "MANAGER"})
public class WarehouseView extends VerticalLayout {

    private final Gridd<WarehouseItem> grid;
    private final WarehouseItemVariantService warehouseItemVariantService;

    private final boolean editable;

    public WarehouseView(WarehouseService warehouseService, AuthenticatedUser authenticatedUser, WarehouseItemVariantService warehouseItemVariantService) {
        this.warehouseItemVariantService = warehouseItemVariantService;
        Set<Role> roles = authenticatedUser.get().orElseThrow().getRoles();
        //
        setSizeFull();
        //
        editable = roles.contains(Role.MANAGER);
        grid = new Gridd<>(WarehouseItem.class,
                        new GenericDataProvider<>(warehouseService, WarehouseItem.class) {
                            @Override
                            protected Stream<WarehouseItem> fetchFromBackEnd(Query<WarehouseItem, GridFilter> query) {
                                Stream<WarehouseItem> warehouseItemStream = super.fetchFromBackEnd(query);
                                //if manager isn't logged in, only sellable items are shown
                                if (!editable) warehouseItemStream = warehouseItemStream.filter(WarehouseItem::isSellable);
                                List<WarehouseItem.Variant> all = warehouseItemVariantService.findAll();
                                return warehouseItemStream.peek(item -> item.setTransientVariants(all.stream().filter(variant -> variant.getOf().equals(item)).collect(Collectors.toSet())));
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
        grid.getCrud().addSaveListener(saveListenerToAdd);
        grid.getCrud().addEditListener(event -> itemOpened.accept(event.getItem()));
        grid.getCrud().addNewListener(event -> itemOpened.accept(event.getItem()));
        grid.getCrud().addEditListener(event -> variantsDetails.setOpened(false));
        grid.getCrud().addNewListener(event -> variantsDetails.setOpened(true));
    }

    private void configureColumns() {
        grid.addColumn("Název", new TextRenderer<>(WarehouseItem::getTitle)).setFrozen(true);
        grid.addColumn("Krátký popis", new TextRenderer<>(item -> StringUtils.abbreviate(item.getBriefDescription(), 85)));
        grid.addColumn("Celková hodnota", new NumberRenderer<>(item -> item.getTotalValue().orElseThrow(), "%.2f Kč"));
        grid.addColumn("Celkový počet", new NumberRenderer<>(item -> item.getTotalQuantity().orElseThrow(), "%d ks"));
        grid.addColumn("Kategorie", new TextRenderer<>(item -> item.getCategory().getTitle()));
        grid.addColumn("Upozornění", new ComponentRenderer<>(item -> {
            HorizontalLayout badgeLayout = new HorizontalLayout();
            badgeLayout.addClassName("badge-container");
            if (item.isOutOfStock().orElseThrow()) {
                Badge soldOut = new Badge("Vyprodáno", Badge.BadgeVariant.ERROR);
                soldOut.setTitle("Některá z variant produktu byla vyprodána.");
                badgeLayout.add(soldOut);
            }
            if (item.getTransientVariants().orElseThrow().isEmpty()) {
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

    private Details variantsDetails;

    private Badge variantsBadge;

    private BinderCrudEditor<WarehouseItem> buildEditor(boolean editable) {
        title = new TextField("Název");
        title.setRequired(true);
        briefDescription = new TextArea("Krátký popis");
        briefDescription.setClearButtonVisible(true);
        category = new Select<>(WarehouseItem.Category.values());
        category.setLabel("Kategorie");
        category.setRequiredIndicatorVisible(true);
        sellable = new Checkbox("Prodejné");
        //
        variants = new VariantEditor();
        variantsBadge = new Badge("0", Badge.BadgeVariant.COUNTER);
        variantsDetails = new Details(new Span(new Span("Varianty "), variantsBadge), variants);
        //
        Binder<WarehouseItem> binder = new BeanValidationBinder<>(WarehouseItem.class);
        binder.bindInstanceFields(this);
        //
        FormLayout formLayout = new FormLayout(title, category, briefDescription, variantsDetails, sellable);
        if (!editable) formLayout.remove(sellable);
        formLayout.setColspan(briefDescription, 2);
        return new BinderCrudEditor<>(binder, formLayout);
    }

    @SuppressWarnings("FieldCanBeLocal")
    private VariantEditor variants;

    private ComponentEventListener<Crud.SaveEvent<WarehouseItem>> saveListenerToAdd;
    private Consumer<WarehouseItem> itemOpened;

    /**
     * If you're reading this, I'm very sorry.
     * I am awfully ashamed of this piece of code, but no time to make it better.
     *
     * Now i'm even more ashamed. Hope no one'll see this.
     *
     * Marku, ani se nepokoušej dívat níž a něco se z toho učit.
     */
    private class VariantEditor extends VerticalLayout {

        List<WarehouseItem.Variant> value = new ArrayList<>();

        private  WarehouseItem currentItem;

        List<WarehouseItem.Variant> awaitingDeletion;

        VerticalLayout variantLayout;

        public VariantEditor() {
            setPadding(false);
            setSpacing(false);
            awaitingDeletion = new ArrayList<>();
            variantLayout = new VerticalLayout();
            variantLayout.setPadding(false);
            variantLayout.setSpacing(false);
            //
            saveListenerToAdd = event -> {
                List<WarehouseItem.Variant> toDelete = new ArrayList<>(awaitingDeletion.stream().filter(AbstractEntity::isPersisted).toList());
                warehouseItemVariantService.deleteAll(toDelete);
                awaitingDeletion.clear();
                //
                value.removeAll(value.stream().filter(variant -> variant.getColour().isBlank()).toList());
                warehouseItemVariantService.saveAll(value);
            };
            itemOpened = item -> {
                this.currentItem = item;
                setValue(item.getTransientVariants().orElse(new ArrayList<>()));
            };
            //
            Button addVariantButton = new Button("Vytvořit variantu", VaadinIcon.PLUS.create());
            addVariantButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            addVariantButton.addClickListener(event -> addNew());
            addVariantButton.addClickListener(event -> grid.setDirty());
            addVariantButton.setEnabled(editable);
            addVariantButton.setVisible(editable);
            //
            Scroller scroller = new Scroller(variantLayout, Scroller.ScrollDirection.VERTICAL);
            scroller.setMaxHeight("400px");
            add(scroller, addVariantButton);
        }

        public void setValue(Collection<WarehouseItem.Variant> variants) {
            value = variants == null ? new ArrayList<>() : new ArrayList<>(variants);
            refresh();
        }

        public void addNew() {
            WarehouseItem.Variant variant = new WarehouseItem.Variant();
            variant.setOf(currentItem);
            value.add(0, variant);
            refresh();
        }

        private void refresh() {
            variantLayout.removeAll();
            value.forEach(this::createRow);
            variantsBadge.setText(String.valueOf(value.size()));
        }

        private void createRow(WarehouseItem.Variant variant) {
            VariantField variantField = new VariantField();
            variantField.setPresentationValue(variant);
            HorizontalLayout horizontalLayout = new HorizontalLayout(variantField, buildDeleteVariantButton(variant));
            horizontalLayout.setAlignItems(Alignment.CENTER);
            Hr hr = new Hr();
            variantLayout.add(horizontalLayout, hr);
        }

        private Button buildDeleteVariantButton(WarehouseItem.Variant variant) {
            Button button = new Button(VaadinIcon.CLOSE.create());
            button.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            button.addClickListener(event -> {
                if (!variant.isPersisted()) deleteVariant(variant);
                else {
                    ConfirmDialog dialog = new ConfirmDialog("Odstranit variantu", String.format("Opravdu si přejete smazat variantu %s?", variant.getColour()), "Odstranit", event1 -> deleteVariant(variant));
                    dialog.setConfirmButtonTheme("error primary");
                    dialog.setCancelText("Zrušit");
                    dialog.setCancelable(true);
                    dialog.open();
                }
            });
            button.setVisible(editable);
            button.setEnabled(editable);
            return button;
        }

        private void deleteVariant(WarehouseItem.Variant variant) {
            value.remove(variant);
            setValue(value);
            awaitingDeletion.add(variant);
            grid.setDirty();
            refresh();
        }

        private class VariantField extends CustomField<WarehouseItem.Variant> {

            private WarehouseItem.Variant variant;

            public VariantField() {
                variant = new WarehouseItem.Variant();
                build();
            }

            TextField colour;
            IntegerField quantity;
            NumberField price;
            TextField note;

            private void build() {
                colour = new TextField("Barva");
                colour.setRequired(true);
                colour.setPattern("^.{1,50}$");
                colour.addThemeVariants(TextFieldVariant.LUMO_SMALL);
                colour.addValueChangeListener(event -> variant.setColour(event.getValue()));
                //
                quantity = new IntegerField("Počet");
                quantity.setRequiredIndicatorVisible(true);
                quantity.setHasControls(true);
                quantity.addThemeVariants(TextFieldVariant.LUMO_SMALL);
                quantity.setMin(0);
                quantity.addValueChangeListener(event -> variant.setQuantity(event.getValue()));
                //
                price = new NumberField("Cena");
                price.setRequiredIndicatorVisible(true);
                price.setSuffixComponent(new Label("Kč"));
                price.addThemeVariants(TextFieldVariant.LUMO_SMALL);
                price.setMin(0);
                price.addValueChangeListener(event -> variant.setPrice(event.getValue()));
                //
                note = new TextField("Poznámka/velikost");
                note.setPattern("^.{0,50}$");
                note.addThemeVariants(TextFieldVariant.LUMO_SMALL);
                note.addValueChangeListener(event -> variant.setNote(event.getValue()));
                //
                colour.setReadOnly(!editable);
                quantity.setReadOnly(!editable);
                price.setReadOnly(!editable);
                note.setReadOnly(!editable);
                updateFields();
                add(colour, new Span(" "), quantity, new Span(" "), price, new Span(" "), note);
            }

            @Override
            protected WarehouseItem.Variant generateModelValue() {
                return variant;
            }

            @Override
            protected void setPresentationValue(WarehouseItem.Variant variant) {
                this.variant = variant;
                updateFields();
            }

            private void updateFields() {
                if (variant == null) return;
                colour.setValue(variant.getColour());
                quantity.setValue(variant.getQuantity());
                price.setValue(variant.getPrice());
                note.setValue(variant.getNote());
            }
        }
    }
}
