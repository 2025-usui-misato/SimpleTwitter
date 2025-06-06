package chapter6.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import chapter6.beans.User;
import chapter6.logging.InitApplication;
import chapter6.service.UserService;

@WebServlet(urlPatterns = { "/signup" })
public class SignUpServlet extends HttpServlet {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public SignUpServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		request.getRequestDispatcher("signup.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		List<String> errorMessages = new ArrayList<String>();

		User user = getUser(request);
		//もし、userとerrorMessage が有効じゃなかったら = なにかしらのエラーが発生していたら
		if (!isValid(user, errorMessages)) {
			//setAttributeしますよ
			request.setAttribute("errorMessages", errorMessages);
			request.getRequestDispatcher("signup.jsp").forward(request, response);
			return;
		}
		//情報がinsertされていく直前
		new UserService().insert(user);
		response.sendRedirect("./");
	}

	private User getUser(HttpServletRequest request) throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		//Userっていう空箱を用意して、User型のuserっていう変数を代入する =　宣言する
		User user = new User();
		//request = jspにタグをつくった（名前とかメールアドレスとか）とこに入力された値を取りまして、userに入れます
		user.setName(request.getParameter("name"));
		user.setAccount(request.getParameter("account"));
		user.setPassword(request.getParameter("password"));
		user.setEmail(request.getParameter("email"));
		user.setDescription(request.getParameter("description"));
		return user;
	}

	//入力したものをここでチェックしている
	private boolean isValid(User user, List<String> errorMessages) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		String name = user.getName();
		String account = user.getAccount();
		String password = user.getPassword();
		String email = user.getEmail();

		if (!StringUtils.isEmpty(name) && (20 < name.length())) {
			errorMessages.add("名前は20文字以下で入力してください");
		}

		if (StringUtils.isEmpty(account)) {
			errorMessages.add("アカウント名を入力してください");
		} else if (20 < account.length()) {
			errorMessages.add("アカウント名は20文字以下で入力してください");
		}

		if (StringUtils.isEmpty(password)) {
			errorMessages.add("パスワードを入力してください");
		}

		if (!StringUtils.isEmpty(email) && (50 < email.length())) {
			errorMessages.add("メールアドレスは50文字以下で入力してください");
		}

		//使える材料…
		//return users.get(0);…1件　だったら、登録してあるよ！被ってるよ！登録しないようにしたい
		//なぜここに → 登録しちゃだめだから、isValidをfalseつまり有効じゃないにしたいから。ここでストップさせてUserService以降にいかないようにしたい
		//falseのとき → 返ってきたusersのlistの中身が1件以上のとき。１件でもあれば登録NG
		//ただ、Daoで「どうであれば登録OK、NGかはかけたけど、Servletにいきなり飛ぶことはできないので、Serviceを挟む
		//Serviceの中で、UserDaoに飛んでるところを呼び出せば、このServletでも返ってきたuserの内容を扱える
		//User user = new UserService().select(loginUser.getId());

		User registeredUserData = new UserService().select(account);
		if (registeredUserData != null) {
			errorMessages.add("すでに存在するアカウントです");
		}

		if (errorMessages.size() != 0) {
			return false;
		}

		return true;
	}
}
