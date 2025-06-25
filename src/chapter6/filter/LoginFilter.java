package chapter6.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import chapter6.beans.User;

@WebFilter({"/setting","/edit"})
public class LoginFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		//引数にしているrequestとresponseの型変換をする
		//なぜ？：sessionを使いたいのに、このままだと使えないから

		//ServletRequestのrequestを、HttpServletRequest型のhttpRequestに変換したい
		//左辺：変換したい型名 変数名		右辺：（変換したい型名）変換元の変数名
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;

		//sessionの取得の仕方は同じ。どのrequestから取るか？がこれまでとちょっと違う
		//requestをそのまま使ってしまうと、sessionを取得するためにせっかく型変換したhttpRequestの使いどころがなくなってしまう
		HttpSession session = httpRequest.getSession();

		//sessionからloginUser（ログイン情報）を取得して、sessionっていう箱に詰める
		User user = (User) session.getAttribute("loginUser");

		//もしloginUser（ログイン情報）が空だったら = ログインしていなかったら
		if(user == null) {
			List<String> errorMessages = new ArrayList<String>();
			errorMessages.add("ログインしてください");
			session.setAttribute("errorMessages", errorMessages);
			httpResponse.sendRedirect("login.jsp");
			return;

		}else if(user != null) {
			chain.doFilter(httpRequest, httpResponse); //EditServlet、SettingServletを実行
		}

	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void destroy() {

	}

}