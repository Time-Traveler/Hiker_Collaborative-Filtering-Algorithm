package Recommend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Similarity {
	public static ArrayList<ArrayList<Integer>> UserList = new ArrayList<ArrayList<Integer>>();

	public static void computeSimilarityMatrix() {
		try {
			// 将量化的用户评分数据读取到内存
			String line = "";
			DecimalFormat df = new DecimalFormat("#0.00"); // 保留两位小数
			
			File file = new File("RealityScore.txt");
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			File destfile = new File("SimilarityMatrix.txt");
			if (destfile.exists()) {
				destfile.createNewFile();
			}
			FileWriter fw = new FileWriter(destfile);
			BufferedWriter bw = new BufferedWriter(fw);

			File destfile2 = new File("SimilarityMatrix_index.txt");
			if (destfile2.exists()) {
				destfile2.createNewFile();
			}
			FileWriter fw2 = new FileWriter(destfile2);
			BufferedWriter bw2 = new BufferedWriter(fw2);

			while (br.ready()) {
				line = br.readLine();
				String[] data = line.split("\t");

				ArrayList<Integer> newsViewedList = new ArrayList<Integer>();
				for (int n = 0; n < data.length; n++) {
					newsViewedList.add(Integer.parseInt(data[n]));
				}
				UserList.add(newsViewedList);
			}
			br.close();
			System.out.println("RealityScore Read over...!"+UserList.size());

			for (int i = 0; i < UserList.size(); i++) {
				String str = "";
				String index = "";
				ArrayList<Integer> aList = UserList.get(i);
				for (int j = 0; j < UserList.size(); j++) {
					if(i == j){
						continue;
					}
					ArrayList<Integer> bList = UserList.get(j);
					int ic = computeIntersectionCount(aList, bList);
					if (ic != 0) {
						double cos = ic
								/ (double) (aList.size() + bList.size());
						str = str + df.format(cos) + "\t";
						index = index + j + "\t";
					}
				}
				bw.write(str + "\r\n");
				bw2.write(index + "\r\n");
				System.out.println(str+"\n"+index);
				System.out.println("============================="+(i+1) + "row over");
			}
			System.out.println("Finished!");
			bw.close();
			bw2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 计算任意两个集合间的交集的元素个数
	 */
	private static int computeIntersectionCount(ArrayList<Integer> aList,
			ArrayList<Integer> bList) {
		int m = 0;
		for (int i = 0; i < aList.size(); i++) {
			for (int j = 0; j < bList.size(); j++) {
				if (aList.get(i) == bList.get(j)) {
					m++;
				}
			}
		}
		return m;
	}
}
