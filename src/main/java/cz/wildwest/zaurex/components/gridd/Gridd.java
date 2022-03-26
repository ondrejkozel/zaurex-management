package cz.wildwest.zaurex.components.gridd;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.crud.CrudI18n;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.selection.MultiSelectionListener;
import com.vaadin.flow.shared.Registration;
import cz.wildwest.zaurex.data.AbstractEntity;
import cz.wildwest.zaurex.data.service.GenericService;
import cz.wildwest.zaurex.data.service.repository.GenericRepository;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Custom component representing a table with a menu bar.
 * <p>
 * Uses Vaadin {@link GridPro}, {@link Crud} (because of it's {@link CrudEditor}) and {@link MenuBar}.
 */
public class Gridd<T extends AbstractEntity> extends VerticalLayout {

    private final Class<T> tClass;
    private Grid<T> grid;

    private MenuBar menuBar;

    /**
     * Creates a new Gridd.
     * <p>
     * Don't forget to set columns also!
     *
     * @param tClass class of T type
     * @param dataProvider data provider of items
     * @param newItemSupplier instantiates new items created by crud editor
     * @param editable if editor should be editable
     * @param editor crud editor, binds user set values to objects
     * @param newItem new item translation
     * @param edit edit item translation
     * @param delete delete item translation
     */
    public Gridd(Class<T> tClass,
                 GenericDataProvider<T, ? extends GenericService<T, ? extends GenericRepository<T>>> dataProvider,
                 Supplier<T> newItemSupplier,
                 boolean editable,
                 CrudEditor<T> editor,
                 String newItem,
                 String edit,
                 String delete) {
        addClassName("grid-pro-container");
        this.tClass = tClass;
        //
        buildGrid();
        //
        buildBottomMenuBar();
        //
        buildCrud(newItemSupplier, editor, newItem, edit, delete);
        //
        buildMenuBar();
        //
        setEditable(editable);
        //
        add(menuBar, grid, bottomMenuBarLayout);
        //
        this.dataProvider = dataProvider;
        grid.setDataProvider(dataProvider);
        //
        workaroundButton = new Button("", (ComponentEventListener<ClickEvent<Button>>) clickEvent -> refreshAll());
        workaroundButton.addClassName("display-none");
        add(workaroundButton);
    }

    private final Button workaroundButton;

    private void setEditable(boolean editable) {
        multiselectMenuItem.setEnabled(editable);
        newObjectButton.setVisible(editable);
        if (!editable) {
            crud.getDeleteButton().setEnabled(false);
            crud.getSaveButton().setEnabled(false);
            crud.getDeleteButton().addClassName("display-none");
            crud.getSaveButton().addClassName("display-none");
            crud.getEditor().getView().getChildren().forEach(component -> {
                if (component instanceof HasValueAndElement<?, ?> hasValueAndElement)
                    hasValueAndElement.setReadOnly(true);
            });
        }
    }

    public void addMultiSelectionListener(MultiSelectionListener<Grid<T>, T> selectionListener) {
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.asMultiSelect().addSelectionListener(selectionListener);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
    }

    public void deselect(T toDeselect) {
        grid.getSelectionModel().deselect(toDeselect);
    }

    public boolean isMultiselectActive() {
        return multiselect;
    }

    public Editor<T> getEditor() {
        return grid.getEditor();
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }

    private void buildGrid() {
        grid = new GridPro<>();
        grid.addClassNames("grid-pro");
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COLUMN_BORDERS);
        //
        grid.getSelectionModel().addSelectionListener(selectionEvent -> {
            if (selectionEvent.getFirstSelectedItem().isPresent() && !multiselect) crud.edit(selectionEvent.getFirstSelectedItem().get(), Crud.EditMode.EXISTING_ITEM);
        });
    }

    private Crud<T> crud;

    private void buildCrud(Supplier<T> newItemSupplier, CrudEditor<T> editor, String newItem, String edit, String delete) {
        crud = new Crud<>();
        crud.setBeanType(tClass);
        crud.setDataProvider(new EmptyDataProvider());
        //
        Div div = new Div(crud);
        div.addClassName("display-none");
        add(div);
        //
        crud.addSaveListener(tSaveEvent -> grid.getSelectionModel().deselectAll());
        crud.addCancelListener(tCancelEvent -> grid.getSelectionModel().deselectAll());
        //
        crud.addSaveListener(tSaveEvent -> {
            dataProvider.save(tSaveEvent.getItem());
            refreshAll();
        });
        crud.addDeleteListener(tDeleteEvent -> {
            dataProvider.delete(tDeleteEvent.getItem());
            refreshAll();
        });
        //
        this.newObjectSupplier = newItemSupplier;
        //
        crud.setEditor(editor);
        crud.setI18n(buildCrudI18n(newItem, edit, delete));
        newObjectButton.setText(newItem);
    }

    public void refreshAll() {
        grid.getSelectionModel().deselectAll();
        dataProvider.refreshAll();
        grid.recalculateColumnWidths();
    }

    private MenuItem multiselectMenuItem;

    private MenuItem viewMenuItem;

    private void buildMenuBar() {
        menuBar = new MenuBar();
        MenuItem edit = menuBar.addItem("Možnosti");
        viewMenuItem = menuBar.addItem("Zobrazit");
        //
        multiselectMenuItem = edit.getSubMenu().addItem("Vícenásobný výběr");
        multiselectMenuItem.setCheckable(true);
        multiselectMenuItem.addClickListener(this::multiselectMenuItemClicked);
        multiselectMenuItem.addClickShortcut(Key.KEY_S, KeyModifier.ALT);
        //
        edit.getSubMenu().add(new Hr());
        edit.getSubMenu().addItem("Obnovit", menuItemClickEvent -> refreshAll()).addClickShortcut(Key.KEY_O, KeyModifier.ALT);
        //
    }

    public void setNewObjectButtonText(String text) {
        newObjectButton.setText(text);
    }

    private boolean multiselect = false;

    private void multiselectMenuItemClicked(ClickEvent<MenuItem> menuItemClickEvent) {
        grid.getSelectionModel().deselectAll();
        multiselect = !multiselect;
        //
        multiselectMenuItem.setChecked(multiselect);
        grid.setSelectionMode(multiselect ? Grid.SelectionMode.MULTI : Grid.SelectionMode.SINGLE);
        deleteSelectedButton.setVisible(multiselect);
        if (multiselect) {
            List<T> items = getItems();
            if (items.size() != 0) {
                //noinspection unchecked
                grid.asMultiSelect().select(items.get(1));
                workaroundButton.clickInClient();
            }
            else refreshAll();
        }
    }

    public Grid.Column<T> addColumn(String header, Renderer<T> renderer, boolean defaultVisibility) {
        Grid.Column<T> tColumn = grid.addColumn(renderer).setHeader(header);
        newColumnAdded(tColumn, header, defaultVisibility);
        return tColumn;
    }

    private void newColumnAdded(Grid.Column<T> column, String header, boolean defaultVisibility) {
        column.setAutoWidth(true);
        column.setVisible(defaultVisibility);
        //
        MenuItem menuItem = viewMenuItem.getSubMenu().addItem(header);
        menuItem.addClickListener(menuItemClickEvent -> toggleColumnVisibility(column, menuItem));
        menuItem.setCheckable(true);
        menuItem.setChecked(defaultVisibility);
    }

    private void toggleColumnVisibility(Grid.Column<T> column, MenuItem menuItem) {
        boolean visible = menuItem.isChecked();
        column.setVisible(visible);
        menuItem.setChecked(visible);
    }

    private final GenericDataProvider<T, ? extends GenericService<T, ? extends GenericRepository<T>>> dataProvider;

    public List<T> getItems() {
        return dataProvider.fetch(new Query<>()).collect(Collectors.toList());
    }

    public void setDirty() {
        crud.setDirty(true);
    }

    public Crud<T> getCrud() {
        return crud;
    }

    public static CrudI18n buildCrudI18n(String newItem, String edit, String delete) {
        CrudI18n crudI18n = new CrudI18n();
        crudI18n.setCancel("Zrušit");
        crudI18n.setDeleteItem("Odstranit...");
        crudI18n.setEditItem(edit);
        crudI18n.setNewItem(newItem);
        crudI18n.setSaveItem("Uložit");
        crudI18n.setConfirm(buildConfirmations(delete));
        return crudI18n;
    }

    private static CrudI18n.Confirmations buildConfirmations(String deleteString) {
        CrudI18n.Confirmations.Confirmation cancel = new CrudI18n.Confirmations.Confirmation();
        cancel.setTitle("Zahodit změny");
        cancel.setContent("Máte neuložené změny. Opravdu si je přejete zahodit?");
        CrudI18n.Confirmations.Confirmation.Button tlacidlaZahodit = new CrudI18n.Confirmations.Confirmation.Button();
        tlacidlaZahodit.setConfirm("Zahodit");
        tlacidlaZahodit.setDismiss("Zrušit");
        cancel.setButton(tlacidlaZahodit);
        //
        CrudI18n.Confirmations.Confirmation delete = new CrudI18n.Confirmations.Confirmation();
        delete.setTitle(deleteString);
        delete.setContent("Opravdu si přejete smazat tento objekt? Tato akce je nevratná.");
        CrudI18n.Confirmations.Confirmation.Button tlacidlaSmazat = new CrudI18n.Confirmations.Confirmation.Button();
        tlacidlaSmazat.setConfirm("Smazat");
        tlacidlaSmazat.setDismiss("Zrušit");
        delete.setButton(tlacidlaSmazat);
        //
        CrudI18n.Confirmations confirmations = new CrudI18n.Confirmations();
        confirmations.setCancel(cancel);
        confirmations.setDelete(delete);
        return confirmations;
    }

    /**
     * A blank {@link DataProvider}. It's purpose is to solace crud's need for a data provider.
     */
    private class EmptyDataProvider implements DataProvider<T, Object> {

        @Override
        public boolean isInMemory() {
            return false;
        }

        @Override
        public int size(Query<T, Object> query) {
            return 0;
        }

        @Override
        public Stream<T> fetch(Query<T, Object> query) {
            return null;
        }

        @Override
        public void refreshItem(T t) {

        }

        @Override
        public void refreshAll() {

        }

        @Override
        public Registration addDataProviderListener(DataProviderListener<T> dataProviderListener) {
            return null;
        }
    }

    private HorizontalLayout bottomMenuBarLayout;

    private Button newObjectButton;

    private Supplier<T> newObjectSupplier;

    private Button deleteSelectedButton;

    private void buildBottomMenuBar() {
        newObjectButton = new Button("Nový", VaadinIcon.PLUS.create());
        newObjectButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newObjectButton.addClickShortcut(Key.KEY_N, KeyModifier.ALT);
        newObjectButton.addClickListener(clickEvent -> crud.edit(newObjectSupplier.get(), Crud.EditMode.NEW_ITEM));
        //
        deleteSelectedButton = new Button("Odstranit vybrané", VaadinIcon.TRASH.create());
        deleteSelectedButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        deleteSelectedButton.addClickShortcut(Key.DELETE);
        deleteSelectedButton.setVisible(multiselect);
        deleteSelectedButton.addClickListener(this::deleteSelectedButtonClicked);
        //
        bottomMenuBarLayout = new HorizontalLayout(deleteSelectedButton, newObjectButton);
        bottomMenuBarLayout.setJustifyContentMode(JustifyContentMode.END);
        bottomMenuBarLayout.setWidth("100%");
    }

    private void deleteSelectedButtonClicked(ClickEvent<Button> clickEvent) {
        if (grid.getSelectedItems().isEmpty()) {
            Notification.show("Nebyly vybrány žádné položky");
            return;
        }
        ConfirmDialog dialog = new ConfirmDialog(
                String.format("Odstranit vybrané položky: %d", grid.getSelectedItems().size()),
                "Opravdu si přejete smazat tyto položky? Tato akce je nevratná.",
                "Odstranit",
                confirmEvent -> {
                    dataProvider.deleteAll(grid.getSelectedItems());
                    refreshAll();
                });
        dialog.setCancelable(true);
        dialog.setCancelText("Zrušit");
        dialog.open();
    }

    public GenericDataProvider<T, ? extends GenericService<T, ? extends GenericRepository<T>>> getDataProvider() {
        return dataProvider;
    }
}
