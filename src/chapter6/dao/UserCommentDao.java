package chapter6.dao;

import static chapter6.utils.CloseableUtil.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;

import chapter6.beans.UserComment;
import chapter6.exception.SQLRuntimeException;
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

	public List<UserComment> select(Connection connection) {

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
			sql.append("    users.name as name ");

			//内部結合するテーブル①はmessagesです、
			sql.append("FROM comments ");

			//内部結合するテーブル②はusersテーブルです
			sql.append("INNER JOIN users ");

			//ON句で、条件を指定する
			//ここでは、commentsのuser_idと、usersのidが一致すればＯＫなので
			sql.append("ON comments.user_id = users.id ");

			ps = connection.prepareStatement(sql.toString());

			ResultSet rs = ps.executeQuery();

			List<UserComment> comments = toCommentMessages(rs);

			return comments;


		} catch (SQLException e) {
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}

	private List<UserComment> toCommentMessages(ResultSet rs) throws SQLException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		List<UserComment> comments = new ArrayList<UserComment>();
		try {
			while (rs.next()) {
				UserComment comment = new UserComment();
				comment.setId(rs.getInt("id"));
				comment.setUserId(rs.getInt("user_id"));
				comment.setMessageId(rs.getInt("message_id"));
				comment.setText(rs.getString("text"));
				comment.setAccount(rs.getString("account"));
				comment.setName(rs.getString("name"));
				comment.setCreatedDate(rs.getTimestamp("created_date"));

				comments.add(comment);
			}
			return comments;
		} finally {
			close(rs);
		}
	}
}