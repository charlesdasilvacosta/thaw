# Thaw Project
## Quentin Béacco & Charles Da Silva Costa
## Master 1 Informatique
## Université Paris-Est Marne la Vallée

1. Require
  JDK9 - 147 </br>
  Apache Ant </br>
  Add SSL exception to your Web Browser </br>

2. Compilation

  Build project with apache and, run ant command like: $ant all
  to make all project: compilation, build jar file and generate javadoc

  Clean compiled file and jar with this command: $ant clear
  
  To just compile and generate class file, type: $ant compil
  
  To create jar file, type: $ant jar
  
  To generate javadoc, type: $ant javadoc

3. Execution

  We made run.sh to run server with all java options, if run.sh doesn't work, type: $sudo chmod 755 run.sh
