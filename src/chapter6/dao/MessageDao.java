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

import chapter6.beans.Message;
import chapter6.exception.SQLRuntimeException;
import chapter6.logging.InitApplication;

public class MessageDao {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public MessageDao() {
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
			sql.append("INSERT INTO messages ( ");
			sql.append("    user_id, ");
			sql.append("    text, ");
			sql.append("    created_date, ");
			sql.append("    updated_date ");
			sql.append(") VALUES ( ");
			sql.append("    ?, "); // user_id
			sql.append("    ?, "); // text
			sql.append("    CURRENT_TIMESTAMP, "); // created_date
			sql.append("    CURRENT_TIMESTAMP "); // updated_date
			sql.append(")");

			ps = connection.prepareStatement(sql.toString());

			ps.setInt(1, message.getUserId());
			ps.setString(2, message.getText());

			ps.executeUpdate();
		} catch (SQLException e) {
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}

	//Daoに新しくメソッドをつくるときのポイント：
	//①名前をつけてあげる、②戻り値があるかないか、③他のコードを参考にするにしても「なんで動いているのか」がわかっている
	//connection = DBに接続するための情報
	public void delete(Connection connection, int id) {

		//psを初期化
		PreparedStatement ps = null;
		try {
			//String型のsqlっていう箱に代入する = messagesテーブルのidを条件にして、全部取ってきたやつ
			//を、バインド変数にして代入する
			//* は使わない = idに紐づいた特定のつぶやきを削除したいから
			String sql = "DELETE FROM messages WHERE id = ?";

			//psに代入する = sqlを引数にして、connectionのprepareStatementメソッドを使って
			ps = connection.prepareStatement(sql);
			//psのsetStringを使って、バインド変数に値をセットします
			ps.setInt(1, id);
			//executeUpdate = データの更新をするexecute = 実行、update = 更新）
			ps.executeUpdate();

		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}

	}

	public Message select(Connection connection, int id) {

		PreparedStatement ps = null;
		try {
			String sql = "SELECT * FROM messages WHERE id = ?";
			ps = connection.prepareStatement(sql);
			ps.setInt(1, id);

			//受け取とって、使いやすい型・自分が使いたい型に変換する
			//①SQLを実行し、結果をResultSetに入れる = 受け取り完了
			//②ResultSetからListに詰め替える → 全てのカラムをまとめて「ResultSet」という型で扱っているため、Stringやintなどとして扱うことができませんので！
			//executeQuery = データを取得する（execute = 実行、query = 問い合わせ結果）
			ResultSet rs = ps.executeQuery();
			//toMessagesメソッドを呼び出している = 呼び出し元がある
			List<Message> messages = toMessages(rs);

			//Listからmessageを取得する
			//Where idする（主キーで絞る）時点で１件(１レコード)しか取れない、だから(0) = これはMessage型
			//存在するidがあれば↓でよし、なかったらトップ画面とエラーメッセージを表示するようにしたい
			//エラーメッセージを表示するかしないかっていうのはここじゃなくて、Servletでやるので、Servletにもってきたいときのふるいかけをする
			//条件2個あって、それぞれ処理が違うのでifelse文使う。ないとき→あるときの順番

			//idが存在しないとき = isEmptyなとき
			//messagesそのものが空じゃないか判定する。get(0)するのは中身があったうえでやること
			if (messages.isEmpty()) {
				return null;
			}
			//存在するならそのまま渡してあげればいいよね
			return messages.get(0);

		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}

	}

	//受け渡す側のメソッド
	private List<Message> toMessages(ResultSet rs) throws SQLException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		//sourceTweetを入れるListを宣言(returnされるのはコレ)
		List<Message> messages = new ArrayList<Message>();

		try {
			//rs.next() は、現在の位置から次の1行へ進んでカーソルを当てる
			//1個取り出す作業
			while (rs.next()) {

				//つぶやき1個分用の箱（messages）を用意する
				//編集したい元ツイ
				Message sourceTweet = new Message();

				//使いやすいように、カラムごとに詰め替える作業
				sourceTweet.setId(rs.getInt("id"));
				sourceTweet.setText(rs.getString("text"));
				sourceTweet.setUserId(rs.getInt("user_id"));
				sourceTweet.setCreatedDate(rs.getTimestamp("created_date"));
				sourceTweet.setUpdatedDate(rs.getTimestamp("updated_date"));

				messages.add(sourceTweet);

			}

			return messages;

		} finally {
			close(rs);
		}
	}

	//Daoに新しくメソッドをつくるときのポイント：
	//①名前をつけてあげる、②戻り値があるかないか、③他のコードを参考にするにしても「なんで動いているのか」がわかっている
	//connection = DBに接続するための情報
	public void update(Connection connection, String text, int id) {

		//psを初期化
		PreparedStatement ps = null;
		try {
			//messagesテーブルのうち、idが?(=バインド変数)のものについて、textというカラムに?(バインド変数。新しいつぶやき)と入れる。
			String sql = "UPDATE messages SET text = ?, updated_date = CURRENT_TIMESTAMP WHERE id = ?";

			ps = connection.prepareStatement(sql);
			//psのsetStringを使って、バインド変数に値をセットします
			//1番目の?にtext、2番目の?にid
			ps.setString(1, text);
			ps.setInt(2, id);
			//executeUpdate = データの更新をするexecute = 実行、update = 更新）
			ps.executeUpdate();

		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}

	}

}
