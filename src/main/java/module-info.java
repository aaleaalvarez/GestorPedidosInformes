module com.example.gestorpedidoshibernate {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;

    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.naming;
    requires java.sql;
    requires java.desktop;
    requires jasperreports; //Si trabajamos con fechas

    opens com.example.gestorpedidoshibernate.domain.ItemPedido;
    opens com.example.gestorpedidoshibernate.domain.Pedido;
    opens com.example.gestorpedidoshibernate.domain.Producto;
    opens com.example.gestorpedidoshibernate.domain.Usuario;

    opens com.example.gestorpedidoshibernate to javafx.fxml;
    opens com.example.gestorpedidoshibernate.controllers to javafx.fxml;

    exports com.example.gestorpedidoshibernate;
    exports com.example.gestorpedidoshibernate.controllers;
}