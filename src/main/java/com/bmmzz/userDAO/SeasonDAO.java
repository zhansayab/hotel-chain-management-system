package com.bmmzz.userDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import com.bmmzz.userDAO.struct.Hotels;
import com.bmmzz.userDAO.struct.SeasonInfo;
import com.bmmzz.userDAO.struct.SeasonPriceInfo;
import com.google.gson.Gson;

public class SeasonDAO {

	private SeasonDAO() {}
	
	public static String getAllSeasons(String auth) {
		Gson gson = new Gson();
		String json = "";
		SeasonInfo seasons = new SeasonInfo();
		
		try {
			ResultSet resultSet =  UserDAO.executeQuery("Select * From mydb.time_period, mydb.hotel h Where h.hotelid='" + EmployeeDAO.getHotelID(auth) + "' group by SeasonName");
			while(resultSet.next()) {
				seasons.add(resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), EmployeeDAO.getHotelID(auth), resultSet.getString(6));
			}
			json = gson.toJson(seasons, SeasonInfo.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	public static String getSeasonPrice(String auth, String seasonName) {
		Gson gson = new Gson();
		String json = "";
		SeasonPriceInfo seasonPrice = new SeasonPriceInfo();
		
		try {
			ResultSet resultSet =  UserDAO.executeQuery("Select * From mydb.initial_price Where hotelid='" + EmployeeDAO.getHotelID(auth) + "' and seasonname='" + seasonName + "'");
			while(resultSet.next()) {
				seasonPrice.add(resultSet.getString(3), resultSet.getString(4), resultSet.getString(1), resultSet.getInt(5));
			}
			json = gson.toJson(seasonPrice, SeasonPriceInfo.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	public static void deleteSeason(String auth, String seasonName, String start, String end) {
		try {
			UserDAO.executeUpdate("Delete from mydb.operates_during Where seasonname='" + seasonName + "' and hotelid='" + EmployeeDAO.getHotelID(auth) + "'");
			UserDAO.executeUpdate("Delete from mydb.initial_price Where seasonname='" + seasonName + "' and hotelid='" + EmployeeDAO.getHotelID(auth) + "'");
			ResultSet result = UserDAO.executeQuery("Select * From mydb.operates_during Where seasonname='" + seasonName + "' and startdate='" + start + "' and enddate='" + end + "'");
			if (!result.next()) {
				UserDAO.executeUpdate("Delete from mydb.time_period Where seasonname='" + seasonName + "' and startdate='" + start + "' and enddate='" + end + "'");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
