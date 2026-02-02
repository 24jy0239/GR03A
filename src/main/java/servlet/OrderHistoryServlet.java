package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.DishDAO;
import manager.OrderManager;
import model.CartItem;
import model.Dish;
import model.Order;
import model.OrderItem;
import model.Visit;

@WebServlet("/order/history")
public class OrderHistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    	DishDAO dishDAO = new DishDAO();

        HttpSession session = request.getSession();
        
        

        String visitId = (String) session.getAttribute("visitId");
        if (visitId == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        OrderManager manager = OrderManager.getInstance();
        Visit visit = manager.getVisit(visitId);

        //デバッグ用
        System.out.println("DEBUG visitId = " + visitId);

        if (visit == null) {
            System.out.println("DEBUG visit is null");
        } else {
            System.out.println("DEBUG orders = " + visit.getOrders());
            if (visit.getOrders() != null) {
                System.out.println("DEBUG orders size = " + visit.getOrders().size());
            }
        }

        
        Map<String, CartItem> summaryMap = new HashMap<>();
        int visitTotal = 0;

        if (visit != null && visit.getOrders() != null) {
            for (Order order : visit.getOrders()) {
               for (OrderItem item : order.getOrderItems()) { 

                    String dishId = item.getDishId();

                    CartItem cartItem = summaryMap.get(dishId);
                    if (cartItem == null) {

                        String photo = null;
                        try {
                            Dish dish = dishDAO.findById(dishId);
                            if (dish != null) {
                                photo = dish.getPhoto();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        cartItem = new CartItem(
                                dishId,
                                item.getDishName(),
                                item.getPrice(),
                                0,
                                photo
                        );

                        summaryMap.put(dishId, cartItem);
                    }


                    // 数量
                    cartItem.setQuantity(
                        cartItem.getQuantity() + item.getQuantity()
                    );

                    // 合計
                    visitTotal += item.getSubtotal();
                }
            }
        }

        List<CartItem> summaryList =
                new ArrayList<>(summaryMap.values());

        request.setAttribute("summaryList", summaryList);
        request.setAttribute("visitTotal", visitTotal);

        request.getRequestDispatcher("/WEB-INF/order-history.jsp")
               .forward(request, response);
    }
}
