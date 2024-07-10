package com.example.application.views.masterdetail;

import com.example.application.data.ProxyAccount;
import com.example.application.services.ProxyAccountService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Master-Detail")
@Route(value = "/:proxyAccountID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@Uses(Icon.class)
public class MasterDetailView extends Div implements BeforeEnterObserver {

    private final String PROXYACCOUNT_ID = "proxyAccountID";
    private final String PROXYACCOUNT_EDIT_ROUTE_TEMPLATE = "/%s/edit";

    private final Grid<ProxyAccount> grid = new Grid<>(ProxyAccount.class, false);

    private TextField aUsername;
    private TextField aPassword;
    private TextField pUsername;
    private TextField pPassword;
    private TextField ip;
    private TextField port;
    private TextField email;
    private TextField ePassword;
    private Checkbox active;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<ProxyAccount> binder;

    private ProxyAccount proxyAccount;

    private final ProxyAccountService proxyAccountService;

    public MasterDetailView(ProxyAccountService proxyAccountService) {
        this.proxyAccountService = proxyAccountService;
        addClassNames("master-detail-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("aUsername").setAutoWidth(true);
        grid.addColumn("aPassword").setAutoWidth(true);
        grid.addColumn("pUsername").setAutoWidth(true);
        grid.addColumn("pPassword").setAutoWidth(true);
        grid.addColumn("ip").setAutoWidth(true);
        grid.addColumn("port").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("ePassword").setAutoWidth(true);
        LitRenderer<ProxyAccount> activeRenderer = LitRenderer.<ProxyAccount>of(
                "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", active -> active.isActive() ? "check" : "minus").withProperty("color",
                        active -> active.isActive()
                                ? "var(--lumo-primary-text-color)"
                                : "var(--lumo-disabled-text-color)");

        grid.addColumn(activeRenderer).setHeader("Active").setAutoWidth(true);

        grid.setItems(query -> proxyAccountService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(PROXYACCOUNT_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(MasterDetailView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(ProxyAccount.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(port).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("port");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.proxyAccount == null) {
                    this.proxyAccount = new ProxyAccount();
                }
                binder.writeBean(this.proxyAccount);
                proxyAccountService.update(this.proxyAccount);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(MasterDetailView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> proxyAccountId = event.getRouteParameters().get(PROXYACCOUNT_ID).map(Long::parseLong);
        if (proxyAccountId.isPresent()) {
            Optional<ProxyAccount> proxyAccountFromBackend = proxyAccountService.get(proxyAccountId.get());
            if (proxyAccountFromBackend.isPresent()) {
                populateForm(proxyAccountFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested proxyAccount was not found, ID = %s", proxyAccountId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(MasterDetailView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        aUsername = new TextField("A Username");
        aPassword = new TextField("A Password");
        pUsername = new TextField("P Username");
        pPassword = new TextField("P Password");
        ip = new TextField("Ip");
        port = new TextField("Port");
        email = new TextField("Email");
        ePassword = new TextField("E Password");
        active = new Checkbox("Active");
        formLayout.add(aUsername, aPassword, pUsername, pPassword, ip, port, email, ePassword, active);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(ProxyAccount value) {
        this.proxyAccount = value;
        binder.readBean(this.proxyAccount);

    }
}
