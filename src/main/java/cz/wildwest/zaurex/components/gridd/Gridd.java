package cz.wildwest.zaurex.components.gridd;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.crud.CrudI18n;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.gridpro.EditColumnConfigurator;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.shared.Registration;
import cz.wildwest.zaurex.data.AbstractEntity;
import cz.wildwest.zaurex.data.service.GenericService;
import cz.wildwest.zaurex.data.service.repository.GenericRepository;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Gridd<T extends AbstractEntity> extends VerticalLayout {

    private final Class<T> tClass;
    private GridPro<T> grid;

    private MenuBar menuBar;

    public Gridd(Class<T> tClass) {
        this.tClass = tClass;
        addClassName("grid-pro-container");
        //
        buildGrid();
        //
        buildCrud();
        //
        buildMenuBar();
        //
        buildBottomMenuBar();
        //
        add(menuBar, grid, bottomMenuBarLayout);
    }

    public Editor<T> getEditor() {
        return grid.getEditor();
    }

    private void buildGrid() {
        grid = new GridPro<>();
        style();
        //
        grid.addItemPropertyChangedListener(tItemPropertyChangedEvent -> dataProvider.save(tItemPropertyChangedEvent.getItem()));
        grid.getSelectionModel().addSelectionListener(selectionEvent -> {
            if (selectionEvent.getFirstSelectedItem().isPresent() && !multiselect) crud.edit(selectionEvent.getFirstSelectedItem().get(), Crud.EditMode.EXISTING_ITEM);
        });
    }

    private void style() {
        grid.addClassNames("grid-pro");
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COLUMN_BORDERS);
        grid.setEditOnClick(true);
    }

    private Crud<T> crud;

    private void buildCrud() {
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
    }

    private void refreshAll() {
        dataProvider.refreshAll();
        grid.recalculateColumnWidths();
    }

    public void setEditor(CrudEditor<T> editor, String newItem, String edit, String delete) {
        crud.setEditor(editor);
        crud.setI18n(buildCrudI18n(newItem, edit, delete));
        newObjectButton.setText(newItem);
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

    private boolean multiselect = false;

    private void multiselectMenuItemClicked(ClickEvent<MenuItem> menuItemClickEvent) {
        grid.getSelectionModel().deselectAll();
        multiselect = !multiselect;
        //
        multiselectMenuItem.setChecked(multiselect);
        grid.setSelectionMode(multiselect ? Grid.SelectionMode.MULTI : Grid.SelectionMode.SINGLE);
        deleteSelectedButton.setVisible(multiselect);
    }

    public Grid.Column<T> addColumn(String header, Renderer<T> renderer) {
        Grid.Column<T> tColumn = grid.addColumn(renderer).setHeader(header);
        newColumnAdded(tColumn, header);
        return tColumn;
    }

    public EditColumnConfigurator<T> addEditColumn(String header, ValueProvider<T, ?> valueProvider, Renderer<T> renderer) {
        EditColumnConfigurator<T> tEditColumnConfigurator = grid.addEditColumn(valueProvider, renderer);
        tEditColumnConfigurator.getColumn().setHeader(header);
        newColumnAdded(tEditColumnConfigurator.getColumn(), header);
        return tEditColumnConfigurator;
    }

    private void newColumnAdded(Grid.Column<T> column, String header) {
        column.setAutoWidth(true);
        //
        MenuItem menuItem = viewMenuItem.getSubMenu().addItem(header);
        menuItem.addClickListener(menuItemClickEvent -> toggleColumnVisibility(column, menuItem));
        menuItem.setCheckable(true);
        menuItem.setChecked(true);
    }

    private void toggleColumnVisibility(Grid.Column<T> column, MenuItem menuItem) {
        boolean visible = menuItem.isChecked();
        column.setVisible(visible);
        menuItem.setChecked(visible);
    }

    private GenericDataProvider<T, ? extends GenericService<T, ? extends GenericRepository<T>>> dataProvider;

    public void setDataProvider(GenericDataProvider<T, ? extends GenericService<T, ? extends GenericRepository<T>>> dataProvider) {
        this.dataProvider = dataProvider;
        grid.setDataProvider(dataProvider);
    }

    public List<T> getItems() {
        return dataProvider.fetch(new Query<>()).collect(Collectors.toList());
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

    public void setNewObjectSupplier(Supplier<T> supplier) {
        newObjectSupplier = supplier;
    }

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
        //
        bottomMenuBarLayout = new HorizontalLayout(deleteSelectedButton, newObjectButton);
        bottomMenuBarLayout.setJustifyContentMode(JustifyContentMode.END);
        bottomMenuBarLayout.setWidth("100%");
    }
}
