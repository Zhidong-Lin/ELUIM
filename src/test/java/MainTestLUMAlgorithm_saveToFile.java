import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainTestLUMAlgorithm_saveToFile {

	static List<Double> runTime=new ArrayList<>();
	static List<Double> memory=new ArrayList<>();
	static List<Long> candidates=new ArrayList<>();
	static List<Integer> pattern=new ArrayList<>();

	public static void main(String [] arg) throws IOException{
		String input1 ="BMSPHM.txt";
		String input2 ="ChessPHM.txt";
		String input3 = "FoodmartPHM.txt";
		String input4 = "MushroomPHM.txt";
		String input5 = "RetailPHM.txt";
        String input6 = "chainstore_500k.txt";
//		String input6 = "accidentsPHM.txt";
		String input7 = "example.txt";
//		String input7="BMSPHM_1W.txt";
		String input8 = "ecommerce.txt";
		String finalInput=input4;

		String input = fileToPath(finalInput);
//		String output = ".//LUIM+//outputoldecommerce.txt";
        String output = ".//example//outputoldecommerceboth.txt";
//        String output = ".//LUIM++//outputoldChessPHM_both_both_new_suchain2_load4_12_12.txt";
//        String output = ".//outputoldChessPHM.txt";
		int[] max_utility = new int[]{80};
		for (int i = 0; i < max_utility.length; i++) {
			MemoryLogger.getInstance().reset();
            ELUIM_preprocess lowUtilityMing = new ELUIM_preprocess();
//            LowUtilityMing_strategy1 lowUtilityMing = new LowUtilityMing_strategy1();
//            LowUtilityMing_strategy2 lowUtilityMing = new LowUtilityMing_strategy2();
//            LowUtilityMing_preprocess_strategy1 lowUtilityMing = new LowUtilityMing_preprocess_strategy1();
//            LowUtilityMing_preprocess_strategy2 lowUtilityMing = new LowUtilityMing_preprocess_strategy2();
//            LowUtilityMing_preprocess_strategy1_strategy2 lowUtilityMing = new LowUtilityMing_preprocess_strategy1_strategy2();
//            LowUtilityMing lowUtilityMing = new LowUtilityMing();
			MemoryLogger.getInstance().checkMemory();
			lowUtilityMing.runAlgorithm(input, max_utility[i], output);
			MemoryLogger.getInstance().checkMemory();
			lowUtilityMing.printStats(runTime,memory,candidates,pattern);
		}
		OutputExp(max_utility,finalInput+"max_"+max_utility[0]);
	}
	private static void OutputExp(int[] max_utility, String input) throws IOException {
		System.out.println("输出input:"+input);
//        String experimentFile = ".//LUIM+//output_preprocess"+max_utility[0]+input;
//        String experimentFile = ".//LUIM+//output_preprocess_strategy1"+max_utility[0]+input;
//          String experimentFile = ".//LUIM+//output_preprocess_strategy1"+max_utility[0]+input;
//          dString experimentFile = ".//LUIM+//output_preprocess_strategy1_strategy2"+max_utility[0]+input;
        String experimentFile = ".//example//output_preprocess"+max_utility[0]+input;
//        String experimentFile = ".//LUIM+//output_both_both_new_suchain2"+max_utility[0]+input;
//        String experimentFile = ".//output"+max_utility[0]+input;
//        String experimentFile = ".//testexpnaive"+input;
        System.out.println("输出experimentFile:"+experimentFile);
		BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(experimentFile));
		bufferedWriter.write("maxUtil: ");
		for (int i = 0; i < max_utility.length; i++) {
			if (i==max_utility.length-1){
				bufferedWriter.write(max_utility[i]+"");
			}else {
				bufferedWriter.write(max_utility[i]+",");
			}
		}
		bufferedWriter.newLine();
		bufferedWriter.write("runTime: ");
		for (int i = 0; i < max_utility.length; i++) {
			if (i==max_utility.length-1){
				bufferedWriter.write(runTime.get(i)+"");
			}else {
				bufferedWriter.write(runTime.get(i)+",");
			}
		}
		bufferedWriter.newLine();
		bufferedWriter.write("memory: ");
		for (int i = 0; i < max_utility.length; i++) {
			if (i==max_utility.length-1){
				bufferedWriter.write(memory.get(i)+"");
			}else {
				bufferedWriter.write(memory.get(i)+",");
			}

		}
		bufferedWriter.newLine();
		bufferedWriter.write("candidates: ");
		for (int i = 0; i < max_utility.length; i++) {
			if (i==max_utility.length-1){
				bufferedWriter.write(candidates.get(i)+"");
			}else {
				bufferedWriter.write(candidates.get(i)+",");
			}

		}
		bufferedWriter.newLine();
		bufferedWriter.write("patterns: ");
		for (int i = 0; i < max_utility.length; i++) {
			if (i==max_utility.length-1){
				bufferedWriter.write(pattern.get(i)+"");
			}else {
				bufferedWriter.write(pattern.get(i)+",");
			}

		}
		bufferedWriter.flush();
		bufferedWriter.close();
	}

	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestLUMAlgorithm_saveToFile.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}

}
