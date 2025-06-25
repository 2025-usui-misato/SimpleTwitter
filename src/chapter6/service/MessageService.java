package chapter6.service;

import static chapter6.utils.CloseableUtil.*;
import static chapter6.utils.DBUtil.*;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import chapter6.beans.Message;
import chapter6.beans.UserMessage;
import chapter6.dao.MessageDao;
import chapter6.dao.UserMessageDao;
import chapter6.logging.InitApplication;

public class MessageService {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public MessageService() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	public void insert(Message message) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		Connection connection = null;
		try {
			connection = getConnection();
			new MessageDao().insert(connection, message);
			commit(connection);
		} catch (RuntimeException e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} catch (Error e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} finally {
			close(connection);
		}
	}

	public List<UserMessage> select(String userId, String start, String end) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		final int LIMIT_NUM = 1000;

		Connection connection = null;
		try {
			connection = getConnection();
			/*
			   * idをnullで初期化
			   * ServletからuserIdの値が渡ってきていたら
			   * 整数型に型変換し、idに代入
			   */
			Integer id = null;
			if (!StringUtils.isEmpty(userId)) {
				id = Integer.parseInt(userId);
			}

			//startが入力されていたら = nullじゃなかったら
			//startにはいっている日付に、時間を足す
			//単純に足し算じゃなくて、加算代入演算子「+=」を使う
			//+= ： x = x+y になる
			if (start != null) {
				start += " 00:00:00";

				//else = 入力されていたら = nullだったら
				//デフォルト値を設定
			} else {
				//この形で渡したいけど、ベタ打ちしてそのまま渡るわけじゃなくて、
				//stratが空のまま渡ってきていて、このあとのDaoにstartの中にデフォルト値を入れた状態で渡したい
				start = "2020-01-01 00:00:00";
			}

			//end が入力されていたら = nullじゃなかったら
			//endに入っている日付に、時間を足す
			if (end != null) {
				end += " 23:59:59";

				//else = 入力されていなかったら = nullだったら
				//デフォルト値を設定。現在の日時を取得する
			} else {
				//現在日時を取得している
				Date nowDate = new Date();
				//日時のフォーマットが、自分の意図する（年-月-日）だとは限らないので、意図する形に指定するために宣言
				SimpleDateFormat formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

				//意図する形に整えられるように宣言した変数のformattedDateに、現在日時を取得するnowDateを使って、
				//現在日時を意図する形に整えて、formatNowDateに入れる
				String formatNowDate = formattedDate.format(nowDate);

				//endが空っぽできていて、このあとDaoにはendにデフォルト値を入れた状態で渡したいので、
				//整えられた現在日時であるformatNowDateを、endに代入する
				end = formatNowDate;
			}

			/*
			* UserMessageDao.selectに引数としてInteger型のidを追加
			* idがnullだったら全件取得する
			* idがnull以外だったら、その値に対応するユーザーIDの投稿を取得する
			*/
			List<UserMessage> messages = new UserMessageDao().select(connection, id, LIMIT_NUM, start, end);
			commit(connection);

			return messages;
		} catch (RuntimeException e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} catch (Error e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} finally {
			close(connection);
		}
	}

	public void delete(int id) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		//Connectionを初期化
		Connection connection = null;
		try {
			connection = getConnection();
			new MessageDao().delete(connection, id);
			commit(connection);
		} catch (RuntimeException e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} catch (Error e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} finally {
			close(connection);
		}
	}

	public Message select(int id) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		Connection connection = null;
		try {
			connection = getConnection();

			//selectしたら、必ず変数に入れる！！入れないと宙ぶらりんになったままです
			//逆にdeleteとかupdateはDaoから返ってこないから変数に入れる必要なし
			Message message = new MessageDao().select(connection, id);
			commit(connection);

			return message;

		} catch (RuntimeException e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} catch (Error e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} finally {
			close(connection);
		}

	}

	public void update(String text, int id) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		//Connectionを初期化
		Connection connection = null;
		try {
			connection = getConnection();
			new MessageDao().update(connection, text, id);
			commit(connection);
		} catch (RuntimeException e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} catch (Error e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} finally {
			close(connection);
		}
	}
}
