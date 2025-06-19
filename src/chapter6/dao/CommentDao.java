package chapter6.dao;

import static chapter6.utils.CloseableUtil.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import chapter6.beans.Message;
import chapter6.exception.SQLRuntimeException;
import chapter6.logging.InitApplication;

public class CommentDao {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public CommentDao() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	public void insert(Connection connection, Message message) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO comments ( ");
			//idはわざわざ書かなくていい = auto_incrementにしてあるから、勝手に生成される
			sql.append("    text, ");
			sql.append("    user_id, ");
			sql.append("    message_id, ");
			sql.append("    created_date, ");
			sql.append("    updated_date ");
			sql.append(") VALUES ( ");
			sql.append("    ?, "); // text
			sql.append("    ?, "); // user_id
			sql.append("    ?, "); // message_id
			sql.append("    CURRENT_TIMESTAMP, "); // created_date
			sql.append("    CURRENT_TIMESTAMP "); // updated_date
			sql.append(")");

			ps = connection.prepareStatement(sql.toString());

			//psに、バインド変数にしたところの値をつめていく
			//idは自動付番されているので、1番目にくるのはtext

			ps.setString(1, message.getText());
			ps.setInt(2, message.getUserId());
			ps.setInt(3, message.getMessageId());

			ps.executeUpdate();

		} catch (SQLException e) {
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}


	public void select(Connection connection,  ) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT ");
			//使いたいデータをここで全部取っておく
			sql.append("    comments.id as id, ");
			sql.append("    comments.user_id as user_id, ");
			sql.append("    comments.text as text, ");
			sql.append("    comments.created_date as created_date, ");
			sql.append("    comments.message_id as message_id, ");
			sql.append("    users.account as account, ");
			sql.append("    users.name as name, ");

			//内部結合するテーブル①はmessagesです、
			sql.append("FROM comments ");

			//内部結合するテーブル②はusersテーブルです
			sql.append("INNER JOIN users ");

			//ON句で、条件を指定する
			//ここでは、commentsのuser_idと、usersのidが一致すればＯＫなので
			sql.append("ON comments.user_id = users.id ");

			//表結合されてたものから、user_idを使って取り出したい
			//でも、毎回じゃなくてuser_idで指定したいときだけ = if文
			//id(commentsのid)がnullじゃなかったら = 返信があったら
			if (id != null) {
			//idが曖昧だと言われている。でもidが1のレコードの性別を変更する場合つまり =1にしちゃうと、それしか見れない
			sql.append(" WHERE user_id = ? ");
			}

			//created_dateでソートする
			sql.append("ORDER BY created_date DESC limit " + num);

			ps = connection.prepareStatement(sql.toString());

			if (id != null) {
				ps.setInt(1, id);
			}

		}
	}
}