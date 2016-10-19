package ClearupData;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class cat {
	public static ArrayList<MyData> arraylist = new ArrayList<MyData>();

	public static void main(String args[]) {
		
//		getUserTable();
//		getViewRecord();
//		test();
//		getNewsTable();
		
	}

	private static void getViewRecord() {
		try {
			File file = new File("train_data.txt");
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			
			File destfile = new File("viewrecord.txt");
			if(destfile.exists()){
				destfile.createNewFile();
			}
			FileWriter fw = new FileWriter(destfile);
			BufferedWriter bw = new BufferedWriter(fw);
			
			File destfile2 = new File("test_set.txt");
			if(destfile2.exists()){
				destfile2.createNewFile();
			}
			FileWriter fw2 = new FileWriter(destfile2);
			BufferedWriter bw2 = new BufferedWriter(fw2);
			
			String line = "";
			String str_box = "";
			int uid_box = -1;
			int i=0;		//读取到的行数
			int j=0;		//写入文件的行数
			while (br.ready()) {
				line = br.readLine();
				String[] data = line.split("\t");
				int uid;
				if(i==0){
					uid = 5218791;
				}else{
					uid = Integer.parseInt(data[0]);
				}
				int newId = Integer.parseInt(data[1]);
				
				if(uid == uid_box && !str_box.equals("")){
					bw.write(str_box);	//回车换行
					j++;
				}
				if(uid != uid_box && !str_box.equals("")){
					bw2.write(str_box);	//回车换行
				}
				uid_box = uid;
				str_box = uid+"\t"+newId+"\t"+data[2]+"\r\n";
				System.out.print(uid+"\t"+newId+"\t"+data[2]+"\r\n");
				i++;
			}
			bw2.write(str_box);	//回车换行
			System.out.println("Record_size: " + i+", "+j);
			bw.close();
			bw2.close();
			br.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void getNewsTable() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		try {
			File file = new File("train_data.txt");
			InputStreamReader isr=new InputStreamReader(new FileInputStream(file),"UTF-8");
			BufferedReader br = new BufferedReader(isr,2);
			File destfile = new File("news.txt");
			if(destfile.exists()){
				destfile.createNewFile();
			}
			FileWriter fw = new FileWriter(destfile);
			BufferedWriter bw = new BufferedWriter(fw);
			String line = "";
			int i=0;
			while (br.ready()) {
				line = br.readLine();
				String[] data = line.split("\t");
				int newId = Integer.parseInt(data[1]);
				boolean exist = false;
				for (int j = 0; j < list.size(); j++) {
					if (newId == list.get(j)) {
						exist = true;
						break;
					}
				}
				if (exist) {
					continue;
				} else {
					list.add(newId); 	// 记录新的新闻
					bw.write(i+"\t"+newId+"\t"+data[3]+"\t"+data[4]+"\t"+ data[5]+"\t"
							+ arraylist.get(i).getRows().size()+"\r\n");	//回车换行
					System.out.println(newId + "\t"+i);
				}
				i++;
			}
			System.out.println("News_size: " + list.size());
			bw.close();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void getUserTable() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		try {
			File file = new File("train_data.txt");
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			File destfile = new File("user.txt");
			if(destfile.exists()){
				destfile.createNewFile();
			}
			FileWriter fw = new FileWriter(destfile);
			BufferedWriter bw = new BufferedWriter(fw);
			String line = "";
			line = br.readLine();
			int i=0;
			while (br.ready()) {
				line = br.readLine();
				String[] data = line.split("\t");
				int d = Integer.parseInt(data[0]);
				boolean exist = false;
				for (int j = 0; j < list.size(); j++) {
					if (d == list.get(j)) {
						exist = true;
						break;
					}
				}
				if (exist) {
					continue;
				} else {
					list.add(d); // 记录新的用户
					bw.write(i+"\t"+d+"\r\n");	//回车换行
					System.out.println(d + "");
				}
				i++;
			}
			System.out.println("User_size: " + list.size());
			bw.close();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void test() {
		

		try {
			int i = 1;
			File file = new File("train_data.txt");
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = "";

			while (br.ready()) {
				i++;
				line = br.readLine();
				String[] data = line.split("\t");
				int d = Integer.parseInt(data[1]);
				boolean exist = false;
				for (int j = 0; j < arraylist.size(); j++) {
					if (d == arraylist.get(j).getNewID()) {
						arraylist.get(j).addRow(i);
						exist = true;
						break;
					}
				}
				//System.out.println("while循环次数：" + i + "\n");

				if (exist) {
					continue;
				} else {
					MyData ms = new MyData();
					ms.newID = d;
					ms.addRow(i);
					arraylist.add(ms);
				}
			}
			System.out.println("size: " + arraylist.size());
//			System.out.println("\n==============================================\n");
//			for (int n = 0; n < arraylist.size(); n++) {
//				System.out.print("id:" + arraylist.get(n).newID);
//				for (int m = 0; m < arraylist.get(n).getRows().size(); m++) {
//					System.out.print("\t" + arraylist.get(n).getRows().get(m));
//				}
//				System.out.println("\n");
//			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
