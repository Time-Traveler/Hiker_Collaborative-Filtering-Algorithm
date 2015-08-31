package Recommend;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;

public class recommendation {
	public static ArrayList<ArrayList<Integer>> UserList = new 

ArrayList<ArrayList<Integer>>();

	public static void main(String arg[]) {
		RealityScore();							//浏览记录

进行量化表示
		Similarity.computeSimilarityMatrix();	//计算两两用户之间的相似度
		Kneighbours.findKneighbours();			//为每个用户选取K个最近邻
		
		Date d1 = new Date();
		PredictScore.predict();					//对每个用户的待推

荐项目进行预测评分
		Date d2 = new Date();
		long duration=d2.getTime()-d1.getTime();
		int hour = (int) (duration/(3600*1000));
		int min = (int) (duration%(3600*1000))/60000;
		int sec = (int) ((duration%(3600*1000))%60000)/1000;
		System.out.println("程序运行共花费时间："+hour+"小时"+min+"分"+sec+"秒");
		
		Evaluation.evaluate();					//对推荐结果进行评

估
	}

	/**
	 * 将用户浏览信息转换为现实的用户评分
	 */
	private static void RealityScore() {
		try {
			String line = "";
			File file = new File("viewrecord.txt");
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			
			File newsfile = new File("news.txt");
			FileReader newsfr = new FileReader(newsfile);
			BufferedReader newsbr = new BufferedReader(newsfr);

			File destfile = new File("RealityScore.txt");
			if(destfile.exists()){
				destfile.createNewFile();
			}
			FileWriter fw = new FileWriter(destfile);
			BufferedWriter bw = new BufferedWriter(fw);
			
			int i=0;
			String userId = "5218791";		//存储上一个用户的ID，用来

辨别当前记录
			newsbr.mark((int)newsfile.length());	//标记，用来使BufferReader

返回文件开头
			ArrayList<Integer> newsViewedList = new ArrayList<Integer>();
			while (br.ready()) {
				line = br.readLine();
				String[] data = line.split("\t");
				if(!data[0].equals(userId) && newsViewedList.size() > 0){
					userId = data[0];
					UserList.add(newsViewedList);
					for(int j=0; j<newsViewedList.size(); j++){
						bw.write(newsViewedList.get(j)+ "\t");
					}
					bw.write("\r\n");
					newsViewedList.clear();
				}
				newsViewedList.add(getNewsIndex(data[1], newsbr));
				System.out.println(i);
				i++;
			}
			
			//把最后一个用户的评分写进去
			for(int j=0; j<newsViewedList.size(); j++){
				bw.write(newsViewedList.get(j)+ "\t");
			}
			bw.write("\r\n");
			
			System.out.println(UserList.size()+"\n"+i);
			br.close();
			bw.close();
			newsbr.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 此函数用来将newsId转换为序号Index
	 * @param newsId
	 * @param br
	 * @return
	 */
	private static int getNewsIndex(String newsId, BufferedReader br) {
		try {
			String line = "";
			br.reset();  //重新回到文本开头
			while (br.ready()) {
				line = br.readLine();
				String[] data = line.split("\t");
				if(data[1].equals(newsId)){
					return Integer.parseInt(data[0]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
}
