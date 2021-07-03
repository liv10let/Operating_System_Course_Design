import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class main {

    public static void main(String[] args) throws Exception {
        main m = new main();

        System.out.println("请输入你要检测的过程的资源数与进程数");
        Scanner s = new Scanner(System.in);
        int rescourse_num = s.nextInt();
        int process_num = s.nextInt();
        int[]R = m.ReadR(rescourse_num); //输入R数组（所有类资源的数量）
        int[]L = new int[process_num];   //自动生成L表，L表中数值为1的进程都可以运行完毕

        int[][]requestMartix = m.ReadRequest(rescourse_num,process_num);    //从文件输入申请矩阵

        int[][]allocationMartix = m.ReadAllocation(rescourse_num,process_num);      //从文件输入分配矩阵

        int[]availables = m.CalculateAvailable(R,allocationMartix,process_num,rescourse_num);   //计算可用资源数组

        int[]work = availables; //工作数组也就是可用资源数组

        L = m.FindIsolateProcess(allocationMartix,requestMartix,L);     //首先找到孤立进程收入L表中

        L = m.FindRunableProcess(allocationMartix,requestMartix,L,rescourse_num,process_num,work);  //其次判断所有的可运行进行，将可以运行的进程收入L表中

        m.Judgement(L);
    }

    //手动输入R数组（各类资源的可用数）
    public int[] ReadR(int rescourse_num) throws FileNotFoundException {
        Scanner s = new Scanner(System.in);
        System.out.println("请依次输入你要检测的过程的各类资源数");
        int[] R = new int[rescourse_num];   //手动输入R数组（所有类资源的数量）
        for (int i = 0; i < rescourse_num; i++) {
            R[i] = s.nextInt();
        }
        return R;
    }

    //从文件中读取申请矩阵
    public int[][] ReadRequest(int rescourse_num,int process_num) throws IOException {
        String requestfilename = "E:\\Java_workspace\\Operate_System_Course_Design\\src\\request.txt";
        BufferedReader buf = new BufferedReader(new FileReader(requestfilename));   //new一个BufferedReader对象进行文件读取

        int[][] requestMatrix= new int[process_num][rescourse_num]; //创建申请矩阵
        int line = 0;
        String str = null;
        while ((str = buf.readLine()) != null) {
            //System.out.println(str);
            String[] row = str.split(" ");
            for (int i = 0; i < rescourse_num; i++) {
                if(row[i] != " "){
                    requestMatrix[line][i] = Integer.valueOf(row[i]);
                }
            }
            line++;
        }
        for(int i = 0; i < process_num; i++){
            for (int j = 0; j < rescourse_num; j++){
                System.out.print(requestMatrix[i][j]);
            }
            System.out.println(" ");
        }
        return requestMatrix;
    }

    //从文件中读取分配矩阵
    public int[][] ReadAllocation(int rescourse_num,int process_num) throws IOException {
        String allocationfilename = "E:\\Java_workspace\\Operate_System_Course_Design\\src\\allocation.txt";
        BufferedReader buf = new BufferedReader(new FileReader(allocationfilename));   //new一个BufferedReader对象进行文件读取

        int[][] allocationMatrix= new int[rescourse_num][process_num]; //创建申请矩阵
        int line = 0;
        String str = null;
        while ((str = buf.readLine()) != null) {
            //System.out.println(str);
            String[] row = str.split(" ");
            for (int i = 0; i < process_num; i++) {
                if(row[i] != " "){
                    allocationMatrix[line][i] = Integer.valueOf(row[i]);
                }
            }
            line++;
        }
        for(int i = 0; i < rescourse_num; i++){
            for (int j = 0; j < process_num; j++){
                System.out.print(allocationMatrix[i][j]);
            }
            System.out.println(" ");
        }
        return allocationMatrix;
    }

    //计算出可用资源数组(R矩阵减去分配矩阵的行之和)
    public int[] CalculateAvailable(int[] R,int[][] allocationMartix,int process_num,int rescourse_num){
        int[] available = new int[rescourse_num];   //可用资源长度为资源类数

        for(int i = 0; i < rescourse_num; i++){
            int sum = 0;   //用于暂时存储分配矩阵每一行的和
            for (int j = 0; j < allocationMartix[i].length; j++) { //计算分配矩阵每一行的和，也就是每一种资源的已分配数
                sum = sum + allocationMartix[i][j];
            }
            available[i] = R[i] - sum;  //每一类的可用资源数为R（总资源数）-已分配资源数
        }
//        for (int i = 0; i < rescourse_num; i++) {
//            System.out.print(available[i]);
//        }
        return available;
    }

    //找孤立进程（既不申请也不分配的进程），将其加入可工作完的数组L表，孤立进程在两个矩阵中的所有位置都为0（因为没有有向边）
    public int[] FindIsolateProcess(int[][] allocationMartix,int [][] requestMartix,int[] L){

        for(int i =0 ; i<requestMartix.length ;i++){        //遍历申请矩阵
            int flag = 0;
            for (int j = 0; j < requestMartix[i].length ; j++){
                //针对每一个进程和资源的组合，都要判断申请和分配两条有向边是否存在，也就是是否为1
                //如果有任意一条有向边存在则说明该进程不是孤立进程
                if(requestMartix[i][j] == 1 || allocationMartix[j][i] == 1)
                    flag = 1;
            }
            //如果将i号进程和所有的资源都组合判断完后flag还是0，则说明该i号进程和周围所有的资源都没有有向边，那么它就应该被放在L表中去
            if(flag == 0)
                L[i] = 1;   //L表的i号位置，就对应了i号进程，如果这个值为1，那么就代表i号进程是孤立进程
        }
//        System.out.println(" ");
//        for(int i = 0 ; i < L.length ; i++){
//            System.out.print(L[i]);
//        }
        return L;
    }

    //找可以运行完毕的进程，将其加入L表中
    public int[] FindRunableProcess(int[][] allocationMartix,int [][] requestMartix,int[] L,int rescourse_num ,int process_num,int[] work){
        //以最坏情况考虑分配进程资源的过程需要多少次判断
        //比如如果有四个进程，那么最坏的情况就是每一次都是最后一个进程才能运行
        //也就是必须要完成最后一个进程的资源分配才能对前面的进程进行分配
//        int sum = 0;
//        for(int i = 1 ; i <= requestMartix.length ; i++){
//            sum = sum + i;
//        }

        //对于每一个进程来说的申请各类资源表
        //alocationMartix.length也就是资源数
        int[] request = new int[rescourse_num];

        //requestMartix.length也就是进程数
        //每一次循环都是对一个进程进行判断
        //考虑到最坏情况是每次都是最后一个进程要运行完才能释放资源
        for (int i = 0; i < process_num; i++) {
            for(int j = 0; j < process_num ; j++){
                int flag = 0;
                for (int k = 0 ; k < rescourse_num ; k++){  //给j号进程申请资源数进行赋值
                    request[k] = requestMartix[j][k];
                }
                //如果对于request数组中的k类资源来说，work可用数组的可用资源数都不足够提供给他
                //那么flag就置为1
                for (int k = 0; k < rescourse_num; k++){
                    if (request[k] > work[k])
                        flag = 1;
                }
                //如果满足request数组中的k类所需资源数都小于work数组的可用资源数(也就是flag=0)
                //也就是这个进程能够申请到足够的资源完成运行
                //那么就把这个进程的已分配资源还给可用数组，然后将这个进程在L表中的位置置1
                //代表这个进程完成了运行，将已占用（分配）的资源释放进入了可用资源区
                if (flag == 0) {
                    for (int k = 0; k < rescourse_num ; k++){
                        work[k] = work[k] + allocationMartix[k][j];
                    }
                    L[j] = 1;
                }
            }
        }
        for (int k = 0; k < L.length; k++) {
            System.out.print(L[k]);
        }
        return L;
    }

    //已经拿到了判断完毕的L表，用L表来判断是否有死锁
    //前面的所有操作让孤立的和可运行的进程在L表中的相应位置是1
    //那么如果L表中有0存在的话，就代表该位置的那个进程无法拿到资源运行完毕，也不是孤立进程，也就是存在死锁
    public void Judgement(int[] L){
        int flag = 0;
        for(int i = 0 ; i < L.length; i++){
            if (L[i] == 0){
                flag = 1;
            }
        }
        if (flag == 0) {
            System.out.println("这一组进程和资源中没有死锁存在");
        }
        if (flag == 1) {
            System.out.println("这一组进程和资源中有死锁存在");
        }
    }
}
