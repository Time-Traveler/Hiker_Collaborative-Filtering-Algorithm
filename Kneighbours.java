package Recommend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class Kneighbours {
	public static final int K_VALUE = 5;
	public static int[][] KneighboursIndex = new int[10000][K_VALUE];
	
	public static void findKneighbours(){
		try {
			String line = "";
			String indexline = "";	//用来存储邻居们对应的用户序号
			File file = new File("SimilarityMatrix.txt");
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			File indexfile = new File("SimilarityMatrix_index.txt");
			FileReader indexfr = new FileReader(indexfile);
			BufferedReader indexbr = new BufferedReader(indexfr);
			
			File destfile = new File("Kneighbours.txt");
			if (destfile.exists()) {
				destfile.createNewFile();
			}
			FileWriter fw = new FileWriter(destfile);
			BufferedWriter bw = new BufferedWriter(fw);
		
			int m=0;
			while (br.ready() && indexbr.ready()) {
				line = br.readLine();
				indexline = indexbr.readLine();
				String[] data = line.split("\t");
				String[] indexdata = indexline.split("\t");
				
				String str = "";
				int[] neighbours = getKMaxUsers(data, indexdata);
				if(neighbours != null){
					for (int i = 0; i < neighbours.length; i++) {
						str = str + neighbours[i] + "\t";
					}
				}
				System.out.println(str);
				bw.write(str + "\r\n");
				m++;
				System.out.println("==============="+m);
			}
			System.out.println("size:"+m);
			br.close();
			indexbr.close();
			bw.close();
		}catch(Exception e ){
			e.printStackTrace();
		}
	}
	
	
	public static int[] getKMaxUsers(String[] data, String[] indexdata) {
		if (data == null || data.length < 1) {
			return null;
		}
		double[] array = new double[data.length];
		int[] indexarray = new int[indexdata.length];
		for (int i = 0; i < data.length && i<indexdata.length; i++) {
			if(!data[i].equals("")){
				array[i] = Double.parseDouble(data[i]);
				indexarray[i] = Integer.parseInt(indexdata[i]);
			}
		}
		if(data.length <= K_VALUE){		//K值以内的数据不做筛选	
			return indexarray;
		}
		ArrayList<Integer> maxlist = new ArrayList<Integer>();
		for (int i = 0; i < K_VALUE; i++) {	//找出前K个最大数
			double max = 0;
			int x = -1;
			for (int j = 0; j < array.length; j++) {
				if(array[j] > max && !maxlist.contains(indexarray[j])){
					max = array[j];
					x = j;
				}
			}
			maxlist.add(indexarray[x]);
		}
		if(maxlist.size() == 0){
			return null;
		}
		int[] newarray = new int[K_VALUE];
		for (int i = 0; i < K_VALUE; i++) {
			newarray[i] = maxlist.get(i);
		}
		return newarray;
	}


	public static ArrayList<Integer> computeUnion(ArrayList<Integer> aList,
			ArrayList<Integer> bList) {
		if(aList == null && bList == null){
			return null;
		}
		
		return null;
	}
	
	/**
	 * 初始化二维数组
	 * @param array[][]
	 */
	private static void InvalidArray(int[][] array){
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[i].length; j++) {
				array[i][j] = -1;
			}
		}
	}
}
