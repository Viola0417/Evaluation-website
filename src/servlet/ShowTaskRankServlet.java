package servlet;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.Log_Dao;
import dao.Task_Dao;
import entity.F_stu_id;
import entity.Log;
import entity.Task;
import service.calculation;
import service.pie;
import service.pie2;

/**
 * Servlet implementation class ShowTaskRankServlet
 */
@WebServlet("/ShowTaskRankServlet")
public class ShowTaskRankServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ShowTaskRankServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//doGet(request, response);
		System.out.println("即将展示题目排名");
		//先判断该题是否存在
		//1.题目本身不存在
		//2.题目还没有人做答
		//3.返回题目做题记录
		String task_id = null;
		request.setCharacterEncoding("utf-8");
		task_id=request.getParameter("task_id");
		//System.out.println("学生要做答的是："+task_id+"题目");
		Task t = new Task();
		Task_Dao t_dao = new Task_Dao();
		Log_Dao l_dao = new Log_Dao();
		int TaskNo = 0;
		int res = 0;
		TaskNo = t_dao.SearchLastNum();//在数据库获取当前题的题号
		//判断输入的题是否在数据库中
		int enterTask = Integer.parseInt(task_id);
		if(enterTask<=TaskNo&&enterTask>0) {
			//System.out.println("要查看的题目在数据库中");
			try {
				res = l_dao.CheckLogByTask(enterTask);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(res!=0) {
				//这道题有记录
				List<Log> log_list = new ArrayList<Log>();
				try {
					log_list = l_dao.QueryByTaskNo(enterTask);
					for(Log l:log_list) {
						System.out.println(l.toString());
					}
					request.getSession().setAttribute("log_list", log_list);
					//String message = "为什么传不过来";
					//request.getSession().setAttribute("message", message);
					//计算学生排名
					calculation c = new calculation();
					List<F_stu_id> fs_list = new ArrayList<F_stu_id>();
					fs_list = c.calRankByTask(enterTask);
					for(F_stu_id f:fs_list) {
						System.out.println(f.toString());
					}
					request.getSession().setAttribute("fs_list", fs_list);
					//生成统计图
					//先得到本题的F分布
					
					List<Integer> f = new ArrayList<Integer>();
					f=c.classify_F(enterTask);
					System.out.println("F已经得到");
					//开始画图
					String path = request.getServletContext().getRealPath("./")+File.separator+"1.jpeg";
					System.out.println("存放图片路径为："+path);
					pie2 p = new pie2();
					p.generatePieChart(f, path);
					response.sendRedirect("../ShowTaskRankSuc.jsp");
					//request.getRequestDispatcher("/ShowTaskRankSuc.jsp").forward(request, response);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				//这题没有学生做答
				String warning="这道题还没有学生做答";
				request.getSession().setAttribute("warning", warning);
				response.sendRedirect("../ShowTaskRankFail.jsp");
				//request.getRequestDispatcher("../ShowTaskRankFail.jsp").forward(request, response);
			}
			

		}else {
			//这题不在数据库中
			String warning="这道题还未被添加";
			request.getSession().setAttribute("warning", warning);
			request.getRequestDispatcher("../ShowTaskRankFail.jsp").forward(request, response);
		}
	}

}
