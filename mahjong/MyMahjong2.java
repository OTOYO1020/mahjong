import java.net.*;
import java.io.*;
import javax.swing.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.image.*;//�摜�����ɕK�v
import java.awt.geom.*;//�摜�����ɕK�v

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
  private int myDirection;  //0:��  1:��  2:��  3:�k
  private int nowDirection = 0;
  private int nowTurn = 0;
  private final int maxTile = (9 * 3 + 7) * 4 - 24;
  private int countTile = 0;  //�v�̐����J�E���g���邽�߂̕ϐ�
  private int tsumo;
  //private int countedPlayers = 0;
  private ArrayList<Integer> tileList = new ArrayList<Integer>(136);
  private int dropTile, numNotPonUser = 0;
  //�͂̕\���p�{�^��
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
  PrintWriter out;//�o�͗p�̃��C�^�[

	public MyMahjong2() {
		//���O�̓��̓_�C�A���O���J��
		String myName = JOptionPane.showInputDialog(null,"���O����͂��Ă�������","���O�̓���",JOptionPane.QUESTION_MESSAGE);
		if(myName.equals("")){
			myName = "No name";//���O���Ȃ��Ƃ��́C"No name"�Ƃ���
		}
    
    String myIP = JOptionPane.showInputDialog(null,"IP�A�h���X����͂��Ă�������","���O�̓���",JOptionPane.QUESTION_MESSAGE);
		if(myIP.equals("")){
			myIP = "localhost";//null�̏ꍇ�Alocalhost�Ƃ���
		}

		//�E�B���h�E���쐬����
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//�E�B���h�E�����Ƃ��ɁC����������悤�ɐݒ肷��
		setTitle("MyMahjong2");//�E�B���h�E�̃^�C�g����ݒ肷��
    gameSizeWidth = 16 * width + 190;
    gameSizeHeight = 3 * width + 4 * height + 200;
		setSize(gameSizeWidth, gameSizeHeight);//�E�B���h�E�̃T�C�Y��ݒ肷��
		c = getContentPane();
    c.setBackground(BackgroundColor);
    
		c.setLayout(null);//�������C�A�E�g�̐ݒ���s��Ȃ�
		//�{�^���̐���
    
    //�v�̐ݒ�
    settingIcons();
    SettingButtons();
    
		//�T�[�o�ɐڑ�����
		Socket socket = null;
		try {
			//"localhost"�́C���������ւ̐ڑ��Dlocalhost��ڑ����IP Address�i"133.42.155.201"�`���j�ɐݒ肷��Ƒ���PC�̃T�[�o�ƒʐM�ł���
			//10000�̓|�[�g�ԍ��DIP Address�Őڑ�����PC�����߂āC�|�[�g�ԍ��ł���PC�㓮�삷��v���O��������肷��
			socket = new Socket("localhost", 10000);
		} catch (UnknownHostException e) {
			System.err.println("�z�X�g�� IP �A�h���X������ł��܂���: " + e);
		} catch (IOException e) {
			 System.err.println("�G���[���������܂���: " + e);
		}
		
		MesgRecvThread mrt = new MesgRecvThread(socket, myName);//��M�p�̃X���b�h���쐬����
		mrt.start();//�X���b�h�𓮂����iRun�������j
	}
		
	//���b�Z�[�W��M�̂��߂̃X���b�h
	public class MesgRecvThread extends Thread {
		
		Socket socket;
		String myName;
		
		public MesgRecvThread(Socket s, String n){
			socket = s;
			myName = n;
		}
		
		//�ʐM�󋵂��Ď����C��M�f�[�^�ɂ���ē��삷��
		public void run() {
			try{
				InputStreamReader sisr = new InputStreamReader(socket.getInputStream());
				BufferedReader br = new BufferedReader(sisr);
				out = new PrintWriter(socket.getOutputStream(), true);
				out.println(myName);//�ڑ��̍ŏ��ɖ��O�𑗂�
        
        String myNumberStr = br.readLine();
        int myNumberInt = Integer.parseInt(myNumberStr);
        myDirection = myNumberInt % 4;
        System.out.println(myDirection);
        
        
				while(true) {
					String inputLine = br.readLine();//�f�[�^����s�������ǂݍ���ł݂�
					if (inputLine != null) {//�ǂݍ��񂾂Ƃ��Ƀf�[�^���ǂݍ��܂ꂽ���ǂ������`�F�b�N����
						String[] inputTokens = inputLine.split(" ");	//���̓f�[�^����͂��邽�߂ɁA�X�y�[�X�Ő؂蕪����
						String cmd = inputTokens[0];//�R�}���h�̎��o���D�P�ڂ̗v�f�����o��
            if(cmd.equals("TILES")){
              String tileString = inputTokens[1];
              String tileNumberString [] = tileString.split(",");
              for(String number: tileNumberString){  //�g��for���ɋC�Â����V�˂ƌĂ�ł���
                tileList.add(Integer.parseInt(number));  //�����ŎI���������ă��X�g��R�Â���
              }
              
              //���߂̔z�z
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
              
              if(differenceDirection != 0){  //���l���v���̂Ă���
                int numSameTile = 0;  //�̂Ĕv�Ɠ����v�����������Ă��邩�𐔂���
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
                }else if(numSameTile == 2){  //�����v���Q���ȏ㎝���Ă���ꍇ
                  out.println("CAN_PON " + myDirection);
                  out.flush();
                }else{  //�|���o���Ȃ��ꍇ
                  out.println("NEXT");  //���̐l�ɏ��Ԃ���
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
              //���̒i�K�ł�nowDirection�́A���O�ɔv���̂Ă��l�̕��p�B�͂̔v�������������s��
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
              
              //���̒i�K�ł́AnowDirection�̓|���������l�B���I�v��`�悷��
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
              //���̒i�K�ł�nowDirection�́A���O�ɔv���̂Ă��l�̕��p�B�͂̔v�������������s��
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
              
              //���̒i�K�ł́AnowDirection�̓|���������l�B���I�v��`�悷��
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
                
                for(int i = 0; i < 13; i++){  //���I�v�ɂȂ����v�̕\�L������
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
            
            if(cmd.equals("RON")){  //�����o���Ă�����������
              judgeFINISH = true;
              int tmpDirection = Integer.parseInt(inputTokens[1]);
              if(tmpDirection == myDirection){
                JOptionPane.showMessageDialog(null, "YOU WIN!!");
              }else{
                JOptionPane.showMessageDialog(null, "PLAYER" + tmpDirection + "�̏����ł�!!");
              }
            }
            
            if(cmd.equals("TSUMO")){  //�����o���Ă�����������
              judgeFINISH = true;
              int tmpDirection = Integer.parseInt(inputTokens[1]);
              if(tmpDirection == myDirection){
                JOptionPane.showMessageDialog(null, "YOU WIN!!");
              }else{
                JOptionPane.showMessageDialog(null, "PLAYER" + tmpDirection + "�̏����ł�!!");
              }
            }
            
          }else{  //�������߂��Ȃ���
            break;
          }
				
				}
				socket.close();
			} catch (IOException e) {
				System.err.println("�G���[���������܂���: " + e);
			}
		}
	}

	public static void main(String[] args) {
		MyMahjong2 net = new MyMahjong2();
		net.setVisible(true);
	}
  	
	public void mouseClicked(MouseEvent e) {//�{�^�����N���b�N�����Ƃ��̏���
    JButton theButton = (JButton)e.getComponent();//�N���b�N�����I�u�W�F�N�g�𓾂�D�^���Ⴄ�̂ŃL���X�g����
    String theArrayIndex = theButton.getActionCommand();//�{�^���̔z��̔ԍ������o��
    
    if(!statusPON && !statusKAN && statusWhole && !judgeFINISH){  //�|���E�J�����o���Ȃ����
      if(countTile != maxTile){  //������v���c���Ă�����
        if(nowDirection == myDirection){  //�����̕��p�ƃQ�[���̕��p����v���Ă��鎞
          if(Integer.parseInt(theArrayIndex) < 13){  //�L���v���������ꍇ
            dropTile = tilesNumber[myDirection][Integer.parseInt(theArrayIndex)];  //�̂Ĕv�̐ݒ�
            tilesNumber[myDirection][Integer.parseInt(theArrayIndex)] = tileList.get(countTile);
            int [] tmpArray = sortingArray(tilesNumber, myDirection);
            
            for(int i = 0; i < 13; i++){
              tilesNumber[myDirection][i] = tmpArray[i];
              buttonTiles[i].setIcon(linkTile(tilesNumber[myDirection][i]));
              c.add(buttonTiles[i]);
            }
          }else if(Integer.parseInt(theArrayIndex) == 13){  //�c���؂�̏ꍇ
            dropTile = tileList.get(countTile);
          }
          out.println("DROP " + nowDirection + " " + dropTile);
          out.flush();
          
          hideTSUMOtile();
        }
      }
    }
    
    if(statusPON && !statusWhole && !judgeFINISH){
      if(Integer.parseInt(theArrayIndex) == 100){  //�|�����邺�I
        out.println("PON " + myDirection);
        out.flush();
      }else if(Integer.parseInt(theArrayIndex) == 400){  //�|���𖳎�����Ƃ�
        out.println("IGNORE");
        out.flush();
      }
    }
    
    if(statusKAN && !statusWhole && !judgeFINISH){
      if(Integer.parseInt(theArrayIndex) == 200){  //�|�����邺�I
        out.println("KAN " + myDirection);
        out.flush();
      }else if(Integer.parseInt(theArrayIndex) == 400){  //�|���𖳎�����Ƃ�
        out.println("IGNORE");
        out.flush();
      }
    }
    
    if(!statusWhole && !judgeFINISH){
      if(Integer.parseInt(theArrayIndex) == 300){
        out.println("RON " + myDirection);
        out.flush();
      }else if(Integer.parseInt(theArrayIndex) == 400){  //�|���𖳎�����Ƃ�
        out.println("IGNORE");
        out.flush();
      }
    }
    
    if(!statusWhole && !judgeFINISH){
      if(Integer.parseInt(theArrayIndex) == 350){
        out.println("TSUMO " + myDirection);
        out.flush();
      }else if(Integer.parseInt(theArrayIndex) == 400){  //�|���𖳎�����Ƃ�
        out.println("IGNORE");
        out.flush();
      }
    }
    
    /*
    if(!statusWhole && !judgeFINISH){
      if(Integer.parseInt(theArrayIndex) == 300){
        out.println("RON " + myDirection);
        out.flush();
      }else if(Integer.parseInt(theArrayIndex) == 400){  //�|���𖳎�����Ƃ�
        out.println("IGNORE");
        out.flush();
      }
    }
    */
    
    if(!statusPON && !statusKAN && !statusWhole && !judgeFINISH){  //�|���������1����������Ԃł����
      if(countTile != maxTile){
        if(nowDirection == myDirection){
          if(tilesNumber[myDirection][Integer.parseInt(theArrayIndex)] != -1){
            dropTile = tilesNumber[myDirection][Integer.parseInt(theArrayIndex)];  //�̂Ĕv�̐ݒ�
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
	
	public void mouseEntered(MouseEvent e) {//�}�E�X���I�u�W�F�N�g�ɓ������Ƃ��̏���
	}
	
	public void mouseExited(MouseEvent e) {//�}�E�X���I�u�W�F�N�g����o���Ƃ��̏���
	}
	
	public void mousePressed(MouseEvent e) {//�}�E�X�ŃI�u�W�F�N�g���������Ƃ��̏����i�N���b�N�Ƃ̈Ⴂ�ɒ��Ӂj
	}
	
	public void mouseReleased(MouseEvent e) {//�}�E�X�ŉ����Ă����I�u�W�F�N�g�𗣂����Ƃ��̏���
	}
	
	public void mouseDragged(MouseEvent e) {//�}�E�X�ŃI�u�W�F�N�g�Ƃ��h���b�O���Ă���Ƃ��̏���
	}

	public void mouseMoved(MouseEvent e) {//�}�E�X���I�u�W�F�N�g��ňړ������Ƃ��̏���
	}
  
  public ImageIcon linkTile (int num){  //��΂����ƌ����������@����
    if(num == -1){
      return BOARD;
    }
    
    int tileType = num / 4;
    if(tileType < 9){  //�ݎq
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
    }else if(tileType < 18){  //���q
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
    }else if(tileType < 27){  //���q
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
    }else if(tileType < 34){  //���v
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
  
  private void settingIcons(){//�v�̐ݒ�
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
    //�|���E�J���E�����E�c���E�����{�^���̍쐬
    {
    //�|���̃{�^���̍쐬
    buttonPON = new JButton(unPONImageIcon);
    buttonPON.setBounds(gameSizeWidth - 5 * width / 2, gameSizeHeight / 2, height , width);
    c.add(buttonPON);
    buttonPON.addMouseListener(this);
    buttonPON.addMouseMotionListener(this);
    buttonPON.setActionCommand(Integer.toString(100));
    
    //�J���̃{�^���̍쐬
    buttonKAN = new JButton(unKANImageIcon);
    buttonKAN.setBounds(gameSizeWidth - 5 * width / 2, gameSizeHeight / 2- 2 * width, height , width);
    c.add(buttonKAN);
    buttonKAN.addMouseListener(this);
    buttonKAN.addMouseMotionListener(this);
    buttonKAN.setActionCommand(Integer.toString(200));
    
    //�����{�^���̍쐬
    buttonRON = new JButton(unRONImageIcon);
    buttonRON.setBounds(gameSizeWidth - 5 * width / 2, gameSizeHeight / 2 + 2 * width, height , width);
    c.add(buttonRON);
    buttonRON.addMouseListener(this);
    buttonRON.addMouseMotionListener(this);
    buttonRON.setActionCommand(Integer.toString(300));
    
    //���̃{�^���̍쐬
    buttonTSUMO = new JButton(unTSUMOImageIcon);
    buttonTSUMO.setBounds(gameSizeWidth - 5 * width / 2, gameSizeHeight / 2 + 4 * width, height , width);
    c.add(buttonTSUMO);
    buttonTSUMO.addMouseListener(this);
    buttonTSUMO.addMouseMotionListener(this);
    buttonTSUMO.setActionCommand(Integer.toString(350));
    
    //�����{�^���̍쐬
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
    
    //���I�֘A�̃{�^��
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
    
    // -1�ȊO�̗v�f���ꎞ�I�Ɋi�[
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