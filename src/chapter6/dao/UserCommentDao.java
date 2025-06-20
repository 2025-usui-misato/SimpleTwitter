package chapter6.dao;

import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;

import chapter6.logging.InitApplication;


public class UserCommentDao extends HttpServlet {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public UserCommentDao() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}
}