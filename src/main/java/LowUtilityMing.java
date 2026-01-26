import com.google.common.base.Joiner;

import java.io.*;
import java.sql.Array;
import java.util.*;
import java.util.stream.Collectors;

import static jdk.nashorn.internal.codegen.OptimisticTypesPersistence.store;

public class LowUtilityMing {
    Set<int[]> preSet=new HashSet<>();
    Generator3 generator;
    int max_utility;
    BufferedWriter writer=null;
    long runtime ;
    int patternCount;
    long candidatesCount;


    public void runAlgorithm(String input, int max_utility,String output) throws IOException {
        this.max_utility=max_utility;
        long startTime=System.currentTimeMillis();
        loadFile(input);
        String delPrex=null;
//        res is MaxDisConSet
        List<int[]> res=genContain(input);
        writer = new BufferedWriter(new FileWriter(output));
        for (int i = 0; i < res.size(); i++) {
           LUM(delPrex, res.get(i));
           preSet.add(res.get(i));
        }
        runtime=System.currentTimeMillis()-startTime;
        writer.close();
    }

    public void LUM(String delPre,int[] itemset) throws IOException {
        for (int[] maxItemset:preSet) {
            List<Integer> l1 = Arrays.stream(maxItemset).boxed().collect(Collectors.toList());
            List<Integer> l2 = Arrays.stream(itemset).boxed().collect(Collectors.toList());
            if (l1.containsAll(l2)){
                return;
            }
        }
        candidatesCount++;
        ULB ulb=generator.getUBofItemset(itemset);
        // obtain the utility of the current itemset
        int uOfRoot=ulb.getUofItemset();
        if (uOfRoot<=max_utility&&uOfRoot>0) {
            myprint(itemset,uOfRoot);
            patternCount++;
        }
        // no need to consider extensions of the itemset
        if (itemset.length==1){
            return;
        }
        int lbOfExD;
        if (delPre!=null){
            String[] delPreArr2=delPre.split("&");
            int lastItem=Integer.valueOf(delPreArr2[delPreArr2.length-1]);
            // obtain the lower bound for depth pruning; if it exceeds maxUtil, no extension is considered
            // compute the leaf shrinkage of the itemset, i.e., the sum of utilities of items preceding the current item
            lbOfExD=ulb.getLBofExD(lastItem);
        }else {
            // obtain the lower bound for depth pruning; if it exceeds maxUtil, no further extension is considered
            lbOfExD=ulb.getMinUOfItem();
        }
        MemoryLogger.getInstance().checkMemory();
        // consider possible extensions
        // perform depth pruning
        if (lbOfExD<=max_utility){
            if (delPre!=null){
                // only elements greater than lastItem can be removed
                String[] delPreArr=delPre.split("&");
                int lastItem=Integer.valueOf(delPreArr[delPreArr.length-1]);
                for (int i = 0; i < itemset.length; i++) {
                    if (itemset[i]<=lastItem){
                        continue;
                    }
                    // obtain the lower bound for width pruning; if it exceeds maxUtil, the itemset and its extensions are pruned
                    int lbOfExB=ulb.getLBofExW(itemset[i]);
                    int[] extendsion=delOneOfItemset(itemset,i,itemset[i]);
                    if (lbOfExB<=max_utility){
                        if (extendsion.length>0){
                           // mycheck(delPre+"&"+itemset[i],extendsion,exteFile);
                            LUM(delPre+"&"+itemset[i],extendsion);
                        }

                    }
                }
            }else //delpre==null
            {
                for (int i = 0; i < itemset.length; i++) {
                    // obtain the lower bound for width pruning; if it exceeds maxUtil, both the current itemset and its extensions are pruned
                    int lbOfExB=ulb.getLBofExW(itemset[i]);
                    int[] extendsion=delOneOfItemset(itemset,i,itemset[i]);
                    if (lbOfExB<=max_utility){
                        if (extendsion.length>0){
                          //  mycheck(String.valueOf(itemset[i]),extendsion,exteFile);
                            LUM(String.valueOf(itemset[i]),extendsion);
                        }
                    }
                }
            }
        }

    }

    private void myprint(int[] itemset, int uOfRoot) throws IOException {
        StringBuffer buffer=new StringBuffer();
        for (int i = 0; i < itemset.length; i++) {
            if (i==itemset.length-1){
               // System.out.print(itemset[i]+":");
                buffer.append(itemset[i]+":");
            }else {
               // System.out.print(itemset[i]+" ");
                buffer.append(itemset[i]+" ");
            }
        }
        //System.out.println(uOfRoot);
        buffer.append(uOfRoot);
        writer.write(buffer.toString());
        writer.newLine();
        writer.flush();
    }


    private int[] delOneOfItemset(int[] itemset,int index, int item) {

        int[] res=new int[itemset.length-1];
        if (itemset[index]==item){
        //int i = 0, j = 0;
        for (int i = 0, j = 0; i < itemset.length&&j < res.length; i++) {
            if (i==index){
                continue;
            }else {
                res[j++]=itemset[i];
            }
        }}else {
            System.out.println("del item error");
        }
        return res;
    }

    public void printStats(List<Double> runTimelist,List<Double> memorylist,List<Long> candidateslist,List<Integer> patternlist) {

        runTimelist.add((double)runtime/1000);
        memorylist.add(MemoryLogger.getInstance().getMaxMemory());
        candidateslist.add(candidatesCount);
        patternlist.add(patternCount);

    }

    public void loadFile(String path) throws IOException {
        Set<Integer> setOfItems = new TreeSet<>();
        Map<Integer, String[]> mapTidToItems = new LinkedHashMap<>();
        Map<Integer, String[]> mapTidToUtilities = new LinkedHashMap<>();
        int tidCount = 0;
        String thisLine;
        BufferedReader myInput = null;

        try {
            myInput = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));

            while ((thisLine = myInput.readLine()) != null) {
                // Skip empty lines or comments
                if (thisLine.isEmpty() ||
                        thisLine.charAt(0) == '#' ||
                        thisLine.charAt(0) == '%' ||
                        thisLine.charAt(0) == '@') {
                    continue;
                }

                tidCount++;
                String[] partions = thisLine.split(":");

                // Check if the line format conforms to the three-part structure
                if (partions.length < 3) {
                    System.err.println("⚠ [警告] 第 " + tidCount + " 行格式异常（缺少冒号）: " + thisLine);
                    continue;
                }

                String[] items = partions[0].trim().split("\\s+");
                String[] utilities = partions[2].trim().split("\\s+");

                // Check if the number of items matches the number of utilities
                if (items.length != utilities.length) {
                    System.err.println("⚠ [警告] 第 " + tidCount + " 行 item 数量("
                            + items.length + ") 与 utility 数量("
                            + utilities.length + ") 不匹配，已跳过。");
                    continue;
                }

                // Parse the item and add it to the global set
                for (String itemStr : items) {
                    try {
                        int item = Integer.parseInt(itemStr);
                        setOfItems.add(item);
                    } catch (NumberFormatException nfe) {
                        System.err.println("⚠ [警告] 第 " + tidCount + " 行存在非法 item 值: " + itemStr);
                        continue;
                    }
                }

                // Store in the map
                mapTidToItems.put(tidCount, items);
                mapTidToUtilities.put(tidCount, utilities);
            }

        } catch (Exception e) {
            System.err.println("❌ [Error] An exception occurred while reading the file: "
                    + e.getMessage());
            e.printStackTrace();
        } finally {
            if (myInput != null) {
                myInput.close();
            }
        }
        generator = new Generator3(setOfItems, tidCount);
        generator.setGenerator(mapTidToItems, mapTidToUtilities);
    }


    List<int[]> genContain(String input) throws IOException {
        List<List<Integer>> trans =new LinkedList<>();
        Set<Integer> setOfItems=new TreeSet<>();

        String thisLine;
        BufferedReader myInput = null;
        try {
            myInput = new BufferedReader(new InputStreamReader(new FileInputStream(input)));
            // for each transaction (line) in the input file
            while ((thisLine = myInput.readLine()) != null) {
                // if the line is  a comment, is  empty or is a
                // kind of metadata
                if (thisLine.isEmpty() == true ||
                        thisLine.charAt(0) == '#' || thisLine.charAt(0) == '%'
                        || thisLine.charAt(0) == '@') {
                    continue;
                }
                String[] partions = thisLine.split(":");
                // int transactionUtility = Integer.parseInt(partions[1]);

                String[] items = partions[0].split(" ");
                List<Integer> tranOne=new ArrayList<>();
                for (int i = 0; i < items.length; i++) {
                    tranOne.add(Integer.valueOf(items[i]));
                }
//                int[] itemsInt=new int[items.length];
//                for (int i = 0; i < items.length; i++) {
//                    itemsInt[i]=Integer.valueOf(items[i]);
//                }
                trans.add(tranOne);
            }
//            for (List<Integer> value : trans) {
//                System.out.println("value的值");
//                System.out.println(value);
//            }
        }  catch (Exception e) {
            // catch exceptions
            e.printStackTrace();
        }finally {
            if(myInput != null){
                // close the file
                myInput.close();
            }
        }
        Collections.sort(trans, new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> o1, List<Integer> o2) {
                return o1.size()-o2.size();
            }
        });
        //去重
        Set<String> stringSet=new LinkedHashSet<>();
        for (int i = 0; i < trans.size(); i++) {
            List<Integer> temp=trans.get(i);
            stringSet.add(Joiner.on("&").join(temp));
        }
        trans.clear();
        List<int[]> res=new ArrayList<>();
        List<String> stringList=new ArrayList<>(stringSet);
        while (!stringList.isEmpty()){
            for (int i = stringList.size()-2; i >= 0 ; i--) {
                String[] items=stringList.get(stringList.size()-1).split("&");
                List<Integer> itemsList1=Arrays.stream(Arrays.stream(items).mapToInt(Integer::parseInt).toArray()).boxed().collect(Collectors.toList());
                String[] items2=stringList.get(i).split("&");
                List<Integer> itemsList2=Arrays.stream(Arrays.stream(items2).mapToInt(Integer::parseInt).toArray()).boxed().collect(Collectors.toList());
                if (itemsList1.containsAll(itemsList2)){
                    stringList.remove(i);
                    //i++;
                }
            }
            res.add(Arrays.stream(stringList.get(stringList.size()-1).split("&")).mapToInt(Integer::parseInt).toArray());
            stringList.remove(stringList.size()-1);
        }
        return  res;
    }

}
