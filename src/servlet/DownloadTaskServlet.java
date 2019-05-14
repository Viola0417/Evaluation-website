package servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.Task_Dao;
import entity.Task;

/**
 * Servlet implementation class DownloadTaskServlet
 */
@WebServlet("/DownloadTaskServlet")
public class DownloadTaskServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DownloadTaskServlet() {
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
		String task_id = null;
		//String filename = null;
		//System.out.println("准备下载task了");
		request.setCharacterEncoding("utf-8");
		//System.out.println("处理post请求");
		task_id=request.getParameter("task_id1");
		//System.out.println(task_id);
		Task t = new Task();
		Task_Dao t_dao = new Task_Dao();
		int TaskNo = 0;
		TaskNo = t_dao.SearchLastNum();//在数据库获取当前题的题号
		//判断输入的题是否在数据库中
		int enterTask = Integer.parseInt(task_id);
		if(enterTask<=TaskNo&&enterTask>0) {

			//System.out.println("压缩下载");
			//尝试打包下载
			ServletContext context = this.getServletContext();
			String train_file_path = context.getRealPath(task_id+"/train");
			//String test_file_path = context.getRealPath(task_id+"/test");
			//文件名称
	        //String[] names={"train.xlsx","test.xlsx"};
			String[] names={"train.xlsx"};
	        FileInputStream input_train = new FileInputStream(new File(train_file_path));
	       // FileInputStream input_test = new FileInputStream(new File(test_file_path));
	        //FileInputStream[] inputs={input_train,input_test};
	        FileInputStream[] inputs={input_train};
	        
	        //ZIP打包
	        String zip_path =  request.getServletContext().getRealPath("./")+File.separator+"zip_train";
	        File zipFile = new File(zip_path);
	        byte[] buf = new byte[1024];
	        int len;
	        ZipOutputStream zout=new ZipOutputStream(new FileOutputStream(zipFile));
	        for (int i = 0; i < inputs.length; i++) {  
	            FileInputStream in =inputs[i];  
	            zout.putNextEntry(new ZipEntry(names[i]));    
	            while ((len = in.read(buf)) > 0) {  
	                zout.write(buf, 0, len);  
	            }  
	            zout.closeEntry();  
	            in.close();  
	        }
	        zout.close();
	        
	        
	        //System.out.println("下载zip");
	        FileInputStream zipInput =new FileInputStream(zipFile);
	        OutputStream out = response.getOutputStream();
	        response.setContentType("application/octet-stream");
	        String zip_name = task_id+".zip";
	        response.setHeader("Content-Disposition", "attachment; filename="+zip_name);
	        while ((len=zipInput.read(buf))!= -1){  
	            out.write(buf,0,len);  
	        }
	        zipInput.close();
	        out.flush();
	        out.close();
	        //删除压缩包
	        zipFile.delete();
			
		}else {
			//System.out.println("这道题不在数据库中");
			String message="题目号码不正确";
			request.getSession().setAttribute("message", message);
			response.sendRedirect("../Res.jsp");
			//request.getRequestDispatcher("../downloadFail.jsp").forward(request, response);
		}
	}

}
