package Exchange.Matching.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Matching {
    // 1. Mapping ResultSet
    ArrayList<Order> order_list = new ArrayList<Order>();

    // Store the Execute info of buy order
    ArrayList<ExecuteOrder> execute_list = new ArrayList<ExecuteOrder>();

    public Matching(ResultSet res, Order temp) throws SQLException {
        mapOrder(res);
        matchOrder(temp);
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
    public void matchOrder(Order temp) {
        double buy_amount = temp.getAmount();
        for (Order or : order_list) {
            double sell_amount = or.getAmount();
            double price = 0;
            if (buy_amount > 0) {
                if (buy_amount == sell_amount) {
                    // compare time
                    if (temp.getTime() < or.getTime()) {
                        price = temp.getLimit();
                    } else {
                        price = or.getLimit();
                    }
                    ExecuteOrder eorder = new ExecuteOrder(temp.getAccountID(), or.getAccountID(), temp.getOrderID(), or.getOrderID(), or.getSymbol(),
                            buy_amount, price);
                    execute_list.add(eorder);
                    buy_amount = 0;
                    sell_amount = 0;
                    or.setStatus("executed");
                    temp.setStatus("executed");
                    // whether need to delete the order that finish(amount = 00? Now don't delete.
                    break;
                } else if (buy_amount > sell_amount) {
                    // compare time
                    if (temp.getTime() < or.getTime()) {
                        price = temp.getLimit();
                    } else {
                        price = or.getLimit();
                    }
                    ExecuteOrder eorder = new ExecuteOrder(temp.getAccountID(), or.getAccountID(), temp.getOrderID(), or.getOrderID(), or.getSymbol(),
                            sell_amount, price);
                    execute_list.add(eorder);
                    sell_amount = 0;
                    buy_amount -= sell_amount;
                    temp.setAmount(buy_amount);
                    or.setStatus("executed");
                } else {
                    // seperate sell order

                    // compare time
                    if (temp.getTime() < or.getTime()) {
                        price = temp.getLimit();
                    } else {
                        price = or.getLimit();
                    }
                    sell_amount -= buy_amount;
                    ExecuteOrder eorder = new ExecuteOrder(temp.getAccountID(), or.getAccountID(), temp.getOrderID(), or.getOrderID(), or.getSymbol(),
                            buy_amount, price);
                    execute_list.add(eorder);
                    buy_amount = 0;
                    sell_amount -= buy_amount;
                    temp.setStatus("executed");
                    Order separate_sell_order = new Order(or.getAccountID(), or.getSymbol(), sell_amount, or.getLimit(),
                            or.getType(), or.getTime());
                }
            }

        }

        // Remaining buy order
        if (buy_amount > 0) {
            // update buy_amount
        }
    }
}
