/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.terramoda.gestor_terramoda;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

class FieldRow extends JPanel {

  public FieldRow(String label, String value) {
    setLayout(new BorderLayout());
    add(new JLabel("<html><b>" + label + ":</b></html>"), BorderLayout.WEST);
    add(new JLabel(value), BorderLayout.CENTER);
  }
}

class ActionRenderer extends JPanel implements TableCellRenderer {

  private JButton btnView = new JButton("Ver");
  private JButton btnEdit = new JButton("Editar");

  public ActionRenderer() {
    setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
    add(btnView);
    add(btnEdit);
  }

  @Override
  public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected,
      boolean hasFocus, int row, int column) {

    return this;
  }
}

class ActionEditor extends AbstractCellEditor implements TableCellEditor {

  private JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
  private JButton btnView = new JButton("Ver");
  private JButton btnEdit = new JButton("Editar");
  private int row;
  private Function<Integer, JPanel> onView;
  private Function<Integer, JPanel> onEdit;

  public ActionEditor(Function<Integer, JPanel> onView, Function<Integer, JPanel> onEdit) {
    this.onView = onView;
    this.onEdit = onEdit;

    panel.add(btnView);
    panel.add(btnEdit);

    btnView.addActionListener(e -> {
      showDialog(onView.apply(row), "Ver");
    });

    btnEdit.addActionListener(e -> {
      showDialog(onEdit.apply(row), "Editar");
    });
  }

  private void showDialog(JPanel content, String title) {
    JDialog dialog = new JDialog((Frame) null, title, true);
    dialog.setContentPane(content);
    dialog.pack();
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
    fireEditingStopped();
  }

  @Override
  public Component getTableCellEditorComponent(
      JTable table, Object value, boolean isSelected,
      int row, int column) {
    this.row = row;

    return panel;
  }

  @Override
  public Object getCellEditorValue() {
    return null;
  }
}

record Pair<A, B>(A a, B b) {

}

class BackgroundPanel extends JPanel {

  private Image background;

  public BackgroundPanel(String imagePath) {
    background = new ImageIcon(getClass().getResource(imagePath)).getImage();
    setLayout(new BorderLayout());
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
  }
}

public class Dashboard extends JFrame {

  JPanel contentPanel;
  GestorVentasClient client;

  public static int perPage() {
    return 20;
  }

  public static String fontY() {
    return "Instrument Sans";
  }

  public static String font() {
    return "Inter";
  }

  public static int fontSize() {
    return 10;
  }

  public static int fontTitleSize() {
    return 20;
  }

  public static Color colorContentPanel() {
    return Color.WHITE;
  }

  public static Color colorYPanel() {
    return new Color(231, 236, 239);
  }

  public static Color colorButton() {
    return new Color(18, 69, 89);
  }

  public static Color colorButtonOnHover() {
    return new Color(41, 121, 152);
  }

  public static Color colorSidebar() {
    return new Color(18, 69, 89);
  }

  public static Color colorFgSidebar() {
    return Color.WHITE;
  }

  public static Color colorHoverFgSidebar() {
    return new Color(255, 255, 72);
  }

  public static Color colorFg() {
    return Color.BLACK;
  }

  public static void removeBgButton(JButton btn) {
    btn.setOpaque(false);
    btn.setContentAreaFilled(false);
  }

  public static void removeBgLabel(JLabel label) {
    label.setOpaque(false);
  }

  public Dashboard(String uriClient) {
    this.client = new GestorVentasClient(uriClient);
    setTitle("Dashboard");
    setSize(1200, 800);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());

    // ---- SIDEBAR ----
    JPanel sidebar = new JPanel();
    sidebar.setPreferredSize(new Dimension(240, 0));
    sidebar.setBackground(Dashboard.colorSidebar());
    sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

    JButton btnCustomersRegister = SidebarComponents.createButton("REGISTRAR CLIENTES");
    JButton btnCustomersList = SidebarComponents.createButton("VER CLIENTES");
    JButton btnSalesRegister = SidebarComponents.createButton("REGISTRAR VENTAS");
    JButton btnSalesList = SidebarComponents.createButton("VER VENTAS");
    JButton btnProductsRegister = SidebarComponents.createButton("REGISTRAR PRODUCTOS");
    JButton btnProductsList = SidebarComponents.createButton("VER PRODUCTOS");
    JButton btnReports = SidebarComponents.createButton("REPORTES");

    List<JButton> btnsSidebar = List.of(
        btnCustomersRegister,
        btnCustomersList,
        btnProductsRegister,
        btnProductsList,
        btnSalesRegister,
        btnSalesList,
        btnReports);

    ImageIcon original = new ImageIcon(getClass().getResource("/images/uni_navidad.png"));
    Image scaled = original.getImage().getScaledInstance(120, 220, Image.SCALE_SMOOTH);
    JLabel iconLabel = new JLabel(new ImageIcon(scaled));
    iconLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    iconLabel.setBorder(new EmptyBorder(10, 10, 20, 10));

    btnsSidebar.forEach(b -> {
      sidebar.add(b);
      b.setEnabled(false);
    });
    sidebar.add(Box.createVerticalGlue());
    sidebar.add(iconLabel);

    // ---- CONTENT PANEL ---
    JPanel root = new JPanel(new BorderLayout());

    JPanel header = new JPanel();
    header.setLayout(new BorderLayout());
    header.setPreferredSize(new Dimension(100, 80));
    header.setBackground(Dashboard.colorYPanel());

    JLabel terramodaTitle = new JLabel("TERRAMODA / AFILIADOS");
    terramodaTitle.setFont(new Font(font(), Font.BOLD, fontSize()));
    terramodaTitle.setHorizontalAlignment(SwingConstants.LEFT);
    terramodaTitle.setBorder(new EmptyBorder(0, 20, 0, 0));

    JPanel panelIconUniversidadPanel = new BackgroundPanel("/images/uni_no_opacity.png");
    panelIconUniversidadPanel.setAlignmentX(SwingConstants.RIGHT);
    panelIconUniversidadPanel.setBorder(new EmptyBorder(20, 0, 0, 20));
    panelIconUniversidadPanel.setPreferredSize(new Dimension(150, 40));

    header.add(terramodaTitle, BorderLayout.WEST);
    header.add(panelIconUniversidadPanel, BorderLayout.EAST);

    // AUTH - reemplazar bloque existente
    JPanel panelAuth = new JPanel(new GridBagLayout());
    panelAuth.setOpaque(false);
    panelAuth.setBorder(new EmptyBorder(10, 20, 10, 20));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // Label Usuario
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 0;
    JLabel userTextLabel = new JLabel("Usuario");
    userTextLabel.setFont(new Font(font(), Font.BOLD, fontTitleSize()));
    panelAuth.add(userTextLabel, gbc);

    // Campo Usuario
    gbc.gridx = 1;
    gbc.weightx = 1;
    JTextField userTextField = new JTextField("admin_terramoda_123", 20);
    userTextField.setPreferredSize(new Dimension(220, 28));
    userTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
    panelAuth.add(userTextField, gbc);

    // Label Contraseña
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 0;
    JLabel passTextLabel = new JLabel("Contraseña");
    passTextLabel.setFont(new Font(font(), Font.BOLD, fontTitleSize()));
    panelAuth.add(passTextLabel, gbc);

    // Campo Contraseña (JPasswordField)
    gbc.gridx = 1;
    gbc.weightx = 1;
    JPasswordField passTextField = new JPasswordField("pass_super_secret_xd", 20);
    passTextField.setPreferredSize(new Dimension(220, 28));
    passTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
    panelAuth.add(passTextField, gbc);

    // (Opcional) botón para acceder dentro del mismo panel, si quieres que aparezca
    // bajo los campos
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.NONE;
    JButton localAcceder = new JButton("ACCEDER");
    panelAuth.add(localAcceder, gbc);

    // si prefieres usar el btnAcceder del header, elimina el localAcceder y usa el
    // existente
    localAcceder.addActionListener(s -> {
      var data = new Login();
      data.user = userTextField.getText();
      data.pass = new String(passTextField.getPassword());
      try {
        client.login(data);
        setView(panelSaleRegister());
        btnsSidebar.forEach(b -> b.setEnabled(true));
      } catch (Exception e) {
        System.out.println(e);
      }
    });

    JPanel contentFather = new BackgroundPanel(
        "/images/uni_image.png");
    JPanel content = new JPanel();
    content.setOpaque(false);
    content.setLayout(new BorderLayout());
    contentFather.add(content, BorderLayout.CENTER);
    content.setBackground(Dashboard.colorContentPanel());
    content.add(panelAuth, BorderLayout.CENTER);

    JPanel footer = new JPanel();
    footer.setLayout(new BorderLayout());
    footer.setPreferredSize(new Dimension(100, 90));
    footer.setBackground(Dashboard.colorYPanel());

    JLabel enterpriseLabel = new JLabel("TERRAMODA/AFILIADOS");
    enterpriseLabel.setFont(new Font(font(), Font.BOLD, fontSize()));
    enterpriseLabel.setForeground(colorFg());

    JLabel incLabel = new JLabel("© 2025-2026 Terramoda.com, Inc. y sus afiliados");
    incLabel.setFont(new Font(font(), Font.PLAIN, fontSize()));

    JPanel panelAffilieds = new JPanel();
    panelAffilieds.setBorder(new EmptyBorder(20, 20, 0, 0));
    panelAffilieds.setAlignmentX(SwingConstants.LEFT);
    panelAffilieds.setLayout(new BoxLayout(panelAffilieds, BoxLayout.Y_AXIS));
    panelAffilieds.add(enterpriseLabel);
    panelAffilieds.add(incLabel);
    panelAffilieds.setOpaque(false);

    JPanel panelIconEnterprisePanel = new BackgroundPanel("/images/terramoda_icon.png");
    panelIconEnterprisePanel.setAlignmentX(SwingConstants.RIGHT);
    panelIconEnterprisePanel.setBorder(new EmptyBorder(20, 0, 0, 20));
    panelIconEnterprisePanel.setPreferredSize(new Dimension(150, 40));
    footer.add(panelAffilieds, BorderLayout.WEST);
    footer.add(panelIconEnterprisePanel, BorderLayout.EAST);

    contentPanel = content;

    root.add(header, BorderLayout.NORTH);
    root.add(contentFather, BorderLayout.CENTER);
    root.add(footer, BorderLayout.SOUTH);

    add(sidebar, BorderLayout.WEST);
    add(root, BorderLayout.CENTER);

    // Eventos
    btnSalesRegister.addActionListener(e -> setView(panelSaleRegister()));
    btnSalesList.addActionListener(e -> setView(panelSales(1, perPage())));
    btnCustomersRegister.addActionListener(e -> setView(panelCustomerRegister()));
    btnCustomersList.addActionListener(e -> setView(panelCustomers(1, perPage())));
    btnProductsRegister.addActionListener(e -> setView(panelProductRegister()));
    btnProductsList.addActionListener(e -> setView(panelProducts(1, perPage())));
    // btnReports.addActionListener(e -> setView(new PanelReportes()));

    setVisible(true);
  }

  private void setView(JPanel newPanel) {
    contentPanel.removeAll();
    contentPanel.add(newPanel, BorderLayout.CENTER);
    contentPanel.revalidate();
    contentPanel.repaint();
  }

  private <T extends Component> Pair<JPanel, T> envolveInPanel(T c) {
    var panel = new JPanel();
    var newLbl = panel.add(c);
    return new Pair(panel, newLbl);
  }

  JPanel panelViewObjectInTable(String title, Object[][] data) {
    var p = new JPanel();
    p.setPreferredSize(new Dimension(480, 400));
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    p.setBorder(new EmptyBorder(40, 40, 40, 40));
    var lblTitle = new JLabel(title);
    lblTitle.setOpaque(false);
    String[] columns = { "Nombre", "Valor" };
    var table = new JTable(data, columns);
    table.setEnabled(false);
    table.setIntercellSpacing(new Dimension(0, 5));
    table.getColumnModel().getColumn(0).setPreferredWidth(100);
    table.getColumnModel().getColumn(1).setPreferredWidth(100);
    table.setRowHeight(20);

    var scroll = new JScrollPane(table);
    p.add(lblTitle);
    p.add(scroll);
    return p;
  }

  Pair<JPanel, JTextField> field(String name, int fieldLimit) {
    var p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
    var lbl = new JLabel(name + ": ");
    var fld = new JTextField(fieldLimit);
    p.add(lbl);
    p.add(fld);
    return new Pair(p, fld);
  }

  Pair<JPanel, JTextField> editableField(String name, String value, int fieldLimit) {
    var p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
    var lbl = new JLabel(name + ": ");
    var fld = new JTextField(value, fieldLimit);
    p.add(lbl);
    p.add(fld);
    return new Pair(p, fld);
  }

  JPanel panelProductView(Product v) {
    Object[][] data = {
        { "SKU", v.sku },
        { "Nombre", v.name },
        { "Descripcion", v.description },
        { "Precio", v.price },
        { "Stock", v.stock }
    };
    return panelViewObjectInTable("Producto: " + v.sku, data);
  }

  public JPanel panelProductEdit(Product v) {
    var p = new JPanel(new GridBagLayout());
    p.setPreferredSize(new Dimension(520, 380));
    p.setBorder(new EmptyBorder(20, 20, 20, 20));
    p.setOpaque(false);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;

    // Title
    var lblTitle = new JLabel("EDITAR PRODUCTO " + v.sku, SwingConstants.CENTER);
    lblTitle.setFont(new Font(font(), Font.BOLD, fontTitleSize()));
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.weightx = 1;
    p.add(lblTitle, gbc);

    gbc.gridwidth = 1;
    gbc.weightx = 0;

    // Nombre
    gbc.gridy = 1;
    gbc.gridx = 0;
    p.add(new JLabel("Nombre:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    var nameField = new JTextField(v.name, 24);
    nameField.setPreferredSize(new Dimension(300, 28));
    p.add(nameField, gbc);

    // Descripcion (textarea con scroll)
    gbc.gridy = 2;
    gbc.gridx = 0;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    p.add(new JLabel("Descripción:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.BOTH;
    var descriptionArea = new JTextArea(v.description, 4, 24);
    descriptionArea.setLineWrap(true);
    descriptionArea.setWrapStyleWord(true);
    var descScroll = new JScrollPane(descriptionArea);
    descScroll.setPreferredSize(new Dimension(360, 100));
    p.add(descScroll, gbc);

    // Precio
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridy = 3;
    gbc.gridx = 0;
    gbc.weightx = 0;
    p.add(new JLabel("Precio:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    var priceField = new JFormattedTextField(new java.text.DecimalFormat("#0.00"));
    priceField.setValue((double) v.price);
    priceField.setColumns(10);
    priceField.setPreferredSize(new Dimension(140, 28));
    p.add(priceField, gbc);

    // Stock
    gbc.gridy = 4;
    gbc.gridx = 0;
    gbc.weightx = 0;
    p.add(new JLabel("Stock:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    var stockSpinner = new JSpinner(new SpinnerNumberModel(v.stock, 0, Integer.MAX_VALUE, 1));
    stockSpinner.setPreferredSize(new Dimension(100, 28));
    p.add(stockSpinner, gbc);

    // Buttons (Edit + Cancel)
    gbc.gridy = 5;
    gbc.gridx = 0;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.NONE;
    var btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
    btnPanel.setOpaque(false);

    var btnEdit = new JButton("GUARDAR");
    var btnCancel = new JButton("CANCELAR");
    btnPanel.add(btnEdit);
    btnPanel.add(btnCancel);
    p.add(btnPanel, gbc);

    // Actions
    btnEdit.addActionListener(l -> {
      var name = nameField.getText().trim();
      var description = descriptionArea.getText().trim();

      double priceValue;
      int stockValue;
      try {
        priceValue = ((Number) priceField.getValue()).doubleValue();
        stockValue = (Integer) stockSpinner.getValue();
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(p, "Precio o Stock no son valores válidos", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      if (name.isEmpty()) {
        JOptionPane.showMessageDialog(p, "El nombre no puede estar vacío", "Validación", JOptionPane.WARNING_MESSAGE);
        return;
      }
      if (priceValue <= 0 || stockValue < 0) {
        JOptionPane.showMessageDialog(p, "Precio debe ser > 0 y stock >= 0", "Validación", JOptionPane.WARNING_MESSAGE);
        return;
      }

      var editable = new ProductToEdit();
      editable.name = name;
      editable.description = description;
      editable.price = (float) priceValue;
      editable.stock = stockValue;

      try {
        this.client.editProduct(v.id, editable);
        JOptionPane.showMessageDialog(null, "Producto editado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        // cerrar el dialogo que contiene este panel si existe
        var win = SwingUtilities.getWindowAncestor(p);
        if (win instanceof JDialog) {
          ((JDialog) win).dispose();
        }
      } catch (Exception e) {
        System.out.println(e);
        JOptionPane.showMessageDialog(p, "Error editando producto", "Error", JOptionPane.ERROR_MESSAGE);
      }
    });

    btnCancel.addActionListener(l -> {
      var win = SwingUtilities.getWindowAncestor(p);
      if (win instanceof JDialog) {
        ((JDialog) win).dispose();
      }
    });

    return p;
  }

  JPanel panelProducts(int page, int per_page) {
    var p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    try {
      var result = this.client.getPaginatedProducts(page, per_page);
      var pnlInformation = panelTableInformation(result, page, per_page,
          (new_page, new_per_page) -> panelProducts(new_page, new_per_page));
      String[] columns = new String[] { "SKU", "Nombre", "Descripcion", "Precio", "Stock", "Ver/Editar" };
      var model = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
          return column == 5;
        }
      };
      var table = new JTable(model);
      for (var item : result.items) {
        model.addRow(new Object[] { item.sku, item.name, item.description, item.price, item.stock, null });
      }
      table.setRowHeight(28);
      table.getColumnModel().getColumn(0).setPreferredWidth(100); // SKU
      table.getColumnModel().getColumn(1).setPreferredWidth(100); // Name
      table.getColumnModel().getColumn(2).setPreferredWidth(120); // Description
      table.getColumnModel().getColumn(3).setPreferredWidth(50); // Price
      table.getColumnModel().getColumn(4).setPreferredWidth(50); // Stock
      table.getColumnModel().getColumn(5).setPreferredWidth(140);

      table.getColumn("Ver/Editar").setCellRenderer(new ActionRenderer());

      table.getColumn("Ver/Editar").setCellEditor(
          new ActionEditor(
              (r) -> {
                var sku = table.getValueAt(r, 0);
                for (var item : result.items) {
                  if (sku.equals(item.sku)) {
                    return panelProductView(item);
                  }
                }
                return new JPanel();
              },
              (r) -> {
                var sku = table.getValueAt(r, 0);
                for (var item : result.items) {
                  if (sku.equals(item.sku)) {
                    return panelProductEdit(item);
                  }
                }
                return new JPanel();
              }));
      var scroll = new JScrollPane(table);
      table.setFillsViewportHeight(true);
      scroll.setPreferredSize(new Dimension(900, 600));
      scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 600));
      p.add(scroll);
      p.add(pnlInformation);
    } catch (Exception e) {
      System.out.println(e);
      var errorText = new JLabel(e.toString());
      p.add(errorText);
    }
    return p;

  }

  public JPanel panelProductRegister() {
    var p = new JPanel(new GridBagLayout());
    p.setOpaque(false);
    p.setBorder(new EmptyBorder(20, 40, 20, 40));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;

    // Title
    var lblTitle = new JLabel("REGISTRAR PRODUCTO", SwingConstants.CENTER);
    lblTitle.setFont(new Font(font(), Font.BOLD, fontTitleSize()));
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.weightx = 1;
    p.add(lblTitle, gbc);

    gbc.gridwidth = 1;
    gbc.weightx = 0;

    // SKU
    gbc.gridy = 1;
    gbc.gridx = 0;
    p.add(new JLabel("SKU:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    var skuField = new JTextField(20);
    skuField.setPreferredSize(new Dimension(300, 28));
    p.add(skuField, gbc);

    // Nombre
    gbc.gridy = 2;
    gbc.gridx = 0;
    gbc.weightx = 0;
    p.add(new JLabel("Nombre:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    var nameField = new JTextField(30);
    nameField.setPreferredSize(new Dimension(300, 28));
    p.add(nameField, gbc);

    // Descripcion (textarea con scroll)
    gbc.gridy = 3;
    gbc.gridx = 0;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.weightx = 0;
    p.add(new JLabel("Descripción:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.BOTH;
    var descriptionArea = new JTextArea(4, 30);
    descriptionArea.setLineWrap(true);
    descriptionArea.setWrapStyleWord(true);
    var descScroll = new JScrollPane(descriptionArea);
    descScroll.setPreferredSize(new Dimension(400, 100));
    p.add(descScroll, gbc);

    // Reset fill/anchor for the rest
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;

    // Stock + Precio in a single compact row
    gbc.gridy = 4;
    gbc.gridx = 0;
    gbc.weightx = 0;
    p.add(new JLabel("Stock:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    // Use JSpinner for stock so the user cannot type invalid negative values
    var stockSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
    stockSpinner.setPreferredSize(new Dimension(100, 28));
    var stockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
    stockPanel.setOpaque(false);
    stockPanel.add(stockSpinner);
    p.add(stockPanel, gbc);

    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.weightx = 0;
    p.add(new JLabel("Precio (COP):"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    var priceField = new JFormattedTextField(new java.text.DecimalFormat("#0.00"));
    priceField.setValue(0.00);
    priceField.setColumns(10);
    priceField.setPreferredSize(new Dimension(120, 28));
    var pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
    pricePanel.setOpaque(false);
    pricePanel.add(priceField);
    p.add(pricePanel, gbc);

    // Register button centered
    gbc.gridy = 6;
    gbc.gridx = 0;
    gbc.gridwidth = 2;
    gbc.weightx = 1;
    gbc.anchor = GridBagConstraints.CENTER;
    var btnRegister = new JButton("REGISTRAR");
    btnRegister.setPreferredSize(new Dimension(140, 32));
    p.add(btnRegister, gbc);

    // Action: validate and create product
    btnRegister.addActionListener(l -> {
      var sku = skuField.getText().trim();
      var name = nameField.getText().trim();
      var description = descriptionArea.getText().trim();

      if (sku.isEmpty() || name.isEmpty()) {
        JOptionPane.showMessageDialog(p, "SKU y Nombre son obligatorios", "Campos faltantes",
            JOptionPane.WARNING_MESSAGE);
        return;
      }

      int stock;
      double price;
      try {
        stock = (Integer) stockSpinner.getValue();
        price = ((Number) priceField.getValue()).doubleValue();
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(p, "Stock o Precio no son valores válidos", "Error de validación",
            JOptionPane.ERROR_MESSAGE);
        return;
      }

      if (stock < 0 || price <= 0) {
        JOptionPane.showMessageDialog(p, "Stock debe ser >= 0 y Precio > 0", "Valores inválidos",
            JOptionPane.ERROR_MESSAGE);
        return;
      }

      var productToCreate = new ProductToCreate();
      productToCreate.sku = sku;
      productToCreate.name = name;
      productToCreate.description = description;
      productToCreate.stock = stock;
      productToCreate.price = (float) price;

      try {
        this.client.createProduct(productToCreate);
        JOptionPane.showMessageDialog(null, "Producto Registrado Exitosamente", "Registro Exitoso",
            JOptionPane.INFORMATION_MESSAGE);
        // Clear fields after success
        skuField.setText("");
        nameField.setText("");
        descriptionArea.setText("");
        stockSpinner.setValue(0);
        priceField.setValue(0.00);
      } catch (Exception e) {
        System.out.println(e);
        JOptionPane.showMessageDialog(p, "Error Registrando producto", "Error Registrando Producto",
            JOptionPane.ERROR_MESSAGE);
      }
    });

    return p;
  }

  public JPanel panelCustomerEdit(Customer e) {
    var p = new JPanel(new GridBagLayout());
    p.setPreferredSize(new Dimension(520, 300));
    p.setBorder(new EmptyBorder(16, 20, 16, 20));
    p.setOpaque(false);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;

    // Title
    var lblTitle = new JLabel("EDITAR CLIENTE " + e.cc, SwingConstants.CENTER);
    lblTitle.setFont(new Font(font(), Font.BOLD, fontTitleSize()));
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.weightx = 1;
    p.add(lblTitle, gbc);

    gbc.gridwidth = 1;
    gbc.weightx = 0;

    // Email
    gbc.gridy = 1;
    gbc.gridx = 0;
    p.add(new JLabel("Email:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    var fldEmail = new JTextField(e.email, 28);
    fldEmail.setPreferredSize(new Dimension(320, 28));
    p.add(fldEmail, gbc);

    // Nombre completo
    gbc.gridy = 2;
    gbc.gridx = 0;
    gbc.weightx = 0;
    p.add(new JLabel("Nombre Completo:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    var fldName = new JTextField(e.name, 28);
    fldName.setPreferredSize(new Dimension(320, 28));
    p.add(fldName, gbc);

    // Teléfono
    gbc.gridy = 3;
    gbc.gridx = 0;
    gbc.weightx = 0;
    p.add(new JLabel("Teléfono:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    var fldPhone = new JTextField(e.phone, 20);
    fldPhone.setPreferredSize(new Dimension(180, 28));
    p.add(fldPhone, gbc);

    // Buttons: Guardar + Cancelar
    gbc.gridy = 4;
    gbc.gridx = 0;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.NONE;
    var btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
    btnPanel.setOpaque(false);

    var btnSave = new JButton("GUARDAR");
    var btnCancel = new JButton("CANCELAR");
    btnSave.setPreferredSize(new Dimension(120, 30));
    btnCancel.setPreferredSize(new Dimension(120, 30));
    btnPanel.add(btnSave);
    btnPanel.add(btnCancel);
    p.add(btnPanel, gbc);

    // Actions
    btnSave.addActionListener(l -> {
      var toEdit = new CustomerToEdit();
      toEdit.email = fldEmail.getText().trim();
      toEdit.name = fldName.getText().trim();
      if (fldPhone.getText().trim().isEmpty()) {
        toEdit.phone = null;
      } else {
        toEdit.phone = fldPhone.getText().trim();
        if (toEdit.phone.length() != 10 && !toEdit.phone.matches("\\d{10}")) {
          JOptionPane.showMessageDialog(p, "El teléfono debe tener 10 dígitos.", "Teléfono inválido",
              JOptionPane.WARNING_MESSAGE);
          return;
        }
      }

      // Validaciones simples
      if (toEdit.name.isEmpty()) {
        JOptionPane.showMessageDialog(p, "El nombre es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
        return;
      }
      if (!toEdit.email.isEmpty()) {
        String emailRegex = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
        if (!toEdit.email.matches(emailRegex)) {
          JOptionPane.showMessageDialog(p, "Ingrese un correo válido o deje el campo vacío.", "Email inválido",
              JOptionPane.WARNING_MESSAGE);
          return;
        }
      }

      try {
        this.client.editCustomer(e.id, toEdit);
        JOptionPane.showMessageDialog(null, "Cliente editado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);

        // cerrar el dialogo que contiene este panel si existe
        var win = SwingUtilities.getWindowAncestor(p);
        if (win instanceof JDialog) {
          ((JDialog) win).dispose();
        } else {
          // si no está en un diálogo, refrescar la vista de clientes
          setView(panelCustomers(1, 10));
        }
      } catch (Exception ex) {
        System.out.println(ex);
        JOptionPane.showMessageDialog(p, "Error editando el cliente", "Error", JOptionPane.ERROR_MESSAGE);
      }
    });

    btnCancel.addActionListener(l -> {
      var win = SwingUtilities.getWindowAncestor(p);
      if (win instanceof JDialog) {
        ((JDialog) win).dispose();
      } else {
        // volver a la vista anterior (opcional)
        setView(panelCustomers(1, 10));
      }
    });

    return p;
  }

  JPanel panelCustomerView(Customer v) {
    Object[][] data = {
        { "Cedula", v.cc },
        { "Nombre", v.name },
        { "Email", v.email },
        { "Telefono", v.phone }
    };
    return panelViewObjectInTable("Cliente: " + v.cc, data);
  }

  JPanel panelCustomers(int page, int per_page) {
    var p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    try {
      var result = this.client.getPaginatedCustomers(page, per_page);
      var pnlInformation = panelTableInformation(result, page, per_page,
          (new_page, new_per_page) -> panelCustomers(new_page, new_per_page));
      String[] columns = new String[] { "Cedula", "Nombre", "Email", "Telefono", "Ver/Editar" };
      var model = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
          return column == 4;
        }
      };
      var table = new JTable(model);
      for (var item : result.items) {
        model.addRow(new Object[] { item.cc, item.name, item.email, item.phone });
      }
      table.setRowHeight(28);
      table.getColumnModel().getColumn(0).setPreferredWidth(100); // CC
      table.getColumnModel().getColumn(1).setPreferredWidth(120); // Name
      table.getColumnModel().getColumn(2).setPreferredWidth(120); // Email
      table.getColumnModel().getColumn(3).setPreferredWidth(100); // Phone
      table.getColumnModel().getColumn(4).setPreferredWidth(150);

      table.getColumn("Ver/Editar").setCellRenderer(new ActionRenderer());

      table.getColumn("Ver/Editar").setCellEditor(
          new ActionEditor(
              (r) -> {
                var id = table.getValueAt(r, 0);
                for (var item : result.items) {
                  if (id.equals(item.cc)) {
                    return panelCustomerView(item);
                  }
                }
                return new JPanel();
              },
              (r) -> {
                var id = table.getValueAt(r, 0);
                for (var item : result.items) {
                  if (id.equals(item.cc)) {
                    return panelCustomerEdit(item);
                  }
                }
                return new JPanel();
              }));
      var scroll = new JScrollPane(table);
      table.setFillsViewportHeight(true);
      scroll.setPreferredSize(new Dimension(900, 600));
      scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 600));
      p.add(scroll);
      p.add(pnlInformation);
    } catch (Exception e) {
      System.out.println(e);
      var errorText = new JLabel(e.toString());
      p.add(errorText);
    }
    return p;
  }

  public JPanel panelCustomerRegister() {
    var p = new JPanel(new GridBagLayout());
    p.setOpaque(false);
    p.setBorder(new EmptyBorder(20, 40, 20, 40));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;

    // Título
    var lblTitle = new JLabel("REGISTRAR CLIENTE", SwingConstants.CENTER);
    lblTitle.setFont(new Font(font(), Font.BOLD, fontTitleSize()));
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.weightx = 1;
    p.add(lblTitle, gbc);

    gbc.gridwidth = 1;
    gbc.weightx = 0;

    // Cedula
    gbc.gridy = 1;
    gbc.gridx = 0;
    p.add(new JLabel("Cédula:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    var fldCc = new JTextField(15);
    fldCc.setPreferredSize(new Dimension(260, 28));
    p.add(fldCc, gbc);

    // Email
    gbc.gridy = 2;
    gbc.gridx = 0;
    gbc.weightx = 0;
    p.add(new JLabel("Email:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    var fldEmail = new JTextField(30);
    fldEmail.setPreferredSize(new Dimension(360, 28));
    p.add(fldEmail, gbc);

    // Nombre completo
    gbc.gridy = 3;
    gbc.gridx = 0;
    gbc.weightx = 0;
    p.add(new JLabel("Nombre Completo:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    var fldName = new JTextField(30);
    fldName.setPreferredSize(new Dimension(360, 28));
    p.add(fldName, gbc);

    // Teléfono
    gbc.gridy = 4;
    gbc.gridx = 0;
    gbc.weightx = 0;
    p.add(new JLabel("Teléfono:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    var fldPhone = new JTextField(15);
    fldPhone.setPreferredSize(new Dimension(180, 28));
    p.add(fldPhone, gbc);

    // Botón registrar centrado
    gbc.gridy = 5;
    gbc.gridx = 0;
    gbc.gridwidth = 2;
    gbc.weightx = 1;
    gbc.anchor = GridBagConstraints.CENTER;
    var btnRegister = new JButton("REGISTRAR");
    btnRegister.setPreferredSize(new Dimension(140, 32));
    p.add(btnRegister, gbc);

    // Acción del botón: validación básica y petición al cliente
    btnRegister.addActionListener(l -> {
      var cc = fldCc.getText().trim();
      var email = fldEmail.getText().trim();
      var name = fldName.getText().trim();
      var phone = fldPhone.getText().trim();

      if (cc.isEmpty() || name.isEmpty() || email.isEmpty()) {
        JOptionPane.showMessageDialog(p, "La cédula, el nombre y el email son obligatorios.", "Campos faltantes",
            JOptionPane.WARNING_MESSAGE);
        return;
      }

      // Validación simple de email (no perfecta pero útil)
      String emailRegex = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
      if (!email.matches(emailRegex)) {
        JOptionPane.showMessageDialog(p, "Ingrese un correo electrónico válido.",
            "Email inválido", JOptionPane.WARNING_MESSAGE);
        return;
      }

      if (!phone.isEmpty() && !phone.matches("^\\+?\\d{10}$")) {
        JOptionPane.showMessageDialog(p, "Ingrese un número de teléfono válido (10 dígitos).",
            "Teléfono inválido", JOptionPane.WARNING_MESSAGE);
        return;
      }

      var customer = new CustomerToCreate();
      customer.cc = cc;
      customer.email = email;
      customer.name = name;
      if (!phone.isEmpty())
        customer.phone = phone;
      else
        customer.phone = null;

      try {
        var result = this.client.createCustomer(customer);
        var msg = "Cliente creado con cédula " + customer.cc;
        JOptionPane.showMessageDialog(null, msg, "Cliente Registrado", JOptionPane.INFORMATION_MESSAGE);

        // Limpiar campos
        fldCc.setText("");
        fldEmail.setText("");
        fldName.setText("");
        fldPhone.setText("");

        // refrescar la vista si quieres mantener el formulario limpio
        setView(panelCustomerRegister());
      } catch (Exception e) {
        System.out.println(e);
        JOptionPane.showMessageDialog(p, "Error registrando el cliente", "Error Registrando Cliente",
            JOptionPane.ERROR_MESSAGE);
      }
    });

    return p;
  }

  JPanel panelSaleView(Sale sale) {
    var panel = new JPanel();
    panel.setPreferredSize(new Dimension(480, 400));
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(40, 40, 40, 40));
    Object[][] data = {
        { "Id", String.valueOf(sale.id) },
        { "Cedula", sale.customer.cc },
        { "Costo Total", String.valueOf(sale.total_amount) },
        { "Fecha", sale.generated_at }
    };
    String[] columns = { "Nombre", "Valor" };
    var tableSale = new JTable(data, columns);
    tableSale.setEnabled(false);
    tableSale.setIntercellSpacing(new Dimension(0, 5));
    tableSale.getColumnModel().getColumn(0).setPreferredWidth(100);
    tableSale.getColumnModel().getColumn(1).setPreferredWidth(100);
    var scrollSale = new JScrollPane(tableSale);

    var mdlSkuUnit = new DefaultTableModel(new String[] { "SKU", "UNIDADES" }, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    for (var product : sale.products) {
      mdlSkuUnit.addRow(new Object[] { product.product.sku, product.quantity });
    }
    var tblSkuUnit = new JTable(mdlSkuUnit);
    tblSkuUnit.getColumnModel().getColumn(0).setPreferredWidth(100);
    tblSkuUnit.getColumnModel().getColumn(1).setPreferredWidth(50);
    tblSkuUnit.setRowHeight(28);
    JScrollPane scrollSkuUnit = new JScrollPane(tblSkuUnit);

    panel.add(scrollSale);
    panel.add(scrollSkuUnit);
    return panel;
  }

  <T> JPanel panelTableInformation(PaginationResult<T> pagination, int page, int per_page,
      BiFunction<Integer, Integer, JPanel> getPanel) {
    var p = new JPanel(new BorderLayout());
    p.setBorder(new EmptyBorder(15, 15, 15, 15));
    p.setOpaque(false);

    // --- Panel central con datos (alineados y estilizados) ---
    var infoPanel = new JPanel(new GridBagLayout());
    infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    infoPanel.setOpaque(true);
    infoPanel.setBackground(new Color(245, 245, 245)); // fondo sutil para separar visualmente

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(6, 8, 6, 8);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    Font labelFont = new Font(font(), Font.BOLD, Math.max(fontSize(), 11));
    Font valueFont = new Font(font(), Font.PLAIN, Math.max(fontSize(), 11));

    // fila 0 - ELEMENTOS TOTALES
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 0;
    gbc.anchor = GridBagConstraints.WEST;
    var lblTotalTitle = new JLabel("ELEMENTOS TOTALES:");
    lblTotalTitle.setFont(labelFont);
    infoPanel.add(lblTotalTitle, gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    gbc.anchor = GridBagConstraints.WEST;
    var lblTotalValue = new JLabel(String.valueOf(pagination.total_items));
    lblTotalValue.setFont(valueFont);
    infoPanel.add(lblTotalValue, gbc);

    // fila 1 - NUMERO DE PAGINA
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 0;
    gbc.anchor = GridBagConstraints.WEST;
    var lblPageTitle = new JLabel("NÚMERO DE PÁGINA:");
    lblPageTitle.setFont(labelFont);
    infoPanel.add(lblPageTitle, gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    gbc.anchor = GridBagConstraints.WEST;
    var lblPageValue = new JLabel(String.valueOf(pagination.current_page));
    lblPageValue.setFont(valueFont);
    infoPanel.add(lblPageValue, gbc);

    // fila 2 - ELEMENTOS VISIBLES
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.weightx = 0;
    gbc.anchor = GridBagConstraints.WEST;
    var lblVisibleTitle = new JLabel("ELEMENTOS VISIBLES:");
    lblVisibleTitle.setFont(labelFont);
    infoPanel.add(lblVisibleTitle, gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    gbc.anchor = GridBagConstraints.WEST;
    var lblVisibleValue = new JLabel(String.valueOf(pagination.items_count));
    lblVisibleValue.setFont(valueFont);
    infoPanel.add(lblVisibleValue, gbc);

    // --- Separador y área de navegación ---
    var separator = new JSeparator();
    separator.setForeground(Color.LIGHT_GRAY);

    // Botones de navegación
    var navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
    navigationPanel.setOpaque(false);

    var btnPrevious = new JButton("⟵ ANTERIOR");
    var btnReload = new JButton("Recargar");
    var btnNext = new JButton("SIGUIENTE ⟶");

    Dimension btnSize = new Dimension(160, 36);
    btnPrevious.setPreferredSize(btnSize);
    btnReload.setPreferredSize(btnSize);
    btnNext.setPreferredSize(btnSize);

    // Habilitar / deshabilitar según estado de paginación
    btnPrevious.setEnabled(page > 1);
    boolean hasMore = pagination.total_items > (pagination.items_count * pagination.current_page);
    btnNext.setEnabled(hasMore);

    // Listeners
    btnReload.addActionListener(l -> setView(getPanel.apply(page, per_page)));
    if (page > 1) {
      btnPrevious.addActionListener(l -> setView(getPanel.apply(page - 1, per_page)));
    }
    if (hasMore) {
      btnNext.addActionListener(l -> setView(getPanel.apply(page + 1, per_page)));
    }

    navigationPanel.add(btnPrevious);
    navigationPanel.add(btnReload);
    navigationPanel.add(btnNext);

    // --- Ensamblado final ---
    var bottomBox = new JPanel();
    bottomBox.setLayout(new BorderLayout());
    bottomBox.setOpaque(false);
    bottomBox.add(separator, BorderLayout.NORTH);
    bottomBox.add(navigationPanel, BorderLayout.CENTER);

    p.add(infoPanel, BorderLayout.CENTER);
    p.add(bottomBox, BorderLayout.SOUTH);

    return p;
  }

  private JPanel panelSales(int page, int per_page) {
    var panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    try {
      var sales = client.getPaginatedSales(page, per_page);
      var pnlInformation = panelTableInformation(sales, page, per_page,
          (new_page, new_per_page) -> panelSales(new_page, new_per_page));

      String[] columns = { "ID", "Cedula", "Total", "Fecha de Venta", "Ver" };
      var model = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
          return column == 4;
        }
      };
      var table = new JTable(model);
      table.setRowHeight(28);
      table.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
      table.getColumnModel().getColumn(1).setPreferredWidth(150); // Cedula
      table.getColumnModel().getColumn(2).setPreferredWidth(100); // Total
      table.getColumnModel().getColumn(3).setPreferredWidth(300); // Fecha
      table.getColumnModel().getColumn(4).setPreferredWidth(10);
      for (var sale : sales.items) {
        model.addRow(new Object[] { sale.id, sale.customer.cc, sale.total_amount, sale.generated_at, null });
      }
      table.getColumn("Ver").setCellRenderer(new ActionRenderer());

      table.getColumn("Ver").setCellEditor(
          new ActionEditor(
              (r) -> {
                var id = table.getValueAt(r, 0);
                for (var item : sales.items) {
                  if (id.equals(item.id)) {
                    return panelSaleView(item);
                  }
                }
                return new JPanel();
              },
              (r) -> {
                return new JPanel();
              }));

      JScrollPane scroll = new JScrollPane(table);
      table.setFillsViewportHeight(true);
      scroll.setPreferredSize(new Dimension(900, 600));
      scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 600));
      panel.add(scroll);
      panel.add(pnlInformation);
    } catch (Exception e) {
      System.out.println(e);
      var errorText = new JLabel(e.toString());
      panel.add(errorText);
    }
    return panel;

  }

  private JPanel panelSaleRegister() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setOpaque(false);
    panel.setBorder(new EmptyBorder(20, 40, 20, 40));

    // TITULO
    JLabel title = new JLabel("REGISTRAR NUEVA VENTA", SwingConstants.CENTER);
    title.setFont(new Font(font(), Font.BOLD, fontTitleSize()));
    title.setBorder(new EmptyBorder(10, 0, 20, 0));
    panel.add(title, BorderLayout.NORTH);

    // --- FORM (cedula + SKU + quantity + agregar)
    JPanel form = new JPanel(new GridBagLayout());
    form.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // Cedula
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 0;
    form.add(new JLabel("CÉDULA"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    JTextField ccTextField = new JTextField(15);
    ccTextField.setPreferredSize(new Dimension(160, 28));
    form.add(ccTextField, gbc);

    // SKU
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 0;
    form.add(new JLabel("SKU"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    JTextField skuTextField = new JTextField(20);
    skuTextField.setPreferredSize(new Dimension(240, 28));
    form.add(skuTextField, gbc);

    // Quantity
    gbc.gridx = 2;
    gbc.gridy = 1;
    gbc.weightx = 0;
    form.add(new JLabel("CANTIDAD"), gbc);

    gbc.gridx = 3;
    gbc.weightx = 0;
    JTextField quantityTextField = new JTextField(5);
    quantityTextField.setPreferredSize(new Dimension(60, 28));
    form.add(quantityTextField, gbc);

    // Agregar button
    gbc.gridx = 4;
    gbc.weightx = 0;
    JButton btnAddProduct = new JButton("AGREGAR");
    form.add(btnAddProduct, gbc);

    panel.add(form, BorderLayout.CENTER);

    // --- TABLE de productos añadidos (más limpia y consistente)
    String[] cols = new String[] { "SKU", "CANTIDAD" };
    DefaultTableModel productsModel = new DefaultTableModel(cols, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    JTable productsTable = new JTable(productsModel);
    productsTable.setFillsViewportHeight(true);
    productsTable.setRowHeight(28);
    productsTable.getColumnModel().getColumn(0).setPreferredWidth(300);
    productsTable.getColumnModel().getColumn(1).setPreferredWidth(80);

    JScrollPane tableScroll = new JScrollPane(productsTable);
    tableScroll.setPreferredSize(new Dimension(760, 220));
    tableScroll.setBorder(BorderFactory.createTitledBorder("Productos añadidos"));
    tableScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

    // Panel inferior con botones: Remover seleccionado, Remover todo, Register
    JPanel bottom = new JPanel(new BorderLayout());
    bottom.setOpaque(false);
    JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
    actions.setOpaque(false);

    JButton btnRemoveSelected = new JButton("REMOVER SELECCIONADO");
    JButton btnRemoveAll = new JButton("REMOVER TODO");
    JButton btnRegister = new JButton("REGISTRAR VENTA");

    actions.add(btnRemoveSelected);
    actions.add(btnRemoveAll);

    // Centrar el botón Register en la parte inferior derecha-ish
    JPanel registerBox = new JPanel(new FlowLayout(FlowLayout.CENTER));
    registerBox.setOpaque(false);
    registerBox.add(btnRegister);

    bottom.add(actions, BorderLayout.NORTH);
    bottom.add(tableScroll, BorderLayout.CENTER);
    bottom.add(registerBox, BorderLayout.SOUTH);

    // Añadir todo al panel principal (centro->form + abajo->tabla y acciones)
    JPanel centerStack = new JPanel();
    centerStack.setOpaque(false);
    centerStack.setLayout(new BoxLayout(centerStack, BoxLayout.Y_AXIS));
    centerStack.add(form);
    centerStack.add(Box.createRigidArea(new Dimension(0, 12)));
    centerStack.add(tableScroll);
    centerStack.add(Box.createRigidArea(new Dimension(0, 12)));
    centerStack.add(actions);
    centerStack.add(Box.createRigidArea(new Dimension(0, 8)));
    centerStack.add(registerBox);

    panel.add(centerStack, BorderLayout.CENTER);

    // --- Lógica: agregar / remover / registrar
    btnAddProduct.addActionListener(e -> {
      var sku = skuTextField.getText().trim();
      if (sku.isEmpty()) {
        JOptionPane.showMessageDialog(panel, "Ingrese un SKU válido", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      int qty;
      try {
        qty = Integer.parseInt(quantityTextField.getText().trim());
        if (qty <= 0)
          throw new NumberFormatException();
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(panel, "Ingrese una cantidad válida (entero > 0)", "Error",
            JOptionPane.ERROR_MESSAGE);
        return;
      }
      // evitar duplicado: sumar si ya existe (o puedes bloquear la duplicación)
      boolean found = false;
      for (int r = 0; r < productsModel.getRowCount(); r++) {
        if (productsModel.getValueAt(r, 0).equals(sku)) {
          int current = Integer.parseInt(productsModel.getValueAt(r, 1).toString());
          productsModel.setValueAt(current + qty, r, 1);
          found = true;
          break;
        }
      }
      if (!found) {
        productsModel.addRow(new Object[] { sku, qty });
      }
      skuTextField.setText("");
      quantityTextField.setText("");
    });

    btnRemoveSelected.addActionListener(e -> {
      int sel = productsTable.getSelectedRow();
      if (sel >= 0) {
        productsModel.removeRow(sel);
      } else {
        JOptionPane.showMessageDialog(panel, "Seleccione una fila para remover", "Info",
            JOptionPane.INFORMATION_MESSAGE);
      }
    });

    btnRemoveAll.addActionListener(e -> {
      productsModel.setRowCount(0);
    });

    btnRegister.addActionListener(e -> {
      String cc = ccTextField.getText().trim();
      if (cc.isEmpty()) {
        JOptionPane.showMessageDialog(panel, "Ingrese la cédula del cliente", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      if (productsModel.getRowCount() == 0) {
        JOptionPane.showMessageDialog(panel, "Agregue al menos un producto", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      var sale = new SaleToCreate();
      sale.customer_cc = cc;
      List<ProductSku> products = new ArrayList<>();
      for (int r = 0; r < productsModel.getRowCount(); r++) {
        var ps = new ProductSku();
        ps.sku = productsModel.getValueAt(r, 0).toString();
        ps.quantity = Integer.parseInt(productsModel.getValueAt(r, 1).toString());
        products.add(ps);
      }
      sale.product_skus_quantity = products;
      try {
        var saleCreated = client.createSale(sale);
        var msg = "Venta Registrada\nId: " + saleCreated.sale_id + "\nCosto total: " + saleCreated.total_amount;
        JOptionPane.showMessageDialog(null, msg, "Venta Registrada", JOptionPane.INFORMATION_MESSAGE);
        setView(panelSaleRegister()); // refrescar la vista
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(panel, "Error registrando venta. Verifique cédula o SKU", "Error",
            JOptionPane.ERROR_MESSAGE);
        System.out.println(ex);
      }
    });

    return panel;
  }
}

class ButtonBase extends JButton {

  public ButtonBase(String text) {
    super(text);
    setFont(new Font("Inter", Font.PLAIN, 14));
    setFocusPainted(false);
  }
}

class SidebarComponents {

  static public JButton createButton(String text) {
    JButton btn = new ButtonBase(text);

    btn.setOpaque(false);
    btn.setContentAreaFilled(false);
    btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
    btn.setAlignmentX(Component.LEFT_ALIGNMENT);

    btn.setForeground(Color.WHITE);
    btn.setBorderPainted(false);
    btn.setFocusPainted(false);

    btn.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        btn.setForeground(Color.YELLOW);
      }

      @Override
      public void mouseExited(MouseEvent e) {
        btn.setForeground(Color.WHITE);
        btn.repaint();
      }
    });
    return btn;
  }
}
