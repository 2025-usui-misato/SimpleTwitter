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

import chapter6.beans.UserMessage;
import chapter6.exception.SQLRuntimeException;
import chapter6.logging.InitApplication;

public class UserMessageDao {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public UserMessageDao() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	public List<UserMessage> select(Connection connection, Integer id, int num, String start, String end) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {

			//sqlに追加するよ、の分
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT ");
			//message.idだと長いから、message.idはidに変更しますよ。他のも同様に考える
			sql.append("    messages.id as id, ");
			sql.append("    messages.text as text, ");
			sql.append("    messages.user_id as user_id, ");
			sql.append("    users.account as account, ");
			sql.append("    users.name as name, ");
			sql.append("    messages.created_date as created_date ");
			//内部結合するテーブル①はmessagesです、
			sql.append("FROM messages ");
			//内部結合するテーブル②はusersテーブルです
			sql.append("INNER JOIN users ");
			sql.append("ON messages.user_id = users.id ");

			//日付で絞り込むのは、idがあってもなくてもやりたいのでif文の外に書かないといけない
			//SELECT * FROM authors WHERE 1 <= id AND id < 2; を参考に！
			//①1 <= idの条件と、② < 2が絶対にくっついた状態で条件付けできる！

			//WHERE カラム名 BETWEEN 日付 AND 日付;    で、2つの日付の間にあるものを指定できる
			//これが曖昧だって言われているので、
			//created_dateが？（バインド変数）のとき、startからendまでの日時で絞り込む、とする
			//どこのテーブルの、なんてカラムなのか！忘れない！
			sql.append(" WHERE messages.created_date BETWEEN ? AND ? ");

			//表結合されてたものから、user_idを使って取り出したい
			//でも、毎回じゃなくてuser_idで指定したいときだけ = if文
			if (id != null) {
				//idが曖昧だと言われている。 =1にしちゃうと、それしか見れないのでバインド変数にする
				sql.append(" AND user_id = ? ");
			}

			sql.append("ORDER BY created_date DESC limit " + num);

			ps = connection.prepareStatement(sql.toString());

			//問い合わせをしたいものをpsにつめるので
			ps.setString(1, start);
			ps.setString(2, end);

			if (id != null) {
				ps.setInt(3, id);
			}

			ResultSet rs = ps.executeQuery();

			List<UserMessage> messages = toUserMessages(rs);
			return messages;
		} catch (SQLException e) {
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}

	private List<UserMessage> toUserMessages(ResultSet rs) throws SQLException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		List<UserMessage> messages = new ArrayList<UserMessage>();
		try {
			while (rs.next()) {
				UserMessage message = new UserMessage();
				message.setId(rs.getInt("id"));
				message.setText(rs.getString("text"));
				message.setUserId(rs.getInt("user_id"));
				message.setAccount(rs.getString("account"));
				message.setName(rs.getString("name"));
				message.setCreatedDate(rs.getTimestamp("created_date"));

				messages.add(message);
			}
			return messages;
		} finally {
			close(rs);
		}
	}
}
