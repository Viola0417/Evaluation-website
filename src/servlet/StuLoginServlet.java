package servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.Student_Dao;
import dao.Teacher_Dao;
import entity.Student;
import entity.Teacher;
import util.DbUtil;
/**
 * Servlet implementation class StuLoginServlet
 */
@WebServlet("/StuLoginServlet")
public class StuLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StuLoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");
		System.out.println("����post����");
		//Teacher t = new Teacher();
		//Connection con=null;
		try {
			//����������������ݿ��������Ƿ�һ��
			Student_Dao s_dao = new Student_Dao();
			Student s_temp = new Student();
			String s_name,s_password;
			s_password=request.getParameter("s_password");
			s_name=request.getParameter("s_name");
			if(s_name!=null) {
				s_temp=s_dao.queryByName(s_name);
			}
			
			System.out.println(s_temp.toString());
			String db_password=s_temp.getS_password();
			System.out.println(db_password);
			if(db_password==null) {
				System.out.println("����û���������");
				String warning="����û�������";
				request.getSession().setAttribute("warning", warning);
				request.getRequestDispatcher("../s_logFail.jsp").forward(request, response);;
				}else if(db_password.equals(s_password)) {
					System.out.println("����һ��");
					request.getSession().setAttribute("logStudent", s_temp);
					//��תҳ��
					request.getRequestDispatcher("../s_func.jsp").forward(request, response);
					}else {
						System.out.println("�������");
						String warning="�û��������벻ƥ��";
						request.getSession().setAttribute("warning", warning);
						request.getRequestDispatcher("../s_logFail.jsp").forward(request, response);;
						//request.getRequestDispatcher("../t_index.jsp").forward(request, response);
					}
		    
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}