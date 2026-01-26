import com.google.common.base.Joiner;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class ELUIM_strategy1_strategy2 {
private final Set<Long> preSet = new HashSet<>();   // 指纹集合
    Generator3 generator;
    int max_utility;
    BufferedWriter writer=null;
    long runtime ;
    int patternCount;
    long candidatesCount;

    private static long finger64(int[] a) {
        long h = 0x345678L;
        long GOLDEN_RATIO = 0x9E3779B97F4A7C15L;

        for (int v : a) {
            long x = v & 0xFFFFFFFFL;
            h ^= x + (h << 6) + (h >> 2);
            h *= GOLDEN_RATIO;
        }

        h ^= (h >> 33);
        h *= GOLDEN_RATIO;
        h ^= (h >> 29);

        return h;
    }
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

            preSet.add(finger64(res.get(i)));
        }
        runtime=System.currentTimeMillis()-startTime;
        writer.close();
    }

    public void LUM(String delPre,int[] itemset) throws IOException {
        long key = finger64(itemset);
        if (!preSet.add(key)) {
            return; // has processed, return
        }
        Pattern pattern;
        if(delPre==null){
            pattern = generator.getPattern(itemset);
        }
        else{
            String[] delPreArr=delPre.split("&");
            int lastItem=Integer.valueOf(delPreArr[delPreArr.length-1]);
            pattern = generator.getPattern2(itemset,lastItem);
        }

        TwoListContainer twoListContainer=pattern.utilityInfo;
        if(itemset.length!=pattern.filteredItemset.length){
            itemset=pattern.filteredItemset;
            long key2 = finger64(itemset);
            if (!preSet.add(key2)) {
                return;
            }
        }
        candidatesCount++;
        // obtain the utility of the current itemset
        int uOfRoot= twoListContainer.listB.get(0);
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
            lbOfExD=getlbOfExD(itemset,lastItem,twoListContainer);
        }else {
            // obtain the lower bound for depth pruning; if it exceeds maxUtil, no further extension is considered
            lbOfExD= twoListContainer.listB.get(1);
        }
        MemoryLogger.getInstance().checkMemory();
        // perform depth pruning
        if (lbOfExD<=max_utility){
            if (delPre!=null){
                // only elements greater than lastItem can be removed
                String[] delPreArr=delPre.split("&");
                int lastItem=Integer.valueOf(delPreArr[delPreArr.length-1]);
                for (int i = 0; i < itemset.length; i++) {
                    if (itemset[i]<lastItem){
                        continue;
                    }
                    // obtain the lower bound for width pruning; if it exceeds maxUtil, the itemset and its extensions are pruned
                    int lbOfExB=getlbOfExD(itemset,itemset[i],twoListContainer);;
                    if (lbOfExB<=max_utility){
                    List<Integer> deleteList = pruneItem(lbOfExB,itemset,itemset[i],twoListContainer);
                    int[] extendsion;
                    if(deleteList.size()==0){
                        extendsion=delOneOfItemset(itemset,i,itemset[i]);
                    }else{
                        if(deleteList.contains(itemset[i])){
                            extendsion=new int[itemset.length-deleteList.size()];
                            for (int j = 0,k = 0; j < itemset.length; j++) {
                                if((!deleteList.contains(itemset[j]))){
                                    extendsion[k++]=itemset[j];
                                }
                            }
                        }else{
                            extendsion=new int[itemset.length-deleteList.size()-1];
                            for (int j = 0,k = 0; j < itemset.length; j++) {
                                if((!deleteList.contains(itemset[j]))&&(j!=i)){
                                    extendsion[k++]=itemset[j];
                                }
                            }
                        }
                    }

                        if (extendsion.length>0){
                            LUM(delPre+"&"+itemset[i],extendsion);
                        }

                    }
                }
            }else //delpre==null
            {
                for (int i = 0; i < itemset.length; i++) {
                    // obtain the lower bound for width pruning; if it exceeds maxUtil, both the current itemset and its extensions are pruned
                    int lbOfExB= twoListContainer.getLBofExD(i);
                    int[] extendsion=delOneOfItemset(itemset,i,itemset[i]);
                    if (lbOfExB<=max_utility){
                        if (extendsion.length>0){
                            LUM(String.valueOf(itemset[i]),extendsion);
                        }
                    }
                }
            }


        }
    }
    public int getlbOfExD(int[] itemset,int lastDelItem,TwoListContainer twoListContainer){
        for(int i=0;i<itemset.length;i++){
            if(itemset[i]>=lastDelItem){
                return twoListContainer.getLBofExD(i);
            }
        }
        return twoListContainer.getLBofExD(itemset.length);
    }
    private List<Integer> pruneItem(int utilOfFront, int[] itemset, int lastItem,TwoListContainer twoListContainer){
        List<Integer> deleteList=new ArrayList<>();
        if(itemset[0]>=lastItem){
            for (int i = 0; i < itemset.length; i++) {
                if(itemset[i]<lastItem){
                    continue;
                }
                int utility = twoListContainer.listA.get(i);
                if (utility>max_utility){
                    deleteList.add(itemset[i]);
                }
            }
            return deleteList;
        }
        for (int i = 0; i < itemset.length; i++) {
            if(itemset[i]<lastItem){
                continue;
            }
            int utility = twoListContainer.listA.get(i);
            int utilOfFrontAndItem=utilOfFront+utility;
            if (utilOfFrontAndItem>max_utility){
                deleteList.add(itemset[i]);
            }
        }
        return deleteList;
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
        Set<Integer> setOfItems=new TreeSet<>();
        Map<Integer,String[]> mapTidToItems = new LinkedHashMap<>();
        Map<Integer,String[]> mapTidToUtilities=new LinkedHashMap<>();
        int tidCount=0;
        String thisLine;
        BufferedReader myInput = null;
        try {
            myInput = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));
            // for each transaction (line) in the input file
            while ((thisLine = myInput.readLine()) != null) {
                // if the line is  a comment, is  empty or is a
                // kind of metadata
                if (thisLine.isEmpty() == true ||
                        thisLine.charAt(0) == '#' || thisLine.charAt(0) == '%'
                        || thisLine.charAt(0) == '@') {
                    continue;
                }
                tidCount++;
                String[] partions=thisLine.split(":");
                // int transactionUtility = Integer.parseInt(partions[1]);

                String[] items = partions[0].split(" ");
                int[] itemsInt=new int[items.length];
                for (int i = 0; i < items.length; i++) {
                    itemsInt[i]=Integer.valueOf(items[i]);
                    setOfItems.add(itemsInt[i]);
                }
                mapTidToItems.put(tidCount,items);
                String[] utilities = partions[2].split(" ");
                mapTidToUtilities.put(tidCount,utilities);
            }
        } catch (Exception e) {
            // catch exceptions
            e.printStackTrace();
        }finally {
            if(myInput != null){
                // close the file
                myInput.close();
            }
        }
        generator=new Generator3(setOfItems,tidCount);
        generator.setGenerator(mapTidToItems,mapTidToUtilities);
        generator.max_utility=this.max_utility;
    }

    /**
     * Read the file and generate transactions, performing deduplication and containment pruning
     */

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
