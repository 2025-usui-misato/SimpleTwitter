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

import chapter6.beans.Comment;
import chapter6.beans.User;
import chapter6.logging.InitApplication;
import chapter6.service.CommentService;

@WebServlet(urlPatterns = { "/comment" })
public class CommentServlet extends HttpServlet {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public CommentServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	//返信されて、初めて動く
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		List<String> errorMessages = new ArrayList<String>();

		//Redirectするのに、情報が保持されないからsessionにつめておく
		HttpSession session = request.getSession();

		//requestから、textをgetParameterする
		//ここでいうtext = 返信した言葉たち
		String text = request.getParameter("text");

		if (!isValid(text, errorMessages)) {

			session.setAttribute("errorMessages", errorMessages);
			response.sendRedirect("./");
			return;
		}

		//Comment型のcommentsを宣言する
		//なんで？ = textも、userIdも、messageIdも詰めたいから。たくさん分けて引数にもってくよりまとめといたほうがよさそう
		Comment comments = new Comment();
		comments.setText(text);

		//今回、commentsテーブルにuser_idとmessage_idも追加したいので
		//①ログイン状態にあって、本人たりえる情報を持ってくる = "loginUser"の中のuser_id
		//②つぶやきに紐づいて返信したいので、そのつぶやきに付与されている、そのつぶやき固有のidを持ってくる = message_id

		//まず①をする。sessionからもってきてuserに入れとく
		User user = (User) session.getAttribute("loginUser");
		//userからgetIdメソッドでidを取ってきて、messageにまとめて入れとく
		comments.setUserId(user.getId());

		//次に②をする
		//どこから？ = jspで「返信」ボタンが押されたときに渡ってくるcommentMessageIdから
		//messageのidは、commentMessageIdのvalueになっているので、キーを使って引っ張り出せばいい
		String messageId = request.getParameter("commentMessageId");
		//なぜ型変換？ = DBに入っているidっていうカラムは、int型だから
		//渡していくときにもint型にしないとDaoでうまく認識してくれない
		int id = Integer.parseInt(messageId);
		comments.setMessageId(id);

		new CommentService().insert(comments);
		response.sendRedirect("./");

	}

	private boolean isValid(String text, List<String> errorMessages) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		//isBlankなので、nullと空文字（"")と空白文字（" "）をチェックしてくれる
		if (StringUtils.isBlank(text)) {
			errorMessages.add("メッセージを入力してください");
		} else if (140 < text.length()) {
			errorMessages.add("140文字以下で入力してください");
		}

		if (errorMessages.size() != 0) {
			return false;
		}
		return true;
	}
}
