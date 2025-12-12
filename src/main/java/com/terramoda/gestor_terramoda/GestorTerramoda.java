/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.terramoda.gestor_terramoda;

import javax.swing.SwingUtilities;

/**
 *
 * @author Emanuel Bonilla
 */
public class GestorTerramoda {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Dashboard dashboard = new Dashboard("https://terramoda.flurts.tech");
            dashboard.setVisible(true);
            
        });
    }
}
