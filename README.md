【内容】
MyServerMahjong.java : 複数人対戦用のサーバ
MyMahjong2.java : ゲーム本体
netprog.cmd : 実行用のプロンプト

【ゲームプレイまでの流れ】
1.netprogを起動して、サーバ、本体の順にコンパイル
javac MyServerMahjong.java  /  javac MyMahjong2.java

2.順に実行
java MyServerMahjong  /  java MyMahjong2

3.プレイ
４つの接続を確認した時点でゲームをプレイするようにしています。
