Java x Spring Boot x Bootstrap Demo
===================================
Webアプリのサンプルです。

---------------------------------------------------------------------------

※Docker で実行する時の Docker と IDEA Ultimate の設定

[ Docker Setting ]

Settings → Expose daemon on tcp://localhost:2375 without TLS にチェックする。

[ IDEA Setting ]

Settings → Build, Execution, Deployment → Docker → TCP socket を選択して、
Engine API URL: tcp://localhost:2375 を入力する。

※Web アプリの起動確認は、Docker の app-1 のログで「Started StudioAzuriteRoxWebApplication～」と表示されてればOKです。
そのあと、ブラウザのアドレスバーで「localhost」と入力してください。

---------------------------------------------------------------------------

# 参考にさせていただいたサイト

Spring Security https://www.docswell.com/s/MasatoshiTada/KGVY9K-spring-security-intro#p1

Paging https://qiita.com/kt921112/items/bab0f9d09f7a81805c69

etc...

Thanks for all.
