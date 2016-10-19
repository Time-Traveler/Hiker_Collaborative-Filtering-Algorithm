package Recommend;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;

public class recommendation {
	public static ArrayList<ArrayList<Integer>> UserList = new ArrayList<ArrayList<Integer>>();

	public static void main(String arg[]) {
		RealityScore();							//�����¼����������ʾ
		Similarity.computeSimilarityMatrix();	//���������û�֮������ƶ�
		Kneighbours.findKneighbours();			//Ϊÿ���û�ѡȡK�������
		
		Date d1 = new Date();
		PredictScore.predict();					//��ÿ���û��Ĵ��Ƽ���Ŀ����Ԥ������
		Date d2 = new Date();
		long duration=d2.getTime()-d1.getTime();
		int hour = (int) (duration/(3600*1000));
		int min = (int) (duration%(3600*1000))/60000;
		int sec = (int) ((duration%(3600*1000))%60000)/1000;
		System.out.println("�������й�����ʱ�䣺"+hour+"Сʱ"+min+"��"+sec+"��");
		
		Evaluation.evaluate();					//���Ƽ������������
	}

	/**
	 * ���û������Ϣת��Ϊ��ʵ���û�����
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
			String userId = "5218791";		//�洢��һ���û���ID���������ǰ��¼
			newsbr.mark((int)newsfile.length());	//��ǣ�����ʹBufferReader�����ļ���ͷ
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
			
			//�����һ���û�������д��ȥ
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
	 * �˺���������newsIdת��Ϊ���Index
	 * @param newsId
	 * @param br
	 * @return
	 */
	private static int getNewsIndex(String newsId, BufferedReader br) {
		try {
			String line = "";
			br.reset();  //���»ص��ı���ͷ
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
