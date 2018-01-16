package New_app;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import com.vaadin.event.*;
import java.util.*;

/**
 * This UI is the application entry point. A UI may either represent a browser window
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be
 * overridden to add component to the user interface and initialize non-component functionality.
 */
   // @import "../valo/valo.scss";
@Theme("mytheme")

public class MyUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();
        GridLayout grid = new GridLayout(4, 2);
        ArrayList<String> persons = new ArrayList<>();
        sqlRequest_p(persons);
        ComboBox<String> select_person = new ComboBox<>();
        select_person.setItems(persons);
        select_person.setWidth("350px");
        select_person.setPlaceholder("Выберите сотрудника для поиска его КЕ");
        select_person.setItemCaptionGenerator(String::new);
        select_person.addValueChangeListener(event -> {
            sqlRequest(layout, event.getValue(), grid);
        });
        grid.setMargin(true);
        grid.setSpacing(true);
        layout.addComponents(select_person);
        grid.addStyleName("align-right");
        grid.addComponent(layout, 0,0);
        setContent(grid);
    }

    public void sqlRequest(VerticalLayout layout, String word_for_search, GridLayout grid) {
        String userName = null;
        String password = null;
        String url_db = null;
        Properties props = new Properties();
        InputStream inputStream = MyUI.class.getClassLoader().getResourceAsStream("config.properties");
        try {
            props.load(inputStream);
            userName = props.getProperty("userName_db");
            password = props.getProperty("password_db");
            url_db = props.getProperty("url_db");
        } catch (IOException e) {
            System.out.println("config.properties not found");
            e.printStackTrace();
        }
            try {
                Connection conn = DriverManager.getConnection(url_db, userName, password);
                Statement statement = conn.createStatement();
                String queryString = "SELECT\n" +
                        "\tPersons.f27_ AS LastFMname,\n" +
                        "\tFLD_ConfigItems.f41_name AS KE_Name,\n" +
                        "\tFLD_ConfigItems.f45_ AS InventoryNumber\n" +
                        "FROM\n" +
                        "\tot10.dbo.Userfields407 AS Persons \n" +
                        "\tINNER JOIN ot10.dbo.ReferenceList2174 AS RL2174 ON Persons.request = RL2174.request\n" +
                        "\tINNER JOIN ot10.dbo.UserFields667 AS FLD_ConfigItems ON RL2174.reference = FLD_ConfigItems.request\n" +
                        "WHERE\n" +
                        "\tPersons.f27_ Like '%" + word_for_search + "%'";
                ResultSet rs = statement.executeQuery(queryString);
                grid.removeAllComponents();
                layout.setWidth("250px");
                grid.addComponent(layout, 1,0);
                grid.addComponent(new Label(" " ), 0,1 );
                grid.addComponent(new Label("Ф.И.О."), 1,1 );
                grid.addComponent(new Label("Наименование КЕ "), 2,1 );
                grid.addComponent(new Label("Инвентарный номер"), 3,1 );

                int i = 1;
                while (rs.next()) {
                    grid.setRows(rs.getRow()+2);
                    i = i + 1;
                    String rowsCount =  " " + rs.getRow();
                    grid.addComponent(new Label(rowsCount), 0, i );
                    grid.addComponent(new Label(rs.getString(1)), 1, i );
                    grid.addComponent(new Label(rs.getString(2)), 2, i );
                    grid.addComponent(new Label(rs.getString(3)), 3, i );
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    public void sqlRequest_p(ArrayList persons) {
        String userName = null;
        String password = null;
        String url_db = null;
        Properties props = new Properties();
        InputStream inputStream = MyUI.class.getClassLoader().getResourceAsStream("config.properties");
        try {
            props.load(inputStream);
            userName = props.getProperty("userName_db");
            password = props.getProperty("password_db");
            url_db = props.getProperty("url_db");
        } catch (IOException e) {
            System.out.println("config.properties not found");
            e.printStackTrace();
        }
        try {
            Connection conn = DriverManager.getConnection(url_db, userName, password);
            Statement statement = conn.createStatement();
            String queryString = "SELECT Persons.f27_ AS LastFMname\n" +
                    "FROM ot10.dbo.Userfields407 AS Persons \n" +
                    "WHERE Persons.f21_cmdbkis != '0' ";
            ResultSet rs = statement.executeQuery(queryString);
            while (rs.next()) {
                persons.add(rs.getString(1));   //.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
