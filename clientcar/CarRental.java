package clientcar;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class CarRental implements ActionListener {
    JFrame f;
    JPanel nav, centerPanel, resultPanel;
    JLabel title, databaseRespTitle, l1, l2, l3, l4;
    JButton  b1, b2, b3, b4, b5, b6;
    JTextField t1, t2, t3, t4;
    Choice c1;
    JTextArea databaseResp;
    String totalDatabaseResp;
    Boolean database = false;
    int iter = 3;

    JTable jt;
    JScrollPane sp;

    DefaultTableModel model;
    static PreparedStatement pstmt;
    static Statement stmt;
    static Connection con;
    static ResultSet rs;
    
    boolean result;
    String colData;
    
    String[] params, txt;

    CarRental(){

        jt = new JTable();
        sp = new JScrollPane(jt);
        txt = new String[3];
        totalDatabaseResp = new String();

        //Creating Frame
        f = new JFrame("Car Rental System");
        f.setSize(800,700);
        f.setVisible(true);
        f.setLayout(new BorderLayout());
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Panel used as Nav
        nav = new JPanel(new FlowLayout(FlowLayout.CENTER));
        nav.setBackground(Color.BLACK);

        //Panel used as center buttons
        centerPanel = new JPanel(null);
        centerPanel.setBackground(Color.red);
        centerPanel.setPreferredSize(new Dimension(800,500));

        //Result Panel used for database response
        resultPanel = new JPanel(new BorderLayout());
        resultPanel.setPreferredSize(new Dimension(800,300));
        resultPanel.setVisible(false);

        //JLabel for title
        //  title = new JLabel("C A R        R E N T A L        S Y S T E M");
        title = new JLabel("CAR    RENTAL    SYSTEM");
        title.setFont(new Font("Courier",Font.PLAIN,40));
        title.setForeground(Color.WHITE);

        //adding title to nav, now no more in nav
        nav.add(title);

        //Buttons will be used evey where
        b1 = new JButton(); 
        b2 = new JButton();
        b3 = new JButton();
        b4 = new JButton();
        b5 = new JButton();
        b6 = new JButton();

        //setting buttons as add,get-info,rent and return
        b1.setText("Add Vehicle");
        b2.setText("Get Availability");
        b3.setText("Rent Vehicle");
        b4.setText("Return Vehicle");
        b5.setText("Search Vehicle");
        b6.setText("All Vehicle Info");

        //setting button position
        b1.setBounds(320,130,150,50);
        b2.setBounds(320,190,150,50);
        b3.setBounds(320,250,150,50);
        b4.setBounds(320,310,150,50);
        b5.setBounds(320,370,150,50);
        b6.setBounds(320,430,150,50);

        //initializing text-fields
        t1 = new JTextField();
        t2 = new JTextField();
        t3 = new JTextField();
        t4 = new JTextField();

        //initializing labels
        l1 = new JLabel();
        l2 = new JLabel();
        l3 = new JLabel();
        l4 = new JLabel();
        databaseRespTitle = new JLabel("Database Response: ");
        databaseRespTitle.setFont(new Font("",Font.PLAIN,20));
        databaseRespTitle.setForeground(Color.BLUE);

        //initializing radio buttons
        c1 = new Choice();

        //initializing Text area
        databaseResp = new JTextArea();
        databaseResp.setBackground(new Color(243, 246, 255));
        databaseResp.setPreferredSize(new Dimension(f.getWidth(),200));
        databaseResp.setFont(new Font("",Font.PLAIN,18));
        databaseResp.setEditable(false);
        databaseResp.setText("");


        //adding labels and text-fields
        //labels
        centerPanel.add(l1);
        centerPanel.add(l2);
        centerPanel.add(l3);
        centerPanel.add(l4);
        //text-fields
        centerPanel.add(t1);
        centerPanel.add(t2);
        centerPanel.add(t3);
        centerPanel.add(t4);
        //buttons
        centerPanel.add(b1);
        centerPanel.add(b2);
        centerPanel.add(b3);
        centerPanel.add(b4);
        centerPanel.add(b5);
        centerPanel.add(b6);
        //radio buttons
        centerPanel.add(c1);
        c1.setVisible(false);

        //scrollpane
        centerPanel.add(sp);

        //adding action listener
        b1.addActionListener(this);
        b2.addActionListener(this);
        b3.addActionListener(this);
        b4.addActionListener(this);
        b5.addActionListener(this);
        b6.addActionListener(this);

        //adding text area to resultPanel
        resultPanel.add(databaseRespTitle,BorderLayout.NORTH);
        resultPanel.add(databaseResp,BorderLayout.CENTER);

        //adding components(Panels) in frame
        f.add(nav,BorderLayout.NORTH);
        f.add(centerPanel,BorderLayout.CENTER);
        f.add(resultPanel,BorderLayout.SOUTH);
    }
    public static void main(String[] args) throws ClassNotFoundException, SQLException
    {
        con=DriverManager.getConnection("jdbc:ucanaccess://path");

        CarRental obj = new CarRental();
        obj.HomePage();
    }

    public int getData(String url, DefaultTableModel model, String[] colData, int mode)
    {
        try {
            jt.setModel(model);
            
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery(url);      
            rs.beforeFirst();

            if (mode == 1 && rs.next())
            {
                System.out.println("TEST Pass");
                return Integer.valueOf(rs.getString(1));
            }

            rs.beforeFirst();
            for (int i = 0; rs.next(); i++) {
                // System.out.println(i);
                Vector<String> row = new Vector<String>(); //row
                
                for (int colIndex = 1; colIndex <= colData.length; colIndex++) {
                    row.add(rs.getString(colIndex));

                    if (i == 0)
                        model.addColumn(colData[colIndex-1]);
                }

                model.addRow(row);  
            }
            
            return 0;
            } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("TEST Fail");
            
            totalDatabaseResp = totalDatabaseResp.concat("Exception Occured: ").concat(e.toString());
            return -1;
        }
    }

    public boolean putData(String url, String[] params) {
        try {
            
            pstmt = con.prepareStatement(url);
            for (int paramIndex = 1; paramIndex <= params.length; paramIndex++)
            {
                pstmt.setString(paramIndex, params[paramIndex - 1]);
                // System.out.println(paramIndex);
            }

            int t = pstmt.executeUpdate();
            totalDatabaseResp = String.valueOf(t);

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            totalDatabaseResp = totalDatabaseResp.concat("Exception Occured: ").concat(e.toString());
            return false;
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if (iter < 1) {
            txt[0] = t1.getText();
            txt[1] = t2.getText();
            txt[2] = t3.getText();
        }

        // System.out.println(txt[0]);
        // System.out.println(txt[1]);
        // System.out.println(txt[2]);

        //all visibility false
        //labels
        l1.setVisible(false);
        l2.setVisible(false);
        l3.setVisible(false);
        l4.setVisible(false);
        //buttons
        b1.setVisible(false);
        b2.setVisible(false);
        b3.setVisible(false);
        b4.setVisible(false);
        b5.setVisible(false);
        b6.setVisible(false);
        //text-fields
        t1.setVisible(false);
        t2.setVisible(false);
        t3.setVisible(false);
        t4.setVisible(false);
        //radio buttons
        c1.setVisible(false);
        //text area
        databaseResp.setVisible(false);
        //resultPanel
        resultPanel.setVisible(false);
        //scrollpane
        sp.setVisible(false);

        if (!database){
            clearContent();
        }
        String value = e.getActionCommand();//this will get button text in value

        switch (value){
            case "Add Vehicle":
                addVehiclePage();
                database = true;
                break;
            case "Get Availability":
                getAvailability();
                break;
            case "Rent Vehicle":
                addBuyerPage();
                iter = 0;
                break;
            case "Return Vehicle":
                addReturnPage();
                database = true;
                break;
            case "Search Vehicle":
                searchVehicle();
                database = true;
                break;
            case "All Vehicle Info":
                allVehicleInfo();
                break;
            case "Next":
                selectVehicle();
                database = true;
                iter++;
                break;
            case "Search":
                searchResult();
                break;
            case "Rent":
                selectVehicle();
                rentDatabaseResp();
                iter++;
                break;
            case "Add":
                addVehiclePage();
                addVehicleInData();
                break;
            case "Return":
                returnVehicle();
                break;
            //no "Back" case as default is home
            default:
                HomePage();
                database = false;
                databaseResp.setText("");
                break;
            // add other as per req
        }
    }

    private void clearContent() {
        t1.setText("");
        t2.setText("");
        t3.setText("");
        t4.setText("");
    }

    private void addVehicleInData() {
        resultPanel.setVisible(true);
        databaseResp.setVisible(true);

        if (t1.getText().isEmpty() || t2.getText().isEmpty() || t3.getText().isEmpty() || !isInt(t3.getText())) {
            totalDatabaseResp = totalDatabaseResp.concat("\n\nPlease Fill all Input Values Correctly...");
            
        } else {
            if ( putData("INSERT INTO cars (vin, make, type, rent, duration, namee, mob, license) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", new String[]{t1.getText(), t2.getText(), c1.getSelectedItem(), t3.getText(), "0", "-", "-", "-"}) ) {
                totalDatabaseResp = totalDatabaseResp.concat(" Record was Added");
            }
        }

        databaseResp.setText(totalDatabaseResp);
        b1.setBounds(10,10,150,40);
        database = false;
        totalDatabaseResp = new String("");
    }

    private void rentDatabaseResp() {
        resultPanel.setVisible(true);
        databaseResp.setVisible(true);

        if (t1.getText().isEmpty() || t2.getText().isEmpty() || t3.getText().isEmpty() || !isInt(t3.getText()) || !isInt(t1.getText())) {

            totalDatabaseResp = totalDatabaseResp.concat("\n\nPlease Fill all Input Values");
        } else {

            if ( putData("UPDATE cars SET duration = ?, namee = ?, mob = ?, license = ? WHERE vin = ? AND make = ? AND type = ? AND duration = ?", new String[]{t3.getText(), txt[0], txt[1], txt[2], t1.getText(), t2.getText(), c1.getSelectedItem(), "0"}) )
                totalDatabaseResp = totalDatabaseResp.concat(" Record was Updated\n");
                // System.out.println("rec was updated" + totalDatabaseResp);

            int val1 = getData("SELECT rent FROM cars WHERE vin = '"+t1.getText()+"'", new DefaultTableModel(), new String[]{"RENT"}, 1);

            int val2 = Integer.valueOf( t3.getText() );
            
            String strVal = String.valueOf(val1 * val2);
            System.out.println(strVal);

            totalDatabaseResp = totalDatabaseResp.concat("\nTotal Rent will be Rs. ").concat(strVal).concat("/-");
        }

        databaseResp.setText(totalDatabaseResp);
        b1.setBounds(10,10,150,40);
        database = false;
        totalDatabaseResp = new String("");
    }

    private void searchResult() {
        
        
        if(t1.getText().isEmpty() || !isInt(t1.getText()) || getData("SELECT vin FROM cars WHERE vin = '"+t1.getText()+"'", new DefaultTableModel(), new String[]{"VIN"}, 1) != Integer.valueOf(t1.getText()) )
        {
            resultPanel.setVisible(true);
            databaseResp.setVisible(true);
            databaseResp.setText("Invalid Value Entered: \" "+t1.getText()+"\" ");
            b1.setBounds(10,10,150,40);

        } else
        {
            getData("SELECT vin, make, type, rent, duration, namee FROM cars WHERE vin = '"+t1.getText()+"'", new DefaultTableModel(), new String[]{"VIN", "Make", "Type", "Rent(Per Day)", "Available After(days)", "Name of Renter"}, 0);

            centerPanel.add(sp);           
            sp.setVisible(true);
            sp.setBounds(20,50,745,300);
            b1.setBounds(300,500,150,40);
        }
        
        b1.setText("Back");
        b1.setVisible(true);
        database = false;
    }

    private void selectVehicle() {
        //turning on visibility of requires buttons,labels and text-fields
        //labels
        l1.setVisible(true);
        l2.setVisible(true);
        l3.setVisible(true);
        l4.setVisible(true);
        //fields
        t1.setVisible(true);
        t2.setVisible(true);
        t3.setVisible(true);
        //buttons
        b1.setVisible(true);
        b2.setVisible(true);
        //radio buttons
        c1.setVisible(true);

        f.repaint();
        //setting labels
        l1.setText("Vehicle ID :");
        l2.setText("Vehicle Name :");
        l3.setText("Vehicle Type :");
        l4.setText("Duration of Rent (Days) :");

        //Adding button to verify as well as to next step and setting position
        b1.setText("Back");
        b2.setText("Rent");

        c1.removeAll(); //remove all previously added items on choice
        //setting radio buttons
        c1.add("2 Seater");
        c1.add("4 Seater");
        c1.add("7 Seater");

        //setting position of labels, text-fields and buttons
        //labels
        l1.setBounds(250,130,200,50);
        l2.setBounds(250,180,200,50);
        l3.setBounds(250,230,200,50);
        l4.setBounds(250,290,200,50);
        //text-field
        t1.setBounds(450,140,200,25);
        t2.setBounds(450,190,200,25);
        t3.setBounds(450,300,200,25);
        //radio buttons
        c1.setBounds(450,240,200,30);
        //buttons
        b1.setBounds(270,370,130,40);
        b2.setBounds(430,370,130,40);

    }

    private void searchVehicle() {
        //turning on visibility of requires buttons,labels and text-fields
        //labels
        l1.setVisible(true);
        //fields
        t1.setVisible(true);
        //buttons
        b1.setVisible(true);
        b2.setVisible(true);

        //setting label
        l1.setText("Enter Vehicle ID : ");
        //setting Button
        b1.setText("Back");
        b2.setText("Search");

        //setting position
        l1.setBounds(250,130,200,50);
        t1.setBounds(420,140,200,25);
        b1.setBounds(270,210,130,40);
        b2.setBounds(430,210,130,40);
    }
    
    private void allVehicleInfo() {
           
        if ( getData("SELECT vin, make, type, rent, duration, namee FROM cars", new DefaultTableModel(), new String[]{"VIN", "Make", "Type", "Rent(Per Day)", "Available After(days)", "Name of Renter"}, 0) == 0)
        {   
            centerPanel.add(sp);           
            sp.setVisible(true);
            sp.setBounds(20,10,745,400);
        }

        
        b1.setVisible(true);
        b1.setBounds(300,500,150,40);
        b1.setText("Back");
    }

    private void addReturnPage() {
        
        //turning on visibility of requires buttons,labels and text-fields
        //labels
        l1.setVisible(true);
        l2.setVisible(true);
        l3.setVisible(true);
        l4.setVisible(true);
        //fields
        t1.setVisible(true);
        t2.setVisible(true);
        t3.setVisible(true);
        t4.setVisible(true);
        //buttons
        b1.setVisible(true);
        b2.setVisible(true);

        f.repaint();
        //setting labels
        l1.setText("Enter Renter's Name : ");
        l2.setText("Enter Mob. No. :");
        l3.setText("Enter License :");
        l4.setText("Enter VIN :");

        //Adding button to verify as well as to next step and setting position
        b1.setText("Back");
        b2.setText("Return");

        //setting position of labels, text-fields and buttons
        //labels
        l1.setBounds(250,130,200,50);
        l2.setBounds(250,180,200,50);
        l3.setBounds(250,230,200,50);
        l4.setBounds(250,280,200,50);

        //text-field
        t1.setBounds(450,140,200,25);
        t2.setBounds(450,190,200,25);
        t3.setBounds(450,240,200,25);
        t4.setBounds(450,290,200,25);
        
        //buttons
        b1.setBounds(270,370,130,40);
        b2.setBounds(430,370,130,40);
    
    }

    private void returnVehicle() {

        b1.setVisible(true);

        if ( t1.getText().isEmpty() || t2.getText().isEmpty() || t3.getText().isEmpty() || t4.getText().isEmpty() || !isInt(t4.getText()) || getData("SELECT duration FROM cars WHERE namee = '"+t1.getText()+"' AND mob = '"+t2.getText()+"' AND license = '"+t3.getText()+"' AND vin = '"+t4.getText()+"'", new DefaultTableModel(), new String[]{"Duration"}, 1) < 0)
        {
            databaseResp.setText("Invalid Values Entered\n\n");
            b1.setBounds(10,10,150,40);

        } else {
            putData("UPDATE cars SET duration = ?, namee = '-', license = '-', mob = '-' WHERE vin = '"+t4.getText()+"'", params = new String[]{"0"});

            totalDatabaseResp = totalDatabaseResp.concat(" Record Updated\n\n");
    
            databaseResp.setText(totalDatabaseResp);
            b1.setBounds(10,10,150,40);

        }
        resultPanel.setVisible(true);
        databaseResp.setVisible(true);
        database = false;
    }

    private void getAvailability() {
        
        getData("SELECT vin, make, type, rent FROM cars WHERE duration = '0'", new DefaultTableModel(), new String[]{"VIN", "Make", "Type", "Rent(Per Day)"}, 0);

        centerPanel.add(sp);
        
        sp.setVisible(true);
        sp.setBounds(20,10,745,400);
        
        b1.setVisible(true);
        b1.setBounds(300,500,150,40);
        b1.setText("Back");
    }

    private void HomePage() {
        b1.setVisible(true);
        b2.setVisible(true);
        b3.setVisible(true);
        b4.setVisible(true);
        b5.setVisible(true);
        b6.setVisible(true);

        //setting buttons as add,get-info,rent and return
        b1.setText("Add Vehicle");
        b2.setText("Get Availability");
        b3.setText("Rent Vehicle");
        b4.setText("Return Vehicle");
        b5.setText("Search Vehicle");
        b6.setText("All Vehicle Info");

        //setting button position
        b1.setBounds(320,130,150,50);
        b2.setBounds(320,190,150,50);
        b3.setBounds(320,250,150,50);
        b4.setBounds(320,310,150,50);
        b5.setBounds(320,370,150,50);
        b6.setBounds(320,430,150,50);
    }

    private void addVehiclePage() {
        //turning on visibility of requires buttons,labels and text-fields
        //labels
        l1.setVisible(true);
        l2.setVisible(true);
        l3.setVisible(true);
        l4.setVisible(true);
        //fields
        t1.setVisible(true);
        t2.setVisible(true);
        t3.setVisible(true);
        //buttons
        b1.setVisible(true);
        b2.setVisible(true);
        //radio buttons
        c1.setVisible(true);

        f.repaint();
        //setting labels
        l1.setText("Vehicle ID :");
        l2.setText("Vehicle Name :");
        l3.setText("Vehicle Type :");
        l4.setText("Rent (Per Day) :");

        //Adding button to verify as well as to next step and setting position
        b1.setText("Back");
        b2.setText("Add");

        c1.removeAll(); //remove all previously added items on choice
        //setting choice
        c1.add("2 Seater");
        c1.add("4 Seater");
        c1.add("7 Seater");

        //setting position of labels, text-fields and buttons
        //labels
        l1.setBounds(250,130,200,50);
        l2.setBounds(250,180,200,50);
        l3.setBounds(250,230,200,50);
        l4.setBounds(250,290,200,50);
        //text-field
        t1.setBounds(450,140,200,25);
        t2.setBounds(450,190,200,25);
        t3.setBounds(450,300,200,25);
        //radio buttons
        c1.setBounds(450,240,200,30);
        //buttons
        b1.setBounds(270,370,130,40);
        b2.setBounds(430,370,130,40);

    }

    private void addBuyerPage() {
        //turning on visibility of requires buttons,labels and text-fields
        //labels
        l1.setVisible(true);
        l2.setVisible(true);
        l3.setVisible(true);
        //fields
        t1.setVisible(true);
        t2.setVisible(true);
        t3.setVisible(true);
        //buttons
        b1.setVisible(true);
        b2.setVisible(true);

        f.repaint();
        //setting labels
        l1.setText("Enter Name : ");
        l2.setText("Enter Mob. No. :");
        l3.setText("Enter License :");

        //Adding button to verify as well as to next step and setting position
        b1.setText("Back");
        b2.setText("Next");

        //setting position of labels, text-fields and buttons
        //labels
        l1.setBounds(250,130,200,50);
        l2.setBounds(250,180,200,50);
        l3.setBounds(250,230,200,50);
        //text-field
        t1.setBounds(450,140,200,25);
        t2.setBounds(450,190,200,25);
        t3.setBounds(450,240,200,25);
        
        //buttons
        b1.setBounds(270,320,130,40);
        b2.setBounds(430,320,130,40);

    }

    private boolean isInt(String val) {
        try {
            Integer.valueOf(val);
            
            result = true;
            return true;
        } catch (Exception e) {
            totalDatabaseResp = totalDatabaseResp.concat("Exception Occured: ").concat(e.toString());
            result = false;
            return false;
        }
    }
}
