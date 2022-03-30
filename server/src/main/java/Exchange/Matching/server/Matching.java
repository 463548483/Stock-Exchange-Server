package Exchange.Matching.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.plaf.basic.BasicOptionPaneUI.ButtonActionListener;

public class Matching {

    private Order order; 
    private ArrayList<Order> order_list;
    private ArrayList<ExecuteOrder> execute_list;
    private ArrayList<Order> sell_order_list; // executed sell order

    public Matching(Order order, ResultSet res) throws SQLException{
        this.order = order;
        // 1. Mapping ResultSet
        this.order_list = new ArrayList<Order>();
        // Store the Execute info of buy order
        this.execute_list = new ArrayList<ExecuteOrder>();
        this.sell_order_list = new ArrayList<Order>();
        mapOrder(res);
        matchOrder();
    }

    public ArrayList<ExecuteOrder> getExecuteList(){
        return execute_list;
    }
    public ArrayList<Order> getSellList(){
        return sell_order_list;
    }
    public Order getOrder(){
        return order;
    }

    public void mapOrder(ResultSet res) throws SQLException {
        while (res.next()) {
            int account_id = res.getInt("ACCOUNT_ID");
            String symbol = res.getString("SYMBOL");
            double amount = res.getDouble("AMOUNT");
            double limit = res.getDouble("BOUND");
            String status = res.getString("STATUS");
            String type = res.getString("TYPE");
            Order order = new Order(account_id, symbol, amount, limit, status, type);
            order_list.add(order);
        }
    }

    // 2. Find matching Orders
    public void matchOrder() {
        double buy_amount = order.getAmount();
        for (Order or : order_list) {
            double sell_amount = or.getAmount();
            double price = 0;
            if (buy_amount > 0) {
                if (buy_amount == sell_amount) {
                    // compare time
                    if (order.getTime() < or.getTime()) {
                        price = order.getLimit();
                    } else {
                        price = or.getLimit();
                    }
                    ExecuteOrder eorder = new ExecuteOrder(order.getAccountID(), or.getAccountID(), order.getOrderID(),
                            or.getOrderID(), or.getSymbol(),
                            buy_amount, price);
                    execute_list.add(eorder);
                    buy_amount = 0;
                    sell_amount = 0;
                    or.setAmount(sell_amount);
                    order.setAmount(buy_amount);

                    sell_order_list.add(or);
                    //or.setStatus("executed");
                    //order.setStatus("executed");
                    // whether need to delete the order that finish(amount = 00? Now don't delete.
                    break;
                } else if (buy_amount > sell_amount) {
                    // compare time
                    if (order.getTime() < or.getTime()) {
                        price = order.getLimit();
                    } else {
                        price = or.getLimit();
                    }
                    ExecuteOrder eorder = new ExecuteOrder(order.getAccountID(), or.getAccountID(), order.getOrderID(),
                            or.getOrderID(), or.getSymbol(),
                            sell_amount, price);
                    execute_list.add(eorder);
                    sell_amount = 0;
                    buy_amount -= sell_amount;
                    order.setAmount(buy_amount);
                    or.setAmount(sell_amount);
                    sell_order_list.add(or);
                    //or.setStatus("executed");
                } else {
                    // compare time
                    if (order.getTime() < or.getTime()) {
                        price = order.getLimit();
                    } else {
                        price = or.getLimit();
                    }
                    ExecuteOrder eorder = new ExecuteOrder(order.getAccountID(), or.getAccountID(), order.getOrderID(),
                            or.getOrderID(), or.getSymbol(),
                            buy_amount, price);
                    execute_list.add(eorder);
                    buy_amount = 0;
                    sell_amount -= buy_amount;
                    order.setAmount(buy_amount);
                    or.setAmount(sell_amount);
                    // update remaining Sell Order
                    sell_order_list.add(or);
                    //order.setStatus("executed");
                }
            }
        }
        // Remaining buy order
    }
}
