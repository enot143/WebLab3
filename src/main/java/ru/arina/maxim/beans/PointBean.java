package ru.arina.maxim.beans;

import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import lombok.Setter;
import org.primefaces.context.RequestContext;
import ru.arina.maxim.model.Point;
import ru.arina.maxim.services.AreaChecker;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
//import javax.enterprise.context.ApplicationScoped;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
//import javax.inject.Named;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@ManagedBean(name = "pointBean")
@ApplicationScoped
@Setter
public class PointBean implements Serializable {
    private Deque<Point> allPoints;
    private Deque<Point> pointsFromDB;
    private int size;
    private DataSource ds;

    private int xFromForm;
    private double yFromForm;
    private boolean[] rFromForm = new boolean[5];
    private int xFromHiddenForm;
    private Double yFromHiddenForm;
    private int rFromHiddenForm;
    private int x;
    private Double y;
    private int r;

    DecimalFormat df = new DecimalFormat("#.###");





//    public PointBean() {
//
//         init();
//    }
//
//    public void init() {
//        allPoints = dbManager.getAllPoints();
//    }


    public void handleClearTable() throws SQLException {

        System.out.println("************************************");
        System.out.println("x = " + xFromForm);
        System.out.println("y = " + yFromForm);
        System.out.println("r = " + rFromHiddenForm);
        clearTable();

//        RequestContext.getCurrentInstance()
//                .execute("alert('invoked from post construct');");
    }

    public String handleSubmitHiddenForm() throws SQLException {

        x = xFromHiddenForm;
        y = Double.parseDouble(df.format(yFromHiddenForm).replace(",", "."));
        r = rFromHiddenForm;



        System.out.println("*****************SUBmit*******************");

        System.out.println("Handles HIDDEN form");

        System.out.println("x = " + x);
        System.out.println("y = " + y);
        System.out.println("r = " + r);
        addPoint(x, y, r);
        return "main";

    }

    public String handleSubmitForm() throws SQLException {

        Integer tmpR = null;

        for (int i = 0; i < rFromForm.length; i ++ ) {
            if ( rFromForm[i] ) {
                tmpR = i + 1;
                break;
            }
        }

        if ( tmpR == null )
            throw new IllegalArgumentException("R not set. Validator has missed incorrect R.");

        r = tmpR;
        x = xFromForm;
        y = Double.parseDouble(df.format(yFromForm).replace(",", "."));

        addPoint(x, y, r);
        // todo Server add point method

        System.out.println("*****************SUBmit*******************");
        System.out.println("Handles USUAL form");

        System.out.println("x = " + x);
        System.out.println("y = " + y);
        System.out.println("r = " + r);
        return "main";
    }


    public void handleClearForm() {
        System.out.println("Clears form");
        xFromForm = 0;
        yFromForm = 0;
        rFromForm = new boolean[]{false, false, false, false, false};
    }






    public void clearTable() throws SQLException {

        Connection conn = ds.getConnection();
        if (conn == null) throw new SQLException("No connection");

            try {
                conn.setAutoCommit(false);
                boolean committed = false;
                try {
                    PreparedStatement newpoint = conn.prepareStatement("TRUNCATE TABLE POINTS");
                    newpoint.executeUpdate();
                    conn.commit();
                    committed = true;
                } finally {
                    if (!committed) conn.rollback();
                }
            } finally {
                conn.close();
            }

        allPoints.clear();


        System.out.println("+-+-+-+TABLE cleared !");
    }






    public void addPoint(int x, Double y, int r) throws SQLException {
        Point currentPoint = new Point(x, y, r, AreaChecker.isInArea(x, y, r)?"YES":"NO");
        System.out.println("addingPoint");
        addPoint(currentPoint);
    }


    public int getxFromForm() {
        return xFromForm;
    }

    public void setxFromForm(int xFromForm) {
        this.xFromForm = xFromForm;
    }

    public Double getyFromForm() {
        return yFromForm;
    }

    public void setyFromForm(Double yFromForm) {
        this.yFromForm = yFromForm;
    }

    public boolean[] getrFromForm() {
        return rFromForm;
    }

    public void setrFromForm(boolean[] rFromForm) {
        this.rFromForm = rFromForm;
    }

    public int getxFromHiddenForm() {
        return xFromHiddenForm;
    }

    public void setxFromHiddenForm(int xFromHiddenForm) {
        this.xFromHiddenForm = xFromHiddenForm;
    }

    public Double getyFromHiddenForm() {
        return yFromHiddenForm;
    }

    public void setyFromHiddenForm(Double yFromHiddenForm) {
        this.yFromHiddenForm = yFromHiddenForm;
    }

    public int getrFromHiddenForm() {
        return rFromHiddenForm;
    }

    public void setrFromHiddenForm(int rFromHiddenForm) {
        this.rFromHiddenForm = rFromHiddenForm;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

//    public List<Point> getAllPoints() {
//        return allPoints;
//    }
//
//    public void setAllPoints(List<Point> allPoints) {
//        this.allPoints = allPoints;
//    }




    {
        allPoints = new ArrayDeque<>();
        try {
            Context context = new InitialContext();
            ds = (DataSource) context.lookup("java:jboss/datasources/oracle");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
//давай

    public void addPoint(Point point) throws SQLException {
        System.out.println("ADDINGPOINT______________");
//        getPoints();
        try {
            try {
                Point last = allPoints.getFirst();
                if (!last.equals(point)) {  //add if not reload page
                    allPoints.addFirst(point);
                }
            } catch (NoSuchElementException e) {
                allPoints.addFirst(point);
            }

            if (ds == null) throw new SQLException("No data source");
            Connection conn = ds.getConnection();
            if (conn == null) throw new SQLException("No connection");

            try {
                conn.setAutoCommit(false);
                boolean committed = false;
                try {
                    PreparedStatement newpoint = conn.prepareStatement("INSERT INTO POINTS VALUES (?,?,?,?)");
//                    newpoint.setString(1, point.getUnique());
                    newpoint.setInt(1, point.getX());
                    newpoint.setDouble(2, point.getY());
                    newpoint.setInt(3, point.getR());
                    newpoint.setString(4, point.getResult());
                    newpoint.executeUpdate();
                    conn.commit();
                    committed = true;
                } finally {
                    if (!committed) conn.rollback();
                }
            } finally {
                conn.close();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public String pointJSON() {
        return '[' + allPoints.parallelStream()
                .map(point -> String.format("{\"x\": %s, \"y\": %s}",
                        point.getX(), point.getY(), point.getR(), point.getResult()))
                .collect(Collectors.joining(", ")) + ']';
    }
    public Deque<Point> getAllPoints() throws SQLException {
        System.out.println("ADD PINTS FROM DB!!!!!!!!!!");
        return allPoints;
    }

    public Deque<Point> getPointsFromDB() throws SQLException {
        System.out.println("gettingAllPoints----------");
        pointsFromDB = new ArrayDeque<>();
        Connection conn = ds.getConnection();
        if (conn == null) throw new SQLException("No connection");

        try {
            conn.setAutoCommit(false);
            boolean committed = false;
            try {
                PreparedStatement newpoint = conn.prepareStatement("SELECT * FROM POINTS");
                ResultSet result2 = newpoint.executeQuery();
                while (result2.next()){
                    pointsFromDB.addFirst(new Point(
                            result2.getInt(1),
                            result2.getDouble(2),
                            result2.getInt(3),
                            result2.getString(4)
                    ));
                }
                conn.commit();
                committed = true;
            } finally {
                if (!committed) conn.rollback();
            }
        } finally {
            conn.close();
        }
        return pointsFromDB;
    }
    public void setAllPoints(Deque<Point> points) {
        this.allPoints = points;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}