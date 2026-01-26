import java.util.*;

public class TwoListContainer {
    List<Integer> listA;
    List<Integer> listB;

    public TwoListContainer() {
        listA = new ArrayList<>();
        listB = new ArrayList<>();
    }

    // 可选：提供构造方法
    public TwoListContainer(List<Integer> listA, List<Integer> listB) {
        this.listA = listA;
        this.listB = listB;
    }
    public int getLBofExD(int lastIndexDelItem){
       int LB=0;
        if(lastIndexDelItem==0){
            return listB.get(1);
        }else {
            for (int i = 0; i < lastIndexDelItem; i++) {
                LB+=listA.get(i);
            }
        }
        return LB;
    }
    public int getLBofExD2(int firstIndex,int lastIndexDelItem){
        int LB=0;
        if(lastIndexDelItem==0){
            return listB.get(1);
        }else {
            for (int i = firstIndex+1; i < lastIndexDelItem; i++) {
                LB+=listA.get(i);
            }
        }
        return LB;
    }
    public int getLBofExDPart(int firstIndexDelItem,int lastIndexDelItem){
        int LB=0;
        if(lastIndexDelItem==0){
            return listB.get(1);
        }else {
            for (int i = firstIndexDelItem; i < lastIndexDelItem; i++) {
                LB+=listA.get(i);
            }
        }
        return LB;
    }
}
