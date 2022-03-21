package cz.wildwest.zaurex.views.warehouse;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import cz.wildwest.zaurex.components.Badge;
import cz.wildwest.zaurex.components.gridd.GenericDataProvider;
import cz.wildwest.zaurex.components.gridd.GridFilter;
import cz.wildwest.zaurex.components.gridd.Gridd;
import cz.wildwest.zaurex.data.AbstractEntity;
import cz.wildwest.zaurex.data.Role;
import cz.wildwest.zaurex.data.entity.WarehouseItem;
import cz.wildwest.zaurex.data.service.WarehouseService;
import cz.wildwest.zaurex.data.service.repository.WarehouseItemVariantRepository;
import cz.wildwest.zaurex.security.AuthenticatedUser;
import cz.wildwest.zaurex.views.MainLayout;

import javax.annotation.security.RolesAllowed;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@PageTitle("Sklad")
@Route(value = "warehouse/all", layout = MainLayout.class)
@RolesAllowed({"SALESMAN", "MANAGER"})
public class WarehouseView extends VerticalLayout {

    private final Gridd<WarehouseItem> grid;
    private final WarehouseItemVariantRepository warehouseItemVariantRepository;

    public WarehouseView(WarehouseService warehouseService, AuthenticatedUser authenticatedUser, WarehouseItemVariantRepository warehouseItemVariantRepository) {
        this.warehouseItemVariantRepository = warehouseItemVariantRepository;
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
        grid.addSaveListener(listenerToAdd);
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
        variants = new VariantEditor();
        //
        Binder<WarehouseItem> binder = new BeanValidationBinder<>(WarehouseItem.class);
        binder.bindInstanceFields(this);
        //
        FormLayout formLayout = new FormLayout(title, category, briefDescription, variants, sellable);
        formLayout.setColspan(briefDescription, 2);
//        formLayout.setColspan(sellable, 2);
//        formLayout.setColspan(variants, 2);
        return new BinderCrudEditor<>(binder, formLayout);
    }

    @SuppressWarnings("FieldCanBeLocal")
    private VariantEditor variants;

    private ComponentEventListener<Crud.SaveEvent<WarehouseItem>> listenerToAdd;

    private class VariantEditor extends VerticalLayout implements HasValue<HasValue.ValueChangeEvent<Set<WarehouseItem.Variant>>, Set<WarehouseItem.Variant>> {

        Set<WarehouseItem.Variant> value = Collections.emptySet();

        List<Component> refreshableComponents;

        List<WarehouseItem.Variant> awaitingDeletion;

        public VariantEditor() {
            setPadding(false);
            setSpacing(false);
            refreshableComponents = new ArrayList<>();
            awaitingDeletion = new ArrayList<>();
            //
            listenerToAdd = (event -> {
                warehouseItemVariantRepository.deleteAll(awaitingDeletion.stream().filter(AbstractEntity::isPersisted).collect(Collectors.toList()));
                // TODO: 22.03.2022 zde by to mělo odstranit dané itemy z databáze, ale nedělá to
                awaitingDeletion.clear();
            });
            // TODO: 22.03.2022 tlačítko pro novou variantu
        }

        @Override
        public void setValue(Set<WarehouseItem.Variant> variants) {
            Set<WarehouseItem.Variant> oldValue = getValue();
            value = variants == null ? Collections.emptySet() : new HashSet<>(variants);
            refresh();
            //
            changeListeners.forEach(changeListener -> changeListener.valueChanged(new ValueChangeEvent<>() {
                @Override
                public HasValue<?, Set<WarehouseItem.Variant>> getHasValue() {
                    return VariantEditor.this;
                }

                @Override
                public boolean isFromClient() {
                    return true;
                }

                @Override
                public Set<WarehouseItem.Variant> getOldValue() {
                    return oldValue;
                }

                @Override
                public Set<WarehouseItem.Variant> getValue() {
                    return VariantEditor.this.getValue();
                }
            }));
        }

        private void refresh() {
            refreshableComponents.forEach(this::remove);
            refreshableComponents.clear();
            value.forEach(this::createRow);
        }

        private void createRow(WarehouseItem.Variant variant) {
            VariantField variantField = new VariantField();
            variantField.setPresentationValue(variant);
            HorizontalLayout horizontalLayout = new HorizontalLayout(variantField, buildDeleteVariantButton(variant));
            horizontalLayout.setAlignItems(Alignment.CENTER);
            Hr hr = new Hr();
            add(horizontalLayout, hr);
            refreshableComponents.add(horizontalLayout);
            refreshableComponents.add(hr);
        }

        private Button buildDeleteVariantButton(WarehouseItem.Variant variant) {
            Button button = new Button(VaadinIcon.CLOSE.create());
            button.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            button.addClickListener(event -> deleteVariant(variant));
            return button;
        }

        private void deleteVariant(WarehouseItem.Variant variant) {
            value.remove(variant);
            setValue(value);
            awaitingDeletion.add(variant);
            grid.setDirty();
            refresh();
        }

        private static class VariantField extends CustomField<WarehouseItem.Variant> {

            private WarehouseItem.Variant variant;

            public VariantField() {
                variant = new WarehouseItem.Variant();
                build();
            }

            TextField colour;
            IntegerField quantity;
            NumberField price;

            private void build() {
                colour = new TextField("Barva");
                colour.setPattern("^.{1,50}$");
                colour.addThemeVariants(TextFieldVariant.LUMO_SMALL);
                colour.addValueChangeListener(event -> variant.setColour(event.getValue()));
                colour.addValueChangeListener(event -> {
                    if (event.getValue().equals("")) colour.setValue("název");
                });
                colour.setValueChangeMode(ValueChangeMode.ON_BLUR);
                //
                quantity = new IntegerField("Počet");
                quantity.setHasControls(true);
                quantity.addThemeVariants(TextFieldVariant.LUMO_SMALL);
                quantity.setMin(0);
                quantity.setWidth("100px");
                quantity.addValueChangeListener(event -> variant.setQuantity(event.getValue()));
                price = new NumberField("Cena");
                price.setSuffixComponent(new Label("Kč"));
                price.addThemeVariants(TextFieldVariant.LUMO_SMALL);
                price.setMin(0);
                price.addValueChangeListener(event -> variant.setPrice(event.getValue()));
                updateFields();
                add(colour, new Span(" "), quantity, new Span(" "), price);
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
            }
        }

        @Override
        public Set<WarehouseItem.Variant> getValue() {
            return Collections.unmodifiableSet(value);
        }

        List<ValueChangeListener<? super ValueChangeEvent<Set<WarehouseItem.Variant>>>> changeListeners = new ArrayList<>();

        @Override
        public Registration addValueChangeListener(ValueChangeListener<? super ValueChangeEvent<Set<WarehouseItem.Variant>>> valueChangeListener) {
            changeListeners.add(valueChangeListener);
            return () -> changeListeners.remove(valueChangeListener);
        }

        @Override
        public void setReadOnly(boolean b) {

        }

        @Override
        public boolean isReadOnly() {
            return false;
        }

        @Override
        public void setRequiredIndicatorVisible(boolean b) {
        }

        @Override
        public boolean isRequiredIndicatorVisible() {
            return false;
        }
    }
}
