package cz.wildwest.zaurex.views.warehouse;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
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
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import cz.wildwest.zaurex.components.Badge;
import cz.wildwest.zaurex.components.HtmlNotification;
import cz.wildwest.zaurex.components.gridd.GenericDataProvider;
import cz.wildwest.zaurex.components.gridd.Gridd;
import cz.wildwest.zaurex.components.gridd.NumberGriddCell;
import cz.wildwest.zaurex.data.AbstractEntity;
import cz.wildwest.zaurex.data.Role;
import cz.wildwest.zaurex.data.entity.WarehouseItem;
import cz.wildwest.zaurex.data.service.WarehouseItemVariantService;
import cz.wildwest.zaurex.data.service.WarehouseService;
import cz.wildwest.zaurex.security.AuthenticatedUser;
import cz.wildwest.zaurex.views.LineAwesomeIcon;
import cz.wildwest.zaurex.views.LocalDateTimeFormatter;
import cz.wildwest.zaurex.views.MainLayout;
import cz.wildwest.zaurex.views.quickAdd.QuickAddView;
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
@Route(value = "warehouse", layout = MainLayout.class)
@RolesAllowed({"SALESMAN", "SHIFT_LEADER", "WAREHOUSEMAN", "MANAGER"})
public class WarehouseView extends VerticalLayout {

    private final Gridd<WarehouseItem> grid;
    private final WarehouseItemVariantService warehouseItemVariantService;
    private final WarehouseService warehouseService;
    private boolean editable;

    public WarehouseView(WarehouseService warehouseService, AuthenticatedUser authenticatedUser, WarehouseItemVariantService warehouseItemVariantService) {
        this.warehouseItemVariantService = warehouseItemVariantService;
        this.warehouseService = warehouseService;
        Set<Role> roles = authenticatedUser.get().orElseThrow().getRoles();
        //
        setSizeFull();
        //
        editable = roles.contains(Role.MANAGER);
        boolean editableAtTheBeggining = editable;
        grid = new Gridd<>(WarehouseItem.class,
                        new GenericDataProvider<>(warehouseService, WarehouseItem.class, () -> {
                            Stream<WarehouseItem> warehouseItemStream = warehouseService.findAll().stream();
                            //if manager isn't logged in, only sellable items are shown
                            if (!editableAtTheBeggining) warehouseItemStream = warehouseItemStream.filter(WarehouseItem::isSellable);
                            return warehouseService.fetchTransientVariants(warehouseItemStream).collect(Collectors.toList());
                        }),
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
        List<Registration> variationsDetailsOpeners = List.of(
                grid.getCrud().addEditListener(event -> variantsDetails.setOpened(false)),
                grid.getCrud().addNewListener(event -> variantsDetails.setOpened(true))
        );
        //
        if (roles.contains(Role.WAREHOUSEMAN) && !roles.contains(Role.MANAGER)) setWarehousemanMode(variationsDetailsOpeners);
        if (roles.contains(Role.WAREHOUSEMAN) || roles.contains(Role.MANAGER)) addQuickAdditionMenuItem();
    }

    private void addQuickAdditionMenuItem() {
        HorizontalLayout bottomMenuBarLayout = grid.getBottomMenuBarLayout();
        Button button = new Button("Rychle naskladnit", new LineAwesomeIcon("las la-rocket"));
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        button.addClickListener(event -> {
            if (event.isAltKey()) UI.getCurrent().navigate(QuickAddView.class);
            else showQuickAdditionDialog();
        });
        bottomMenuBarLayout.addComponentAsFirst(button);
    }

    private void showQuickAdditionDialog() {
        ConfirmDialog dialog = new ConfirmDialog("Rychle naskladnit", "", "Zavřít", event -> {});
        dialog.setConfirmButtonTheme("tertiary");
        dialog.add(new QuickAddView(warehouseService, warehouseItemVariantService, s -> {
            Notification.show(""); // i don't get it, but this line solves buggy html notification
            HtmlNotification.show(s);
        }));
        dialog.addCancelListener(event -> grid.refreshAll());
        dialog.addConfirmListener(event -> grid.refreshAll());
        dialog.open();
    }

    private void setWarehousemanMode(List<Registration> variationsDetailsOpeners) {
        variationsDetailsOpeners.forEach(Registration::remove);
        variantsDetails.setOpened(true);
        //
        editable = true;
        //
        grid.getCrud().getSaveButton().setVisible(true);
        grid.getCrud().getSaveButton().removeClassName("display-none");
        //
        grid.setNewObjectButtonVisible(true);
        //
        variants.setButtonsActive(true);
        //
        grid.getCrud().addNewListener(event -> setTopFieldsReadonly(false));
        grid.getCrud().addEditListener(event -> setTopFieldsReadonly(true));
    }

    private void setTopFieldsReadonly(boolean readonly) {
        title.setReadOnly(readonly);
        briefDescription.setReadOnly(readonly);
        category.setReadOnly(readonly);
        if (readonly) briefDescription.setHelperText("Jste oprávněni pouze k úpravě variant.");
        else briefDescription.setHelperText("");
    }

    private void configureColumns() {
        grid.addColumn("Název", new TextRenderer<>(WarehouseItem::getTitle), true);
        grid.addColumn("Krátký popis", new TextRenderer<>(item -> StringUtils.abbreviate(item.getBriefDescription(), 50)), false);
        grid.addColumn("Celková hodnota", new ComponentRenderer<>(item -> new NumberGriddCell(String.format(LocalDateTimeFormatter.LOCALE, "%.2f Kč", item.getTotalValue().orElseThrow()))), false);
        grid.addColumn("Celkový počet", new ComponentRenderer<>(item -> new NumberGriddCell(String.format("%d ks", item.getTotalQuantity().orElseThrow()))), true);
        grid.addColumn("Kategorie", new TextRenderer<>(item -> item.getCategory().getTitle()), false);
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
        }), true);
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

        private boolean buttonsActive;
        private final Button addVariantButton;

        public VariantEditor() {
            setPadding(false);
            setSpacing(false);
            awaitingDeletion = new ArrayList<>();
            variantLayout = new VerticalLayout();
            variantLayout.setPadding(false);
            variantLayout.setSpacing(false);
            //
            saveListenerToAdd = event -> {
                List<WarehouseItem.Variant> toDelete = awaitingDeletion.stream().filter(AbstractEntity::isPersisted).collect(Collectors.toList());
                warehouseItemVariantService.deleteAll(toDelete);
                awaitingDeletion.clear();
                //
                value.removeAll(value.stream().filter(variant -> variant.getColour().isBlank()).collect(Collectors.toList()));
                warehouseItemVariantService.saveAll(value);
            };
            itemOpened = item -> {
                this.currentItem = item;
                setValue(item.getTransientVariants().orElse(new ArrayList<>()));
            };
            //
            addVariantButton = new Button("Vytvořit variantu", VaadinIcon.PLUS.create());
            addVariantButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            addVariantButton.addClickListener(event -> addNew());
            addVariantButton.addClickListener(event -> grid.setDirty());
            setButtonsActive(editable);
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
            button.setVisible(buttonsActive);
            button.setEnabled(buttonsActive);
            return button;
        }

        public void setButtonsActive(boolean visible) {
            buttonsActive = visible;
            addVariantButton.setVisible(visible);
            addVariantButton.setEnabled(visible);
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
                setFieldsReadOnly(!editable);
                updateFields();
                add(colour, new Span(" "), quantity, new Span(" "), price, new Span(" "), note);
            }

            public void setFieldsReadOnly(boolean readOnly) {
                colour.setReadOnly(readOnly);
                quantity.setReadOnly(readOnly);
                price.setReadOnly(readOnly);
                note.setReadOnly(readOnly);
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
