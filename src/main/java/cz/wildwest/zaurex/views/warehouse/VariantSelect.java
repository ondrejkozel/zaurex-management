package cz.wildwest.zaurex.views.warehouse;

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

public class VariantSelect extends CustomField<WarehouseItem.Variant> {

    private final ComboBox<WarehouseItem> itemComboBox;
    private final ComboBox<WarehouseItem.Variant> variantComboBox;

    private final HorizontalLayout layout;

    public VariantSelect(List<WarehouseItem> itemsWithTransientValues) {
        if (!itemsWithTransientValues.isEmpty() && itemsWithTransientValues.get(0).getTransientVariants().isEmpty()) throw new IllegalArgumentException("Without transient variants.");
        itemComboBox = new ComboBox<>("Zboží", itemsWithTransientValues);
        itemComboBox.setWidth("25em");
        itemComboBox.setMaxWidth("calc(100vw - 32px)");
        //
        variantComboBox = new ComboBox<>("Varianta");
        variantComboBox.setEnabled(false);
        variantComboBox.setHelperText("Nejdříve vyberte zboží.");
        //
        itemComboBox.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                variantComboBox.setEnabled(true);
                //noinspection OptionalGetWithoutIsPresent
                variantComboBox.setItems(event.getValue().getTransientVariants().get());
                variantComboBox.setHelperText("");
            }
            else {
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
    }

    private IntegerField numberField;

    public void addVariantDependentNumberField(IntegerField numberField, Consumer<WarehouseItem.Variant> onVariantSelectionChange) {
        this.numberField = numberField;
        numberField.setHelperText("Nejdříve vyberte variantu.");
        variantComboBox.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                numberField.setEnabled(true);
                numberField.setValue(0);
                numberField.setHelperText(String.format("Nyní je na skladě %d kusů.", event.getValue().getQuantity()));
                onVariantSelectionChange.accept(event.getValue());
            }
            else {
                numberField.setEnabled(false);
                numberField.setValue(null);
                numberField.setHelperText("Nejdříve vyberte variantu.");
            }
        });
        numberField.setEnabled(false);
        layout.add(numberField);
    }

    public Button addSubmitButton() {
        Button button = new Button();
        button.setText("Potvrdit");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        updateButton(button, 0);
        numberField.addValueChangeListener(event -> updateButton(button, event.getValue()));
        layout.add(button);
        button.getStyle().set("display", "flex");
        button.getStyle().set("padding-bottom", "14px");
        button.getStyle().set("padding-top", "14px");
        return button;
    }

    private void updateButton(Button button, Integer event) {
        if (event == null || event == 0) {
            button.setEnabled(false);
            button.removeThemeVariants(ButtonVariant.LUMO_ERROR);
            button.setIcon(new LineAwesomeIcon("las la-check"));
        }
        else if (event < 0) {
            button.setEnabled(true);
            button.addThemeVariants(ButtonVariant.LUMO_ERROR);
            button.setIcon(new LineAwesomeIcon("las la-minus"));
        }
        else {
            button.setEnabled(true);
            button.removeThemeVariants(ButtonVariant.LUMO_ERROR);
            button.setIcon(new LineAwesomeIcon("las la-plus"));
        }
    }

    @Override
    protected WarehouseItem.Variant generateModelValue() {
        return variantComboBox.getValue();
    }

    @Override
    protected void setPresentationValue(WarehouseItem.Variant newPresentationValue) {
        itemComboBox.setValue(newPresentationValue.getOf());
        variantComboBox.setValue(newPresentationValue);
    }

    public void setItems(List<WarehouseItem> all) {
        if (!all.isEmpty() && all.get(0).getTransientVariants().isEmpty()) throw new IllegalArgumentException("Without transient variants.");
        itemComboBox.setItems(all);
        itemComboBox.setValue(null);
    }
}
