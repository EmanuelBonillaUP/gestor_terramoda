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
    btnSalesList.addActionListener(e -> setView(panelSales(1, 10)));
    btnCustomersRegister.addActionListener(e -> setView(panelCustomerRegister()));
    btnCustomersList.addActionListener(e -> setView(panelCustomers(1, 10)));
    btnProductsRegister.addActionListener(e -> setView(panelProductRegister()));
    btnProductsList.addActionListener(e -> setView(panelProducts(1, 10)));
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

  JPanel panelProductEdit(Product v) {
    var p = new JPanel();
    p.setPreferredSize(new Dimension(480, 400));
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    p.setBorder(new EmptyBorder(40, 40, 40, 40));
    var lblTitle = new JLabel("EDITAR PRODUCTO " + v.sku);
    lblTitle.setOpaque(false);
    var nameField = editableField("Nombre", v.name, 15);
    var descriptionField = editableField("Descripcion", v.description, 50);
    var priceField = editableField("Precio", String.valueOf(v.price), 10);
    var stockField = editableField("Stock", String.valueOf(v.stock), 10);
    var btnEdit = new JButton("EDITAR");
    p.add(lblTitle);
    p.add(nameField.a());
    p.add(descriptionField.a());
    p.add(priceField.a());
    p.add(stockField.a());
    p.add(btnEdit);
    btnEdit.addActionListener(l -> {
      var name = nameField.b().getText();
      var description = descriptionField.b().getText();
      var priceString = priceField.b().getText();
      var stockString = stockField.b().getText();

      float price = 0;
      int stock = 0;
      try {
        price = Float.valueOf(priceString);
        stock = Integer.valueOf(stockString);
      } catch (Exception e) {
        JOptionPane.showMessageDialog(p, "Error Stock o Price no son valores numericos validos",
            "Error Editando Producto", JOptionPane.ERROR_MESSAGE);
        return;
      }
      if (stock < 0 || price <= 0) {
        JOptionPane.showMessageDialog(p, "Error Stock o Price no son valores validos", "Error Editando Producto",
            JOptionPane.ERROR_MESSAGE);
        return;
      }
      var editable = new ProductToEdit();
      editable.name = name;
      editable.description = description;
      editable.price = price;
      editable.stock = stock;
      try {
        this.client.editProduct(v.id, editable);
        JOptionPane.showMessageDialog(null, "Producto Editado Exitosamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
      } catch (Exception e) {
        System.out.println(e);
        JOptionPane.showMessageDialog(p, "Error editando producto", "Error Editando Producto",
            JOptionPane.ERROR_MESSAGE);
      }
    });
    return p;
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
      scroll.setPreferredSize(new Dimension(900, 300));
      scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
      p.add(scroll);
      p.add(pnlInformation);
    } catch (Exception e) {
      System.out.println(e);
      var errorText = new JLabel(e.toString());
      p.add(errorText);
    }
    return p;

  }

  JPanel panelProductRegister() {
    var p = new JPanel();
    p.setOpaque(false);
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    var lblTitle = new JLabel("REGISTRAR PRODUCTO");
    lblTitle.setOpaque(false);
    var skuField = field("SKU", 15);
    var nameField = field("Nombre", 20);
    var descriptionField = field("Descripcion", 40);
    var stockField = field("Stock", 5);
    var priceField = field("Precio", 10);
    var btnRegister = new JButton("REGISTRAR");
    p.add(lblTitle);
    p.add(skuField.a());
    p.add(nameField.a());
    p.add(descriptionField.a());
    p.add(stockField.a());
    p.add(priceField.a());
    p.add(btnRegister);
    btnRegister.addActionListener(l -> {
      var sku = skuField.b().getText();
      var name = nameField.b().getText();
      var description = descriptionField.b().getText();

      float price;
      int stock;
      try {
        price = Float.parseFloat(priceField.b().getText());
        stock = Integer.parseInt(stockField.b().getText());
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(p, "Error Stock o Price no son valores numericos validos",
            "Error Editando Producto", JOptionPane.ERROR_MESSAGE);
        return;
      }
      if (stock < 0 || price <= 0) {
        JOptionPane.showMessageDialog(p, "Error Stock o Price no son valores validos", "Error Editando Producto",
            JOptionPane.ERROR_MESSAGE);
        return;
      }
      var productToCreate = new ProductToCreate();
      productToCreate.sku = sku;
      productToCreate.name = name;
      productToCreate.stock = stock;
      productToCreate.price = price;
      productToCreate.description = description;
      try {
        this.client.createProduct(productToCreate);
        JOptionPane.showMessageDialog(null, "Producto Registrado Exitosamente", "Registro Exitoso",
            JOptionPane.INFORMATION_MESSAGE);
      } catch (Exception e) {
        System.out.println(e);
        JOptionPane.showMessageDialog(p, "Error Registrando producto", "Error Registrando Producto",
            JOptionPane.ERROR_MESSAGE);
      }
    });
    return p;
  }

  JPanel panelCustomerEdit(Customer e) {
    var p = new JPanel();
    p.setPreferredSize(new Dimension(480, 400));
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    p.setBorder(new EmptyBorder(40, 40, 40, 40));
    var lblTitle = new JLabel("EDITAR CLIENTE " + e.cc);
    lblTitle.setOpaque(false);
    // Email
    var pEmail = new JPanel();
    pEmail.setLayout(new BoxLayout(pEmail, BoxLayout.X_AXIS));
    var lblEmail = new JLabel("Email: ");
    var fldEmail = new JTextField(e.email, 20);
    pEmail.add(lblEmail);
    pEmail.add(fldEmail);
    // Name
    var pName = new JPanel();
    pName.setLayout(new BoxLayout(pName, BoxLayout.X_AXIS));
    var lblName = new JLabel("Nombre Completo: ");
    var fldName = new JTextField(e.name, 20);
    pName.add(lblName);
    pName.add(fldName);
    // Phone
    var pPhone = new JPanel();
    pPhone.setLayout(new BoxLayout(pPhone, BoxLayout.X_AXIS));
    var lblPhone = new JLabel("Telefono: ");
    var fldPhone = new JTextField(e.phone, 10);
    pPhone.add(lblPhone);
    pPhone.add(fldPhone);
    // edit
    var btnEdit = new JButton("GUARDAR");
    btnEdit.addActionListener(l -> {
      var toEdit = new CustomerToEdit();
      toEdit.email = fldEmail.getText();
      toEdit.name = fldName.getText();
      toEdit.phone = fldPhone.getText();
      try {
        this.client.editCustomer(e.id, toEdit);
        JOptionPane.showMessageDialog(null, "Cliente Editado Exitosamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
      } catch (Exception exception) {
        System.out.println(exception);
        JOptionPane.showMessageDialog(p, "Error Editando el cliente", "Error Editando Cliente",
            JOptionPane.ERROR_MESSAGE);
      }
    });

    p.add(lblTitle);
    p.add(pEmail);
    p.add(pName);
    p.add(pPhone);
    p.add(btnEdit);
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
      scroll.setPreferredSize(new Dimension(900, 300));
      scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
      p.add(scroll);
      p.add(pnlInformation);
    } catch (Exception e) {
      System.out.println(e);
      var errorText = new JLabel(e.toString());
      p.add(errorText);
    }
    return p;
  }

  JPanel panelCustomerRegister() {
    var p = new JPanel();
    p.setOpaque(false);
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    var lblTitle = new JLabel("REGISTRAR CLIENTE");
    lblTitle.setOpaque(false);

    // cc
    var pCc = new JPanel();
    pCc.setLayout(new BoxLayout(pCc, BoxLayout.X_AXIS));
    var lblCc = new JLabel("Cedula: ");
    var fldCc = new JTextField(10);
    pCc.add(lblCc);
    pCc.add(fldCc);
    // email
    var pEmail = new JPanel();
    pEmail.setLayout(new BoxLayout(pEmail, BoxLayout.X_AXIS));
    var lblEmail = new JLabel("Email: ");
    var fldEmail = new JTextField(20);
    pEmail.add(lblEmail);
    pEmail.add(fldEmail);
    // Name
    var pName = new JPanel();
    pName.setLayout(new BoxLayout(pName, BoxLayout.X_AXIS));
    var lblName = new JLabel("Nombre Completo: ");
    var fldName = new JTextField(20);
    pName.add(lblName);
    pName.add(fldName);
    // Phone
    var pPhone = new JPanel();
    pPhone.setLayout(new BoxLayout(pPhone, BoxLayout.X_AXIS));
    var lblPhone = new JLabel("Telefono: ");
    var fldPhone = new JTextField(10);
    pPhone.add(lblPhone);
    pPhone.add(fldPhone);

    // button crate
    var btnRegister = new JButton("REGISTRAR");
    btnRegister.addActionListener(l -> {
      var cc = fldCc.getText();
      var email = fldEmail.getText();
      var name = fldName.getText();
      var phone = fldPhone.getText();
      var customer = new CustomerToCreate();
      customer.cc = cc;
      customer.email = email;
      customer.name = name;
      customer.phone = phone;
      try {
        var result = this.client.createCustomer(customer);
        var msg = "Cliente Creado con Cedula " + customer.cc;
        JOptionPane.showMessageDialog(null, msg, "Cliente Registrado", JOptionPane.INFORMATION_MESSAGE);
        setView(panelCustomerRegister());
      } catch (Exception e) {
        System.out.println(e);
        JOptionPane.showMessageDialog(p, "Error Registrando el cliente", "Error Registrando Cliente",
            JOptionPane.ERROR_MESSAGE);
      }
    });

    p.add(lblTitle);
    p.add(pCc);
    p.add(pEmail);
    p.add(pName);
    p.add(pPhone);
    p.add(btnRegister);
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
    var p = new JPanel();

    p.setLayout(new BorderLayout());
    p.setBorder(new EmptyBorder(15, 15, 15, 15)); // margen general elegante

    // --- Panel central con datos ---
    var infoPanel = new JPanel();
    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
    infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

    var totalSales = new JLabel("ELEMENTOS TOTALES: " + pagination.total_items);
    var pageNumber = new JLabel("NUMERO DE PAGINA: " + pagination.current_page);
    var visibleSales = new JLabel("ELEMENTOS VISIBLES: " + pagination.items_count);

    // Espaciado más uniforme
    totalSales.setBorder(new EmptyBorder(5, 0, 5, 0));
    pageNumber.setBorder(new EmptyBorder(5, 0, 5, 0));
    visibleSales.setBorder(new EmptyBorder(5, 0, 5, 0));

    infoPanel.add(totalSales);
    infoPanel.add(pageNumber);
    infoPanel.add(visibleSales);

    // --- Panel inferior para botones ---
    var navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

    var btnPrevious = new JButton("⟵ ANTERIOR");
    var btnReload = new JButton("Recargar");
    var btnNext = new JButton("SIGUIENTE ⟶");

    // Tamaño igual para ambos botones
    Dimension btnSize = new Dimension(150, 35);
    btnPrevious.setPreferredSize(btnSize);
    btnNext.setPreferredSize(btnSize);
    btnReload.setPreferredSize(btnSize);
    btnReload.addActionListener(l -> {
      setView(getPanel.apply(page, per_page));
    });

    navigationPanel.add(btnPrevious);
    navigationPanel.add(btnReload);
    navigationPanel.add(btnNext);

    // --- Agregar todo al panel principal ---
    p.add(infoPanel, BorderLayout.CENTER);
    p.add(navigationPanel, BorderLayout.SOUTH);
    if (page > 1) {
      btnPrevious.addActionListener(l -> {
        setView(getPanel.apply(page - 1, per_page));
      });
    }
    if (pagination.total_items > (pagination.items_count * pagination.current_page)) {
      btnNext.addActionListener(l -> {
        setView(getPanel.apply(page + 1, per_page));
      });
    }
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
      scroll.setPreferredSize(new Dimension(900, 300));
      scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
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
