package com.app.swagse.model.category;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class CategoryResponse{

	@SerializedName("category")
	private List<CategoryItem> category;

	@SerializedName("status")
	private String status;

	public void setCategory(List<CategoryItem> category){
		this.category = category;
	}

	public List<CategoryItem> getCategory(){
		return category;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"CategoryResponse{" + 
			"category = '" + category + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}