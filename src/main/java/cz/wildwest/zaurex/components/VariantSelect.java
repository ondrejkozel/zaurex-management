package cz.wildwest.zaurex.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import cz.wildwest.zaurex.data.entity.WarehouseItem;
import cz.wildwest.zaurex.views.LineAwesomeIcon;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class VariantSelect extends CustomField<WarehouseItem.Variant> {

    private final ComboBox<WarehouseItem> itemComboBox;
    private final ComboBox<WarehouseItem.Variant> variantComboBox;

    private final HorizontalLayout layout;
    private final IntegerField amountChange;
    private final Button submitButton;
    private IntegerField numberField;

    public VariantSelect(List<WarehouseItem> itemsWithTransientValues) {
        if (!itemsWithTransientValues.isEmpty() && itemsWithTransientValues.get(0).getTransientVariants().isEmpty())
            throw new IllegalArgumentException("Without transient variants.");
        itemComboBox = new ComboBox<>("Zboží", itemsWithTransientValues.stream().filter(item -> !item.getTransientVariants().orElseThrow().isEmpty()).collect(Collectors.toList()));
        itemComboBox.setWidth("22.5em");
        itemComboBox.setMaxWidth("calc(100vw - 115px)");
        //
        variantComboBox = new ComboBox<>("Varianta");
        variantComboBox.setEnabled(false);
        variantComboBox.setHelperText("Nejdříve vyberte zboží.");
        //
        itemComboBox.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                variantComboBox.setEnabled(true);
                //noinspection OptionalGetWithoutIsPresent
                variantComboBox.setItems(event.getValue().getTransientVariants().get().stream().filter(variant -> variant.getQuantity() > 0).collect(Collectors.toList()));
                variantComboBox.setHelperText("");
            } else {
                variantComboBox.setEnabled(false);
                variantComboBox.setItems();
                variantComboBox.setHelperText("Nejdříve vyberte zboží.");
            }
        });
        //
        layout = new HorizontalLayout(itemComboBox, variantComboBox);
        layout.getStyle().set("align-items", "baseline");
        layout.getStyle().set("display", "flex");
        layout.getStyle().set("flex-wrap", "wrap");
        layout.getStyle().set("gap", "var(--lumo-space-xs)");
        add(layout);
        //
        amountChange = new IntegerField("Změna počtu kusů");
        amountChange.addValueChangeListener(event -> {
            if (event.getValue() != null && event.getValue() < amountChange.getMin())
                amountChange.setValue(amountChange.getMin());
        });
        amountChange.getStyle().set("width", "10em");
        amountChange.setHasControls(true);
        addVariantDependentNumberField(amountChange, variant -> amountChange.setMin(-variant.getQuantity()));
        //
        submitButton = new Button();
        submitButton.setText("Potvrdit");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        updateButton(submitButton, 0);
        numberField.addValueChangeListener(event -> updateButton(submitButton, event.getValue()));
        layout.add(submitButton);
        submitButton.getStyle().set("display", "flex");
        submitButton.getStyle().set("padding-bottom", "14px");
        submitButton.getStyle().set("padding-top", "14px");
    }

    public void addSubmitButtonClickListener(ComponentEventListener<ClickEvent<Button>> listener) {
        submitButton.addClickListener(listener);
    }

    public int getAmount() {
        return amountChange.getValue();
    }

    private void addVariantDependentNumberField(IntegerField numberField, Consumer<WarehouseItem.Variant> onVariantSelectionChange) {
        this.numberField = numberField;
        numberField.setHelperText("Nejdříve vyberte variantu.");
        variantComboBox.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                numberField.setEnabled(true);
                numberField.setValue(0);
                numberField.setHelperText(String.format("Nyní je na skladě %d kusů.", event.getValue().getQuantity()));
                onVariantSelectionChange.accept(event.getValue());
            } else {
                numberField.setEnabled(false);
                numberField.setValue(null);
                numberField.setHelperText("Nejdříve vyberte variantu.");
            }
        });
        numberField.setEnabled(false);
        layout.add(numberField);
    }

    private void updateButton(Button button, Integer event) {
        if (event == null || event == 0) {
            button.setEnabled(false);
            button.removeThemeVariants(ButtonVariant.LUMO_ERROR);
            button.setIcon(new LineAwesomeIcon("las la-check"));
        } else if (event < 0) {
            button.setEnabled(true);
            button.addThemeVariants(ButtonVariant.LUMO_ERROR);
            button.setIcon(new LineAwesomeIcon("las la-minus"));
        } else {
            button.setEnabled(true);
            button.removeThemeVariants(ButtonVariant.LUMO_ERROR);
            button.setIcon(new LineAwesomeIcon("las la-plus"));
        }
    }

    @Override
    public WarehouseItem.Variant generateModelValue() {
        return variantComboBox.getValue();
    }

    @Override
    public void setPresentationValue(WarehouseItem.Variant newPresentationValue) {
        itemComboBox.setValue(newPresentationValue.getOf());
        variantComboBox.setValue(newPresentationValue);
    }

    public void setItems(List<WarehouseItem> all) {
        if (!all.isEmpty() && all.get(0).getTransientVariants().isEmpty())
            throw new IllegalArgumentException("Without transient variants.");
        itemComboBox.setItems(all);
        itemComboBox.setValue(null);
    }

    public void setSellMode() {
        submitButton.setVisible(false);
        amountChange.setTitle("Počet kusů");
        variantComboBox.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                amountChange.setMin(1);
                amountChange.setMax(event.getValue().getQuantity());
            }
        });
    }
}
