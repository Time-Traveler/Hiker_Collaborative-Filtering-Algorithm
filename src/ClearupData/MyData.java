package ClearupData;
import java.util.ArrayList;


public class MyData {
	
	public int newID;		//����ID
	public ArrayList<Integer> rows;	//�洢����������ͬ���к�
	
	public MyData(){
		newID = 0;
		rows = new ArrayList<Integer>();
	}

	public int getNewID() {
		return newID;
	}

	public ArrayList<Integer> getRows() {
		return rows;
	}

	public void setRows(ArrayList<Integer> rows) {
		this.rows = rows;
	}

	public void setNewID(int newID) {
		this.newID = newID;
	}
	
	public void addRow(Integer i){
		this.rows.add(i);
	}

}
