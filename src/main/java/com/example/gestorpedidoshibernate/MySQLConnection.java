package com.example.gestorpedidoshibernate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Esta clase proporciona una conexión JDBC a la base de datos MySQL para el sistema de gestión de pedidos.
 *
 * @author Alejandro Álvarez Mérida
 * @version 28-01-2024
 */
public class MySQLConnection {
    static private Connection con;

    static{
        try {
            con =  DriverManager.getConnection("jdbc:mysql://localhost/GestorPedidos","root","04112003");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection(){
        return con;
    }
}
