package brm;

import java.awt.event.*;
import java.awt.*;
import java.awt.GridBagLayout;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class BookFrame {
    Connection con = null;
    String Insert = "Insert";
    String View = "View";
    PreparedStatement ps;
    JFrame frame = new JFrame("BRM Project");
    JTabbedPane tabbedPane = new JTabbedPane();
    JPanel insertPanel, viewPanel;
    JLabel l1, l2, l3, l4, l5;
    JTextField t1, t2, t3, t4, t5;
    JButton saveButton, upDateButton, deleteButton;
    JTable table;
    JScrollPane scrollPane;
    DefaultTableModel tm;
    String[] columnNames = { "Book ID", "Title", "Price", "Author", "Publisher" };

    public BookFrame() {
        getConnectionFromMySQL();
        initComponents();
    }

    void getConnectionFromMySQL() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            String url = "jdbc:mariadb://localhost:3306/db1";
            con = DriverManager.getConnection(url, "root", "vbnm");
            System.out.println("conection is good");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    void initComponents() {

        l1 = new JLabel("Book ID:");
        l2 = new JLabel("Title:");
        l3 = new JLabel("Price:");
        l4 = new JLabel("Author:");
        l5 = new JLabel("Publisher:");

        t1 = new JTextField();
        t2 = new JTextField();
        t3 = new JTextField();
        t4 = new JTextField();
        t5 = new JTextField();

        saveButton = new JButton("Save");

        l1.setBounds(150, 100, 100, 30);
        l2.setBounds(150, 150, 100, 30);
        l3.setBounds(150, 200, 100, 30);
        l4.setBounds(150, 250, 100, 30);
        l5.setBounds(150, 300, 100, 30);

        t1.setBounds(250, 100, 150, 30);
        t2.setBounds(250, 150, 150, 30);
        t3.setBounds(250, 200, 150, 30);
        t4.setBounds(250, 250, 150, 30);
        t5.setBounds(250, 300, 150, 30);

        saveButton.setBounds(250, 350, 100, 30);

        saveButton.addActionListener(new InsertBookRecord());
        insertPanel = new JPanel();
        insertPanel.setLayout(null);
        insertPanel.add(l1);
        insertPanel.add(l2);
        insertPanel.add(l3);
        insertPanel.add(l4);
        insertPanel.add(l5);
        insertPanel.add(t1);
        insertPanel.add(t2);
        insertPanel.add(t3);
        insertPanel.add(t4);
        insertPanel.add(t5);
        insertPanel.add(saveButton);
        ArrayList<Book> booklist = fetchBookRecords();
        setDataOnTable(booklist);
        upDateButton = new JButton("Update Book");
        upDateButton.addActionListener(new UpdateBookRecord());
        deleteButton = new JButton("Delete Book");
        deleteButton.addActionListener(new DeleteBookRecords());
        viewPanel = new JPanel();
        //viewPanel.
        viewPanel.add(upDateButton);
        viewPanel.add(deleteButton);
        scrollPane = new JScrollPane(table);
        viewPanel.add(scrollPane);

        tabbedPane.add(Insert,insertPanel);
        tabbedPane.add(View,viewPanel);
        frame.setLayout(new BorderLayout());
        frame.add(tabbedPane, BorderLayout.CENTER);
        tabbedPane.addChangeListener(new TabChangeHandler());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setVisible(true);

    }

    void setDataOnTable(ArrayList<Book> booklist) {
        Object[][] obj = new Object[booklist.size()][5];
        for (int i = 0; i < booklist.size(); i++) {
            obj[i][0] = booklist.get(i).getBookid();
            obj[i][1] = booklist.get(i).getTitle();
            obj[i][2] = booklist.get(i).getPrice();
            obj[i][3] = booklist.get(i).getAuthor();
            obj[i][4] = booklist.get(i).getPublisher();
        }
        table = new JTable();
        tm = new DefaultTableModel();
        tm.setColumnCount(5);
        tm.setRowCount(booklist.size());
        tm.setColumnIdentifiers(columnNames);
        for (int i = 0; i < booklist.size(); i++) {
            tm.setValueAt(obj[i][0], i, 0);
            tm.setValueAt(obj[i][1], i, 1);
            tm.setValueAt(obj[i][2], i, 2);
            tm.setValueAt(obj[i][3], i, 3);
            tm.setValueAt(obj[i][4], i, 4);
        }

        table.setModel(tm);
    }

    void updateTable(ArrayList<Book> booklist) {
        Object[][] obj = new Object[booklist.size()][5];
        for (int i = 0; i < booklist.size(); i++) {
            obj[i][0] = booklist.get(i).getBookid();
            obj[i][1] = booklist.get(i).getTitle();
            obj[i][2] = booklist.get(i).getPrice();
            obj[i][3] = booklist.get(i).getAuthor();
            obj[i][4] = booklist.get(i).getPublisher();
        }
        tm.setColumnCount(5);
        tm.setRowCount(booklist.size());
        for (int i = 0; i < booklist.size(); i++) {
            tm.setValueAt(obj[i][0], i, 0);
            tm.setValueAt(obj[i][1], i, 1);
            tm.setValueAt(obj[i][2], i, 2);
            tm.setValueAt(obj[i][3], i, 3);
            tm.setValueAt(obj[i][4], i, 4);
        }

        table.setModel(tm);
    }

    ArrayList<Book> fetchBookRecords() {
        ArrayList<Book> bookList = new ArrayList<>();
        String Query = "SELECT * FROM Book";
        try {
            ps = con.prepareStatement(Query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Book b = new Book();
                b.setBookid(rs.getInt(1));
                b.setTitle(rs.getString(2));
                b.setPrice(rs.getDouble(3));
                b.setAuthor(rs.getString(4));
                b.setPublisher(rs.getString(5));
                bookList.add(b);
            }
        } catch (SQLException exp) {
            System.out.println(exp.getMessage());
        } finally {
            return bookList;
        }
    }

    class InsertBookRecord implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Book B1 = readFromData();
            String Query = "INSERT INTO Book (bookid,title,price,author,publisher) values(?,?,?,?,?)";
            try {
                ps = con.prepareStatement(Query);
                ps.setInt(1, B1.getBookid());
                ps.setString(2, B1.getTitle());
                ps.setDouble(3, B1.getPrice());
                ps.setString(4, B1.getAuthor());
                ps.setString(5, B1.getPublisher());
                ps.execute();
                t1.setText(" ");
                t2.setText(" ");
                t3.setText(" ");
                t4.setText(" ");
                t5.setText(" ");
            } catch (SQLException exp) {
                System.out.println(exp.getMessage());
            }
        }

        Book readFromData() {
            Book b1 = new Book();
            b1.setBookid(Integer.parseInt(t1.getText().trim()));
            b1.setTitle(t2.getText().trim());
            b1.setPrice(Double.parseDouble(t3.getText().trim()));
            b1.setAuthor(t4.getText().trim());
            b1.setPublisher(t5.getText().trim());
            return b1;
        }

    }

    class TabChangeHandler implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent changeEvent) {
            int index = tabbedPane.getSelectedIndex();
            if (index == 0) {
                System.out.println("insert");
            }
            if (index == 1) {
                ArrayList<Book> booklist = fetchBookRecords();
                updateTable(booklist);

            }
        }
    }

    class UpdateBookRecord implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            ArrayList<Book> updatedBookList = readTableData();
            String Query = "update Book set title=?,price=?,author=?,publisher=? where bookid=?";
            try {
                ps = con.prepareStatement(Query);
                for (int i = 0; i < updatedBookList.size(); i++) {
                    ps.setString(1, updatedBookList.get(i).getTitle());
                    ps.setDouble(2, updatedBookList.get(i).getPrice());
                    ps.setString(3, updatedBookList.get(i).getAuthor());
                    ps.setString(4, updatedBookList.get(i).getPublisher());
                    ps.setInt(5, updatedBookList.get(i).getBookid());
                    ps.executeUpdate();
                }
            } catch (SQLException exp) {
                System.out.println(exp.getMessage());
            }
        }

        ArrayList<Book> readTableData() {
            ArrayList<Book> updatedbooklist = new ArrayList<>();
            for (int i = 0; i < table.getRowCount(); i++) {
                Book b = new Book();
                b.setBookid(Integer.parseInt(table.getValueAt(i, 0).toString()));
                b.setTitle(table.getValueAt(i, 1).toString());
                b.setPrice(Double.parseDouble(table.getValueAt(i, 2).toString()));
                b.setAuthor(table.getValueAt(i, 3).toString());
                b.setPublisher(table.getValueAt(i, 4).toString());
                updatedbooklist.add(b);
            }
            return updatedbooklist;
        }
    }

    class DeleteBookRecords implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            int rowNo = table.getSelectedRow();
            if (rowNo != -1) {
                int id = (int) table.getValueAt(rowNo, 0);
                String Query = "DELETE FROM Book WHERE bookid=?";
                try {
                    ps = con.prepareStatement(Query);
                    ps.setInt(1, id);
                    ps.execute();
                } catch (SQLException exp) {
                    System.out.println(exp.getMessage());
                } finally {
                    ArrayList<Book> booklist = fetchBookRecords();
                    updateTable(booklist);
                }
            }
        }
    }
}
