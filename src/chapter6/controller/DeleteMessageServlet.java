package chapter6.controller;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import chapter6.logging.InitApplication;
import chapter6.service.MessageService;

@WebServlet(urlPatterns = { "/delete" })
public class DeleteMessageServlet extends HttpServlet {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	 * @return
	*/
	public DeleteMessageServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	//①idをString型で受け取って、②Daoで書いたSQL文を実行させるのにServiceに送り出したい
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		//getParameter…JSPでインプットしたものを回収、Stringに格納している = 受け取り完了
		//intに変換。Daoでintにして扱いたいから
		String messageId = request.getParameter("deleteMessageId");
		int id = Integer.parseInt(messageId);

		//MessageServiceのdeleteメソッド(引数はid)に飛んできますよ
		//MessageServiceのdeleteメソッド(引数はid) ←これ、ないから追加する
		new MessageService().delete(id);
		response.sendRedirect("./");
	}

}
