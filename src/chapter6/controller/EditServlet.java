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

import chapter6.beans.Message;
import chapter6.logging.InitApplication;
import chapter6.service.MessageService;

@WebServlet(urlPatterns = { "/edit" })
public class EditServlet extends HttpServlet {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public EditServlet() {
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

		//String型できているupdateMessageIdを、一旦受け取って、messageIdに格納して
		String messageId = request.getParameter("updateMessageId");
		List<String> errorMessages = new ArrayList<String>();

		//早速型変換したいところだけど、idがintじゃなかったり、なんにもない状態でくるのはここで除きたいので、
		//①渡ってきたidが数字じゃなかったら、かつ、②なんも入ってなかったらの条件でふるいにかける
		//isBlankなのは？ = 半角スペースが入るかもだから
		if (StringUtils.isBlank(messageId) || !messageId.matches("^[0-9]*$")) {

			//エラーメッセージを追加
			errorMessages.add("不正なパラメータが入力されました");

			//エラーメッセージを渡すためにセットする = edit.jspで表示するんじゃなくてtop.jspに遷移してから表示したいので！
			//渡してあげるためにパッキングしてやる
			HttpSession session = request.getSession();
			session.setAttribute("errorMessages", errorMessages);

			//top.jspに
			response.sendRedirect("./");

			//どこにreturnする？ →  top.jsp、つまり("./")
			return;
		}

		//if分入らなかったものたちだけをint型にしたいので型変換して、
		//MessageServiceに渡す
		int id = Integer.parseInt(messageId);

		//0件のときはnullが返ってくる、1件のときはMessage型のものが1個返ってくる
		Message messages = new MessageService().select(id);

		//数字なんだけど、まだ生成されていないidで渡ってきたとき
		//messagesの中身がnullだったとき（0件だったとき） = 存在しないとき
		if (messages == null) {
			errorMessages.add("不正なパラメータが入力されました");
			request.setAttribute("errorMessages", errorMessages);
			request.getRequestDispatcher("./").forward(request, response);
			return;
		}

		//requestにsetAttributeします、messagesを
		//requestにsetAttributeします、errorMessagesを
		request.setAttribute("messages", messages);
		request.setAttribute("errorMessages", errorMessages);

		//Dogetしているときのfoward処理
		request.getRequestDispatcher("/edit.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		List<String> errorMessages = new ArrayList<String>();

		//getParameter…JSPでインプットしたものを回収、Stringに格納している = 受け取り完了
		//intに変換。Daoでintにして扱いたいから
		//なんで？ = DBに入っているidがint型として登録されているから。そこがint型なのにString型で持ってこうとしても合致しない
		//updateMessageId = JSPでnameタグをつけてあげたもの。これとこれが入っているものはこれだ、とするやつ
		String messageId = request.getParameter("updateMessageId");
		int id = Integer.parseInt(messageId);

		//textって名前をつけられたものからgetParameterして、それが141字以上だったらエラーメッセージを表示したい
		//MessageServletにヒント
		//idを使って、表示させるためのメッセージをとってきて、それをrequestにつめてforwardする

		//表示させるためのメッセージをとってきて、
		String text = request.getParameter("text");

		//Message型のmessages を新しく宣言
		//なんで？ = textを詰めてあげないといけないから
		//表示して保持させたいつぶやき（messages.text）は、textをキーにして取り出せるvalueであるし、取り出したものはしまっとかないと宙ぶらりんになるため
		Message messages = new Message();

		//messagesに出したいつぶやき（"messages.text"）をセットします
		messages.setText(text);
		messages.setId(id);

		//140字よりたくさんあったら
		if (!isValid(text, errorMessages)) {
			request.setAttribute("errorMessages", errorMessages);
			//セット済みの、（出したいつぶやき（"messages.text"）, 型はMessage）を、今度はrequestにセットします
			request.setAttribute("messages", messages);
			request.getRequestDispatcher("./edit.jsp").forward(request, response);
			return;
		}

		//MessageServiceのdeleteメソッド(引数はid)に飛んできますよ
		//なぜRedirectか = データをちゃんと取ってくるにはRedirectだから
		new MessageService().update(text, id);
		response.sendRedirect("./");
	}

	private boolean isValid(String text, List<String> errorMessages) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		//isBlankなので、nullと空文字（"")と空白文字（" "）をチェックしてくれる
		if (StringUtils.isBlank(text)) {
			errorMessages.add("入力してください");
		} else if (140 < text.length()) {
			errorMessages.add("140文字以下で入力してください");
		}

		if (errorMessages.size() != 0) {
			return false;
		}
		return true;
	}
}