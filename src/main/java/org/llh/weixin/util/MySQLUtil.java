package org.llh.weixin.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.llh.weixin.pojo.Knowledge;
import org.llh.weixin.pojo.UserLocation;
import org.llh.weixin.pojo.Game;
import org.llh.weixin.pojo.GameRound;
/**
 * Mysql���ݿ������
 * 
 * @author llh
 * @date 2014-11-19
 */
public class MySQLUtil {
	/**
	 * ��ȡMysql���ݿ�����
	 * 
	 * @return Connection
	 */
	private Connection getConn() {
		Connection conn = null;

		// ��request����ͷ��ȡ��IP���˿ڡ��û���������
//		String host = request.getHeader("BAE_ENV_ADDR_SQL_IP");
//		String port = request.getHeader("BAE_ENV_ADDR_SQL_PORT");
//		String username = request.getHeader("BAE_ENV_AK");
//		String password = request.getHeader("BAE_ENV_SK");
		// ���ݿ�����

		String host = "localhost";
        String port ="3306" ;
		String username ="root";
		String password ="root";
		// ���ݿ�����
		String dbName = "linkyoulink";
		// JDBC URL
		String url = String.format("jdbc:mysql://%s:%s/%s", host, port, dbName);

		try {
			// ����MySQL����
			Class.forName("com.mysql.jdbc.Driver");
			// ��ȡ���ݿ�����
			conn = DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	/**
	 * �ͷ�JDBC��Դ
	 *
	 * @param conn ���ݿ�����
	 * @param ps
	 * @param rs ��¼��
	 */
	private void releaseResources(Connection conn, PreparedStatement ps, ResultSet rs) {
		try {
			if (null != rs)
				rs.close();
			if (null != ps)
				ps.close();
			if (null != conn)
				conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * �����û�����λ��
	 * 
	 * @param request �������
	 * @param openId �û���OpenID
	 * @param lng �û����͵ľ���
	 * @param lat �û����͵�γ��
	 * @param bd09_lng �����ٶ�����ת����ľ���
	 * @param bd09_lat �����ٶ�����ת�����γ��
	 */
	public static void saveUserLocation(HttpServletRequest request, String openId, String lng, String lat, String bd09_lng, String bd09_lat) {
		String sql = "insert into user_location(open_id, lng, lat, bd09_lng, bd09_lat) values (?, ?, ?, ?, ?)";
		try {
			Connection conn = new MySQLUtil().getConn();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, openId);
			ps.setString(2, lng);
			ps.setString(3, lat);
			ps.setString(4, bd09_lng);
			ps.setString(5, bd09_lat);
			ps.executeUpdate();
			// �ͷ���Դ
			ps.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ȡ�û����һ�η��͵ĵ���λ��
	 * 
	 * @param request �������
	 * @param openId �û���OpenID
	 * @return UserLocation
	 */
	public static UserLocation getLastLocation(HttpServletRequest request, String openId) {
		UserLocation userLocation = null;
		String sql = "select open_id, lng, lat, bd09_lng, bd09_lat from user_location where open_id=? order by location_id desc limit 0,1";
		try {
			Connection conn = new MySQLUtil().getConn();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, openId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				userLocation = new UserLocation();
				userLocation.setOpenId(rs.getString("open_id"));
				userLocation.setLng(rs.getString("lng"));
				userLocation.setLat(rs.getString("lat"));
				userLocation.setBd09Lng(rs.getString("bd09_lng"));
				userLocation.setBd09Lat(rs.getString("bd09_lat"));
			}
			// �ͷ���Դ
			rs.close();
			ps.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userLocation;
	}
	/**
	 * ������Ϸ��Ϣ
	 *
	 * @param request �������
	 * @param game ��Ϸ����
	 * @return gameId
	 */
	public static int saveGame(HttpServletRequest request, Game game) {
		int gameId = -1;
		String sql = "insert into game(open_id, game_answer, create_time, game_status, finish_time) values(?, ?, ?, ?, ?)";

		MySQLUtil mysqlUtil = new MySQLUtil();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = mysqlUtil.getConn();
			// ������Ϸ
			ps = conn.prepareStatement(sql);
			ps.setString(1, game.getOpenId());
			ps.setString(2, game.getGameAnswer());
			ps.setString(3, game.getCreateTime());
			ps.setInt(4, game.getGameStatus());
			ps.setString(5, game.getFinishTime());
			ps.executeUpdate();
			// ��ȡ��Ϸ��id
			sql = "select game_id from game where open_id=? and game_answer=? order by game_id desc limit 0,1";
			ps = conn.prepareStatement(sql);
			ps.setString(1, game.getOpenId());
			ps.setString(2, game.getGameAnswer());
			rs = ps.executeQuery();
			if (rs.next()) {
				gameId = rs.getInt("game_id");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// �ͷ���Դ
			mysqlUtil.releaseResources(conn, ps, rs);
		}
		return gameId;
	}

	/**
	 * ��ȡ�û����һ�δ�������Ϸ <br>
	 *
	 * @param request �������
	 * @param openId �û���OpendID
	 * @return
	 */
	public static Game getLastGame(HttpServletRequest request, String openId) {
		Game game = null;
		String sql = "select * from game where open_id=? order by game_id desc limit 0,1";

		MySQLUtil mysqlUtil = new MySQLUtil();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = mysqlUtil.getConn();
			ps = conn.prepareStatement(sql);
			ps.setString(1, openId);
			rs = ps.executeQuery();
			if (rs.next()) {
				game = new Game();
				game.setGameId(rs.getInt("game_id"));
				game.setOpenId(rs.getString("open_id"));
				game.setGameAnswer(rs.getString("game_answer"));
				game.setCreateTime(rs.getString("create_time"));
				game.setGameStatus(rs.getInt("game_status"));
				game.setFinishTime(rs.getString("finish_time"));
			}
		} catch (SQLException e) {
			game = null;
			e.printStackTrace();
		} finally {
			// �ͷ���Դ
			mysqlUtil.releaseResources(conn, ps, rs);
		}
		return game;
	}

	/**
	 * ������Ϸid�޸���Ϸ״̬�����ʱ��
	 *
	 * @param request �������
	 * @param gameId ��Ϸid
	 * @param gameStatus ��Ϸ״̬��0:��Ϸ�� 1:�ɹ� 2:ʧ�� 3:ȡ����
	 * @param finishTime ��Ϸ���ʱ��
	 */
	public static void updateGame(HttpServletRequest request, int gameId, int gameStatus, String finishTime) {
		String sql = "update game set game_status=?, finish_time=? where game_id=?";
		MySQLUtil mysqlUtil = new MySQLUtil();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = mysqlUtil.getConn();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, gameStatus);
			ps.setString(2, finishTime);
			ps.setInt(3, gameId);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// �ͷ���Դ
			mysqlUtil.releaseResources(conn, ps, null);
		}
	}

	/**
	 * ������Ϸ�Ļغ���Ϣ
	 *
	 * @param request �������
	 * @param gameRound ��Ϸ�غ϶���
	 */
	public static void saveGameRound(HttpServletRequest request, GameRound gameRound) {
		String sql = "insert into game_round(game_id, open_id, guess_number, guess_time, guess_result) values (?, ?, ?, ?, ?)";
		MySQLUtil mysqlUtil = new MySQLUtil();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = mysqlUtil.getConn();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, gameRound.getGameId());
			ps.setString(2, gameRound.getOpenId());
			ps.setString(3, gameRound.getGuessNumber());
			ps.setString(4, gameRound.getGuessTime());
			ps.setString(5, gameRound.getGuessResult());
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// �ͷ���Դ
			mysqlUtil.releaseResources(conn, ps, null);
		}
	}

	/**
	 * ������Ϸid��ȡ��Ϸ��ȫ���غ�<br>
	 *
	 * @param request �������
	 * @param gameId ��Ϸid
	 * @return
	 */
	public static List<GameRound> findAllRoundByGameId(HttpServletRequest request, int gameId) {
		List<GameRound> roundList = new ArrayList<GameRound>();
		// ����id��������
		String sql = "select * from game_round where game_id=? order by id asc";
		MySQLUtil mysqlUtil = new MySQLUtil();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = mysqlUtil.getConn();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, gameId);
			rs = ps.executeQuery();
			GameRound round = null;
			while (rs.next()) {
				round = new GameRound();
				round.setGameId(rs.getInt("game_id"));
				round.setOpenId(rs.getString("open_id"));
				round.setGuessNumber(rs.getString("guess_number"));
				round.setGuessTime(rs.getString("guess_time"));
				round.setGuessResult(rs.getString("guess_result"));
				roundList.add(round);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// �ͷ���Դ
			mysqlUtil.releaseResources(conn, ps, rs);
		}
		return roundList;
	}

	/**
	 * ��ȡ�û���ս��
	 *
	 * @param request �������
	 * @param openId �û���OpenID
	 * @return HashMap<Integer, Integer>
	 */
	public static HashMap<Integer, Integer> getScoreByOpenId(HttpServletRequest request, String openId) {
		HashMap<Integer, Integer> scoreMap = new HashMap<Integer, Integer>();
		// ����id��������
		String sql = "select game_status,count(*) from game where open_id=? group by game_status order by game_status asc";
		MySQLUtil mysqlUtil = new MySQLUtil();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = mysqlUtil.getConn();
			ps = conn.prepareStatement(sql);
			ps.setString(1, openId);
			rs = ps.executeQuery();
			while (rs.next()) {
				scoreMap.put(rs.getInt(1), rs.getInt(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// �ͷ���Դ
			mysqlUtil.releaseResources(conn, ps, rs);
		}
		return scoreMap;
	}
	/**
	 * ��ȡ�ʴ�֪ʶ�������м�¼
	 *
	 * @return List<Knowledge>
	 */
	public static List<Knowledge> findAllKnowledge() {
		List<Knowledge> knowledgeList = new ArrayList<Knowledge>();
		String sql = "select * from knowledge";
		MySQLUtil mysqlUtil = new MySQLUtil();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = mysqlUtil.getConn();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				Knowledge knowledge = new Knowledge();
				knowledge.setId(rs.getInt("id"));
				knowledge.setQuestion(rs.getString("question"));
				knowledge.setAnswer(rs.getString("answer"));
				knowledge.setCategory(rs.getInt("category"));
				knowledgeList.add(knowledge);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// �ͷ���Դ
			mysqlUtil.releaseResources(conn, ps, rs);
		}
		return knowledgeList;
	}

	/**
	 * ��ȡ��һ�ε��������
	 *
	 * @param openId �û���OpenID
	 * @return chatCategory
	 */
	public static int getLastCategory(String openId) {
		int chatCategory = -1;
		String sql = "select chat_category from chat_log where open_id=? order by id desc limit 0,1";

		MySQLUtil mysqlUtil = new MySQLUtil();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = mysqlUtil.getConn();
			ps = conn.prepareStatement(sql);
			ps.setString(1, openId);
			rs = ps.executeQuery();
			if (rs.next()) {
				chatCategory = rs.getInt("chat_category");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// �ͷ���Դ
			mysqlUtil.releaseResources(conn, ps, rs);
		}
		return chatCategory;
	}

	/**
	 * ����֪ʶid�����ȡһ����
	 *
	 * @param knowledgeId �ʴ�֪ʶid
	 * @return
	 */
	public static String getKnowledSub(int knowledgeId) {
		String knowledgeAnswer = "";
		String sql = "select answer from knowledge_sub where pid=? order by rand() limit 0,1";

		MySQLUtil mysqlUtil = new MySQLUtil();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = mysqlUtil.getConn();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, knowledgeId);
			rs = ps.executeQuery();
			if (rs.next()) {
				knowledgeAnswer = rs.getString("answer");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// �ͷ���Դ
			mysqlUtil.releaseResources(conn, ps, rs);
		}
		return knowledgeAnswer;
	}

	/**
	 * �����ȡһ��Ц��
	 *
	 * @return String
	 */
	public static String getJoke() {
		String jokeContent = "";
		String sql = "select joke_content from joke order by rand() limit 0,1";

		MySQLUtil mysqlUtil = new MySQLUtil();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = mysqlUtil.getConn();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				jokeContent = rs.getString("joke_content");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// �ͷ���Դ
			mysqlUtil.releaseResources(conn, ps, rs);
		}
		return jokeContent;
	}

	/**
	 * ���������¼
	 *
	 * @param openId �û���OpenID
	 * @param createTime ��Ϣ����ʱ��
	 * @param reqMsg �û����е���Ϣ
	 * @param respMsg �����˺Żظ�����Ϣ
	 * @param chatCategory �������
	 */
	public static void saveChatLog(String openId, String createTime, String reqMsg, String respMsg, int chatCategory) {
		String sql = "insert into chat_log(open_id, create_time, req_msg, resp_msg, chat_category) values(?, ?, ?, ?, ?)";

		MySQLUtil mysqlUtil = new MySQLUtil();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = mysqlUtil.getConn();
			ps = conn.prepareStatement(sql);
			ps.setString(1, openId);
			ps.setString(2, createTime);
			ps.setString(3, reqMsg);
			ps.setString(4, respMsg);
			ps.setInt(5, chatCategory);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// �ͷ���Դ
			mysqlUtil.releaseResources(conn, ps, rs);
		}
	}

}
