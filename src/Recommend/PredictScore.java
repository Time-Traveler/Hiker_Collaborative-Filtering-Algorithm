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
	public static final int RECOM_COUNT =9;	//Ϊÿλ�û��Ƽ����ŵ���Ŀ��

	public static void predict() {
		try {
			String line = "";
			String simline = "";
			String indexline = "";
			
			File file = new File("Kneighbours.txt");	//K-����
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			File simfile = new File("SimilarityMatrix.txt");	//�����Ծ���
			FileReader simfr = new FileReader(simfile);
			BufferedReader simbr = new BufferedReader(simfr);

			File indexfile = new File("SimilarityMatrix_index.txt");
			FileReader indexfr = new FileReader(indexfile);
			BufferedReader indexbr = new BufferedReader(indexfr);

			File scorefile = new File("RealityScore.txt");		//�û����������ű��
			FileReader scorefr = new FileReader(scorefile);
			BufferedReader scorebr = new BufferedReader(scorefr);

			File destfile = new File("RecommendNews.txt");		//��ÿ���û����Ƽ����
			if(destfile.exists()){
				destfile.createNewFile();
			}
			FileWriter fw = new FileWriter(destfile);
			BufferedWriter bw = new BufferedWriter(fw);
			
			File destfile2 = new File("RecommendNews_extent.txt");	//ÿ���Ƽ������Ӧ�ĳ̶�
			if(destfile2.exists()){
				destfile2.createNewFile();
			}
			FileWriter fw2 = new FileWriter(destfile2);
			BufferedWriter bw2 = new BufferedWriter(fw2);
			
			int i = 0;
			scorebr.mark((int)scorefile.length());
			while (br.ready()) {		//��ÿλ�û������Ƽ����
				line = br.readLine();
				simline = simbr.readLine();
				indexline = indexbr.readLine();
				String[] neighbourdata = line.split("\t");
				String[] simdata = simline.split("\t");
				String[] indexdata = indexline.split("\t");

				// ɸѡ����ѡ��
				scorebr.reset();
				ArrayList<Integer> candidatesItem = filterItems(neighbourdata,
						i, scorebr, (int)scorefile.length());
				ArrayList<Double> candidatesScore = new ArrayList<Double>();
				double[] similarity = getSimilarity(neighbourdata, simdata,
						indexdata);

				// ����ÿ����ѡ��Ŀ��Ԥ������
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
						double R_u_i = 1 + A / (double) B;		//���յ�Ԥ���ֵ
						candidatesScore.add(R_u_i);
						System.out.println("User"+i+">��ѡ��: "+j+"/"+candidatesItem.size()+"==== R_u_i:" + R_u_i);
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
			DecimalFormat df = new DecimalFormat("#0.00"); // ������λС��
			if (candidatesItem.size()<=RECOM_COUNT) {	//�����Ƽ���Ŀ�����ݲ���ɸѡ	
				for (int i = 0; i < candidatesScore.size(); i++) {
					recom = recom + candidatesItem.get(i) + "\t";
					recom_extent = recom_extent + df.format(candidatesScore.get(i)) + "\t";
				}
				System.out.println(">>>>>>>>>>>>�Ƽ����ű��>>>>>>>>>>>>>>:"+recom);
				System.out.println(">>>>>>>>>>>>>�Ƽ��̶�>>>>>>>>>>>>>>>>:"+recom_extent);
				bw.write(recom + "\r\n");
				bw2.write(recom_extent + "\r\n");
				return;
			}
			ArrayList<Integer> topIndex = new ArrayList<Integer>();
			for (int i = 0; i < RECOM_COUNT; i++) {		//�ҳ�ǰM�������
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
			System.out.println(">>>>>>>>>>>>�Ƽ����ű��>>>>>>>>>>>>>>:"+recom);
			System.out.println(">>>>>>>>>>>>>�Ƽ��̶�>>>>>>>>>>>>>>>>:"+recom_extent);
			bw.write(recom + "\r\n");
			bw2.write(recom_extent + "\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ɸѡ���õ��Ƽ����ŵļ��ϣ�����������������
	 * 
	 * @param data
	 */
	private static ArrayList<Integer> filterItems(String[] neighbourdata,
			int userIndex, BufferedReader scorebr, int filelength) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		try {
			// ���н��ڵ��û���ȡ����
			scorebr.mark(filelength);
			for (int i = 0; i < neighbourdata.length; i++) {

				// ��ȡ���û����Ķ��б�
				scorebr.reset();
				String line = "";
				int neighbourId = Integer.parseInt(neighbourdata[i]);
				for (int n = 0; n <= neighbourId; n++) { // ��λ�����û��ļ�¼��
					line = scorebr.readLine();
				}
				if (line == null || line.equals("")) {
					return null;
				}
				String[] newsId = line.split("\t");

				// �����ű����ӵ����Ƽ��б���
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

			// Ȼ��ȡ��� �ϲ����б� -�û����Ķ��б�
			scorebr.reset();
			String line = "";
			for (int n = 0; n <= userIndex; n++) { // ��λ�����û��ļ�¼��
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
	 * ��ѯ�û�֮������ƶȣ�����Ԥ�����ּ���
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
	 * ��ѯһ�������Ƿ�ĳ���û������
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
