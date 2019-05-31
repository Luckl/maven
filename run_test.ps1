rm -r C:\work\maven\apache-maven\target\apache-maven-3.6.2-SNAPSHOT

mvn -DskipTests=true package

tar -xzvf C:\work\maven\apache-maven\target\apache-maven-3.6.2-SNAPSHOT-bin.tar.gz -C C:\work\maven\apache-maven\target\

C:\work\maven\apache-maven\target\apache-maven-3.6.2-SNAPSHOT\bin\mvn.cmd -f C:\Users\LucK\IdeaProjects\slf4jadapter package