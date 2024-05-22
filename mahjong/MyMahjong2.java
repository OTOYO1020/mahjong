import java.net.*;
import java.io.*;
import javax.swing.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.image.*;//画像処理に必要
import java.awt.geom.*;//画像処理に必要

public class MyMahjong2 extends JFrame implements MouseListener,MouseMotionListener {
	private JButton buttonTiles[] = new JButton[13];
  private JButton tsumoTile;
  private int tilesNumber [][] = new int [4][13];
	private Container c;
	private ImageIcon TON, NAN, SYA, PEI, HAKU, HATSU, CHUN;
  private ImageIcon MANZU1, MANZU2, MANZU3, MANZU4, MANZU5, MANZU6, MANZU7, MANZU8, MANZU9;
  private ImageIcon PINZU1, PINZU2, PINZU3, PINZU4, PINZU5, PINZU6, PINZU7, PINZU8, PINZU9;
  private ImageIcon SOUZU1, SOUZU2, SOUZU3, SOUZU4, SOUZU5, SOUZU6, SOUZU7, SOUZU8, SOUZU9;
  private ImageIcon BOARD;
  
  private final int width = 46, height = 62, gameSizeHeight, gameSizeWidth;
  private int myDirection;  //0:東  1:南  2:西  3:北
  private int nowDirection = 0;
  private int nowTurn = 0;
  private final int maxTile = (9 * 3 + 7) * 4 - 24;
  private int countTile = 0;  //牌の数をカウントするための変数
  private int tsumo;
  //private int countedPlayers = 0;
  private ArrayList<Integer> tileList = new ArrayList<Integer>(136);
  private int dropTile, numNotPonUser = 0;
  //河の表示用ボタン
  private JButton myDrop [][] = new JButton[6][3];
  private JButton kamicha [][] = new JButton[6][3];
  private JButton toimen [][]= new JButton[6][3];
  private JButton shimocha [][]= new JButton[6][3];
  
  private JButton myHuuro [][] = new JButton [4][4];
  private JButton kamichaHuuro [][] = new JButton [4][4];
  private JButton toimenHuuro [][] = new JButton [4][4];
  private JButton shimochaHuuro [][] = new JButton [4][4];
  
  private boolean statusPON = false, statusKAN = false, statusSpecial = false, statusWhole = true, statusRON = false, statusTSUMO = false, judgeFINISH = false;
  private JButton buttonPON, buttonKAN, buttonRON, buttonTSUMO, buttonIgnore;
  private ImageIcon PONImageIcon, unPONImageIcon, KANImageIcon, unKANImageIcon, RONImageIcon, unRONImageIcon, TSUMOImageIcon, unTSUMOImageIcon, IgnoreImageIcon, unIgnoreImageIcon;
  
  private int countMyDrop = 0, countKamicha = 0, countToimen = 0, countShimocha = 0;
  private int countMyHuuro = 0, countKamichaHuuro = 0, countToimenHuuro = 0, countShimochaHuuro = 0;
  
  private Color BackgroundColor = new Color(45, 163, 21);
  PrintWriter out;//出力用のライター

	public MyMahjong2() {
		//名前の入力ダイアログを開く
		String myName = JOptionPane.showInputDialog(null,"名前を入力してください","名前の入力",JOptionPane.QUESTION_MESSAGE);
		if(myName.equals("")){
			myName = "No name";//名前がないときは，"No name"とする
		}
    
    String myIP = JOptionPane.showInputDialog(null,"IPアドレスを入力してください","名前の入力",JOptionPane.QUESTION_MESSAGE);
		if(myIP.equals("")){
			myIP = "localhost";//nullの場合、localhostとする
		}

		//ウィンドウを作成する
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//ウィンドウを閉じるときに，正しく閉じるように設定する
		setTitle("MyMahjong2");//ウィンドウのタイトルを設定する
    gameSizeWidth = 16 * width + 190;
    gameSizeHeight = 3 * width + 4 * height + 200;
		setSize(gameSizeWidth, gameSizeHeight);//ウィンドウのサイズを設定する
		c = getContentPane();
    c.setBackground(BackgroundColor);
    
		c.setLayout(null);//自動レイアウトの設定を行わない
		//ボタンの生成
    
    //牌の設定
    settingIcons();
    SettingButtons();
    
		//サーバに接続する
		Socket socket = null;
		try {
			//"localhost"は，自分内部への接続．localhostを接続先のIP Address（"133.42.155.201"形式）に設定すると他のPCのサーバと通信できる
			//10000はポート番号．IP Addressで接続するPCを決めて，ポート番号でそのPC上動作するプログラムを特定する
			socket = new Socket("localhost", 10000);
		} catch (UnknownHostException e) {
			System.err.println("ホストの IP アドレスが判定できません: " + e);
		} catch (IOException e) {
			 System.err.println("エラーが発生しました: " + e);
		}
		
		MesgRecvThread mrt = new MesgRecvThread(socket, myName);//受信用のスレッドを作成する
		mrt.start();//スレッドを動かす（Runが動く）
	}
		
	//メッセージ受信のためのスレッド
	public class MesgRecvThread extends Thread {
		
		Socket socket;
		String myName;
		
		public MesgRecvThread(Socket s, String n){
			socket = s;
			myName = n;
		}
		
		//通信状況を監視し，受信データによって動作する
		public void run() {
			try{
				InputStreamReader sisr = new InputStreamReader(socket.getInputStream());
				BufferedReader br = new BufferedReader(sisr);
				out = new PrintWriter(socket.getOutputStream(), true);
				out.println(myName);//接続の最初に名前を送る
        
        String myNumberStr = br.readLine();
        int myNumberInt = Integer.parseInt(myNumberStr);
        myDirection = myNumberInt % 4;
        System.out.println(myDirection);
        
        
				while(true) {
					String inputLine = br.readLine();//データを一行分だけ読み込んでみる
					if (inputLine != null) {//読み込んだときにデータが読み込まれたかどうかをチェックする
						String[] inputTokens = inputLine.split(" ");	//入力データを解析するために、スペースで切り分ける
						String cmd = inputTokens[0];//コマンドの取り出し．１つ目の要素を取り出す
            if(cmd.equals("TILES")){
              String tileString = inputTokens[1];
              String tileNumberString [] = tileString.split(",");
              for(String number: tileNumberString){  //拡張for文に気づいた天才と呼んでくれ
                tileList.add(Integer.parseInt(number));  //ここで鯖からもらってリストを紐づける
              }
              
              //初めの配布
              for(int i = 0; i < 4; i++){
                for(int j = 0; j < 13; j++){
                  tilesNumber[i][j] = tileList.get(countTile);
                  countTile++;
                }
                
                int [] tmpArray = sortingArray(tilesNumber, i);
                
                for(int j = 0; j < 13; j++){
                  tilesNumber[i][j] = tmpArray[j];
                }
              }
              
              for(int i = 0; i < 13; i++){
                buttonTiles[i].setIcon(linkTile(tilesNumber[myDirection][i]));
                c.add(buttonTiles[i]);
              }
              
              if(nowDirection == myDirection){
                tsumoTile.setIcon(linkTile(tileList.get(countTile)));
                c.add(tsumoTile);
              }
              
              //countTile++;
            }
            
            if(cmd.equals("DROP")){
              int tmpDirection = Integer.parseInt(inputTokens[1]);
              dropTile = Integer.parseInt(inputTokens[2]);
              int differenceDirection = (myDirection - tmpDirection + 4) % 4;
              
              ImageIcon resizedTile = resizeImageIcon(linkTile(dropTile));
              Icon icon1 = new ImageIcon();
              icon1 = resizedTile;
              if(differenceDirection == 0){
                myDrop[countMyDrop % 6][countMyDrop / 6].setIcon(resizedTile);
                c.add(myDrop[countMyDrop % 6][countMyDrop / 6]);
                countMyDrop++;
              }else if(differenceDirection == 3){
                Icon icon2 = (Icon) new RotateIcon(icon1, 270);
                kamicha[countKamicha % 6][countKamicha / 6].setIcon(icon2);
                c.add(kamicha[countKamicha % 6][countKamicha / 6]);
                countKamicha++;
              }else if(differenceDirection == 2){
                Icon icon2 = (Icon) new RotateIcon(icon1, 180);
                toimen[countToimen % 6][countToimen / 6].setIcon(icon2);
                c.add(toimen[countToimen % 6][countToimen / 6]);
                countToimen++;
              }else if(differenceDirection == 1){
                Icon icon2 = (Icon) new RotateIcon(icon1, 90);
                shimocha[countShimocha % 6][countShimocha / 6].setIcon(icon2);
                c.add(shimocha[countShimocha % 6][countShimocha / 6]);
                countShimocha++;
              }
              
              if(differenceDirection != 0){  //他人が牌を捨てた時
                int numSameTile = 0;  //捨て牌と同じ牌を何枚持っているかを数える
                for(int i = 0; i < 13; i++){
                  if(tilesNumber[myDirection][i] / 4 == dropTile / 4){
                    numSameTile++;
                  }
                }
                
                int tmpArray [] = new int [14];
                for(int i = 0; i < 13; i++){
                  tmpArray[i] = tilesNumber[myDirection][i] / 4;
                }
                tmpArray[13] = dropTile / 4;
                Arrays.sort(tmpArray);
                int num3 = countANKO(tmpArray) + countMyHuuro;
                boolean JANTOU = judgeJANTOU(tmpArray);
                
                if(num3 == 4 && JANTOU){
                  out.println("CAN_RON " + myDirection);
                  out.flush();
                }
                
                countTile++;
                
                if(numSameTile == 3){
                  out.println("CAN_KAN " + myDirection);
                  out.flush();
                }else if(numSameTile == 2){  //同じ牌を２枚以上持っている場合
                  out.println("CAN_PON " + myDirection);
                  out.flush();
                }else{  //ポン出来ない場合
                  out.println("NEXT");  //次の人に順番を回す
                  out.flush();
                }
              }
              
              if(!statusWhole){
                statusWhole = true;
              }
              
              
            }
            
            if(cmd.equals("NEXT")){
              if(nowDirection == myDirection){
                int tmpArray[] = new int [14];
                for(int i = 0; i < 13; i++){
                  tmpArray[i] = tilesNumber[myDirection][i] / 4;
                }
                tmpArray[13] = tsumo / 4;
                Arrays.sort(tmpArray);
                int num3 = countANKO(tmpArray) + countMyHuuro;
                boolean JANTOU = judgeJANTOU(tmpArray);
                
                if(num3 == 4 && JANTOU){
                  out.println("CAN_TSUMO " + myDirection);
                  out.flush();
                }
              }
              
              
              
              numNotPonUser++;
              if(numNotPonUser == 3){
                nowDirection = (nowDirection + 1) % 4;
                if(nowDirection == myDirection){
                  tsumoTile.setIcon(linkTile(tileList.get(countTile)));
                }
                numNotPonUser = 0;
              }
              
              
              
              
            }
            
            if(cmd.equals("CAN_PON")){
              statusWhole = false;
              int tmpDirection = Integer.parseInt(inputTokens[1]);
              if(myDirection == tmpDirection){
                statusPON = true;
                statusSpecial = true;
                buttonPON.setIcon(PONImageIcon);
                c.add(buttonPON);
                buttonIgnore.setIcon(IgnoreImageIcon);
                c.add(buttonIgnore);
              }
            }
            
            if(cmd.equals("CAN_KAN")){
              statusWhole = false;
              int tmpDirection = Integer.parseInt(inputTokens[1]);
              if(myDirection == tmpDirection){
                statusKAN = true;
                statusPON = true;
                statusSpecial = true;
                buttonKAN.setIcon(KANImageIcon);
                buttonPON.setIcon(PONImageIcon);
                c.add(buttonKAN);
                c.add(buttonPON);
                buttonIgnore.setIcon(IgnoreImageIcon);
                c.add(buttonIgnore);
              }
            }
            
            if(cmd.equals("PON")){
              //この段階でのnowDirectionは、直前に牌を捨てた人の方角。河の牌を消す処理を行う
              int differenceDirection = (myDirection - nowDirection + 4) % 4;
              if(differenceDirection == 0){
                countMyDrop--;
                myDrop[countMyDrop % 6][countMyDrop / 6].setIcon(BOARD);
                c.add(myDrop[countMyDrop % 6][countMyDrop / 6]);
              }else if(differenceDirection == 3){
                countKamicha--;
                kamicha[countKamicha % 6][countKamicha / 6].setIcon(BOARD);
                c.add(kamicha[countKamicha % 6][countKamicha / 6]);
              }else if(differenceDirection == 2){
                countToimen--;
                toimen[countToimen % 6][countToimen / 6].setIcon(BOARD);
                c.add(toimen[countToimen % 6][countToimen / 6]);
              }else if(differenceDirection == 1){
                countShimocha--;
                shimocha[countShimocha % 6][countShimocha / 6].setIcon(BOARD);
                c.add(shimocha[countShimocha % 6][countShimocha / 6]);
              }
              
              //この段階では、nowDirectionはポンをした人。副露牌を描画する
              nowDirection = Integer.parseInt(inputTokens[1]);
              ImageIcon resizedTile = resizeImageIcon(linkTile(dropTile));
              Icon icon1 = new ImageIcon();
              icon1 = resizedTile;
              
              differenceDirection = (myDirection - nowDirection + 4) % 4;
              if(differenceDirection == 0){
                for(int i = 0; i < 3; i++){
                  myHuuro[countMyHuuro][i].setIcon(resizedTile);
                  c.add(myHuuro[countMyHuuro][i]);
                }
                countMyHuuro++;
                
                for(int i = 0; i < 13; i++){
                  if(tilesNumber[myDirection][i] / 4 == dropTile / 4){
                    tilesNumber[myDirection][i] = -1;
                  }
                }
                int [] tmpArray = sortingArray(tilesNumber, myDirection);
                for(int i = 0; i < 13; i++){
                  buttonTiles[i].setIcon(linkTile(tilesNumber[myDirection][i]));
                  c.add(buttonTiles[i]);
                }
                
              }else if(differenceDirection == 1){
                Icon icon2 = (Icon) new RotateIcon(icon1, 270);
                for(int i = 0; i < 3; i++){
                  kamichaHuuro[countKamichaHuuro][i].setIcon(icon2);
                  c.add(kamichaHuuro[countKamichaHuuro][i]);
                }
                countKamichaHuuro++;
              }else if(differenceDirection == 2){
                Icon icon2 = (Icon) new RotateIcon(icon1, 180);
                for(int i = 0; i < 3; i++){
                  toimenHuuro[countToimenHuuro][i].setIcon(icon2);
                  c.add(toimenHuuro[countToimenHuuro][i]);
                }
                countToimenHuuro++;
              }else if(differenceDirection == 3){
                Icon icon2 = (Icon) new RotateIcon(icon1, 90);
                for(int i = 0; i < 3; i++){
                  shimochaHuuro[countShimochaHuuro][i].setIcon(icon2);
                  c.add(shimochaHuuro[countShimochaHuuro][i]);
                }
                countShimochaHuuro++;
              }
              statusPON = false;
              statusKAN = false;
              statusWhole = false;
              buttonPON.setIcon(unPONImageIcon);
              c.add(buttonPON);
              buttonKAN.setIcon(unKANImageIcon);
              c.add(buttonKAN);
              buttonIgnore.setIcon(unIgnoreImageIcon);
              c.add(buttonIgnore);
            }
            
            if(cmd.equals("KAN")){
              //この段階でのnowDirectionは、直前に牌を捨てた人の方角。河の牌を消す処理を行う
              int differenceDirection = (myDirection - nowDirection + 4) % 4;
              if(differenceDirection == 0){
                countMyDrop--;
                myDrop[countMyDrop % 6][countMyDrop / 6].setIcon(BOARD);
                c.add(myDrop[countMyDrop % 6][countMyDrop / 6]);
              }else if(differenceDirection == 3){
                countKamicha--;
                kamicha[countKamicha % 6][countKamicha / 6].setIcon(BOARD);
                c.add(kamicha[countKamicha % 6][countKamicha / 6]);
              }else if(differenceDirection == 2){
                countToimen--;
                toimen[countToimen % 6][countToimen / 6].setIcon(BOARD);
                c.add(toimen[countToimen % 6][countToimen / 6]);
              }else if(differenceDirection == 1){
                countShimocha--;
                shimocha[countShimocha % 6][countShimocha / 6].setIcon(BOARD);
                c.add(shimocha[countShimocha % 6][countShimocha / 6]);
              }
              
              //この段階では、nowDirectionはポンをした人。副露牌を描画する
              nowDirection = Integer.parseInt(inputTokens[1]);
              ImageIcon resizedTile = resizeImageIcon(linkTile(dropTile));
              Icon icon1 = new ImageIcon();
              icon1 = resizedTile;
              
              differenceDirection = (myDirection - nowDirection + 4) % 4;
              if(differenceDirection == 0){
                for(int i = 0; i < 4; i++){
                  myHuuro[countMyHuuro][i].setIcon(resizedTile);
                  c.add(myHuuro[countMyHuuro][i]);
                }
                countMyHuuro++;
                
                for(int i = 0; i < 13; i++){  //副露牌になった牌の表記を消す
                  if(tilesNumber[myDirection][i] / 4 == dropTile / 4){
                    tilesNumber[myDirection][i] = -1;
                  }
                }
                int [] tmpArray = sortingArray(tilesNumber, myDirection);
                for(int i = 0; i < 13; i++){
                  buttonTiles[i].setIcon(linkTile(tilesNumber[myDirection][i]));
                  c.add(buttonTiles[i]);
                }
                
              }else if(differenceDirection == 1){
                Icon icon2 = (Icon) new RotateIcon(icon1, 270);
                for(int i = 0; i < 4; i++){
                  kamichaHuuro[countKamichaHuuro][i].setIcon(icon2);
                  c.add(kamichaHuuro[countKamichaHuuro][i]);
                }
                countKamichaHuuro++;
              }else if(differenceDirection == 2){
                Icon icon2 = (Icon) new RotateIcon(icon1, 180);
                for(int i = 0; i < 4; i++){
                  toimenHuuro[countToimenHuuro][i].setIcon(icon2);
                  c.add(toimenHuuro[countToimenHuuro][i]);
                }
                countToimenHuuro++;
              }else if(differenceDirection == 3){
                Icon icon2 = (Icon) new RotateIcon(icon1, 90);
                for(int i = 0; i < 4; i++){
                  shimochaHuuro[countShimochaHuuro][i].setIcon(icon2);
                  c.add(shimochaHuuro[countShimochaHuuro][i]);
                }
                countShimochaHuuro++;
              }
              statusPON = false;
              statusKAN = false;
              statusWhole = false;
              buttonPON.setIcon(unPONImageIcon);
              c.add(buttonPON);
              buttonKAN.setIcon(unKANImageIcon);
              c.add(buttonKAN);
              buttonIgnore.setIcon(unIgnoreImageIcon);
              c.add(buttonIgnore);
            }
            
            if(cmd.equals("IGNORE")){
              nowDirection = (nowDirection + 1) % 4;
              if(nowDirection == myDirection){
                tsumoTile.setIcon(linkTile(tileList.get(countTile)));
                c.add(tsumoTile);
                tsumo = tileList.get(countTile);
                
                buttonPON.setIcon(unPONImageIcon);
                c.add(buttonPON);
                buttonKAN.setIcon(unKANImageIcon);
                c.add(buttonKAN);
                buttonIgnore.setIcon(unIgnoreImageIcon);
                c.add(buttonIgnore);
              }
              statusPON = false;
              statusKAN = false;
              statusWhole = true;
            }
            
            if(cmd.equals("CAN_RON")){
              statusWhole = false;
              int tmpDirection = Integer.parseInt(inputTokens[1]);
              if(myDirection == tmpDirection){
                statusRON = true;
                buttonRON.setIcon(RONImageIcon);
                c.add(buttonRON);
                buttonIgnore.setIcon(IgnoreImageIcon);
                c.add(buttonIgnore);
              }
            }
            
            if(cmd.equals("CAN_TSUMO")){
              statusWhole = false;
              int tmpDirection = Integer.parseInt(inputTokens[1]);
              if(myDirection == tmpDirection){
                statusTSUMO = true;
                buttonTSUMO.setIcon(TSUMOImageIcon);
                c.add(buttonTSUMO);
                buttonIgnore.setIcon(IgnoreImageIcon);
                c.add(buttonIgnore);
              }
            }
            
            if(cmd.equals("RON")){  //音声出してもいいかもね
              judgeFINISH = true;
              int tmpDirection = Integer.parseInt(inputTokens[1]);
              if(tmpDirection == myDirection){
                JOptionPane.showMessageDialog(null, "YOU WIN!!");
              }else{
                JOptionPane.showMessageDialog(null, "PLAYER" + tmpDirection + "の勝利です!!");
              }
            }
            
            if(cmd.equals("TSUMO")){  //音声出してもいいかもね
              judgeFINISH = true;
              int tmpDirection = Integer.parseInt(inputTokens[1]);
              if(tmpDirection == myDirection){
                JOptionPane.showMessageDialog(null, "YOU WIN!!");
              }else{
                JOptionPane.showMessageDialog(null, "PLAYER" + tmpDirection + "の勝利です!!");
              }
            }
            
          }else{  //何も命令がない時
            break;
          }
				
				}
				socket.close();
			} catch (IOException e) {
				System.err.println("エラーが発生しました: " + e);
			}
		}
	}

	public static void main(String[] args) {
		MyMahjong2 net = new MyMahjong2();
		net.setVisible(true);
	}
  	
	public void mouseClicked(MouseEvent e) {//ボタンをクリックしたときの処理
    JButton theButton = (JButton)e.getComponent();//クリックしたオブジェクトを得る．型が違うのでキャストする
    String theArrayIndex = theButton.getActionCommand();//ボタンの配列の番号を取り出す
    
    if(!statusPON && !statusKAN && statusWhole && !judgeFINISH){  //ポン・カンが出来ない状態
      if(countTile != maxTile){  //引ける牌が残っている状態
        if(nowDirection == myDirection){  //自分の方角とゲームの方角が一致している時
          if(Integer.parseInt(theArrayIndex) < 13){  //有効牌を引いた場合
            dropTile = tilesNumber[myDirection][Integer.parseInt(theArrayIndex)];  //捨て牌の設定
            tilesNumber[myDirection][Integer.parseInt(theArrayIndex)] = tileList.get(countTile);
            int [] tmpArray = sortingArray(tilesNumber, myDirection);
            
            for(int i = 0; i < 13; i++){
              tilesNumber[myDirection][i] = tmpArray[i];
              buttonTiles[i].setIcon(linkTile(tilesNumber[myDirection][i]));
              c.add(buttonTiles[i]);
            }
          }else if(Integer.parseInt(theArrayIndex) == 13){  //ツモ切りの場合
            dropTile = tileList.get(countTile);
          }
          out.println("DROP " + nowDirection + " " + dropTile);
          out.flush();
          
          hideTSUMOtile();
        }
      }
    }
    
    if(statusPON && !statusWhole && !judgeFINISH){
      if(Integer.parseInt(theArrayIndex) == 100){  //ポンするぜ！
        out.println("PON " + myDirection);
        out.flush();
      }else if(Integer.parseInt(theArrayIndex) == 400){  //ポンを無視するとき
        out.println("IGNORE");
        out.flush();
      }
    }
    
    if(statusKAN && !statusWhole && !judgeFINISH){
      if(Integer.parseInt(theArrayIndex) == 200){  //ポンするぜ！
        out.println("KAN " + myDirection);
        out.flush();
      }else if(Integer.parseInt(theArrayIndex) == 400){  //ポンを無視するとき
        out.println("IGNORE");
        out.flush();
      }
    }
    
    if(!statusWhole && !judgeFINISH){
      if(Integer.parseInt(theArrayIndex) == 300){
        out.println("RON " + myDirection);
        out.flush();
      }else if(Integer.parseInt(theArrayIndex) == 400){  //ポンを無視するとき
        out.println("IGNORE");
        out.flush();
      }
    }
    
    if(!statusWhole && !judgeFINISH){
      if(Integer.parseInt(theArrayIndex) == 350){
        out.println("TSUMO " + myDirection);
        out.flush();
      }else if(Integer.parseInt(theArrayIndex) == 400){  //ポンを無視するとき
        out.println("IGNORE");
        out.flush();
      }
    }
    
    /*
    if(!statusWhole && !judgeFINISH){
      if(Integer.parseInt(theArrayIndex) == 300){
        out.println("RON " + myDirection);
        out.flush();
      }else if(Integer.parseInt(theArrayIndex) == 400){  //ポンを無視するとき
        out.println("IGNORE");
        out.flush();
      }
    }
    */
    
    if(!statusPON && !statusKAN && !statusWhole && !judgeFINISH){  //ポンした後に1枚減った状態でやるやつ
      if(countTile != maxTile){
        if(nowDirection == myDirection){
          if(tilesNumber[myDirection][Integer.parseInt(theArrayIndex)] != -1){
            dropTile = tilesNumber[myDirection][Integer.parseInt(theArrayIndex)];  //捨て牌の設定
            if(statusSpecial){
              tilesNumber[myDirection][Integer.parseInt(theArrayIndex)] = -1;
              statusSpecial = false;
            }else{
              tilesNumber[myDirection][Integer.parseInt(theArrayIndex)] = tileList.get(countTile);
            }
            int [] tmpArray = sortingArray(tilesNumber, myDirection);
            
            for(int i = 0; i < 13; i++){
              tilesNumber[myDirection][i] = tmpArray[i];
              buttonTiles[i].setIcon(linkTile(tilesNumber[myDirection][i]));
              c.add(buttonTiles[i]);
            }
            
            out.println("DROP " + nowDirection + " " + dropTile);
            out.flush();
            
            statusWhole = true;
          }
        }
      }
    }
	}
	
	public void mouseEntered(MouseEvent e) {//マウスがオブジェクトに入ったときの処理
	}
	
	public void mouseExited(MouseEvent e) {//マウスがオブジェクトから出たときの処理
	}
	
	public void mousePressed(MouseEvent e) {//マウスでオブジェクトを押したときの処理（クリックとの違いに注意）
	}
	
	public void mouseReleased(MouseEvent e) {//マウスで押していたオブジェクトを離したときの処理
	}
	
	public void mouseDragged(MouseEvent e) {//マウスでオブジェクトとをドラッグしているときの処理
	}

	public void mouseMoved(MouseEvent e) {//マウスがオブジェクト上で移動したときの処理
	}
  
  public ImageIcon linkTile (int num){  //絶対もっと効率いい方法ある
    if(num == -1){
      return BOARD;
    }
    
    int tileType = num / 4;
    if(tileType < 9){  //萬子
      if(tileType == 0){
        return MANZU1;
      }else if(tileType == 1){
        return MANZU2;
      }else if(tileType == 2){
        return MANZU3;
      }else if(tileType == 3){
        return MANZU4;
      }else if(tileType == 4){
        return MANZU5;
      }else if(tileType == 5){
        return MANZU6;
      }else if(tileType == 6){
        return MANZU7;
      }else if(tileType == 7){
        return MANZU8;
      }else if(tileType == 8){
        return MANZU9;
      }
    }else if(tileType < 18){  //筒子
      tileType -= 9;
      if(tileType == 0){
        return PINZU1;
      }else if(tileType == 1){
        return PINZU2;
      }else if(tileType == 2){
        return PINZU3;
      }else if(tileType == 3){
        return PINZU4;
      }else if(tileType == 4){
        return PINZU5;
      }else if(tileType == 5){
        return PINZU6;
      }else if(tileType == 6){
        return PINZU7;
      }else if(tileType == 7){
        return PINZU8;
      }else if(tileType == 8){
        return PINZU9;
      }
    }else if(tileType < 27){  //索子
      tileType -= 18;
      if(tileType == 0){
        return SOUZU1;
      }else if(tileType == 1){
        return SOUZU2;
      }else if(tileType == 2){
        return SOUZU3;
      }else if(tileType == 3){
        return SOUZU4;
      }else if(tileType == 4){
        return SOUZU5;
      }else if(tileType == 5){
        return SOUZU6;
      }else if(tileType == 6){
        return SOUZU7;
      }else if(tileType == 7){
        return SOUZU8;
      }else if(tileType == 8){
        return SOUZU9;
      }
    }else if(tileType < 34){  //字牌
      tileType -= 27;
      if(tileType == 0){
        return TON;
      }else if(tileType == 1){
        return NAN;
      }else if(tileType == 2){
        return SYA;
      }else if(tileType == 3){
        return PEI;
      }else if(tileType == 4){
        return HAKU;
      }else if(tileType == 5){
        return HATSU;
      }else if(tileType == 6){
        return CHUN;
      }
    }
    return null;
  }
  
  private void settingIcons(){//牌の設定
    TON = new ImageIcon("./janpai/ton.jpg");
    NAN = new ImageIcon("./janpai/nan.jpg");
    SYA = new ImageIcon("./janpai/sya.jpg");
    PEI = new ImageIcon("./janpai/pei.jpg");
    HAKU = new ImageIcon("./janpai/haku.jpg");
    HATSU = new ImageIcon("./janpai/hatsu.jpg");
    CHUN = new ImageIcon("./janpai/chun.jpg");
    
    MANZU1 = new ImageIcon("./janpai/manzu1.jpg");
    MANZU2 = new ImageIcon("./janpai/manzu2.jpg");
    MANZU3 = new ImageIcon("./janpai/manzu3.jpg");
    MANZU4 = new ImageIcon("./janpai/manzu4.jpg");
    MANZU5 = new ImageIcon("./janpai/manzu5.jpg");
    MANZU6 = new ImageIcon("./janpai/manzu6.jpg");
    MANZU7 = new ImageIcon("./janpai/manzu7.jpg");
    MANZU8 = new ImageIcon("./janpai/manzu8.jpg");
    MANZU9 = new ImageIcon("./janpai/manzu9.jpg");
    
    PINZU1 = new ImageIcon("./janpai/pinzu1.jpg");
    PINZU2 = new ImageIcon("./janpai/pinzu2.jpg");
    PINZU3 = new ImageIcon("./janpai/pinzu3.jpg");
    PINZU4 = new ImageIcon("./janpai/pinzu4.jpg");
    PINZU5 = new ImageIcon("./janpai/pinzu5.jpg");
    PINZU6 = new ImageIcon("./janpai/pinzu6.jpg");
    PINZU7 = new ImageIcon("./janpai/pinzu7.jpg");
    PINZU8 = new ImageIcon("./janpai/pinzu8.jpg");
    PINZU9 = new ImageIcon("./janpai/pinzu9.jpg");
    
    SOUZU1 = new ImageIcon("./janpai/souzu1.jpg");
    SOUZU2 = new ImageIcon("./janpai/souzu2.jpg");
    SOUZU3 = new ImageIcon("./janpai/souzu3.jpg");
    SOUZU4 = new ImageIcon("./janpai/souzu4.jpg");
    SOUZU5 = new ImageIcon("./janpai/souzu5.jpg");
    SOUZU6 = new ImageIcon("./janpai/souzu6.jpg");
    SOUZU7 = new ImageIcon("./janpai/souzu7.jpg");
    SOUZU8 = new ImageIcon("./janpai/souzu8.jpg");
    SOUZU9 = new ImageIcon("./janpai/souzu9.jpg");
    
    BOARD = new ImageIcon("./others/background.jpg");
    PONImageIcon = new ImageIcon("./others/buttonPON.png");
    unPONImageIcon = new ImageIcon("./others/buttonPON_2.jpg");
    KANImageIcon = new ImageIcon("./others/buttonKAN.png");
    unKANImageIcon = new ImageIcon("./others/buttonKAN_2.png");
    RONImageIcon = new ImageIcon("./others/buttonRON.png");
    unRONImageIcon = new ImageIcon("./others/buttonRON_2.png");
    TSUMOImageIcon = new ImageIcon("./others/buttonTSUMO.png");
    unTSUMOImageIcon = new ImageIcon("./others/buttonTSUMO_2.png");
    IgnoreImageIcon = new ImageIcon("./others/ignore.png");
    unIgnoreImageIcon = new ImageIcon("./others/ignore_2.png");
  }
  
  public ImageIcon resizeImageIcon(ImageIcon originalIcon){
    Image originalImage = originalIcon.getImage();
    Image resizedImage = originalImage.getScaledInstance(width / 2, height / 2, Image.SCALE_DEFAULT);
    ImageIcon resizedImageIcon = new ImageIcon(resizedImage);
    return resizedImageIcon;
  }
  
  class RotateIcon implements Icon{
    private final Dimension d = new Dimension();
    private final Image image;
    private AffineTransform trans;
    protected RotateIcon(Icon icon, int rotate){
      d.setSize(icon.getIconWidth(), icon.getIconHeight());
      image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
      Graphics g = image.getGraphics();
      icon.paintIcon(null, g, 0, 0);
      g.dispose();
      
      int numquadrants = (rotate / 90) % 4;
      if(numquadrants == 1){
        trans = AffineTransform.getTranslateInstance(d.height, 0);
        int v = d.width;
        d.width = d.height;
        d.height = v;
      }else if(numquadrants == 2){
        trans = AffineTransform.getTranslateInstance(d.width, d.height);
      }else if(numquadrants == 3){
        trans = AffineTransform.getTranslateInstance(0, d.width);
        int v = d.width;
        d.width = d.height;
        d.height = v;
      }
      trans.quadrantRotate(numquadrants);
    }
    
    @Override public void paintIcon(Component c, Graphics g, int x, int y){
      Graphics2D g2 = (Graphics2D) g.create();
      g2.translate(x, y);
      g2.drawImage(image, trans, c);
      g2.dispose();
    }
    
    @Override public int getIconWidth(){
      return d.width;
    }
    
    @Override public int getIconHeight(){
      return d.height;
    }
  }
  
  public void SettingButtons(){
    //ポン・カン・ロン・ツモ・無視ボタンの作成
    {
    //ポンのボタンの作成
    buttonPON = new JButton(unPONImageIcon);
    buttonPON.setBounds(gameSizeWidth - 5 * width / 2, gameSizeHeight / 2, height , width);
    c.add(buttonPON);
    buttonPON.addMouseListener(this);
    buttonPON.addMouseMotionListener(this);
    buttonPON.setActionCommand(Integer.toString(100));
    
    //カンのボタンの作成
    buttonKAN = new JButton(unKANImageIcon);
    buttonKAN.setBounds(gameSizeWidth - 5 * width / 2, gameSizeHeight / 2- 2 * width, height , width);
    c.add(buttonKAN);
    buttonKAN.addMouseListener(this);
    buttonKAN.addMouseMotionListener(this);
    buttonKAN.setActionCommand(Integer.toString(200));
    
    //ロンボタンの作成
    buttonRON = new JButton(unRONImageIcon);
    buttonRON.setBounds(gameSizeWidth - 5 * width / 2, gameSizeHeight / 2 + 2 * width, height , width);
    c.add(buttonRON);
    buttonRON.addMouseListener(this);
    buttonRON.addMouseMotionListener(this);
    buttonRON.setActionCommand(Integer.toString(300));
    
    //自摸ボタンの作成
    buttonTSUMO = new JButton(unTSUMOImageIcon);
    buttonTSUMO.setBounds(gameSizeWidth - 5 * width / 2, gameSizeHeight / 2 + 4 * width, height , width);
    c.add(buttonTSUMO);
    buttonTSUMO.addMouseListener(this);
    buttonTSUMO.addMouseMotionListener(this);
    buttonTSUMO.setActionCommand(Integer.toString(350));
    
    //無視ボタンの作成
    buttonIgnore = new JButton(unIgnoreImageIcon);
    buttonIgnore.setBounds(gameSizeWidth - 5 * width / 2, gameSizeHeight / 2 - 4 * width, height , width);
    c.add(buttonIgnore);
    buttonIgnore.addMouseListener(this);
    buttonIgnore.addMouseMotionListener(this);
    buttonIgnore.setActionCommand(Integer.toString(400));
    }
    
    for(int i = 0; i < 13; i++){
      buttonTiles[i] = new JButton();
      c.add(buttonTiles[i]);
      buttonTiles[i].setBounds(i * width + 80, 3 * width + 3 * height + 120, width, height);
      buttonTiles[i].addMouseListener(this);
      buttonTiles[i].addMouseMotionListener(this);
      buttonTiles[i].setActionCommand(Integer.toString(i));
    }
    
    tsumoTile = new JButton(BOARD);
    c.add(tsumoTile);
    tsumoTile.setBounds(13 * width + 110, 3 * width + 3 * height + 120, width, height);
    tsumoTile.addMouseListener(this);
    tsumoTile.addMouseMotionListener(this);
    tsumoTile.setActionCommand(Integer.toString(13));
    
    for(int i = 0; i < 3; i++){
      for(int j = 0; j < 6; j++){
        myDrop[j][i] = new JButton(BOARD);
        c.add(myDrop[j][i]);
        myDrop[j][i].setBounds(3 * height / 2 + j * width / 2 + 255, 3 * width + 3 * height / 2 + 80 + i * height / 2, width / 2, height / 2);
      }
    }
    
    for(int i = 0; i < 3; i++){
      for(int j = 0; j < 6; j++){
        kamicha[j][i] = new JButton(BOARD);
        c.add(kamicha[j][i]);
        kamicha[j][i].setBounds(3 * width + 3 * height / 2 + 255 + i * height / 2, 5 * width / 2 + 3 * height / 2 + 80 - j * width / 2, height / 2, width / 2);
      }
    }
    
    for(int i = 0; i < 3; i++){
      for(int j = 0; j < 6; j++){
        toimen[j][i] = new JButton(BOARD);
        c.add(toimen[j][i]);
        toimen[j][i].setBounds(5 * width / 2 + 3 * height / 2 + 255 - j * width / 2, height + 80 - i * height / 2, width / 2, height / 2);
      }
    }
    
    for(int i = 0; i < 3; i++){
      for(int j = 0; j < 6; j++){
        shimocha[j][i] = new JButton(BOARD);
        c.add(shimocha[j][i]);
        shimocha[j][i].setBounds(height + 255 - i * height / 2, 3 * height / 2 + 80 + j * width / 2, height / 2, width / 2);
      }
    }
    
    //副露関連のボタン
    {
      for(int i = 0; i < 4; i++){
        for(int j = 0; j < 4; j++){
          myHuuro[i][j] = new JButton(BOARD);
          c.add(myHuuro[i][j]);
          myHuuro[i][j].setBounds(gameSizeWidth - 5 * width - j * width / 2, gameSizeHeight - 4 * height + height / 2 - i * height / 2, width / 2, height / 2);
        }
      }
    
      for(int i = 0; i < 4; i++){
        for(int j = 0; j < 4; j++){
          kamichaHuuro[i][j] = new JButton(BOARD);
          c.add(kamichaHuuro[i][j]);
          kamichaHuuro[i][j].setBounds(gameSizeWidth - 2 * width - 110 - 2 * height + i * height / 2, 40 + j * width / 2, height / 2, width / 2);
        }
      }
      
      
      for(int i = 0; i < 4; i++){
        for(int j = 0; j < 4; j++){
          toimenHuuro[i][j] = new JButton(BOARD);
          c.add(toimenHuuro[i][j]);
          toimenHuuro[i][j].setBounds(100 + j * width / 2, 30 + i * height / 2, width / 2, height / 2);
        }
      }
      
      for(int i = 0; i < 4; i++){
        for(int j = 0; j < 4; j++){
          shimochaHuuro[i][j] = new JButton(BOARD);
          c.add(shimochaHuuro[i][j]);
          shimochaHuuro[i][j].setBounds(100 + i * height / 2, gameSizeHeight - 3 * height - 2 * width + j * width / 2, height / 2, width / 2);
        }
      }
    }
  }
  
  public static int[] sortingArray(int[][] normalArray, int direction) {
    int[] sortedArray = new int[13];
    
    // -1以外の要素を一時的に格納
    for(int i = 0; i < 13; i++){
      if (normalArray[direction][i] == -1) {
        sortedArray[i] = 100000;
      }else{
        sortedArray[i] = normalArray[direction][i];
      }
    }
    
    Arrays.sort(sortedArray);
    
    for(int i = 0; i < 13; i++){
      if(sortedArray[i] == 100000){
        sortedArray[i] = -1;
      }
    }
    
    return sortedArray;
  }
  
  public static int countANKO(int [] array){
    int numANKO = 0;
    int counttmp [] = toolCount(array);
    for(int i = 0; i < 34; i++){
      if(counttmp[i] == 3){
        numANKO++;
      }
    }
    return numANKO;
  }
  
  public static boolean judgeJANTOU(int [] array){
    boolean result = false;
    int counttmp [] = toolCount(array);
    
    for(int i = 0; i < 34; i++){
      if(counttmp[i] == 2){
        result = true;
      }
    }
    
    return result;
  }
  
  public void hideTSUMOtile(){
    tsumoTile.setIcon(BOARD);
  }
  
  public static int [] toolCount(int [] array){
    int judge [] = new int [34];
    for(int i = 0; i < 34; i++){
      judge[i] = 0;
    }
    
    for(int i = 0; i < 14; i++){
      if(array[i] != -1){
        judge[array[i]]++;
      }
    }
    
    return judge;
  }
}