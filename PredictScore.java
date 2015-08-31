package Recommend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class PredictScore {
	public static final int RECOM_COUNT =9;	//为每位用户推荐新闻的条目数

	public static void predict() {
		try {
			String line = "";
			String simline = "";
			String indexline = "";
			
			File file = new File("Kneighbours.txt");	//K-近邻
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			File simfile = new File("SimilarityMatrix.txt");	//相似性矩阵
			FileReader simfr = new FileReader(simfile);
			BufferedReader simbr = new BufferedReader(simfr);

			File indexfile = new File("SimilarityMatrix_index.txt");
			FileReader indexfr = new FileReader(indexfile);
			BufferedReader indexbr = new BufferedReader(indexfr);

			File scorefile = new File("RealityScore.txt");		//用户看过的新闻表格
			FileReader scorefr = new FileReader(scorefile);
			BufferedReader scorebr = new BufferedReader(scorefr);

			File destfile = new File("RecommendNews.txt");		//对每个用户的推荐结果
			if(destfile.exists()){
				destfile.createNewFile();
			}
			FileWriter fw = new FileWriter(destfile);
			BufferedWriter bw = new BufferedWriter(fw);
			
			File destfile2 = new File("RecommendNews_extent.txt");	//每条推荐结果对应的程度
			if(destfile2.exists()){
				destfile2.createNewFile();
			}
			FileWriter fw2 = new FileWriter(destfile2);
			BufferedWriter bw2 = new BufferedWriter(fw2);
			
			int i = 0;
			scorebr.mark((int)scorefile.length());
			while (br.ready()) {		//对每位用户计算推荐结果
				line = br.readLine();
				simline = simbr.readLine();
				indexline = indexbr.readLine();
				String[] neighbourdata = line.split("\t");
				String[] simdata = simline.split("\t");
				String[] indexdata = indexline.split("\t");

				// 筛选出候选集
				scorebr.reset();
				ArrayList<Integer> candidatesItem = filterItems(neighbourdata,
						i, scorebr, (int)scorefile.length());
				ArrayList<Double> candidatesScore = new ArrayList<Double>();
				double[] similarity = getSimilarity(neighbourdata, simdata,
						indexdata);

				// 计算每个候选项目的预测评分
				if(candidatesItem == null || candidatesItem.size() < 1){
					System.out.println("Empty");
				}else{
					for (int j = 0; j < candidatesItem.size(); j++) {
						double A = 0;
						double B = 0;
						for (int k = 0; k < neighbourdata.length; k++) {
							scorebr.reset();
							A = A + similarity[k]
									* (isItemViewedByUser(
											Integer.parseInt(neighbourdata[k]),
											candidatesItem.get(j), scorebr, (int)scorefile.length()) - 1);
							B += similarity[k];
							//System.out.println("A:" + A + "\tB:" + B);
						}
						double R_u_i = 1 + A / (double) B;		//最终的预测分值
						candidatesScore.add(R_u_i);
						System.out.println("User"+i+">候选项: "+j+"/"+candidatesItem.size()+"==== R_u_i:" + R_u_i);
					}
				}
				
				System.out.println(i+"=====================================================");
				getTopRecommendations(candidatesScore, candidatesItem, bw, bw2);
				scorebr.reset();
				i++;
			}

			br.close();
			simbr.close();
			indexbr.close();
			scorebr.close();
			bw.close();
			bw2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void getTopRecommendations(ArrayList<Double> candidatesScore, 
			ArrayList<Integer> candidatesItem, BufferedWriter bw, BufferedWriter bw2){
		try {
			String recom = "";
			String recom_extent = "";
			DecimalFormat df = new DecimalFormat("#0.00"); // 保留两位小数
			if (candidatesItem.size()<=RECOM_COUNT) {	//不够推荐数目的数据不做筛选	
				for (int i = 0; i < candidatesScore.size(); i++) {
					recom = recom + candidatesItem.get(i) + "\t";
					recom_extent = recom_extent + df.format(candidatesScore.get(i)) + "\t";
				}
				System.out.println(">>>>>>>>>>>>推荐新闻编号>>>>>>>>>>>>>>:"+recom);
				System.out.println(">>>>>>>>>>>>>推荐程度>>>>>>>>>>>>>>>>:"+recom_extent);
				bw.write(recom + "\r\n");
				bw2.write(recom_extent + "\r\n");
				return;
			}
			ArrayList<Integer> topIndex = new ArrayList<Integer>();
			for (int i = 0; i < RECOM_COUNT; i++) {		//找出前M个最大数
				double max = 0;
				int x = -1;
				for (int j = 0; j < candidatesScore.size(); j++) {
					if(candidatesScore.get(j) > max && !topIndex.contains(j)){
						max = candidatesScore.get(j);
						x = j;
					}
				}
				if(x != -1){
					topIndex.add(x);
				}
			}
			for (int i = 0; i < topIndex.size(); i++) {
				recom = recom + candidatesItem.get(topIndex.get(i)) + "\t";
				recom_extent = recom_extent + df.format(candidatesScore.get(topIndex.get(i))) + "\t";
			}
			System.out.println(">>>>>>>>>>>>推荐新闻编号>>>>>>>>>>>>>>:"+recom);
			System.out.println(">>>>>>>>>>>>>推荐程度>>>>>>>>>>>>>>>>:"+recom_extent);
			bw.write(recom + "\r\n");
			bw2.write(recom_extent + "\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 筛选备用的推荐新闻的集合，本质是做并集运算
	 * 
	 * @param data
	 */
	private static ArrayList<Integer> filterItems(String[] neighbourdata,
			int userIndex, BufferedReader scorebr, int filelength) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		try {
			// 所有近邻的用户先取并集
			scorebr.mark(filelength);
			for (int i = 0; i < neighbourdata.length; i++) {

				// 读取该用户的阅读列表
				scorebr.reset();
				String line = "";
				int neighbourId = Integer.parseInt(neighbourdata[i]);
				for (int n = 0; n <= neighbourId; n++) { // 定位到该用户的记录行
					line = scorebr.readLine();
				}
				if (line == null || line.equals("")) {
					return null;
				}
				String[] newsId = line.split("\t");

				// 把新闻标号添加到待推荐列表中
				for (int j = 0; j < newsId.length; j++) {
					boolean exist = false;
					for (int k = 0; k < list.size(); k++) {
						if (Integer.parseInt(newsId[j]) == list.get(k)) {
							exist = true;
							break;
						}
					}
					if (!exist) {
						list.add(Integer.parseInt(newsId[j]));
					}
				}
			}

			// 然后取差集： 合并的列表 -用户已阅读列表
			scorebr.reset();
			String line = "";
			for (int n = 0; n <= userIndex; n++) { // 定位到该用户的记录行
				line = scorebr.readLine();
			}
			String[] newsId = line.split("\t");

			for (int i = 0; i < newsId.length; i++) {
				int newsIndex = Integer.parseInt(newsId[i]);
				for (int j = 0; j < list.size(); j++) {
					if (newsIndex == list.get(j)) {
						list.remove(j);
					}
				}
			}
//			scorebr.reset();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}

	/**
	 * 查询用户之间的相似度，用作预测评分计算
	 * 
	 * @param neighbourdata
	 * @param simdata
	 * @param indexdata
	 * @return
	 */
	private static double[] getSimilarity(String[] neighbourdata,
			String[] simdata, String[] indexdata) {
		double[] similarity = new double[neighbourdata.length];
		for (int i = 0; i < neighbourdata.length; i++) {
			int x = -1;
			for (int j = 0; j < indexdata.length; j++) {
				if (neighbourdata[i].equals(indexdata[j])) {
					x = j;
					break;
				}
			}
			if (x == -1) {
				similarity[i] = 0;
			} else {
				similarity[i] = Double.parseDouble(simdata[x]);
			}
			// System.out.print(x + "\t");
		}
		System.out.println();
		return similarity;
	}

	/**
	 * 查询一条新闻是否被某个用户浏览过
	 * 
	 * @param userIndex
	 * @param newsIndex
	 * @param scorebr
	 * @return
	 */
	private static int isItemViewedByUser(int userIndex, int newsIndex,
			BufferedReader scorebr, int filelength) {
		try {
			String line = "";
			scorebr.mark(filelength);
			for (int i = 0; i <= userIndex; i++) {
				line = scorebr.readLine();
			}
			if(line == null || line.equals("")){
				return 0;
			}
			String data[] = line.split("\t");
			for (int i = 0; i < data.length; i++) {
				if (data[i].equals(newsIndex + "")) {
					return 1;
				}
			}
			scorebr.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
