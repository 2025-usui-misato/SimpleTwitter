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
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import chapter6.beans.User;
import chapter6.exception.NoRowsUpdatedRuntimeException;
import chapter6.logging.InitApplication;
import chapter6.service.UserService;

@WebServlet(urlPatterns = { "/setting" })
public class SettingServlet extends HttpServlet {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public SettingServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		HttpSession session = request.getSession();
		//sessionに入っているloginUser（ログインするための情報）をgetしている
		User loginUser = (User) session.getAttribute("loginUser");

		//User型のuserに代入する = UserServiceのselectメソッドを使って（loginUserからgetIdしたものを）
		//↑で取ってきたloginUserのIdをキーにして、1レコード取ってきている
		User user = new UserService().select(loginUser.getId());

		//それを画面に出している
		request.setAttribute("user", user);
		request.getRequestDispatcher("setting.jsp").forward(request, response);
	}

	@Override
	//login.jspからPOSTされているので以下の処理が行われる
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		HttpSession session = request.getSession();
		//String型のerrorMessagesリストを新しくつくりますよ～
		List<String> errorMessages = new ArrayList<String>();

		//User型のuserに代入する = getUserメソッドを使ってrequestをgetしている
		User user = getUser(request);

		//isValid = 期待する状態と一致するか否か
		//もし（userに対してerrorMessageが合致するものがあるなら）
		//エラーメッセージ合致するよってなったらcatch文へいく
		if (isValid(user, errorMessages)) {
			try {
				//UserServiceのupdateメソッドを使います(userを引数にして）
				//UserServiceに飛んでいく
				new UserService().update(user);

			} catch (NoRowsUpdatedRuntimeException e) {
				log.warning("他の人によって更新されています。最新のデータを表示しました。データを確認してください。");
				errorMessages.add("他の人によって更新されています。最新のデータを表示しました。データを確認してください。");
			}
		}

		if (errorMessages.size() != 0) {
			request.setAttribute("errorMessages", errorMessages);
			request.setAttribute("user", user);
			request.getRequestDispatcher("setting.jsp").forward(request, response);
			return;
		}

		session.setAttribute("loginUser", user);
		response.sendRedirect("./");
	}

	private User getUser(HttpServletRequest request) throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		User user = new User();
		user.setId(Integer.parseInt(request.getParameter("id")));
		user.setName(request.getParameter("name"));
		user.setAccount(request.getParameter("account"));
		user.setPassword(request.getParameter("password"));
		user.setEmail(request.getParameter("email"));
		user.setDescription(request.getParameter("description"));
		return user;
	}

	//isValid 入力された入力値のチェック
	private boolean isValid(User user, List<String> errorMessages) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		//user.id=ご本人情報で、getなんちゃらしてStringに入れている
		String name = user.getName();
		String account = user.getAccount();
		String email = user.getEmail();

		if (!StringUtils.isEmpty(name) && (20 < name.length())) {
			errorMessages.add("名前は20文字以下で入力してください");
		}
		if (StringUtils.isEmpty(account)) {
			errorMessages.add("アカウント名を入力してください");
		} else if (20 < account.length()) {
			errorMessages.add("アカウント名は20文字以下で入力してください");
		}

		if (!StringUtils.isEmpty(email) && (50 < email.length())) {
			errorMessages.add("メールアドレスは50文字以下で入力してください");
		}

		//UserDaoで書いたUserSelectメソッドを呼び出したいので、UserServiceを呼び出す
		User registeredUserData = new UserService().select(account);
		//今自分がシステムを見てるという前提のもと、
		//条件：①アカウント名がなかったら（nullだったら）NG、②もってきたものが他人のものでもNG
		//他人のものであること = 1個あるけど、それが今自分が見るのに使っているアカウントじゃない
		//他人のものであることがわかる条件 = 今自分がログインしている情報と違うもの
		//ログインするための情報 = アカウント名またはメールアドレス、パスワード
		//

		if (registeredUserData != null && registeredUserData.getId() != user.getId()) {
			errorMessages.add("すでに存在するアカウントです");
		}

		if (errorMessages.size() != 0) {
			return false;
		}
		return true;
	}
}
