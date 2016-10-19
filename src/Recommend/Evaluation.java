package Recommend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Evaluation {
	
	public static void evaluate(){
		try {
			File file = new File("test_set.txt");	//测试集
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			
			File recfile = new File("RecommendNews.txt");	//推荐结果
			FileReader recfr = new FileReader(recfile);
			BufferedReader recbr = new BufferedReader(recfr);
			
			File newsfile = new File("news.txt");	//新闻库
			FileReader newsfr = new FileReader(newsfile);
			BufferedReader newsbr = new BufferedReader(newsfr);
			
			String line = "";
			String newsId = "";
			int i = 0;
			int m = 0;
			int N = 0;
			newsbr.mark((int) newsfile.length());
			while(recbr.ready()){
				line = recbr.readLine();
				String data[] = line.split("\t");
				
				String testline = br.readLine();
				String testData[] = testline.split("\t");
				
				if(data.length >= 1 && !data[0].equals("")){
					for(int n=0;n<data.length;n++){
						newsbr.reset();
						newsId = getNewsIDbyIndex(data[n], newsbr);
						
						System.out.print(data[n]+"="+newsId+"\t");
						System.out.println(testData[1]+"和"+newsId+"\t");
						if(newsId.equals(testData[1])){
							m++;
						}
					}
					N = N+data.length;
				}
				i++;
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>User"+i+", 命中："+m+"---推荐总项："+N);
			}
			br.close();
			newsbr.close();
			recbr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 通过新闻的标号的新闻的id
	 * @param index
	 * @param newsbr
	 * @return
	 */
	private static String getNewsIDbyIndex(String index, BufferedReader newsbr){
		if(index == null || index.equals("")){
			return null;
		}
		try {
			String newsline = "";
			while(newsbr.ready()){
				newsline = newsbr.readLine();
				String newsData[] = newsline.split("\t");
				if(index.equals(newsData[0])){
					return newsData[1];
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
