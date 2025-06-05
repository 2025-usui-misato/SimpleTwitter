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

import org.apache.commons.lang.StringUtils;

import chapter6.beans.User;
import chapter6.exception.NoRowsUpdatedRuntimeException;
import chapter6.exception.SQLRuntimeException;
import chapter6.logging.InitApplication;

public class UserDao {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public UserDao() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	public void insert(Connection connection, User user) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO users ( ");
			sql.append("    account, ");
			sql.append("    name, ");
			sql.append("    email, ");
			sql.append("    password, ");
			sql.append("    description, ");
			sql.append("    created_date, ");
			sql.append("    updated_date ");
			sql.append(") VALUES ( ");
			sql.append("    ?, "); // account
			sql.append("    ?, "); // name
			sql.append("    ?, "); // email
			sql.append("    ?, "); // password
			sql.append("    ?, "); // description
			sql.append("    CURRENT_TIMESTAMP, "); // created_date
			sql.append("    CURRENT_TIMESTAMP "); // updated_date
			sql.append(")");

			ps = connection.prepareStatement(sql.toString());

			ps.setString(1, user.getAccount());
			ps.setString(2, user.getName());
			ps.setString(3, user.getEmail());
			ps.setString(4, user.getPassword());
			ps.setString(5, user.getDescription());

			ps.executeUpdate();
		} catch (SQLException e) {
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}

	public User select(Connection connection, String accountOrEmail, String password) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			String sql = "SELECT * FROM users WHERE (account = ? OR email = ?) AND password = ?";

			ps = connection.prepareStatement(sql);

			ps.setString(1, accountOrEmail);
			ps.setString(2, accountOrEmail);
			ps.setString(3, password);

			//SQLを実行すると、ResultSetに格納される
			ResultSet rs = ps.executeQuery();

			///ResultSet rsを、List<User>に詰め替え
			List<User> users = toUsers(rs);

			if (users.isEmpty()) {
				return null;
			} else if (2 <= users.size()) {
				log.log(Level.SEVERE, "ユーザーが重複しています",
						new IllegalStateException());
				throw new IllegalStateException("ユーザーが重複しています");
			} else {
				return users.get(0);
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}

	}

	//ResultSet rsを、List<User>に詰め替えているメソッド
	private List<User> toUsers(ResultSet rs) throws SQLException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		List<User> users = new ArrayList<User>();
		try {
			//rsの数文繰り返している
			while (rs.next()) {

				//User型の変数[user]を宣言、newする。
				User user = new User();

				//変数[user]にSetしたい！(rsに入っているいろいろをget)
				user.setId(rs.getInt("id"));
				user.setAccount(rs.getString("account"));
				user.setName(rs.getString("name"));
				user.setEmail(rs.getString("email"));
				user.setPassword(rs.getString("password"));
				user.setDescription(rs.getString("description"));
				user.setCreatedDate(rs.getTimestamp("created_date"));
				user.setUpdatedDate(rs.getTimestamp("updated_date"));

				//Listに格納
				users.add(user);
			}
			return users;
		} finally {
			close(rs);
		}
	}

	public User select(Connection connection, int id) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			String sql = "SELECT * FROM users WHERE id = ?";

			ps = connection.prepareStatement(sql);

			ps.setInt(1, id);

			ResultSet rs = ps.executeQuery();

			List<User> users = toUsers(rs);
			if (users.isEmpty()) {
				return null;
			} else if (2 <= users.size()) {
				log.log(Level.SEVERE, "ユーザーが重複しています", new IllegalStateException());
				throw new IllegalStateException("ユーザーが重複しています");
			} else {
				return users.get(0);
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}

	public void update(Connection connection, User user) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			StringBuilder sql = new StringBuilder();
			//passwordってなに？となるので、ここで定義しておく
			String password = user.getPassword();

			sql.append("UPDATE users SET ");
			sql.append("    account = ?, ");
			sql.append("    name = ?, ");
			sql.append("    email = ?, ");
			//passwordが入力されていたら(空じゃなかったら)、passwordをsqlに追加する
			if (!StringUtils.isBlank(password)) {
				sql.append("    password = ?, ");
			}
			sql.append("    description = ?, ");
			sql.append("    updated_date = CURRENT_TIMESTAMP ");
			sql.append("WHERE id = ?");

			ps = connection.prepareStatement(sql.toString());

			ps.setString(1, user.getAccount());
			ps.setString(2, user.getName());
			ps.setString(3, user.getEmail());

			//入力されていなかったら、4descriptionに・5をIDにしたい。なかったときに繰り上げたい
			//if-else?
			if (!StringUtils.isBlank(password)) {
				ps.setString(4, user.getPassword());
				ps.setString(5, user.getDescription());
				ps.setInt(6, user.getId());
			} else {
				ps.setString(4, user.getDescription());
				ps.setInt(5, user.getId());
			}

			int count = ps.executeUpdate();
			if (count == 0) {
				log.log(Level.SEVERE, "更新対象のレコードが存在しません", new NoRowsUpdatedRuntimeException());
				throw new NoRowsUpdatedRuntimeException();
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}

	public User select(Connection connection, String account) {

		PreparedStatement ps = null;
		try {
			//String型のsqlっていう箱に代入する = usersテーブルのaccountを条件にして、全部取ってきたやつ
			//を、バインド変数にして代入する
			String sql = "SELECT * FROM users WHERE account = ?";

			//psに代入する = sqlを引数にして、connectionのprepareStatementメソッドを使って
			ps = connection.prepareStatement(sql);
			//psのsetStringを使って、バインド変数に値をセットします
			ps.setString(1, account);

			//ResultSet型のrsに代入する = SQL文を実行したものを
			ResultSet rs = ps.executeQuery();

			//ぱっと見わかんないけど、usersにResultSet rsを、List<User>に詰め替え終わっている
			//メソッドジャンプしてみて！
			List<User> users = toUsers(rs);

			//詰め替え終わったusersを使って、
			//SQLの取得結果が、0件のとき（未登録のとき）
			if (users.isEmpty()) {
				return null;
				//SQLの取得結果が2件以上のとき（なぜか重複してるとき）
			} else if (2 <= users.size()) {
				throw new IllegalStateException("ユーザーが重複しています");
				//SQLの取得結果が1件だったとき（正常パターン。登録済みのものがちゃんとおさまっている状態）
			} else {
				return users.get(0);
			}
		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}

}